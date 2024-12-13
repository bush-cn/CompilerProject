package backend;

import backend.instructions.BranchInst.B;
import backend.instructions.BranchInst.BNEZ;
import backend.instructions.DataPseudo;
import backend.instructions.JumpInst.JAL;
import backend.instructions.JumpInst.JR;
import backend.instructions.MemInst.*;
import backend.instructions.RIInst.*;
import backend.instructions.RRInst.*;
import backend.instructions.SYSCALL;
import backend.optimizer.DataFlow;
import backend.optimizer.Graph;
import midend.llvm.*;
import midend.llvm.Module;
import midend.llvm.globalvalues.ConstString;
import midend.llvm.globalvalues.GlobalArrayVar;
import midend.llvm.globalvalues.GlobalVariable;
import midend.llvm.instructions.*;
import midend.llvm.types.ArrayType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 后端单例类
 * 翻译器，将中间代码翻译为MIPS汇编代码
 */
public class Translator {
    private static final Translator translator = new Translator();

    private Translator() {
    }

    public static Translator getInstance() {
        return translator;
    }

    // 当前函数，用于区分label名，以及判断是否为main函数，获取跨块活跃变量
    private Function curFunction;
    // 当前函数的栈帧
    private StackFrame curStackFrame;
    // 寄存器池
    private RegisterPool registerPool;
    // 跨基本块活跃变量
    private Set<Slot> interBlockLive;

    /**
     * 对外接口，翻译中间代码为MIPS汇编代码
     * @param module LLVM模块
     * @return MIPS汇编代码
     */
    public MIPSCode translate(Module module) {
        MIPSCode mipsCode = new MIPSCode();
        // 数据流分析
        DataFlow.liveVariable(module);

        fillDataSeg(mipsCode, module);
        fillTextSeg(mipsCode, module);
        return mipsCode;
    }

    private void fillDataSeg(MIPSCode mipsCode, Module module) {
        for (GlobalValue globalValue : module.globalValues) {
            if (globalValue instanceof GlobalVariable globalVariable) {
                // 单个全局变量
                mipsCode.addDataPseudo(translateGlobalVariable(globalVariable));
            } else if (globalValue instanceof GlobalArrayVar globalArrayVar) {
                // 数组全局变量
                mipsCode.addDataPseudo(translateGlobalArrayVar(globalArrayVar));
            }
        }
        // 免去4字节对齐，最后加入字符串常量
        for (GlobalValue globalValue : module.globalValues) {
            if (globalValue instanceof ConstString constString) {
                // 字符串常量
                mipsCode.addDataPseudo(new DataPseudo(constString.name, DataPseudo.DataType.ASCIIZ, '\"' + constString.constString + '\"'));
            }
        }
    }

    private DataPseudo translateGlobalVariable(GlobalVariable globalVariable) {
        // 单个全局变量
        // 对于i8类型，也以word的4字节存储
        return new DataPseudo(globalVariable.name,
                DataPseudo.DataType.WORD, String.valueOf(globalVariable.initValue));
    }

    private DataPseudo translateGlobalArrayVar(GlobalArrayVar globalArrayVar) {
        // 数组全局变量
        // 对于i8类型，也以word的4字节存储
        // 初始值
        String value;
        if (globalArrayVar.initValues == null || globalArrayVar.initValues.length == 0) {
            // zeroinitializer
            value = "0:" + globalArrayVar.type.length;
        } else {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (; i < globalArrayVar.initValues.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(globalArrayVar.initValues[i]);
            }
            for (; i < globalArrayVar.type.length; i++) {
                sb.append(", 0");
            }
            value = sb.toString();
        }
        return new DataPseudo(globalArrayVar.name, DataPseudo.DataType.WORD, value);
    }


    /**
     * 生成主要的代码段.text
     * @param mipsCode MIPS代码
     * @param module LLVM模块
     *               以函数为单位生成代码
     */
    private void fillTextSeg(MIPSCode mipsCode, Module module) {
        List<GlobalValue> globalValues = module.globalValues;
        Function mainFunction = (Function)globalValues.get(globalValues.size() - 1);
        // 先翻译main函数，作为MIPS代码程序入口
        translateFunction(mipsCode, mainFunction);
        // 翻译其他函数
        for (int i = 0; i < globalValues.size() - 1; i++) {
            if (globalValues.get(i) instanceof Function function) {
                translateFunction(mipsCode, function);
            }
        }
    }

