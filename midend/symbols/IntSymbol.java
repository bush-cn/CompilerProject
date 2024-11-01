package midend.symbols;

import midend.Symbol;

public class IntSymbol extends Symbol {
    public int value;

    public void assign(int value) {
        this.value = value;
    }

    public IntSymbol(String name) {
        super(name, SymbolType.Int);
    }
}
