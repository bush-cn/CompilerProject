package backend.instructions.RIInst;

import backend.Register;

/**
 * 伪指令，表示将src乘以immediate的结果的低32位存入dest
 */
public class MULI extends RIInst{
    public MULI(Register dest, Register src, int immediate) {
        super(dest, src, immediate);
    }

    @Override
    public String toMIPS() {
        return "mul " + dest + ", " + src + ", " + immediate;
    }
}
