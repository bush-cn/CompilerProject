package backend.instructions.RRInst;

import backend.Register;
import backend.instructions.MIPSInst;

/**
 * RRInst抽象类，表示参数为两个寄存器的指令
 */
public abstract class RRInst extends MIPSInst {
    public Register dest;
    public Register src1;
    public Register src2;

    public RRInst(Register dest, Register src1, Register src2) {
        this.dest = dest;
        this.src1 = src1;
        this.src2 = src2;
    }

    public RRInst(Register src1, Register src2) {
        this.src1 = src1;
        this.src2 = src2;
    }
}
