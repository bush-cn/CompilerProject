package backend.instructions.RRInst;

import backend.Register;

// slt dest, src1, src2 # dest = 1 if src1 < src2, else 0
public class SLT extends RRInst {
    public SLT(Register dest, Register src1, Register src2) {
        super(dest, src1, src2);
    }

    @Override
    public String toMIPS() {
        return "slt " + dest + ", " + src1 + ", " + src2;
    }
}