    /**
     *  以【函数】为单位翻译，每个函数由一个寄存器池管理
     * @param mipsCode MIPS代码
     * @param function 函数
     *
     */
    private void translateFunction(MIPSCode mipsCode, Function function) {
        // 函数入口标签
        mipsCode.addLabel(new Label(function.name));
        // 初始化栈帧
        registerPool = new RegisterPool(mipsCode, function);
        curStackFrame = new StackFrame();
        curFunction = function;
        // 图着色全局寄存器分配
        Graph.graphColoring(function, registerPool, curStackFrame);
        for (BasicBlock basicBlock : function.basicBlocks) {
            translateBasicBlock(mipsCode, basicBlock);
        }
    }

    private void translateBasicBlock(MIPSCode mipsCode, BasicBlock basicBlock) {
        mipsCode.addLabel(new Label(curFunction, basicBlock.label));
        for (midend.llvm.Instruction instruction : basicBlock.instructions) {
            translateInstruction(mipsCode, instruction);
            registerPool.flushTemp();   // 清空temp
        }
    }

    /**
     * 翻译大多数指令的步骤如下：
     *      1. 使用 find 获取 use 的寄存器
     *      2. 使用 deallocUse 回收 use 中不活跃变量的寄存器
     *      2. 使用 allocDef 分配 def 使用的寄存器【allocDef可能返回null，此时需要保存到栈上】
     *      3. 使用 allocTemp 分配不绑定slot的寄存器
     *      3. 生成MIPS指令
     *
     * @param mipsCode MIPS代码
     * @param instruction 中间代码指令
     */
    private void translateInstruction(MIPSCode mipsCode, Instruction instruction) {
        if (instruction instanceof AllocaInst allocaInst) { // 【result存的是地址！】
            registerPool.deallocUse(allocaInst);
            Register r = registerPool.allocDef(allocaInst, curStackFrame);
            int totalSize;
            if (allocaInst.type instanceof ArrayType arrayType) {
                // 数组类型
                totalSize = arrayType.length * 4;
            } else {
                // 非数组类型
                totalSize = 4;
            }
            curStackFrame.allocStackSize(totalSize);    // 分配栈空间
            if (r != null) {
                mipsCode.addMIPSInst(new ADDIU(r, Register.SP, -curStackFrame.getSize()).setComment(allocaInst.toText()));
            } else {
                // 若r为null，不需要分配寄存器，保存地址在栈上
                Register temp = registerPool.allocTemp(curStackFrame);  // temp存的是地址，但是要保存在栈上
                mipsCode.addMIPSInst(new ADDIU(temp, Register.SP, -curStackFrame.getSize()).setComment(allocaInst.toText()));
                curStackFrame.recordLocal((Slot)allocaInst.result, 4);  // 地址大小为4字节
                mipsCode.addMIPSInst(new SW(temp, Register.SP, curStackFrame.getOffset((Slot)allocaInst.result)).setComment("spilled inter-alive"));
            }
        } else if (instruction instanceof LoadInst loadInst) {
            if (loadInst.pointer instanceof GlobalVariable || loadInst.pointer instanceof GlobalArrayVar) {
                // 全局变量
                Register r = registerPool.allocDef(loadInst, curStackFrame);
                if (r != null) {
                    mipsCode.addMIPSInst(new LWLabel(r, ((GlobalValue)loadInst.pointer).name));
                } else {
                    Register temp = registerPool.allocTemp(curStackFrame);
                    mipsCode.addMIPSInst(new LWLabel(temp, ((GlobalValue)loadInst.pointer).name));
                    mipsCode.addMIPSInst(new SW(temp, Register.SP, curStackFrame.getOffset((Slot)loadInst.result)));
                }
            } else {
                // 局部变量
                Register p = registerPool.find((Slot) loadInst.pointer, curStackFrame);
                registerPool.deallocUse(loadInst);
                Register r = registerPool.allocDef(loadInst, curStackFrame);
                if (r != null) {
                    // 从栈中加载
                    mipsCode.addMIPSInst(new LW(r, p, 0));
                } else {
                    Register temp = registerPool.allocTemp(curStackFrame);
                    mipsCode.addMIPSInst(new LW(temp, p, 0));
                    mipsCode.addMIPSInst(new SW(temp, Register.SP, curStackFrame.getOffset((Slot)loadInst.result)));
                }
            }
        } else if (instruction instanceof StoreInst storeInst) {
            Register v;
            if (storeInst.initValue instanceof Slot) {
                v = registerPool.find((Slot) storeInst.initValue, curStackFrame);
            } else {
                v = registerPool.allocTemp(curStackFrame);
                mipsCode.addMIPSInst(new LI(v, ((Immediate) storeInst.initValue).immediate));
            }
            if (storeInst.pointer instanceof GlobalVariable || storeInst.pointer instanceof GlobalArrayVar) {
                // 全局变量
                registerPool.deallocUse(storeInst); // 无result，不需要分配
                mipsCode.addMIPSInst(new SWLabel(v, ((GlobalValue)storeInst.pointer).name));
            } else {
                // 局部变量
                Register p = registerPool.find((Slot) storeInst.pointer, curStackFrame);
                registerPool.deallocUse(storeInst); // 无result，不需要分配
                // 存入栈中
                mipsCode.addMIPSInst(new SW(v, p, 0));
            }
        } else if (instruction instanceof BinaryInst binaryInst) {
            translateBinaryInst(mipsCode, binaryInst);
        } else if (instruction instanceof BrInst brInst) {
            mipsCode.addMIPSInst(new B(new Label(curFunction, brInst.label)));
        } else if (instruction instanceof BrCondInst brCondInst) {
            if (brCondInst.cond instanceof Immediate imm) {
                // 若条件是立即数，直接跳转
                if (imm.immediate != 0) {
                    mipsCode.addMIPSInst(new B(new Label(curFunction, brCondInst.ifTrueLabel)));
                } else {
                    mipsCode.addMIPSInst(new B(new Label(curFunction, brCondInst.ifFalseLabel)));
                }
            } else {
                Register cond = registerPool.find((Slot) brCondInst.cond, curStackFrame);
                registerPool.deallocUse(brCondInst);    // 无result，不需要分配
                mipsCode.addMIPSInst(new BNEZ(cond, new Label(curFunction, brCondInst.ifTrueLabel)));
                mipsCode.addMIPSInst(new B(new Label(curFunction, brCondInst.ifFalseLabel)));
            }
        } else if (instruction instanceof GEPInst gepInst) {
            // 获取数组元素地址
            // TODO: 若index是立即数，可以优化为不使用GEP指令，而是在后端直接使用 lw指令加偏移量
            translateGEPInst(mipsCode, gepInst);
        } else if (instruction instanceof PutStrInst putStrInst) {
            mipsCode.addMIPSInst(new LI(Register.V0, 4));   // 将系统调用号4加载到v0
            Register temp = null;
            if (useA0(putStrInst)) temp = saveA0(mipsCode); // 若a0被使用，保存a0
            mipsCode.addMIPSInst(new LALabel(Register.A0, putStrInst.constString.name));  // 将字符串地址加载到a0
            mipsCode.addMIPSInst(new SYSCALL());   // 系统调用
            if (useA0(putStrInst)) restoreA0(mipsCode, temp); // 恢复a0
        } else if (instruction instanceof CallInst callInst) {
            // 在LLVM IR中，使用外部链接函数进行I/O；而在MIPS中，使用系统调用进行I/O
            switch (callInst.name) {
                case "putint" -> translatePutInt(mipsCode, callInst);
                case "putch" -> translatePutChar(mipsCode, callInst);
                case "getint" -> translateGetInt(mipsCode, callInst);
                case "getchar" -> translateGetChar(mipsCode, callInst);
                default -> translateCallInst(mipsCode, callInst);
            }
        } else if (instruction instanceof RetInst retInst) {
            translateRetInst(mipsCode, retInst);
        } else if (instruction instanceof ZextInst zextInst) {
            // zext指令只将对应key修改，不转换类型，因为char和int在MIPS中用4字节存储
            registerPool.replaceSlot((Slot) zextInst.value, (Slot) zextInst.result);
        } else if (instruction instanceof TruncInst truncInst) {
            // truc指令同上
            registerPool.replaceSlot((Slot) truncInst.value, (Slot) truncInst.result);
        } else {
            throw new RuntimeException("Unknown instruction: " + instruction);
        }
    }

