package midend.symbols;

import midend.Symbol;

public class ConstIntSymbol extends Symbol {
    public int value;

    public ConstIntSymbol(String name) {
        super(name, SymbolType.ConstInt);
    }
}
