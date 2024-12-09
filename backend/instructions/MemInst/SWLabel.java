package backend.instructions.MemInst;

import backend.Register;
import backend.instructions.MIPSInst;

public class SWLabel extends MIPSInst {
    public Register src;
    public String globalVar;
    public int offset;

    public SWLabel(Register src, String globalVar) {
        this.src = src;
        this.globalVar = globalVar;
        this.offset = 0;
    }
    public SWLabel(Register src, String globalVar, int offset) {
        this.src = src;
        this.globalVar = globalVar;
        this.offset = offset;
    }

    @Override
    public String toMIPS() {
        if (offset == 0) {
            return "sw " + src + ", " + globalVar;
        } else {
            return "sw " + src + ", " + globalVar + "+" + offset;
        }
    }
}