    private void translateBinaryInst(MIPSCode mipsCode, BinaryInst binaryInst) {
        // 实际在LLVM IR中使用到的运算符只有case中的几种，（在enum里查看次数）
        // 若后续需要添加新的运算符，需要在这里添加对应的MIPS指令
        if (binaryInst.op1 instanceof Slot && binaryInst.op2 instanceof Slot) {
            // RR指令
            Register s1 = registerPool.find((Slot) binaryInst.op1, curStackFrame);
            Register s2 = registerPool.find((Slot) binaryInst.op2, curStackFrame);
            registerPool.deallocUse(binaryInst);
            Register r = registerPool.allocDef(binaryInst, curStackFrame);
            boolean spillInterLive = false;
            if (r == null) {
                r = registerPool.allocTemp(curStackFrame);
                spillInterLive = true;
            }
            // 生成MIPS指令
            switch (binaryInst.binaryOp) {
                case add -> mipsCode.addMIPSInst(new ADDU(r, s1, s2));   // 加
                case sub -> mipsCode.addMIPSInst(new SUBU(r, s1, s2));   // 减
                case mul -> mipsCode.addMIPSInst(new MUL(r, s1, s2));  // 乘
                case sdiv -> mipsCode.addMIPSInst(new DIV(r, s1, s2));  // 除
                case srem -> mipsCode.addMIPSInst(new REM(r, s1, s2));  // 取余
                case eq -> mipsCode.addMIPSInst(new SEQ(r, s1, s2));
                case ne -> mipsCode.addMIPSInst(new SNE(r, s1, s2));
                case sgt -> mipsCode.addMIPSInst(new SGT(r, s1, s2));
                case sge -> mipsCode.addMIPSInst(new SGE(r, s1, s2));
                case slt -> mipsCode.addMIPSInst(new SLT(r, s1, s2));
                case sle -> mipsCode.addMIPSInst(new SLE(r, s1, s2));
                default -> throw new RuntimeException("Unknown binary operation: " + binaryInst.binaryOp);
            }
            if (spillInterLive) {
                mipsCode.addMIPSInst(new SW(r, Register.SP, curStackFrame.getOffset((Slot) binaryInst.result)));
            }
        } else {
            // RI指令，不会有两个操作数全是立即数的情况，但需要注意立即数和寄存器的顺序
            if (binaryInst.op1 instanceof Slot && binaryInst.op2 instanceof Immediate) {
                // 与MIPS指令顺序相同，第二个操作数是立即数，则直接使用RI指令
                Register s = registerPool.find((Slot) binaryInst.op1, curStackFrame);
                int imm = ((Immediate) binaryInst.op2).immediate;
                registerPool.deallocUse(binaryInst);
                Register r = registerPool.allocDef(binaryInst, curStackFrame);
                boolean spillInterLive = false;
                if (r == null) {
                    r = registerPool.allocTemp(curStackFrame);
                    spillInterLive = true;
                }
                // 生成MIPS指令
                switch (binaryInst.binaryOp) {
                    case add -> mipsCode.addMIPSInst(new ADDIU(r, s, imm));   // 加
                    case sub -> mipsCode.addMIPSInst(new SUBIU(r, s, imm));   // 减
                    // TODO: 开启优化，将乘法优化为移位、将除法优化为乘法？
                    case mul -> mipsCode.addMIPSInst(new MULI(r, s, imm));  // 乘
                    case sdiv -> mipsCode.addMIPSInst(new DIVI(r, s, imm));  // 除
                    case srem -> mipsCode.addMIPSInst(new REMI(r, s, imm));  // 取余
                    case eq -> mipsCode.addMIPSInst(new SEQI(r, s, imm));
                    case ne -> mipsCode.addMIPSInst(new SNEI(r, s, imm));
                    case sgt -> mipsCode.addMIPSInst(new SGTI(r, s, imm));
                    case sge -> mipsCode.addMIPSInst(new SGEI(r, s, imm));
                    case slt -> mipsCode.addMIPSInst(new SLTI(r, s, imm));
                    case sle -> mipsCode.addMIPSInst(new SLEI(r, s, imm));
                    default -> throw new RuntimeException("Unknown binary operation: " + binaryInst.binaryOp);
                }
                if (spillInterLive) {
                    mipsCode.addMIPSInst(new SW(r, Register.SP, curStackFrame.getOffset((Slot) binaryInst.result)));
                }
            } else {
                Register s = registerPool.find((Slot) binaryInst.op2, curStackFrame);
                int imm = ((Immediate) binaryInst.op1).immediate;
                registerPool.deallocUse(binaryInst);
                Register r = registerPool.allocDef(binaryInst, curStackFrame);
                boolean spillInterLive = false;
                if (r == null) {
                    r = registerPool.allocTemp(curStackFrame);
                    spillInterLive = true;
                }
                // 与MIPS指令顺序相反，第一个操作数是立即数
                // 【add, mul, eq, ne】交换顺序不影响计算结果，【sgt, slt, sge, sle】可交换并取反，【sub, sdiv, srem】不可交换
                if (binaryInst.binaryOp == BinaryInst.BinaryOp.add || binaryInst.binaryOp == BinaryInst.BinaryOp.mul
                        || binaryInst.binaryOp == BinaryInst.BinaryOp.eq || binaryInst.binaryOp == BinaryInst.BinaryOp.ne) {
                    // 交换顺序不影响计算结果
                    // 生成MIPS指令
                    switch (binaryInst.binaryOp) {
                        case add -> mipsCode.addMIPSInst(new ADDIU(r, s, imm));   // 加
                        case mul -> mipsCode.addMIPSInst(new MULI(r, s, imm));  // 乘
                        case eq -> mipsCode.addMIPSInst(new SEQI(r, s, imm));
                        case ne -> mipsCode.addMIPSInst(new SNEI(r, s, imm));
                    }
                } else if (binaryInst.binaryOp == BinaryInst.BinaryOp.sgt) {
                    mipsCode.addMIPSInst(new SLTI(r, s, imm)); // 取反
                } else if (binaryInst.binaryOp == BinaryInst.BinaryOp.slt) {
                    mipsCode.addMIPSInst(new SGTI(r, s, imm)); // 取反
                } else if (binaryInst.binaryOp == BinaryInst.BinaryOp.sge) {
                    mipsCode.addMIPSInst(new SLEI(r, s, imm)); // 取反
                } else if (binaryInst.binaryOp == BinaryInst.BinaryOp.sle) {
                    mipsCode.addMIPSInst(new SGEI(r, s, imm)); // 取反
                } else if (binaryInst.binaryOp == BinaryInst.BinaryOp.sub || binaryInst.binaryOp == BinaryInst.BinaryOp.sdiv
                        || binaryInst.binaryOp == BinaryInst.BinaryOp.srem) {
                    // 【sub, sdiv, srem】不可交换
                    // 生成MIPS指令
                    Register temp = registerPool.allocTemp(curStackFrame);
                    mipsCode.addMIPSInst(new LI(temp, imm));
                    switch (binaryInst.binaryOp) {
                        case sub -> mipsCode.addMIPSInst(new SUBU(r, temp, s));   // 减
                        case sdiv -> mipsCode.addMIPSInst(new DIV(r, temp, s));  // 除
                        case srem -> mipsCode.addMIPSInst(new REM(r, temp, s));  // 取余
                    }
                } // 增加其他LLVM指令
                if (spillInterLive) {
                    mipsCode.addMIPSInst(new SW(r, Register.SP, curStackFrame.getOffset((Slot) binaryInst.result)));
                }
            }
        }
    }

