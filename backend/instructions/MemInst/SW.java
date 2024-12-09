package backend.instructions.MemInst;

import backend.Register;

/**
 * sw target, offset(base) # Store the word value of target into memory [base + offset]
 */
public class SW extends MemInst {
    public SW(Register target, Register base, int offset) {
        super(target, base, offset);
    }

    @Override
    public String toMIPS() {
        return "sw " + target + ", " + offset + "(" + base + ")";
    }
}
