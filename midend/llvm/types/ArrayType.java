package midend.llvm.types;

// 在只有一维数组的情况下，baseType和eleType等价
public class ArrayType extends Type {
    public int length;
    public Type eleType;
    public ArrayType(int length, Type eleType) {
        super(eleType.baseType);
        this.length = length;
        this.eleType = eleType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != getClass()) return false;

        ArrayType t = (ArrayType) o;
        return baseType.equals(t.baseType) && length == t.length;
    }

    @Override
    public String toString() {
        return "[" + length + " x " + eleType + "]";
    }
}
