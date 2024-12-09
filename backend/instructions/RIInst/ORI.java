package backend.instructions.RIInst;

import backend.Register;

public class ORI extends RIInst {
    public ORI(Register dest, Register src, int immediate) {
        super(dest, src, immediate);
    }

    @Override
    public String toMIPS() {
        return "ori " + dest + ", " + src + ", " + immediate;
    }
}
