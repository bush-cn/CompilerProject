package backend.instructions.RRInst;

import backend.Register;

public class ADD extends RRInst {
    public ADD(Register dest, Register src1, Register src2) {
        super(dest, src1, src2);
    }

    @Override
    public String toMIPS() {
        return "add " + dest + ", " + src1 + ", " + src2;
    }
}
