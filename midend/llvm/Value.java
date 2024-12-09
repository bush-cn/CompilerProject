package midend.llvm;

public abstract class Value {
    public abstract String toText();

    @Override
    public String toString() {
        return toText();
    }
}
