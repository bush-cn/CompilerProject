package backend.instructions.RRInst;

import backend.Register;
import backend.instructions.MIPSInst;

/**
 * 只用于除法优化时，进行无符号乘法
 */
public class MULTU extends MIPSInst {
    Register src1;
    Register src2;

    public MULTU(Register src1, Register src2) {
        this.src1 = src1;
        this.src2 = src2;
    }

    @Override
    public String toMIPS() {
        return "multu " + src1 + ", " + src2;
    }
}
