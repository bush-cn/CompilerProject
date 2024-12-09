package midend.visitors;

import error.CompileError;
import frontend.lexer.Token;
import frontend.parser.expression.*;
import frontend.parser.expression.unaryexps.FunctionCall;
import frontend.parser.expression.unaryexps.PrimaryExp;
import frontend.parser.expression.unaryexps.primaryexps.Character;
import frontend.parser.expression.unaryexps.primaryexps.LVal;
import frontend.parser.expression.unaryexps.primaryexps.Number;
import frontend.parser.expression.unaryexps.primaryexps.ParenthesisExp;
import midend.Symbol;
import midend.Visitor;
import midend.llvm.*;
import midend.llvm.instructions.*;
import midend.llvm.types.ArrayType;
import midend.llvm.types.PointerType;
import midend.llvm.types.Type;
import midend.symbols.*;

import java.util.ArrayList;
import java.util.List;

/**
 * AddExp以及子语法成分均返回的是i32类型值
 * RelExp统一变为i1类型
 * 赋值给char类型时需要Truncate
 */
public class ExpVisitor {
    static Visitor visitor = Visitor.getInstance();
    public static int visitConstExp(AddExp addExp) {
        return ((Immediate)visitAddExp(addExp)).immediate;
    }

    public static Value visitExp(Exp exp) {
        return visitAddExp(exp.getAddExp());
    }

    public static Value visitAddExp(AddExp addExp) {
        Value left = visitMulExp(addExp.getMulExp());
        for (AddExp.OpMulExp opMulExp: addExp.getOpMulExps()) {
            Value right = visitMulExp(opMulExp.mulExp);
            if (left instanceof Immediate l && right instanceof Immediate r) {
                // 可以计算得立即数
                switch (opMulExp.op.getTokenType()) {
                    case PLUS -> left = new Immediate(l.immediate + r.immediate);
                    case MINU -> left = new Immediate(l.immediate - r.immediate);
                }
            } else {
                Value result = new Slot(visitor.curFunction);
                switch (opMulExp.op.getTokenType()) {
                    case PLUS -> {
                        visitor.curBasicBlock().addInst(
                                new BinaryInst(result, BinaryInst.BinaryOp.add, Type.i32, left, right)
                        );
                    }
                    case MINU -> {
                        visitor.curBasicBlock().addInst(
                                new BinaryInst(result, BinaryInst.BinaryOp.sub, Type.i32, left, right)
                        );
                    }
                }
                left = result;
            }
        }
        return left;
    }

    public static Value visitMulExp(MulExp mulExp) {
        Value left = visitUnaryExp(mulExp.getUnaryExp());
        for (MulExp.OpUnaryExp opUnaryExp: mulExp.getOpUnaryExps()) {
            Value right = visitUnaryExp(opUnaryExp.unaryExp);
            if (left instanceof Immediate l && right instanceof Immediate r) {
                // 可以计算得立即数
                switch (opUnaryExp.op.getTokenType()) {
                    case MULT -> left = new Immediate(l.immediate * r.immediate);
                    case DIV -> left = new Immediate(l.immediate / r.immediate);
                    case MOD -> left = new Immediate(l.immediate % r.immediate);
                }
            } else {
                Value result = new Slot(visitor.curFunction);
                switch (opUnaryExp.op.getTokenType()) {
                    case MULT -> {
                        visitor.curBasicBlock().addInst(
                                new BinaryInst(result, BinaryInst.BinaryOp.mul, Type.i32, left, right)
                        );
                    }
                    case DIV -> {
                        visitor.curBasicBlock().addInst(
                                new BinaryInst(result, BinaryInst.BinaryOp.sdiv, Type.i32, left, right)
                        );
                    }
                    case MOD -> {
                        visitor.curBasicBlock().addInst(
                                new BinaryInst(result, BinaryInst.BinaryOp.srem, Type.i32, left, right)
                        );
                    }
                }
                left = result;
            }
        }
        return left;
    }

