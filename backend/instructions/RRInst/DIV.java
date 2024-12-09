package backend.instructions.RRInst;

import backend.Register;

/**
 * 伪指令，将src1整数除以src2的结果的商存入dest
 * 【注意】基本指令"div src1, src2"的功能是将src1整数除以src2的结果的商存入LO，余数存入HI
 */
public class DIV extends RRInst {
    public DIV(Register dest, Register src1, Register src2) {
        super(dest, src1, src2);
    }

    @Override
    public String toMIPS() {
        return "div " + dest + ", " + src1 + ", " + src2;
    }
}
