package midend.symbols;

import midend.Symbol;

public class ConstIntArraySymbol extends Symbol {
    public int[] value;

    public ConstIntArraySymbol(String name) {
        super(name, SymbolType.ConstIntArray);
    }
}
