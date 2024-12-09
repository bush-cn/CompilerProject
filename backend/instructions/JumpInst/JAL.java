package backend.instructions.JumpInst;

import backend.Label;
import backend.instructions.MIPSInst;

/**
 * jump and link: jump to the label and store the return address in $ra
 */
public class JAL extends MIPSInst {
    public Label label;

    public JAL(Label label) {
        this.label = label;
    }

    @Override
    public String toMIPS() {
        return "jal " + label;
    }
}
