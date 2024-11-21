package midend.llvm.instructions;

import midend.llvm.Instruction;
import midend.llvm.Value;
import midend.llvm.types.Type;

public class AllocaInst extends Instruction {
    public Value result;
    public Type type;

    @Override
    public String toText() {
        return result.toText() + " = alloca " + type;
    }

    public AllocaInst(Value result, Type type) {
        this.result = result;
        this.type = type;
    }
}