    /**
     * 若为全局变量，分配结果寄存器后加载标签地址，然后自增加上偏移；
     * 若为局部变量，分配结果寄存器后，地址所在寄存器加上偏移保存至结果寄存器
     * @param mipsCode MIPS代码
     * @param gepInst GEP指令
     */
    private void translateGEPInst(MIPSCode mipsCode, GEPInst gepInst) {
        if (gepInst.index instanceof Slot) {
            Register index = registerPool.find((Slot) gepInst.index, curStackFrame);
            if (gepInst.arrayValue instanceof GlobalVariable || gepInst.arrayValue instanceof GlobalArrayVar) {
                // 全局变量
                registerPool.deallocUse(gepInst);
                Register r = registerPool.allocDef(gepInst, curStackFrame);
                boolean spillInterLive = false;
                if (r == null) {
                    r = registerPool.allocTemp(curStackFrame);
                    spillInterLive = true;
                }
                Register tmp = registerPool.allocTemp(curStackFrame);
                mipsCode.addMIPSInst(new SLL(tmp, index, 2));     // tmp = index * 4
                mipsCode.addMIPSInst(new LALabel(r, ((GlobalValue)gepInst.arrayValue).name));
                mipsCode.addMIPSInst(new ADDU(r, r, tmp));      // r = r + tmp
                if (spillInterLive) {
                    mipsCode.addMIPSInst(new SW(r, Register.SP, curStackFrame.getOffset((Slot) gepInst.result)));
                }
            } else {
                // 局部变量
                Register pointer = registerPool.find((Slot) gepInst.arrayValue, curStackFrame);
                registerPool.deallocUse(gepInst);
                Register r = registerPool.allocDef(gepInst, curStackFrame);
                boolean spillInterLive = false;
                if (r == null) {
                    r = registerPool.allocTemp(curStackFrame);
                    spillInterLive = true;
                }
                Register tmp = registerPool.allocTemp(curStackFrame);
                mipsCode.addMIPSInst(new SLL(tmp, index, 2));     // t = index * 4
                mipsCode.addMIPSInst(new ADDU(r, pointer, tmp));               // r = p + t
                if (spillInterLive) {
                    mipsCode.addMIPSInst(new SW(r, Register.SP, curStackFrame.getOffset((Slot) gepInst.result)));
                }
            }
        } else {
            int imm = ((Immediate) gepInst.index).immediate;
            if (gepInst.arrayValue instanceof GlobalVariable || gepInst.arrayValue instanceof GlobalArrayVar) {
                // 全局变量
                registerPool.deallocUse(gepInst);
                Register r = registerPool.allocDef(gepInst, curStackFrame);
                if (r != null) {
                    mipsCode.addMIPSInst(new LALabel(r, ((GlobalValue)gepInst.arrayValue).name, imm * 4));
                } else {
                    Register temp = registerPool.allocTemp(curStackFrame);
                    mipsCode.addMIPSInst(new LALabel(temp, ((GlobalValue)gepInst.arrayValue).name, imm * 4));
                    mipsCode.addMIPSInst(new SW(temp, Register.SP, curStackFrame.getOffset((Slot) gepInst.result)));
                }
            } else {
                // 局部变量
                Register pointer = registerPool.find((Slot) gepInst.arrayValue, curStackFrame);
                registerPool.deallocUse(gepInst);
                Register r = registerPool.allocDef(gepInst, curStackFrame);
                if (r != null) {
                    mipsCode.addMIPSInst(new ADDIU(r, pointer, imm * 4));
                } else {
                    Register temp = registerPool.allocTemp(curStackFrame);
                    mipsCode.addMIPSInst(new ADDIU(temp, pointer, imm * 4));
                    mipsCode.addMIPSInst(new SW(temp, Register.SP, curStackFrame.getOffset((Slot) gepInst.result)));
                }
            }
        }
    }

