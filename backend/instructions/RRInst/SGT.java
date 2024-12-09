package backend.instructions.RRInst;

import backend.Register;

public class SGT extends RRInst {
    public SGT(Register dest, Register src1, Register src2) {
        super(dest, src1, src2);
    }

    @Override
    public String toMIPS() {
        return "sgt " + dest + ", " + src1 + ", " + src2;
    }
}
