package backend.instructions.JumpInst;

import backend.Register;
import backend.instructions.MIPSInst;

/**
 * jump register unconditionally: jump to the address in the register
 * 一般用于函数返回：jr $ra
 */
public class JR extends MIPSInst {
    public Register reg;
    public JR(Register reg) {
        this.reg = reg;
    }

    @Override
    public String toMIPS() {
        return "jr " + reg;
    }
}