    /**
     * 函数调用：翻译call指令
     * @param mipsCode MIPS代码
     * @param callInst call指令
     * 【调用者动作】：
     *          1. 保存ra寄存器
     *          2. 将后续需要用到的保留寄存器值存入栈中
     *          3. 将参数压栈、[如果a0-a3有冲突（需要传递且后续要用到），则需要保存并恢复]
     *          4. 更改sp寄存器的值
     *          5. 使用jal指令
     *          6. 取v0返回值并填入相应寄存器（若有返回值）
     *          7. 将sp寄存器的值恢复
     *          8. 恢复ra寄存器值、保存的寄存器值、恢复冲突的a0-a3寄存器值
     *          9. 继续其他指令……
     *  【被调用者动作】：
     *          1. 执行函数逻辑，若使用参数则取寄存器值或栈上值
     *          2. 执行到ret指令，返回值存入v0（若有）
     *          3. 使用jr ra返回
     *
     *                 【12.12bug】参数压栈后，再对a0-a3赋值，则可能会出现压栈完又溢出寄存器而导致错误
     */
    private void translateCallInst(MIPSCode mipsCode, CallInst callInst) {
        // 函数调用
        // 【注意】因为被调用者需要靠偏移取参数，所以此时只能分配参数的栈空间，应该禁止alloc寄存器防止溢出。
        //  因此提前alloc一个临时寄存器，专门用于存放参数值
        Register temp = registerPool.allocTemp(curStackFrame);

        int formerStackSize = curStackFrame.getSize();    // 记录调用前栈帧大小
        // 保存ra寄存器
        curStackFrame.allocStackSize(4);
        mipsCode.addMIPSInst(new SW(Register.RA, Register.SP, -curStackFrame.getSize()));
        curStackFrame.saveRegister(Register.RA);

        // 保存现场，先保留必须的【保留】寄存器值
        for (Map.Entry<Slot, Register> entry: registerPool.globalAllocation.entrySet()) {
            curStackFrame.allocStackSize(4);
            mipsCode.addMIPSInst(new SW(entry.getValue(), Register.SP, -curStackFrame.getSize()));
            curStackFrame.saveRegister(entry.getValue());
        }
        // 后续可能用到的临时寄存器值
        for (Map.Entry<Slot, Register> entry: registerPool.getAllocated().entrySet()) {
            // 还未deallocUse，无此判断条件可能多余保存后面不会用到的参数
            if (callInst.liveOut.contains(entry.getKey())) {
                curStackFrame.allocStackSize(4);
                mipsCode.addMIPSInst(new SW(entry.getValue(), Register.SP, -curStackFrame.getSize()));
                curStackFrame.saveRegister(entry.getValue());
            }
        }
        // 传递参数
        List<Function.Param> params = callInst.params;
        for (int i = params.size() - 1; i >= 4; i--) {
            // 若超过4个参数，将多余的参数存入栈中
            if (params.get(i).value instanceof Immediate imm) {
                mipsCode.addMIPSInst(new LI(temp, imm.immediate));
            } else {
                temp = registerPool.findParam((Slot) params.get(i).value, temp, curStackFrame);
            }
            curStackFrame.allocStackSize(4);
            mipsCode.addMIPSInst(new SW(temp, Register.SP, -curStackFrame.getSize()));
        }
        for (int i = 3; i >= 0; i--) {
            Register a = switch (i) {
                case 0 -> Register.A0;
                case 1 -> Register.A1;
                case 2 -> Register.A2;
                case 3 -> Register.A3;
                default -> null;
            };
            if (i < curFunction.params.size()
                    && callInst.liveOut.contains((Slot)curFunction.params.get(i).value)
                    && i < params.size()) {
                // 若a0-a3有冲突（需要传递且后续要用到），则需要保存并恢复
                // TODO: 用寄存器保存or压栈保存？
                mipsCode.addMIPSInst(new SW(a, Register.SP, i * 4));
            }
            // 前4个参数存入寄存器
            curStackFrame.allocStackSize(4);
            if (i < params.size()) {
                if (params.get(i).value instanceof Immediate imm) {
                    mipsCode.addMIPSInst(new LI(a, imm.immediate));
                } else {
                    temp = registerPool.findParam((Slot) params.get(i).value, temp, curStackFrame);
                    mipsCode.addMIPSInst(new MOVE(a, temp));
                }
            }
            // 用寄存器传参，但是栈空间仍要保留
        }
        registerPool.deallocUse(callInst);      // 只有在find完后才能deallocUse
        // 调用函数，生成MIPS指令
        mipsCode.addMIPSInst(new ADDIU(Register.SP, Register.SP, -curStackFrame.getSize())); // 更改sp寄存器的值
        mipsCode.addMIPSInst(new JAL(new Label(callInst.name)));
        mipsCode.addMIPSInst(new ADDIU(Register.SP, Register.SP, curStackFrame.getSize())); // 恢复sp寄存器的值
        // 恢复寄存器和栈帧
        List<Register> savedRegisters = curStackFrame.getSavedRegisters();
        for (int i = 0; i < savedRegisters.size(); i++) {
            mipsCode.addMIPSInst(new LW(savedRegisters.get(i), Register.SP, -(formerStackSize + (i + 1) * 4)));
        }
        curStackFrame.restore(formerStackSize);    // 恢复栈帧，包括大小和保存的寄存器
        // 恢复a0-a3寄存器
        for (int i = 3; i >= 0; i--) {
            Register a = switch (i) {
                case 0 -> Register.A0;
                case 1 -> Register.A1;
                case 2 -> Register.A2;
                case 3 -> Register.A3;
                default -> null;
            };
            if (i < curFunction.params.size()
                    && callInst.liveOut.contains((Slot)curFunction.params.get(i).value)
                    && i < params.size()) {
                // 若a0-a3有冲突（需要传递且后续要用到），则需要保存并恢复
                mipsCode.addMIPSInst(new LW(a, Register.SP, i * 4));
            }
        }
        // 从v0寄存器取返回值（若有）
        if (callInst.result != null) {
            Register r = registerPool.allocDef(callInst, curStackFrame);
            if (r != null) {
                mipsCode.addMIPSInst(new MOVE(r, Register.V0));
            } else {
                Register t = registerPool.allocTemp(curStackFrame);
                mipsCode.addMIPSInst(new MOVE(t, Register.V0));
                mipsCode.addMIPSInst(new SW(t, Register.SP, curStackFrame.getOffset((Slot)callInst.result)));
            }
        }
    }

