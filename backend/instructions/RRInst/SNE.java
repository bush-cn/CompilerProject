package backend.instructions.RRInst;

import backend.Register;

public class SNE extends RRInst {
    public SNE(Register dest, Register src1, Register src2) {
        super(dest, src1, src2);
    }

    @Override
    public String toMIPS() {
        return "sne " + dest + ", " + src1 + ", " + src2;
    }
}
