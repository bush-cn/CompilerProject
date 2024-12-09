package midend.llvm.instructions;

import midend.llvm.Instruction;
import midend.llvm.Slot;
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

        def.add((Slot) result);
        // pointer也可能不是Slot，因为可能是全局变量，不占用寄存器
        if (pointer instanceof Slot) {
            use.add((Slot) pointer);
        }
    }

    @Override
    public String toText() {
        if (comment == null) {
            return result.toText() + " = load " + type
                    + ", " + pointerType + " " + pointer.toText();
        }
        return result.toText() + " = load " + type
                + ", " + pointerType + " " + pointer.toText() + "\t\t;" + comment;
    }
}
