package backend.instructions.MemInst;

import backend.Register;
import backend.instructions.MIPSInst;

public class LI extends MIPSInst {
    public Register target;
    public int immediate;

    public LI(Register target, int immediate) {
        this.target = target;
        this.immediate = immediate;
    }

    @Override
    public String toMIPS() {
        return "li " + target + ", " + immediate;
    }
}
