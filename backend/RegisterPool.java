package backend;

import backend.instructions.MemInst.LW;
import backend.instructions.MemInst.SW;
import midend.llvm.BasicBlock;
import midend.llvm.Function;
import midend.llvm.Instruction;
import midend.llvm.Slot;

import java.util.*;


/**
 * 寄存器池，用于分配和回收寄存器
 * 维护一个allocated表，记录虚拟寄存器slot对应的物理寄存器
 */
public class RegisterPool {
    // 使用FIFO算法分配寄存器
    public static final List<Register> tempRegisters = List.of(Register.T0, Register.T1, Register.T2, Register.T3, Register.T4, Register.T5, Register.T6, Register.T7, Register.T8, Register.T9);
    public static final List<Register> savedRegisters = List.of(Register.S0, Register.S1, Register.S2, Register.S3, Register.S4, Register.S5, Register.S6, Register.S7);
    private final MIPSCode mipsCode;
    private final Function function;    // 一个函数由一个RegisterPool管理
    private final List<BasicBlock> notTranslated;  // 未翻译的基本块
    public RegisterPool(MIPSCode mipsCode, Function function) {
        this.mipsCode = mipsCode;
        this.function = function;
        notTranslated = new ArrayList<>(function.basicBlocks);
    }
    // 记录虚拟寄存器slot对应的物理寄存器
    private final Map<Slot, Register> allocated = new HashMap<>();
    // 用于实现FIFO算法溢出寄存器
    private final LinkedList<Register> tempQueue = new LinkedList<>();
    private final LinkedList<Register> savedQueue = new LinkedList<>();
    public Map<Slot, Register> getAllocated() {
        return allocated;
    }

    // 查找slot对应的寄存器
    public Register find(Slot slot, StackFrame curStackFrame) {
        if (allocated.containsKey(slot)) {
            return allocated.get(slot);
        } else {
            // 判断是否为局部变量
            if (curStackFrame.isLocal(slot) ||
                    slot.slotId >= 4) {
                // 从栈上加载局部变量（富偏移），或者大于4个的函数参数（非负偏移）
                Register register = allocate(slot, curStackFrame);
                mipsCode.addMIPSInst(new LW(register, Register.SP, curStackFrame.getOffset(slot)));
                return register;
            } else {
                // 函数参数a0-a3
                return switch (slot.slotId) {
                    case 0 -> Register.A0;
                    case 1 -> Register.A1;
                    case 2 -> Register.A2;
                    case 3 -> Register.A3;
                    default -> null;
                };
            }
        }
    }

    /**
     * 回收指令instruction的use集不再使用的寄存器
     * 【precondition】：use集合中的slot分配的寄存器已通过find找到
     * @param instruction 需要处理的指令
     */
    public void deallocUse(Instruction instruction) {
        // 先释放不活跃变量使用的寄存器，【必须确保调用前已经通过find找到使用的寄存器】
        Iterator<Map.Entry<Slot, Register>> iterator = allocated.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Slot, Register> entry = iterator.next();
            if (!instruction.liveOut.contains(entry.getKey()) && !function.interBlockLive.contains(entry.getKey())) {
                // 在指令层面，不能释放跨块活跃变量的寄存器
                tempQueue.remove(entry.getValue());
                savedQueue.remove(entry.getValue());
                iterator.remove();
            }
        }
    }

    // 释放保留寄存器
    public void deallocUse(BasicBlock block) {
        notTranslated.remove(block);
        // 若对于所有未翻译的基本块，变量都不再活跃，则释放保留寄存器
        for (Slot slot :function.interBlockLive) {
            boolean active = false;
            for (BasicBlock notTrans: notTranslated) {
                if (notTrans.liveIn.contains(slot)) {
                    active = true;
                    break;
                }
            }
            if (!active) {
                tempQueue.remove(allocated.get(slot));
                savedQueue.remove(allocated.get(slot));
                allocated.remove(slot);
            }
        }
    }

    /**
     * 分配指令instruction的def需要用到的寄存器
     * @param instruction 需要处理的指令
     * @param curStackFrame 当前的栈帧，用于溢出时分配栈空间
     * @return 分配的临时寄存器（若不需要分配，则返回null）
     */
    public Register allocDef(Instruction instruction, StackFrame curStackFrame) {
        // 有定义的寄存器，分配一个临时寄存器
        Slot slot = instruction.def.iterator().next();
        return allocate(slot, curStackFrame);
    }

    // TODO: 分配寄存器--优化

    /**
     * 私有方法，遍历从 registers 中分配一个寄存器
     * @param slot 需要分配的slot
     * @param curStackFrame 当前的栈帧
     * @return 分配的寄存器
     */
    private Register allocate(Slot slot, StackFrame curStackFrame) {
        // 如果是跨块活跃变量，分配保留寄存器，否则分配临时寄存器
        List<Register> registers =
            function.interBlockLive.contains(slot) ? savedRegisters : tempRegisters;
        for (Register r: registers) {
            if (!allocated.containsValue(r)) {
                // 若空闲，即找到
                if (registers == tempRegisters) {
                    tempQueue.addLast(r);
                } else {
                    savedQueue.addLast(r);
                }
                allocated.put(slot, r);
                return r;
            }
        }
        // 溢出处理
        return spill(slot, curStackFrame);
    }

    // 分配一个临时寄存器，不存放在allocated中，不保存其中的值
    // 用于翻译LLVM IR的一条指令时，需要一个临时寄存器
    // 若在一条指令中还分配了其他寄存器，需要先分配其他需要记录的寄存器，最后分配不记录的寄存器
    public Register allocateTemp(StackFrame curStackFrame) {
        for (Register r: tempRegisters) {
            if (!allocated.containsValue(r)) {
                return r;
            }
        }
        return spill(null, curStackFrame);
    }

    /**
     * 溢出处理，将溢出寄存器的值存到栈上
     * @param curStackFrame 当前的栈帧
     * @param slot 需要分配的slot，可为null
     */
    private Register spill(Slot slot, StackFrame curStackFrame) {
        // FIFO算法，取出最早分配的寄存器
        Register spillRegister;
        if (slot == null || !function.interBlockLive.contains(slot)) {
            spillRegister = tempQueue.removeFirst();
        } else {
            spillRegister = savedQueue.removeFirst();
        }
        Slot spillSlot = null;
        for (Map.Entry<Slot, Register> entry: allocated.entrySet()) {
            if (entry.getValue() == spillRegister) {
                spillSlot = entry.getKey();
            }
        }
        // 将寄存器的值存到栈上
        curStackFrame.recordLocal(spillSlot, 4);
        mipsCode.addMIPSInst(new SW(spillRegister, Register.SP, -curStackFrame.getSize()).setComment("\tspill reg"));
        // 移除allocated中的记录
        allocated.remove(spillSlot);
        if (slot != null) {
            if (function.interBlockLive.contains(slot)) {
                savedQueue.addLast(spillRegister);
            } else {
                tempQueue.addLast(spillRegister);
            }
            allocated.put(slot, spillRegister);
        }
        return spillRegister;
    }

    // 将map中的slot值改为新的slot，用于处理zext和truct指令
    public void replaceSlot(Slot oldSlot, Slot newSlot) {
        Register register = allocated.get(oldSlot);
        allocated.remove(oldSlot);
        allocated.put(newSlot, register);
    }
}
