package backend.instructions.RIInst;

import backend.Register;

/**
 * 伪指令 subi
 */
public class SUBI extends RIInst {
    public SUBI(Register dest, Register src, int immediate) {
        super(dest, src, immediate);
    }

    @Override
    public String toMIPS() {
        return "subi " + dest + ", " + src + ", " + immediate;
    }
}
