package backend.instructions.RRInst;

import backend.Register;

/**
 * 伪指令，将src1除以src2的结果的余数存入dest
 */
public class REM extends RRInst {
    public REM(Register dest, Register src1, Register src2) {
        super(dest, src1, src2);
    }

    @Override
    public String toMIPS() {
        return "rem " + dest + ", " + src1 + ", " + src2;
    }
}
