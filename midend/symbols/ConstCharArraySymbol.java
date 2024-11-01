package midend.symbols;

import midend.Symbol;

public class ConstCharArraySymbol extends Symbol {
    public char[] value;

    public ConstCharArraySymbol(String name) {
        super(name, SymbolType.ConstCharArray);
    }
}
