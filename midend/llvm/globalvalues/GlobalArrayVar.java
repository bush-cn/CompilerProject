package midend.llvm.globalvalues;

import midend.llvm.GlobalValue;
import midend.llvm.Value;
import midend.llvm.types.ArrayType;
import midend.llvm.types.Type;

public class GlobalArrayVar extends GlobalValue {
    public ArrayType type;
    public int[] initValues;   // 若为null，表示没有初始值，则用zeroinitializer初始为0
    public boolean isConst;
    public Value refValue; // "@" + name

    public GlobalArrayVar(String name, ArrayType type, int[] initValues, boolean isConst) {
        this.name = name;
        this.type = type;
        this.initValues = initValues;
        this.isConst = isConst;
        this.refValue = new Value() {
            @Override
            public String toText() {
                return "@" + name;
            }
        };
    }


    @Override
    public String globalText() {
        StringBuilder sb = new StringBuilder();
        sb.append("@")
                .append(name)
                .append(" = dso_local ")
                .append(isConst ? "constant ": "global ")
                .append(type);
        if (initValues == null || initValues.length == 0) {
            // 当有初始值但为空时，也使用zeroinitializer
            sb.append(" zeroinitializer");
        } else {
            // 暂时不考虑直接使用字符串为char数组赋值
            // 若后续代码优化可以提升，则在此处考虑替换
            sb.append(" [");
            int i = 0;
            for (; i < initValues.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(type.baseType)
                        .append(" ")
                        .append(initValues[i]);
            }
            for (; i < type.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(type.baseType)
                        .append(" 0");
            }
            sb.append("]");
        }
        return sb.toString();
    }

    @Override
    public String toText() {
        return "@" + name;
    }
}
