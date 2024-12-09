package backend.instructions.RIInst;

import backend.Register;

public class SNEI extends RIInst {
    public SNEI(Register dest, Register src, int immediate) {
        super(dest, src, immediate);
    }

    @Override
    public String toMIPS() {
        return "sne " + dest + ", " + src + ", " + immediate;
    }
}
