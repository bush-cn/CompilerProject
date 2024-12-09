package backend.instructions.RRInst;

import backend.Register;

public class SUB extends RRInst {
    public SUB(Register dest, Register op1, Register op2) {
        super(dest, op1, op2);
    }

    @Override
    public String toMIPS() {
        return "sub " + dest + ", " + src1 + ", " + src2;
    }
}
