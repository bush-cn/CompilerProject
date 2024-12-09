package backend.instructions.RIInst;

import backend.Register;

public class SEQI extends RIInst {
    public SEQI(Register dest, Register src, int immediate) {
        super(dest, src, immediate);
    }

    @Override
    public String toMIPS() {
        return "seq " + dest + ", " + src + ", " + immediate;
    }
}
