package midend.symbols;

import midend.Symbol;
import midend.llvm.Value;

import java.util.List;

/**
 * 共包含三种函数，其 SymbolType 属性分别是 VoidFunc, CharFunc, IntFunc
 */
public class FuncSymbol extends Symbol {
    // 形参类型，有且仅有四种Int, Char, IntArray, CharArray
    public List<SymbolType> fParams;

    public FuncSymbol(String name, SymbolType type, List<SymbolType> fParams) {
        super(name, type);
        this.fParams = fParams;
    }
}
