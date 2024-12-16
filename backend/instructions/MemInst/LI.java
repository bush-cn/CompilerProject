package backend.instructions.MemInst;

import backend.Register;
import backend.instructions.MIPSInst;

public class LI extends MIPSInst {
    public Register target;
    public long immediate;

    public LI(Register target, long immediate) {
        this.target = target;
        this.immediate = immediate;
    }

    @Override
    public String toMIPS() {
        return "li " + target + ", " + immediate;
    }
}
