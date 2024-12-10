package backend.instructions.MemInst;

import backend.Register;
import backend.instructions.MIPSInst;

/**
 * 用于加载全局变量地址
 */
public class LALabel extends MIPSInst {
    public Register dest;
    public String globalVar;
    public int offset;

    public LALabel(Register dest, String globalVar) {
        this.dest = dest;
        this.globalVar = globalVar;
        this.offset = 0;
    }

    public LALabel(Register dest, String globalVar, int offset) {
        this.dest = dest;
        this.globalVar = globalVar;
        this.offset = offset;
    }

    @Override
    public String toMIPS() {
        if (offset == 0) {
            return "la " + dest + ", " + globalVar;
        } else {
            return "la " + dest + ", " + globalVar + " + " + offset;
        }
    }
}