    public static Value visitUnaryExp(UnaryExp unaryExp) {
        Value result; // 在最后计算UnaryOp
        if (unaryExp.getUnaryExpWithoutOp() instanceof FunctionCall functionCall) {
            // 不会出现调用函数的标识符类型不匹配这种错误（“将变量当作函数调用”）
            // 检查使用未定义符号（错误c）
            Token funcName = functionCall.getIdent().getIdent();
            Symbol symbol = visitor.curSymbolTab.lookupSymbol(funcName.getValue());
            if (symbol == null) {
                CompileError.raiseError(funcName.getLine(), CompileError.ErrorType.c);
                return null;
            }
            // 接着检查函数参数个数不匹配（错误d）
            assert symbol instanceof FuncSymbol;
            FuncSymbol funcSymbol = (FuncSymbol) symbol;
            if (functionCall.getFuncRParams() == null) {
                if (!funcSymbol.fParams.isEmpty()) {
                    CompileError.raiseError(funcName.getLine(), CompileError.ErrorType.d);
                    return null;
                }
            }
            else {
                if (funcSymbol.fParams.size() != functionCall.getFuncRParams().getExps().size()) {
                    CompileError.raiseError(funcName.getLine(), CompileError.ErrorType.d);
                    return null;
                }
            }
            // 最后检查参数类型不匹配（错误e）
            boolean raiseErrorE = false;  // 多个参数均不匹配时，只输出一条错误


            List<Function.Param> params = new ArrayList<>();
            for (int i = 0; i < funcSymbol.fParams.size(); i++) {
                Symbol.SymbolType fParamType = funcSymbol.fParams.get(i); // 有且仅有四种
                Exp exp = functionCall.getFuncRParams().getExps().get(i);
                // 【错误处理】
                // 【如果传值为数组类型，则应由FuncRParam一路推导至Ident，不存在任何其他符号】
                if (exp.getAddExp().getMulExp().getUnaryExp().getUnaryExpWithoutOp() instanceof PrimaryExp primaryExp) {
                    if (primaryExp instanceof LVal lVal) {
                        Token t = lVal.getIdent().getIdent();
                        Symbol s = visitor.curSymbolTab.lookupSymbol(t.getValue());
                        // 常量数组不作为参数传入到函数中
                        // 那么有且仅有两种情况下传入的exp为数组，且符号一定已经填入
                        if (lVal.getExp() == null && s != null && s.type == Symbol.SymbolType.IntArray) {
                            // 传入的参数为int型数组
                            if (fParamType != Symbol.SymbolType.IntArray) {
                                raiseErrorE = true;
                            }
                        } else if (lVal.getExp() == null && s != null && s.type == Symbol.SymbolType.CharArray) {
                            // 传入的参数为char型数组
                            if (fParamType != Symbol.SymbolType.CharArray) {
                                raiseErrorE = true;
                            }
                        } else {
                            // 【剩余情况均为：传入的参数exp是变量而不是数组】
                            if (fParamType == Symbol.SymbolType.CharArray
                                    || fParamType == Symbol.SymbolType.IntArray) {
                                raiseErrorE = true;
                            }
                        }
                    } else {
                        // 【剩余情况均为：传入的参数exp是变量而不是数组】
                        if (fParamType == Symbol.SymbolType.CharArray
                                || fParamType == Symbol.SymbolType.IntArray) {
                            raiseErrorE = true;
                        }
                    }
                } // 【剩余情况均为：传入的参数exp是变量而不是数组】
                else {
                    if (fParamType == Symbol.SymbolType.CharArray
                            || fParamType == Symbol.SymbolType.IntArray) {
                        raiseErrorE = true;
                    }
                }
                // 【代码生成中新增】需要记录调用函数的实参：
                Type paramType = switch (fParamType) {
                    case Int, ConstInt -> Type.i32;
                    case Char, ConstChar -> Type.i8;
                    case IntArray, ConstIntArray -> new PointerType(Type.i32);
                    default -> new PointerType(Type.i8);
                };

                if (paramType instanceof PointerType pointerType) {
                    // 数组传参时，一路推到标识符，传递指针即可
                    String name = ((LVal)exp.getAddExp().getMulExp().getUnaryExp().getUnaryExpWithoutOp()).getIdent().getIdent().getValue();
                    Symbol arrayParam = visitor.curSymbolTab.lookupSymbol(name);
                    Value p;
                    if (arrayParam instanceof IntArraySymbol intArraySymbol) {
                        if (intArraySymbol.length == 0) {
                            p = intArraySymbol.address;
                        } else {
                            p = new Slot(visitor.curFunction);
                            visitor.curBasicBlock().addInst(
                                    new GEPInst(p, new ArrayType(intArraySymbol.length, Type.i32), arrayParam.address, new Immediate(0)).
                                            setComment("load pointer param " + name)
                            );
                        }
                    } else if (arrayParam instanceof CharArraySymbol charArraySymbol){
                        if (charArraySymbol.length == 0) {
                            p = charArraySymbol.address;
                        } else {
                            p = new Slot(visitor.curFunction);
                            visitor.curBasicBlock().addInst(
                                    new GEPInst(p, new ArrayType(charArraySymbol.length, Type.i8), arrayParam.address, new Immediate(0)).
                                            setComment("load pointer param " + name)
                            );
                        }
                    } else if (arrayParam instanceof ConstIntArraySymbol constIntArraySymbol) {
                        p = new Slot(visitor.curFunction);
                        visitor.curBasicBlock().addInst(
                                new GEPInst(p, new ArrayType(constIntArraySymbol.values.length, Type.i32), arrayParam.address, new Immediate(0)).
                                        setComment("load pointer param " + name)
                        );
                    } else {
                        p = new Slot(visitor.curFunction);
                        visitor.curBasicBlock().addInst(
                                new GEPInst(p, new ArrayType(((ConstCharArraySymbol)arrayParam).values.length, Type.i8), arrayParam.address, new Immediate(0)).
                                        setComment("load pointer param " + name)
                        );
                    }
                    params.add(new Function.Param(paramType, p));
                } else {
                    Value expValue = visitExp(exp);  // 均为i32类型
                    if (paramType.equals(Type.i8) && expValue instanceof Slot) {
                        Value truc = new Slot(visitor.curFunction);
                        visitor.curBasicBlock().addInst(
                                new TruncInst(truc, Type.i32, expValue, Type.i8)
                        );
                        expValue = truc;
                    }
                    // 加入params
                    params.add(new Function.Param(paramType, expValue));
                }

            }
            if (raiseErrorE) {
                CompileError.raiseError(funcName.getLine(), CompileError.ErrorType.e);
            }
            // 【代码生成新增】生成call指令
            Type retType;
            switch (funcSymbol.type) {
                case IntFunc -> {
                    retType = Type.i32;
                    result = new Slot(visitor.curFunction);
                }
                case CharFunc -> {
                    retType = Type.i8;
                    result = new Slot(visitor.curFunction);
                }
                default -> {
                    retType = Type.Void;
                    result = null;
                }
            };
            visitor.curBasicBlock().addInst(
                    new CallInst(result, retType, funcName.getValue(), params)
            );
            if (retType.equals(Type.i8)) {
                Value zext = new Slot(visitor.curFunction);
                visitor.curBasicBlock().addInst(
                        new ZextInst(zext, Type.i8, result, Type.i32)
                );
                result = zext;
            }
        } else {
            // unaryExp instanceof PrimaryExp
            PrimaryExp primaryExp = (PrimaryExp) unaryExp.getUnaryExpWithoutOp();
            if (primaryExp instanceof ParenthesisExp parenthesisExp) {
                result =  visitExp((parenthesisExp.getExp()));
            } else if (primaryExp instanceof LVal lVal) {
                // TODO: 不每次都使用load获取值，并处理phi值。先获取value，若无则load并存储
                result = visitLValValue(lVal);
            } else if (primaryExp instanceof Number number) {
                result = new Immediate(Integer.parseInt(number.getIntConst().getIntConst().getValue()));
            } else {
                Character character = (Character) primaryExp;
                char c = character.getCharConst().getCharConst().getValue().charAt(1);
                if (c != '\\') {
                    return new Immediate(c);
                } else {
                    return new Immediate(getEscapedCharacter(character.getCharConst().getCharConst().getValue().charAt(2)));
                }
            }
        }
        List<UnaryOp> unaryOps = unaryExp.getUnaryOps();
        for (int i = unaryOps.size() - 1; i >= 0; i--) {
            UnaryOp unaryOp = unaryOps.get(i);
            switch (unaryOp.getUnaryToken().getTokenType()) {
                case MINU -> {
                    if (result instanceof Immediate immediate) {
                        result = new Immediate(-immediate.immediate);
                    } else {
                        Value s = new Slot(visitor.curFunction);
                        visitor.curBasicBlock().addInst(
                                new BinaryInst(s, BinaryInst.BinaryOp.sub, Type.i32, new Immediate(0), result)
                        );
                        result = s;
                    }
                }
                case NOT -> {
                    // 虽然只有条件关系式中出现NOT，但是仍有要返回i32类型
                    if (result instanceof Immediate immediate) {
                        result = new Immediate(immediate.immediate == 0? 1: 0);
                    } else {
                        // !a逻辑运算结果等价于与(a==0)
                        Value s = new Slot(visitor.curFunction);
                        visitor.curBasicBlock().addInst(
                                new BinaryInst(s, BinaryInst.BinaryOp.eq, Type.i32, new Immediate(0), result).
                                        setComment("NOT operation")
                        );
                        Value zext = new Slot(visitor.curFunction);
                        visitor.curBasicBlock().addInst(
                                new ZextInst(zext, Type.i1, s, Type.i32)
                        );
                        result = zext;
                    }
                }
                default -> {
                    // "+"，do nothing
                }
            }
        }
        return result;
    }

