package backend.instructions.RIInst;

import backend.Register;

public class SLL extends RIInst {
    public SLL(Register dest, Register src, int immediate) {
        super(dest, src, immediate);
    }

    @Override
    public String toMIPS() {
        return "sll " + dest + ", " + src + ", " + immediate;
    }
}
