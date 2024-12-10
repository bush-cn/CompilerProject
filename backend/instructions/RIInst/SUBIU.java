package backend.instructions.RIInst;

import backend.Register;

/**
 * 伪指令 subi
 */
public class SUBIU extends RIInst {
    public SUBIU(Register dest, Register src, int immediate) {
        super(dest, src, immediate);
    }

    @Override
    public String toMIPS() {
        return "subiu " + dest + ", " + src + ", " + immediate;
    }
}