    /*
    返回左值的指针，用于Stmt中赋值
     */
    public static Value visitLValPointer(LVal lVal) {
        Token token = lVal.getIdent().getIdent();
        Symbol symbol = visitor.curSymbolTab.lookupSymbol(token.getValue());
        if (symbol == null) {
            CompileError.raiseError(token.getLine(), CompileError.ErrorType.c);
            return null; // 代码生成中，不会执行到此
        }

        if (lVal.getExp() != null) {
            // 数组
            // 生成getelemnetptr指令
            Value expValue = visitExp(lVal.getExp());
            Value basePointer = symbol.address;
            Value result = new Slot(visitor.curFunction);
            if (symbol instanceof CharArraySymbol charArraySymbol) {
                if (charArraySymbol.length == 0) {
                    // 数组指针参数
                    visitor.curBasicBlock().addInst(
                            new GEPInst(result, Type.i8, basePointer, expValue).
                                    setComment("get &" + token.getValue() + "[" + expValue.toText() + "] from param")
                    );
                } else {
                    visitor.curBasicBlock().addInst(
                            new GEPInst(result, new ArrayType(charArraySymbol.length, Type.i8), basePointer, expValue).
                                    setComment("get &" + token.getValue() + "[" + expValue.toText() + "]")
                    );
                }
            } else if (symbol instanceof IntArraySymbol intArraySymbol){
                if (intArraySymbol.length == 0) {
                    // 数组指针参数
                    visitor.curBasicBlock().addInst(
                            new GEPInst(result, Type.i32, basePointer, expValue).
                                    setComment("get &" + token.getValue() + "[" + expValue.toText() + "] from param")
                    );
                } else {
                    visitor.curBasicBlock().addInst(
                            new GEPInst(result, new ArrayType(intArraySymbol.length, Type.i32), basePointer, expValue).
                                    setComment("get &" + token.getValue() + "[" + expValue.toText() + "]")
                    );
                }
            }  // 不考虑对常量赋值
            return result;
        } else {
            return symbol.address;
        }
    }

