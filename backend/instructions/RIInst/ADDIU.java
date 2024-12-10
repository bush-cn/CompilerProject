package backend.instructions.RIInst;

import backend.Register;

public class ADDIU extends RIInst {
    public ADDIU(Register dest, Register src, int immediate) {
        super(dest, src, immediate);
    }

    @Override
    public String toMIPS() {
        return "addiu " + dest + ", " + src + ", " + immediate;
    }
}
