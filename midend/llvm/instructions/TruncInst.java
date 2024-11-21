package midend.llvm.instructions;

import midend.llvm.Instruction;
import midend.llvm.Value;
import midend.llvm.types.Type;

public class TruncInst extends Instruction {
    Value result;
    Type type;
    Value value;
    Type type2;

    public TruncInst(Value result, Type type, Value value, Type type2) {
        this.result = result;
        this.type = type;
        this.value = value;
        this.type2 = type2;
    }

    @Override
    public String toText() {
        return result.toText() + " = trunc "
                + type + " " + value.toText() + " to " + type2;
    }
}
