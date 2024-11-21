package midend.llvm.instructions;

import midend.llvm.Function;
import midend.llvm.Instruction;
import midend.llvm.Value;
import midend.llvm.types.Type;

import java.util.ArrayList;
import java.util.List;

public class CallInst extends Instruction {
    public Value result; // void函数则为null
    public Type retType;
    public String name;
    public List<Function.Param> params = new ArrayList<>();

    public CallInst(Value result, Type retType, String name, List<Function.Param> params) {
        this.result = result;
        this.retType = retType;
        this.name = name;
        this.params = params;
    }

    @Override
    public String toText() {
        StringBuilder sb = new StringBuilder();
        if (result != null) {
            sb.append(result.toText())
                    .append(" = ");
        }
        sb.append("call ")
                .append(retType)
                .append(" @")
                .append(name)
                .append("(");
        for (int i = 0; i < params.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(params.get(i).type)
                    .append(" ")
                    .append(params.get(i).value.toText());
        }
        sb.append(")");

        return sb.toString();
    }
}
