package backend;

import midend.llvm.Slot;

import java.util.*;

/**
 * 栈帧结构（从高到低增长）：
 * sp+size->|- - - - - - - |   以下为栈帧内容：
 *          |  局部变量      |
 *          |- - - - - - - |   以下调用函数后需生成的：
 *          |  返回地址      |
 *          |- - - - - - - |
 *          |  保留寄存器    |   （若后续寄存器还需要使用，则需要保存）
 *          |- - - - - - - |
 *          |  被调用者参数  |   （包括a0-a3。从高到低，先压最后一个参数）
 *  sp->    |- - - - - - - |
 */
public class StackFrame {
    // 字节大小，栈帧需要8字节对齐（此实验中不考虑）
    private int size;
    // 局部变量虚拟寄存器相对于栈顶的偏移（负数）
    private final HashMap<Slot, Integer> slotOffset = new HashMap<>();

    // 保存的现场（寄存器）【包括ra和其他需要用到的寄存器，不包括参数】
    private final List<Register> savedRegisters = new ArrayList<>();

    public int getSize() {
        return size;
    }
    public List<Register> getSavedRegisters() {
        return savedRegisters;
    }

    public void saveRegister(Register register) {
        savedRegisters.add(register);
        size += 4;
    }

    public void addArgNum() {
        size += 4;
    }

    public void restore(int downSize) {
        size -= downSize;
        savedRegisters.clear();
    }
    public StackFrame() {
        size = 0;
    }

    // 【仅保存】局部变量在栈上的偏移，仍需要手动生成sp寄存器的加减指令
    public void recordLocal(Slot slot) {
        size += 4;      // i8类型也分配4字节
        slotOffset.put(slot, -size);
    }

    // 判断是否为局部变量
    public boolean isLocal(Slot slot) {
        return slotOffset.containsKey(slot);
    }

    public int getOffset(Slot slot) {
        if (slotOffset.containsKey(slot)) {
            // 局部变量相对于sp寄存器（即栈顶）的偏移
            return size + slotOffset.get(slot);
        } else {
            // 不是局部变量，则是函数参数
            return size + 4 * slot.slotId;
        }
    }
}
