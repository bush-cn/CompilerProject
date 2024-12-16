package backend.instructions.RIInst;

import backend.Register;

/**
 * Shift left logical
 * <p>
 *     SLL dest, src, immediate # dest = src << immediate
 * </p>
 */
public class SLL extends RIInst {
    public SLL(Register dest, Register src, int immediate) {
        super(dest, src, immediate);
    }

    @Override
    public String toMIPS() {
        return "sll " + dest + ", " + src + ", " + immediate;
    }
}
