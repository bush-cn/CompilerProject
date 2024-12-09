package backend.instructions.MemInst;

import backend.Register;

/**
 * store one byte to memory
 */
public class SB extends MemInst {
    public SB(Register target, Register base, int offset) {
        super(target, base, offset);
    }

    @Override
    public String toMIPS() {
        return "sb " + target + ", " + offset + "(" + base + ")";
    }
}
