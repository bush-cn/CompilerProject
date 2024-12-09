package midend.llvm.instructions;

import midend.llvm.Instruction;
import midend.llvm.Slot;
import midend.llvm.Value;

public class BrCondInst extends Instruction {
    public Value cond;
    public Value ifTrueLabel;
    public Value ifFalseLabel;

    public BrCondInst(Value cond, Value ifTrueLabel, Value ifFalseLabel) {
        this.cond = cond;
        this.ifTrueLabel = ifTrueLabel;
        this.ifFalseLabel = ifFalseLabel;

        if (cond instanceof Slot slot) {
            use.add(slot);
        }
    }

    @Override
    public String toText() {
        if (comment == null) {
            return "br i1 " + cond.toText() + ", label "
                    + ifTrueLabel.toText() + ", label " + ifFalseLabel.toText();
        }
        return "br i1 " + cond.toText() + ", label "
                + ifTrueLabel.toText() + ", label " + ifFalseLabel.toText() + "\t\t;" + comment;
    }
}
