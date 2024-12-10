package backend.instructions.RRInst;

import backend.Register;

public class SUBU extends RRInst {
    public SUBU(Register dest, Register op1, Register op2) {
        super(dest, op1, op2);
    }

    @Override
    public String toMIPS() {
        return "subu " + dest + ", " + src1 + ", " + src2;
    }
}
