package backend.instructions.RRInst;

import backend.Register;

/**
 * add 指令会检测溢出，如果溢出则会抛出异常，而 addu 指令不会检测溢出，直接进行运算
 */
public class ADDU extends RRInst {
    public ADDU(Register dest, Register src1, Register src2) {
        super(dest, src1, src2);
    }

    @Override
    public String toMIPS() {
        return "addu " + dest + ", " + src1 + ", " + src2;
    }
}
