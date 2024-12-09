package backend.instructions.RRInst;

import backend.Register;

public class AND extends RRInst {
    public AND(Register dest, Register src1, Register src2) {
        super(dest, src1, src2);
    }

    @Override
    public String toMIPS() {
        return "and " + dest + ", " + src1 + ", " + src2;
    }
}