    /*
    返回LVal的值（立即数或槽），用于UnaryExp中计算
    由于Exp计算时始终使用i32计算，因此统一返回i32的值
     */
    public static Value visitLValValue(LVal lVal) {
        Token token = lVal.getIdent().getIdent();
        Symbol symbol = visitor.curSymbolTab.lookupSymbol(token.getValue());
        if (symbol == null) {
            CompileError.raiseError(token.getLine(), CompileError.ErrorType.c);
            return null; // 代码生成中，不会执行到此
        }

        // 形参的的address为null，值为curValue

        if (lVal.getExp() != null) {
            // 数组
            Value expValue = visitExp(lVal.getExp());
            // 对于可以计算索引的数组常量：直接求得
            if ((symbol.type == Symbol.SymbolType.ConstIntArray
                    || symbol.type == Symbol.SymbolType.ConstCharArray)
                    && expValue instanceof Immediate i) {
                if (symbol.type == Symbol.SymbolType.ConstIntArray) {
                    return new Immediate(((ConstIntArraySymbol)symbol).values[i.immediate]);
                } else {
                    return new Immediate(((ConstCharArraySymbol)symbol).values[i.immediate]);
                }
            }
            // 剩余变量数组，或索引不可计算的常量数组：生成getelemnetptr指令
            Value basePointer = symbol.address;
            Value pointer = new Slot(visitor.curFunction);
            Type eleType;
            if (symbol instanceof CharArraySymbol charArraySymbol) {
                eleType = Type.i8;
                if (charArraySymbol.length == 0) {
                    // 数组指针参数
                    visitor.curBasicBlock().addInst(
                            new GEPInst(pointer, Type.i8, basePointer, expValue).
                                    setComment("get &" + token.getValue() + "[" + expValue.toText() + "] from param")
                    );
                } else {
                    visitor.curBasicBlock().addInst(
                            new GEPInst(pointer, new ArrayType(charArraySymbol.length, Type.i8), basePointer, expValue).
                                    setComment("get &" + token.getValue() + "[" + expValue.toText() + "]")
                    );
                }
            } else if (symbol instanceof IntArraySymbol intArraySymbol) {
                eleType = Type.i32;
                if (intArraySymbol.length == 0) {
                    // 数组指针参数
                    visitor.curBasicBlock().addInst(
                            new GEPInst(pointer, Type.i32, basePointer, expValue).
                                    setComment("get &" + token.getValue() + "[" + expValue.toText() + "] from param")
                    );
                } else {
                    visitor.curBasicBlock().addInst(
                            new GEPInst(pointer, new ArrayType(intArraySymbol.length, Type.i32), basePointer, expValue).
                                    setComment("get &" + token.getValue() + "[" + expValue.toText() + "]")
                    );
                }
            } else if (symbol instanceof ConstCharArraySymbol constCharArraySymbol) {
                eleType = Type.i8;
                visitor.curBasicBlock().addInst(
                        new GEPInst(pointer, new ArrayType(constCharArraySymbol.values.length, Type.i8), basePointer, expValue).
                                setComment("get &" + token.getValue() + "[" + expValue.toText() + "]")
                );
            } else {
                ConstIntArraySymbol constIntArraySymbol = (ConstIntArraySymbol) symbol;
                eleType = Type.i32;
                visitor.curBasicBlock().addInst(
                        new GEPInst(pointer, new ArrayType(constIntArraySymbol.values.length, Type.i32), basePointer, expValue).
                                setComment("get &" + token.getValue() + "[" + expValue.toText() + "]")
                );
            }
            // 再根据指针取值
            Value result = new Slot(visitor.curFunction);
            visitor.curBasicBlock().addInst(
                    new LoadInst(result, eleType, new PointerType(eleType), pointer).
                            setComment("load value of " + token.getValue() + "[" + expValue.toText() + "]")
            );
            // 若为char型，则用zext转换类型
            if (eleType.equals(Type.i8)) {
                Value zext = new Slot(visitor.curFunction);
                visitor.curBasicBlock().addInst(
                        new ZextInst(zext, Type.i8, result, Type.i32)
                );
                result = zext;
            }
            return result;
        } else {
            if (symbol.type == Symbol.SymbolType.ConstInt) {
                // 常量直接返回立即数值
                return new Immediate(((ConstIntSymbol)symbol).value);
            } else if (symbol.type == Symbol.SymbolType.ConstChar) {
                return new Immediate(((ConstCharSymbol)symbol).value);
            } else if (symbol.type == Symbol.SymbolType.Int) {
                Value result = new Slot(visitor.curFunction);  // slot加入在continue新建块之前！！！
                visitor.curBasicBlock().addInst(
                        new LoadInst(result, Type.i32, new PointerType(Type.i32), symbol.address).
                                setComment("load value of " + token.getValue())
                );
                return result;
            } else { // Char
                Value result = new Slot(visitor.curFunction);
                visitor.curBasicBlock().addInst(
                        new LoadInst(result, Type.i8, new PointerType(Type.i8), symbol.address).
                                setComment("load value of " + token.getValue())
                );
                Value zext = new Slot(visitor.curFunction);
                visitor.curBasicBlock().addInst(
                        new ZextInst(zext, Type.i8, result, Type.i32)
                );
                return zext;
            }

        }
    }

