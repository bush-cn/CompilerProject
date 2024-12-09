package backend.instructions.RRInst;

import backend.Register;

public class SGE extends RRInst {
    public SGE(Register dest, Register src1, Register src2) {
        super(dest, src1, src2);
    }

    @Override
    public String toMIPS() {
        return "sge " + dest + ", " + src1 + ", " + src2;
    }
}
