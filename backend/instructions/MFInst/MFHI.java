package backend.instructions.MFInst;

import backend.Register;
import backend.instructions.MIPSInst;

/**
 * 将HI寄存器的值存入目标寄存器dest
 * 若使用基本指令"div src1, src2"，则使用MFHI获取HI寄存器的值（余数），使用MFLO获取LO寄存器的值（商）
 */
public class MFHI extends MIPSInst {
    public Register dest;
    public MFHI(Register dest) {
        this.dest = dest;
    }

    @Override
    public String toMIPS() {
        return "mfhi " + dest;
    }
}
