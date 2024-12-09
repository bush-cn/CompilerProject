package backend.instructions.RIInst;

import backend.Register;

public class SGEI extends RIInst {
    public SGEI(Register dest, Register src, int immediate) {
        super(dest, src, immediate);
    }

    @Override
    public String toMIPS() {
        return "sge " + dest + ", " + src + ", " + immediate;
    }
}