    private void translateRetInst(MIPSCode mipsCode, RetInst retInst) {
        // 若为main函数，通过系统调用退出
        if (curFunction.name.equals("main")) {
            mipsCode.addMIPSInst(new LI(Register.V0, 10));
            mipsCode.addMIPSInst(new SYSCALL());
            return;
        }
        if (retInst.value != null) {
            if (retInst.value instanceof Slot) {
                Register r = registerPool.find((Slot) retInst.value, curStackFrame);
                registerPool.deallocUse(retInst); // 无result，不需要分配
                mipsCode.addMIPSInst(new MOVE(Register.V0, r));
            } else {
                mipsCode.addMIPSInst(new LI(Register.V0, ((Immediate) retInst.value).immediate));
            }
        }
        mipsCode.addMIPSInst(new JR(Register.RA));
    }

    private boolean useA0(Instruction inst) {
        return (!curFunction.params.isEmpty() && inst.liveOut.contains((Slot)curFunction.params.get(0).value));
    }
    private Register saveA0(MIPSCode mipsCode) {
        // 先保存a0寄存器的值
        Register temp = registerPool.allocTemp(curStackFrame);
        mipsCode.addMIPSInst(new MOVE(temp, Register.A0));
        return temp;
    }
    private void restoreA0(MIPSCode mipsCode, Register temp) {
        // 恢复a0寄存器的值
        mipsCode.addMIPSInst(new MOVE(Register.A0, temp));
    }
    /**
     * 翻译putint、putch、getint、getchar四个I/O指令
     * 若修改 a0寄存器，需要先保存a0寄存器的值，然后恢复
     * TODO: 优化时，后续不使用a0就不用保存和恢复。或者保存在栈上，不用a0（性能更低？）
     * @param mipsCode MIPS代码
     * @param callInst call指令
     */
    private void translatePutInt(MIPSCode mipsCode, CallInst callInst) {
        if (callInst.params.get(0).value instanceof Immediate imm) {
            mipsCode.addMIPSInst(new LI(Register.V0, 1));   // 将系统调用号1加载到v0

            // 若后续需要用到a0，则保存a0寄存器的值
            Register temp = null;
            if (useA0(callInst)) temp = saveA0(mipsCode);

            mipsCode.addMIPSInst(new LI(Register.A0, imm.immediate));  // 将整数值加载到a0
            mipsCode.addMIPSInst(new SYSCALL());   // 系统调用

            if (useA0(callInst)) restoreA0(mipsCode, temp);
        } else {
            Register r = registerPool.find((Slot) callInst.params.get(0).value, curStackFrame);
            mipsCode.addMIPSInst(new LI(Register.V0, 1));   // 将系统调用号1加载到v0

            // 若后续需要用到a0，则保存a0寄存器的值
            Register temp = null;
            if (useA0(callInst)) temp = saveA0(mipsCode);

            registerPool.deallocUse(callInst); // 无result，不需要分配
            mipsCode.addMIPSInst(new MOVE(Register.A0, r));  // 将整数值加载到a0
            mipsCode.addMIPSInst(new SYSCALL());   // 系统调用

            if (useA0(callInst)) restoreA0(mipsCode, temp);
        }
    }

