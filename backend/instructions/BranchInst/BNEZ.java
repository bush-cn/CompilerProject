package backend.instructions.BranchInst;

import backend.Label;
import backend.Register;

public class BNEZ extends BranchInst {
    public BNEZ(Register src1, Label label) {
        super(src1, label);
    }

    @Override
    public String toMIPS() {
        return "bnez " + src1 + ", " + label;
    }
}
