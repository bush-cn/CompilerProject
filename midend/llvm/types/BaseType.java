package midend.llvm.types;

public enum BaseType {
    i32("i32"),
    i8("i8"),
    i1("i1"),
    Void("void");

    private final String str;
    private BaseType(String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        return str;
    }
}
