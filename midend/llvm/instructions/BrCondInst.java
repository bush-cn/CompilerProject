package midend.llvm.instructions;

import midend.llvm.Instruction;
import midend.llvm.Value;

public class BrCondInst extends Instruction {
    public Value cond;
    public Value ifTrueLabel;
    public Value ifFalseLabel;

    public BrCondInst(Value cond, Value ifTrueLabel, Value ifFalseLabel) {
        this.cond = cond;
        this.ifTrueLabel = ifTrueLabel;
        this.ifFalseLabel = ifFalseLabel;
    }

    @Override
    public String toText() {
        return "br i1 " + cond.toText() + ", label "
                + ifTrueLabel.toText() + ", label " + ifFalseLabel.toText();
    }
}
