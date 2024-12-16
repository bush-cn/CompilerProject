package backend.instructions.RIInst;

import backend.Register;

/**
 * 算数右移指令
 */
public class SRA extends RIInst {
    public SRA(Register dest, Register src, int immediate) {
        super(dest, src, immediate);
    }

    @Override
    public String toMIPS() {
        return "sra " + dest + ", " + src + ", " + immediate;
    }
}
