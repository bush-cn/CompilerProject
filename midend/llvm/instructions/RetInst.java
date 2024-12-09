package midend.llvm.instructions;

import midend.llvm.Instruction;
import midend.llvm.Slot;
import midend.llvm.Value;
import midend.llvm.types.Type;

public class RetInst extends Instruction {
    public Type retType;
    public Value value; // 若返回void则不存在，为null

    public RetInst(Type retType, Value value) {
        this.retType = retType;
        this.value = value;

        if (value instanceof Slot slot) {
            use.add(slot);
        }
    }

    @Override
    public String toText() {
        if (retType == Type.Void) {
            return "ret void";
        } else {
            return "ret " + retType + " " + value.toText();
        }
    }
}
