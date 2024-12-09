package backend.instructions.RRInst;

import backend.Register;

public class SEQ extends RRInst {
    public SEQ(Register dest, Register src1, Register src2) {
        super(dest, src1, src2);
    }

    @Override
    public String toMIPS() {
        return "seq " + dest + ", " + src1 + ", " + src2;
    }
}
