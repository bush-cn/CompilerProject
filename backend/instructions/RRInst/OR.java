package backend.instructions.RRInst;

import backend.Register;

public class OR extends RRInst {
    public OR(Register dest, Register src1, Register src2) {
        super(dest, src1, src2);
    }

    @Override
    public String toMIPS() {
        return "or " + dest + ", " + src1 + ", " + src2;
    }
}
