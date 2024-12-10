package midend.llvm.instructions;

import midend.llvm.Instruction;
import midend.llvm.Slot;
import midend.llvm.Value;
import midend.llvm.types.Type;

public class TruncInst extends Instruction {
    public Value result;
    public Type type;
    public Value value;
    public Type type2;

    public TruncInst(Value result, Type type, Value value, Type type2) {
        this.result = result;
        this.type = type;
        this.value = value;
        this.type2 = type2;

        def.add((Slot)result);
        if (value instanceof Slot slot) {
            use.add(slot);
        }
    }

    @Override
    public String toText() {
        return result.toText() + " = trunc "
                + type + " " + value.toText() + " to " + type2;
    }
}
