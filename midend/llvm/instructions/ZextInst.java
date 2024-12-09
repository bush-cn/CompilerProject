package midend.llvm.instructions;

import midend.llvm.Instruction;
import midend.llvm.Slot;
import midend.llvm.Value;
import midend.llvm.types.Type;

public class ZextInst extends Instruction {
    public Value result;
    public Type type;
    public Value value;
    public Type type2;

    public ZextInst(Value result, Type type, Value value, Type type2) {
        this.result = result;
        this.type = type;
        this.value = value;
        this.type2 = type2;

        def.add((Slot) result);
        use.add((Slot) value);
    }

    @Override
    public String toText() {
        return result.toText() + " = zext "
                + type + " " + value.toText() + " to " + type2;
    }
}
