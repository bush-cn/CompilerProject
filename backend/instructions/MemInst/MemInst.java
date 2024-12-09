package backend.instructions.MemInst;

import backend.Register;
import backend.instructions.MIPSInst;

/**
 * Memory instructions
 */
public abstract class MemInst extends MIPSInst {
    public Register target;
    public int offset;
    public Register base;

    public MemInst(Register target, Register base, int offset) {
        this.target = target;
        this.base = base;
        this.offset = offset;
    }
}
