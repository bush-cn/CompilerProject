package backend.instructions.RRInst;

import backend.Register;

/**
 * move伪指令，将src1的值移动到dest中，相当于addu $dest, $src1, $zero
 */
public class MOVE extends RRInst {
    public MOVE(Register dest, Register src) {
        super(dest, src, Register.ZERO);
    }

    @Override
    public String toMIPS() {
        // 即addu $dest, $src1, $zero
        return "move " + dest + ", " + src1;
    }
}
