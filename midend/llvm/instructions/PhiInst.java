package midend.llvm.instructions;

import midend.llvm.Instruction;
import midend.llvm.Slot;
import midend.llvm.Value;
import midend.llvm.types.Type;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PhiInst extends Instruction {
    public Value result;
    public Type type;
    public HashMap<Value, Value> valueLabel;

    public PhiInst(Value result, Type type, HashMap<Value, Value> valueLabel) {
        this.result = result;
        this.type = type;
        this.valueLabel = valueLabel;

        // TODO: 将phi指令进行数据流分析
    }

    @Override
    public String toText() {
        StringBuilder sb = new StringBuilder();
        sb.append(result.toText())
                .append(" = phi ")
                .append(type);
        boolean first = true;
        for (Map.Entry<Value, Value> entry : valueLabel.entrySet()) {
            if (!first) {
                first = false;
                sb.append(",");
            }
            sb.append(" ")
                    .append(entry.getValue().toText())
                    .append(", ")
                    .append(entry.getKey().toText());
        }
        return sb.toString();
    }
}
