package midend.llvm.instructions;

import midend.llvm.Instruction;
import midend.llvm.Value;
import midend.llvm.types.Type;

public class LoadInst extends Instruction {
    public Value result;
    public Type type;
    public Type pointerType;
    public Value pointer;

    public LoadInst(Value result, Type type, Type pointerType, Value pointer) {
        this.result = result;
        this.type = type;
        this.pointerType = pointerType;
        this.pointer = pointer;
    }

    @Override
    public String toText() {
        return result.toText() + " = load " + type
                + ", " + pointerType + " " + pointer.toText();
    }
}
