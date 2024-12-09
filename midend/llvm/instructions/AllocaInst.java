package midend.llvm.instructions;

import midend.llvm.Instruction;
import midend.llvm.Slot;
import midend.llvm.Value;
import midend.llvm.types.Type;

public class AllocaInst extends Instruction {
    public Value result;
    public Type type;

    @Override
    public String toText() {
        if (comment == null) {
            return result.toText() + " = alloca " + type;
        }
        return result.toText() + " = alloca " + type + "\t\t\t\t;" + comment;
    }

    public AllocaInst(Value result, Type type) {
        this.result = result;
        this.type = type;

        def.add((Slot)result);
    }
}
