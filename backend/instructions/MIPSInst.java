package backend.instructions;

import backend.MIPSElement;

/**
 * MIPS指令，类似LLVM的Instruction
 * 需要注意的是由于mips指令太多，不好一一修改toMIPS方法，因此注释在顶层MIPSCode中输出
 */
public abstract class MIPSInst implements MIPSElement {
    public String comment = null;   // 被MIPSCode类中的toString方法使用
    public MIPSInst setComment(String comment) {
        this.comment = comment;
        return this;
    }
    abstract public String toMIPS();
}
