package backend.instructions.RRInst;

import backend.Register;
import backend.instructions.MIPSInst;

/**
 * mul指令，将src1和src2的相乘，高32位存入hi，低32位存入lo和dest
 * （不带溢出）
 */
public class MUL extends RRInst {

    public MUL(Register dest, Register src1, Register src2) {
        super(dest, src1, src2);
    }

    @Override
    public String toMIPS() {
        return "mul " + dest + ", " + src1 + ", " + src2;
    }
}
