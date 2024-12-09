package backend.instructions.MFInst;

import backend.Register;
import backend.instructions.MIPSInst;

/**
 * 将LO寄存器的值存入目标寄存器dest
 */
public class MFLO extends MIPSInst {
    public Register dest;
    public MFLO(Register dest) {
        this.dest = dest;
    }

    @Override
    public String toMIPS() {
        return "mflo " + dest;
    }
}
