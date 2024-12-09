package midend.llvm;

public abstract class GlobalValue extends Value {
    public String name;
    public abstract String globalText();
}
