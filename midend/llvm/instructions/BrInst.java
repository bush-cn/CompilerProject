package midend.llvm.instructions;

import midend.llvm.Instruction;
import midend.llvm.Value;

public class BrInst extends Instruction {
    public Value label;

    public BrInst(Value label) {
        this.label = label;
    }

    @Override
    public String toText() {
        if (comment == null) {
            return "br label " + label.toText();
        }
        return "br label " + label.toText() + "\t\t;" + comment;
    }
}
