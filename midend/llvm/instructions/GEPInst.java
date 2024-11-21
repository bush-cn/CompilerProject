package midend.llvm.instructions;

import midend.llvm.Instruction;
import midend.llvm.Value;
import midend.llvm.types.ArrayType;
import midend.llvm.types.PointerType;
import midend.llvm.types.Type;

// getelemnetptr
// `<result> = getelementptr <ty>, ptr <ptrval>{, <ty> <idx>}*`
// 只使用这一形式： %3 = getelementptr i32, i32* @a, i32 3 （获取a[3]地址）
public class GEPInst extends Instruction {
    Value result;
    ArrayType arrayType;
    Value arrayValue;
    Value index; // 只有一维数组

    public GEPInst(Value result, ArrayType arrayType, Value arrayValue, Value index) {
        this.result = result;
        this.arrayType = arrayType;
        this.arrayValue = arrayValue;
        this.index = index;
    }

    @Override
    public String toText() {
        return result.toText() + " = getelementptr " + arrayType +
                ", " + new PointerType(arrayType) + " " + arrayValue.toText() +
                ", i32 0, i32 " + index.toText();
    }
}