    // 只在Cond里会出现LOrExp
    // TODO: 短路求值
    public static void visitLOrExp(LOrExp lOrExp,
                                   BasicBlock trueJump,
                                   BasicBlock falseJump) {
        if (lOrExp.getOpLAndExps().isEmpty()) {
            visitLAndExp(lOrExp.getlAndExp(),
                    trueJump,
                    falseJump
            );
        } else {
            BasicBlock newBlock = new BasicBlock();
            // link
            visitor.curBasicBlock().linkTo(newBlock);
            newBlock.linkTo(trueJump);
            newBlock.linkTo(falseJump);
            visitLAndExp(lOrExp.getlAndExp(),
                    trueJump,
                    newBlock
            );
            int i = 0;
            for (; i < lOrExp.getOpLAndExps().size() - 1; i++) {
                LAndExp lAndExp = lOrExp.getOpLAndExps().get(i).lAndExp;
                BasicBlock nextNewBlock = new BasicBlock();
                // link
                newBlock.linkTo(nextNewBlock);
                nextNewBlock.linkTo(trueJump);
                nextNewBlock.linkTo(falseJump);
                visitor.curFunction.addBasicBlock(newBlock);
                visitor.checkoutBlock(newBlock);
                visitLAndExp(lAndExp,
                        trueJump,
                        nextNewBlock
                        );
                newBlock = nextNewBlock;
            }
            LAndExp lAndExp = lOrExp.getOpLAndExps().get(i).lAndExp;
            visitor.curFunction.addBasicBlock(newBlock);
            visitor.checkoutBlock(newBlock);
            visitLAndExp(lAndExp,
                    trueJump,
                    falseJump);
        }

    }
    public static void visitLAndExp(LAndExp lAndExp,
                                    BasicBlock trueJump,
                                    BasicBlock falseJump) {
        if (lAndExp.getOpEqExps().isEmpty()) {
            Value eqExpValue = visitEqExp(lAndExp.getEqExp());
            // 生成跳转指令
            visitor.curBasicBlock().addInst(
                    new BrCondInst(eqExpValue, trueJump.label, falseJump.label).
                            setComment("single LAndExp")
            );
            visitor.curBasicBlock().linkTo(trueJump);
            visitor.curBasicBlock().linkTo(falseJump);
        } else {
            BasicBlock newBlock = new BasicBlock();
            Value eqExpValue = visitEqExp(lAndExp.getEqExp());
            // 生成跳转指令
            visitor.curBasicBlock().addInst(
                    new BrCondInst(eqExpValue, newBlock.label, falseJump.label).
                            setComment("#1 LAndExp")
            );
            visitor.curBasicBlock().linkTo(newBlock);
            visitor.curBasicBlock().linkTo(falseJump);
            int i = 0;
            for (; i < lAndExp.getOpEqExps().size() - 1; i++) {
                EqExp eqExp = lAndExp.getOpEqExps().get(i).eqExp;
                BasicBlock nextNewBlock = new BasicBlock();
                visitor.curFunction.addBasicBlock(newBlock);
                visitor.checkoutBlock(newBlock);
                eqExpValue = visitEqExp(eqExp);
                visitor.curBasicBlock().addInst(
                        new BrCondInst(eqExpValue, nextNewBlock.label, falseJump.label).
                                setComment("#" + (i + 2) + " LAndExp")
                );
                visitor.curBasicBlock().linkTo(nextNewBlock);
                visitor.curBasicBlock().linkTo(falseJump);
                newBlock = nextNewBlock;
            }
            visitor.curFunction.addBasicBlock(newBlock);
            visitor.checkoutBlock(newBlock);
            EqExp eqExp = lAndExp.getOpEqExps().get(i).eqExp;
            eqExpValue = visitEqExp(eqExp);
            visitor.curBasicBlock().addInst(
                    new BrCondInst(eqExpValue, trueJump.label, falseJump.label).
                            setComment("#" + (i + 2) + " LAndExp")
            );
            visitor.curBasicBlock().linkTo(trueJump);
            visitor.curBasicBlock().linkTo(falseJump);
        }
    }

