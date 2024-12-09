package backend.instructions.RIInst;

import backend.Register;

public class ADDI extends RIInst {
    public ADDI(Register dest, Register src, int immediate) {
        super(dest, src, immediate);
    }

    @Override
    public String toMIPS() {
        return "addi " + dest + ", " + src + ", " + immediate;
    }
}
