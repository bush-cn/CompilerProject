package backend.instructions.RIInst;

import backend.Register;

/**
 * 伪指令，表示将src除以immediate的结果存入dest
 */
public class DIVI extends RIInst {
    public DIVI(Register dest, Register src, int immediate) {
        super(dest, src, immediate);
    }

    @Override
    public String toMIPS() {
        return "div " + dest + ", " + src + ", " + immediate;
    }
}
