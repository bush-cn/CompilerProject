package backend.instructions.MemInst;

import backend.Register;

/**
 * load one byte from memory
 */
public class LB extends MemInst {
    public LB(Register target, Register base, int offset) {
        super(target, base, offset);
    }

    @Override
    public String toMIPS() {
        return "lb " + target + ", " + offset + "(" + base + ")";
    }
}
