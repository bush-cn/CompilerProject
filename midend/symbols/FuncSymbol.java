package midend.symbols;

import midend.Symbol;

import java.util.List;

public class FuncSymbol extends Symbol {
    // 形参类型，有且仅有四种Int, Char, IntArray, CharArray
    public List<SymbolType> fParams;

    public FuncSymbol(String name, SymbolType type, List<SymbolType> fParams) {
        super(name, type);
        this.fParams = fParams;
    }
}
