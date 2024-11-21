package midend.llvm.globalvalues;

import midend.llvm.GlobalValue;
import midend.llvm.Value;
import midend.llvm.types.Type;

/**
 *全局变量和常量
 */
public class GlobalVariable extends GlobalValue {
    public String name;
    public Type type;
    public int initValue;   // 若无则为0
    public boolean isConst;

    public GlobalVariable(String name, Type type, int initValue, boolean isConst) {
        this.name = name;
        this.type = type;
        this.initValue = initValue;
        this.isConst = isConst;
    }

    @Override
    public String toText() {
        return "@" + name;
    }

    public String globalText() {
        if (isConst) {
            return "@" + name + " = dso_local constant " + type + " " + initValue;
        } else {
            return "@" + name + " = dso_local global " + type + " " + initValue;
        }
    }
}
