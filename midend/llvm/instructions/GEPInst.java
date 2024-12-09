package midend.llvm.instructions;

import midend.llvm.Instruction;
import midend.llvm.Slot;
import midend.llvm.Value;
import midend.llvm.types.ArrayType;
import midend.llvm.types.BaseType;
import midend.llvm.types.PointerType;
import midend.llvm.types.Type;

// getelemnetptr
// `<result> = getelementptr <ty>, ptr <ptrval>{, <ty> <idx>}*`
// 只使用这一形式： %3 = getelementptr i32, i32* @a, i32 3 （获取a[3]地址）
public class GEPInst extends Instruction {
    public Value result;
    public Type type;
    public Value arrayValue;
    public Value index; // 只有一维数组

    public GEPInst(Value result, ArrayType arrayType, Value arrayValue, Value index) {
        this.result = result;
        this.type = arrayType;
        this.arrayValue = arrayValue;
        this.index = index;

        def.add((Slot) result);
        if (arrayValue instanceof Slot slot) {  // 有可能是全局变量
            use.add(slot);
        }
        if (index instanceof Slot slot) {
            use.add(slot);
        }
    }

    // 对于不知道长度的数组参数，将其当作指针，即这里的Type只会是i32或i8类型
    public GEPInst(Value result, Type type, Value arrayValue, Value index) {
        this.result = result;
        this.type = type;
        this.arrayValue = arrayValue;
        this.index = index;

        def.add((Slot) result);
        use.add((Slot) arrayValue);
        if (index instanceof Slot slot) {
            use.add(slot);
        }
    }

    @Override
    public String toText() {
        String text;
        if (type instanceof ArrayType arrayType) {
            text = result.toText() + " = getelementptr " + arrayType +
                    ", " + new PointerType(arrayType) + " " + arrayValue.toText() +
                    ", i32 0, i32 " + index.toText();
        } else {
            text = result.toText() + " = getelementptr " + type +
                    ", " + new PointerType(type) + " " + arrayValue.toText() +
                    ", i32 " + index.toText();
        }
        if (comment == null) {
            return text;
        }
        return text + "\t\t;" + comment;
    }
}
