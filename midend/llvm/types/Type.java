package midend.llvm.types;

/**
 * 原本设置为枚举类，但数组长度可变，于是设置为单例模式
 */
public class Type {
    public BaseType baseType;
    protected Type(BaseType baseType) {
        this.baseType = baseType;
    }

    public static Type i32 = new Type(BaseType.i32);
    public static Type i8 = new Type(BaseType.i8);
    public static Type i1 = new Type(BaseType.i1);

    public static Type Void = new Type(BaseType.Void);
    @Override
    public String toString() {
        return baseType.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != getClass()) return false;

        Type t = (Type) o;
        return baseType.equals(t.baseType);
    }
}