    private void translatePutChar(MIPSCode mipsCode, CallInst callInst) {
        if (callInst.params.get(0).value instanceof Immediate imm) {
            mipsCode.addMIPSInst(new LI(Register.V0, 11));   // 将系统调用号11加载到v0

            // 若后续需要用到a0，则保存a0寄存器的值
            Register temp = null;
            if (useA0(callInst)) temp = saveA0(mipsCode);

            mipsCode.addMIPSInst(new LI(Register.A0, imm.immediate));  // 将字符值加载到a0
            mipsCode.addMIPSInst(new SYSCALL());   // 系统调用

            if (useA0(callInst)) restoreA0(mipsCode, temp);
        } else {
            Register r = registerPool.find((Slot) callInst.params.get(0).value, curStackFrame);
            mipsCode.addMIPSInst(new LI(Register.V0, 11));   // 将系统调用号11加载到v0

            // 若后续需要用到a0，则保存a0寄存器的值
            Register temp = null;
            if (useA0(callInst)) temp = saveA0(mipsCode);

            registerPool.deallocUse(callInst); // 无result，不需要分配
            mipsCode.addMIPSInst(new MOVE(Register.A0, r));  // 将字符值加载到a0
            mipsCode.addMIPSInst(new SYSCALL());   // 系统调用

            if (useA0(callInst)) restoreA0(mipsCode, temp);
        }
    }

