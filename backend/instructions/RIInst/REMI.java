package backend.instructions.RIInst;

import backend.Register;

/**
 * 伪指令，表示将src除以immediate的余数存入dest
 */
public class REMI extends RIInst {
    public REMI(Register dest, Register src, int immediate) {
        super(dest, src, immediate);
    }

    @Override
    public String toMIPS() {
        return "rem " + dest + ", " + src + ", " + immediate;
    }
}
