package backend.instructions.BranchInst;

import backend.Label;
import backend.Register;
import backend.instructions.MIPSInst;

/**
 * Abstract class for branch instructions
 * In llvm IR, there are only BrInst and BrCondInst after calculating the cond value
 * In MIPS, there are b, beq, bne, ble, blt, bge, bgt
 *
 * Before optimization, we use pseudo instruction "b" and "bnez".
 * TODO: So there can be a way to optimize the code by using the correct branch instruction.
 */
public abstract class BranchInst extends MIPSInst {
    public Register src1;
    public Register src2;
    public Label label;

    public BranchInst(Register src1, Register src2, Label label) {
        this.src1 = src1;
        this.src2 = src2;
        this.label = label;
    }

    public BranchInst(Register src1, Label label) {
        this.src1 = src1;
        this.label = label;
    }

    public BranchInst(Label label) {
        this.label = label;
    }
}