    private void translateGetInt(MIPSCode mipsCode, CallInst callInst) {
        mipsCode.addMIPSInst(new LI(Register.V0, 5));   // 将系统调用号5加载到v0
        mipsCode.addMIPSInst(new SYSCALL());   // 系统调用
        registerPool.deallocUse(callInst);
        Register r = registerPool.allocDef(callInst, curStackFrame);
        if (r != null) {
            mipsCode.addMIPSInst(new MOVE(r, Register.V0));
        } else {
            r = registerPool.allocTemp(curStackFrame);
            mipsCode.addMIPSInst(new MOVE(r, Register.V0));
            mipsCode.addMIPSInst(new SW(r, Register.SP, curStackFrame.getOffset((Slot) callInst.result)));
        }
    }

    private void translateGetChar(MIPSCode mipsCode, CallInst callInst) {
        mipsCode.addMIPSInst(new LI(Register.V0, 12));   // 将系统调用号12加载到v0
        mipsCode.addMIPSInst(new SYSCALL());   // 系统调用
        registerPool.deallocUse(callInst);
        Register r = registerPool.allocDef(callInst, curStackFrame);
        if (r != null) {
            mipsCode.addMIPSInst(new MOVE(r, Register.V0));
        } else {
            r = registerPool.allocTemp(curStackFrame);
            mipsCode.addMIPSInst(new MOVE(r, Register.V0));
            mipsCode.addMIPSInst(new SW(r, Register.SP, curStackFrame.getOffset((Slot) callInst.result)));
        }
    }
}