    // 返回i1类型
    // 比较运算符没有优先级！虽然EqExp比RelExp高一级，但是运算优先级相等！！
    public static Value visitEqExp(EqExp eqExp) {
        Value left = visitRelExp(eqExp.getRelExp());
        if (eqExp.getOpRelExps().isEmpty()) {
            // 逻辑运算a等价于a!=0
            if (left instanceof Immediate imm) {
                // 可以计算得立即数，保证不会有操作数均为立即数的指令
                return new Immediate(imm.immediate != 0 ? 1 : 0);
            }
            Value result = new Slot(visitor.curFunction);
            visitor.curBasicBlock().addInst(
                    new BinaryInst(result, BinaryInst.BinaryOp.ne, Type.i32, left, new Immediate(0)).
                            setComment("single RelExp, that is != 0")
            );
            return result;
        }
        for (EqExp.OpRelExp opRelExp: eqExp.getOpRelExps()) {
            Value right = visitRelExp(opRelExp.relExp);
            if (left instanceof Immediate l && right instanceof Immediate r) {
                // 可以计算得立即数
                switch (opRelExp.op.getTokenType()) {
                    case EQL -> left = new Immediate(l.immediate == r.immediate ? 1 : 0);
                    case NEQ -> left = new Immediate(l.immediate != r.immediate ? 1 : 0);
                }
            } else {
                Value result = new Slot(visitor.curFunction);
                switch (opRelExp.op.getTokenType()) {
                    case EQL -> {
                        visitor.curBasicBlock().addInst(
                                new BinaryInst(result, BinaryInst.BinaryOp.eq, Type.i32, left, right)
                        );
                    }
                    case NEQ -> {
                        visitor.curBasicBlock().addInst(
                                new BinaryInst(result, BinaryInst.BinaryOp.ne, Type.i32, left, right)
                        );
                    }
                }
                left = result;
            }
        }
        return left;
    }

