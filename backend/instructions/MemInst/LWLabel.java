package backend.instructions.MemInst;

import backend.Register;
import backend.instructions.MIPSInst;

/**
 * 用于加载全局变量值
 */
public class LWLabel extends MIPSInst {
    public Register dest;
    public String globalVar;
    public int offset;

    public LWLabel(Register dest, String globalVar) {
        this.dest = dest;
        this.globalVar = globalVar;
        this.offset = 0;
    }
    public LWLabel(Register dest, String globalVar, int offset) {
        this.dest = dest;
        this.globalVar = globalVar;
        this.offset = offset;
    }

    @Override
    public String toMIPS() {
        if (offset == 0) {
            return "lw " + dest + ", " + globalVar;
        } else {
            return "lw " + dest + ", " + globalVar + " + " + offset;
        }
    }
}
