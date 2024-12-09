package backend.instructions.MemInst;

import backend.Register;

/**
 * lw target, offset(base) # Load word from memory [base + offset] into target
 * a word is 4 bytes
 */
public class LW extends MemInst {
    public LW(Register target, Register base, int offset) {
        super(target, base, offset);
    }

    @Override
    public String toMIPS() {
        return "lw " + target + ", " + offset + "(" + base + ")";
    }
}
