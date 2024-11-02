package midend;

public abstract class Symbol {
    public SymbolTable tableIn;

    public String name;
    public SymbolType type;

    public Symbol(String name, SymbolType type) {
        this.name = name;
        this.type = type;
    }

    public enum SymbolType {
        ConstChar, // char型常量
        ConstInt, // int型常量
        ConstCharArray, // char型常量数组
        ConstIntArray, // int型常量数组
        Char, // char型变量
        Int, // int型变量
        CharArray, // char型变量数组
        IntArray, // int型变量数组
        VoidFunc, // void型函数
        CharFunc, // char型函数
        IntFunc, // int型函数
    }
}
