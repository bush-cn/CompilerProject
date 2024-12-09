package backend.instructions.RIInst;

import backend.Register;

public class SLEI extends RIInst {
    public SLEI(Register dest, Register src, int immediate) {
        super(dest, src, immediate);
    }

    @Override
    public String toMIPS() {
        return "sle " + dest + ", " + src + ", " + immediate;
    }
}
