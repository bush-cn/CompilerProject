package midend.llvm.instructions;

import midend.llvm.Instruction;
import midend.llvm.globalvalues.ConstString;
import midend.llvm.types.ArrayType;
import midend.llvm.types.PointerType;
import midend.llvm.types.Type;

public class PutStrInst extends Instruction {
    public ConstString constString;

    public PutStrInst(ConstString constString) {
        this.constString = constString;
    }

    @Override
    public String toText() {
        Type arrayType = new ArrayType(constString.length, Type.i8);
        if (comment == null) {
            return "call void @putstr(i8* getelementptr inbounds ("
                    + arrayType + ", " + new PointerType(arrayType)
                    + " @" + constString.name + ", i64 0, i64 0))";
        }
        return "call void @putstr(i8* getelementptr inbounds ("
                + arrayType + ", " + new PointerType(arrayType)
                + " @" + constString.name + ", i64 0, i64 0))" + "\t\t;" + comment;
    }
}
