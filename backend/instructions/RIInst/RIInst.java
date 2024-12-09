package backend.instructions.RIInst;

import backend.Register;
import backend.instructions.MIPSInst;

/**
 * RIInst抽象类，表示参数为一个寄存器和一个立即数的指令
 * 为了与RRInst类名区分，RIInst类名以I结尾，但是在生成MIPS代码时，指令名不一定包含i
 */
public abstract class RIInst extends MIPSInst {
    public Register dest;
    public Register src;
    public int immediate;

    public RIInst(Register dest, Register src, int immediate) {
        this.dest = dest;
        this.src = src;
        this.immediate = immediate;
    }
}
