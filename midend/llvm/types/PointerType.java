package midend.llvm.types;

public class PointerType extends Type{
    Type eleType;
    public PointerType(Type eleType) {
        super(eleType.baseType);
        this.eleType = eleType;
    }

    @Override
    public String toString() {
        return eleType + "*";
    }
}
