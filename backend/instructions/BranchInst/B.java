package backend.instructions.BranchInst;

import backend.Label;

public class B extends BranchInst {
    public B(Label label) {
        super(label);
    }

    @Override
    public String toMIPS() {
        return "b " + label;
    }
}