    // 返回i32类型（因为可能直接比较EqExp 3==4这种）
    public static Value visitRelExp(RelExp relExp) {
        Value left = visitAddExp(relExp.getAddExp());
        for (RelExp.OpAddExp opAddExp: relExp.getOpAddExps()) {
            Value right = visitAddExp(opAddExp.addExp);
            if (left instanceof Immediate l && right instanceof Immediate r) {
                // 可以计算得立即数
                switch (opAddExp.op.getTokenType()) {
                    case LSS -> left = new Immediate(l.immediate < r.immediate ? 1 : 0);
                    case GRE -> left = new Immediate(l.immediate > r.immediate ? 1 : 0);
                    case LEQ -> left = new Immediate(l.immediate <= r.immediate ? 1 : 0);
                    case GEQ -> left = new Immediate(l.immediate >= r.immediate ? 1 : 0);
                }
            } else {
                Value result = new Slot(visitor.curFunction);
                switch (opAddExp.op.getTokenType()) {
                    case LSS -> {
                        visitor.curBasicBlock().addInst(
                                new BinaryInst(result, BinaryInst.BinaryOp.slt, Type.i32, left, right)
                        );
                    }
                    case GRE -> {
                        visitor.curBasicBlock().addInst(
                                new BinaryInst(result, BinaryInst.BinaryOp.sgt, Type.i32, left, right)
                        );
                    }
                    case LEQ -> {
                        visitor.curBasicBlock().addInst(
                                new BinaryInst(result, BinaryInst.BinaryOp.sle, Type.i32, left, right)
                        );
                    }
                    case GEQ -> {
                        visitor.curBasicBlock().addInst(
                                new BinaryInst(result, BinaryInst.BinaryOp.sge, Type.i32, left, right)
                        );
                    }
                }
                Value zext = new Slot(visitor.curFunction);
                visitor.curBasicBlock().addInst(
                        new ZextInst(zext, Type.i1, result, Type.i32)
                );
                left = zext;
            }
        }
        return left;
    }

    public static char getEscapedCharacter(char c) {
        // '\r'不需要转义
        switch (c) {
            case 'a' -> {
                return (char)7; // '\a'响铃
            }
            case 'b' -> {
                return '\b';  // 退格符
            }
            case 't' -> {
                return '\t';  // 制表符
            }
            case 'n' -> {
                return '\n';  // 换行符
            }
            case 'v' -> {
                return (char)11;  // '\v'垂直制表符
            }
            case 'f' -> {
                return '\f';  // 换页符
            }
            case '"' -> {
                return '\"'; // 双引号
            }
            case '\'' -> {
                return '\''; // 单引号
            }
            case '\\' -> {
                return '\\'; // 反斜杠
            }
            case '0' -> {
                return '\0'; // 空字符
            }
            default -> {
                return c;
            }
        }
    }
}
