package midend.llvm.instructions;

import midend.llvm.Instruction;
import midend.llvm.Slot;
import midend.llvm.Value;
import midend.llvm.types.PointerType;
import midend.llvm.types.Type;

public class StoreInst extends Instruction {
    public Type type;
    public Value initValue;
    public PointerType pointerType;
    public Value pointer;

    public StoreInst(Type type, Value initValue, PointerType pointerType, Value pointer) {
        this.type = type;
        this.initValue = initValue;
        this.pointerType = pointerType;
        this.pointer = pointer;

        if (pointer instanceof Slot slot) { // 可能是全局变量
            use.add(slot);
        }
        if (initValue instanceof Slot slot) {
            use.add(slot);
        }
    }

    @Override
    public String toText() {
        if (comment == null) {
            return "store " + type + " " + initValue.toText() +
                    ", " + pointerType + " " + pointer.toText();
        }
        return "store " + type + " " + initValue.toText() +
                ", " + pointerType + " " + pointer.toText() + "\t\t\t;" + comment;
    }
}
