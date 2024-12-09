package backend.instructions.RIInst;

import backend.Register;

/**
 * 【注意】比较指令除slt、slti均为伪指令，由编译器转换为slt、slti指令
 * TODO：伪指令实际上是几条指令，可能可以优化
 */
public class SLTI extends RIInst {
    public SLTI(Register dest, Register src, int immediate) {
        super(dest, src, immediate);
    }

    @Override
    public String toMIPS() {
        return "slti " + dest + ", " + src + ", " + immediate;
    }
}
