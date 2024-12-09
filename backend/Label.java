package backend;


import midend.llvm.Function;
import midend.llvm.Slot;
import midend.llvm.Value;

public class Label implements MIPSElement {
    public String name;

    public Label(String funcName) {
        this.name = funcName;
    }

    public Label(Function function, Value label) {
        this.name = function.name + "_label_" + ((Slot)label).slotId;
    }

    @Override
    public String toMIPS() {
        return name + ":";
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Label) {
            return name.equals(((Label) obj).name);
        }
        return false;
    }
}
