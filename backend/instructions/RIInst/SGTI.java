package backend.instructions.RIInst;

import backend.Register;

public class SGTI extends RIInst {
    public SGTI(Register dest, Register src, int immediate) {
        super(dest, src, immediate);
    }

    @Override
    public String toMIPS() {
        return "sgt " + dest + ", " + src + ", " + immediate;
    }
}
