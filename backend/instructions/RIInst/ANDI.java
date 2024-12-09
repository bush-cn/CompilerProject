package backend.instructions.RIInst;

import backend.Register;

public class ANDI extends RIInst {
    public ANDI(Register dest, Register src, int immediate) {
        super(dest, src, immediate);
    }

    @Override
    public String toMIPS() {
        return "andi " + dest + ", " + src + ", " + immediate;
    }
}
