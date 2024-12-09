package backend.instructions.RRInst;

import backend.Register;

public class SLE extends RRInst {
    public SLE(Register dest, Register src1, Register src2) {
        super(dest, src1, src2);
    }

    @Override
    public String toMIPS() {
        return "sle " + dest + ", " + src1 + ", " + src2;
    }
}
