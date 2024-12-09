package backend.instructions.MemInst;

import backend.Register;
import backend.instructions.MIPSInst;

/**
 * 用于加载全局变量地址
 */
public class LALabel extends MIPSInst {
    public Register dest;
    public String globalVar;

    public LALabel(Register dest, String globalVar) {
        this.dest = dest;
        this.globalVar = globalVar;
    }

    @Override
    public String toMIPS() {
        return "la " + dest + ", " + globalVar;
    }
}
