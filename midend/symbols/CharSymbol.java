package midend.symbols;

import midend.Symbol;

public class CharSymbol extends Symbol {
    public char value;

    public void assign(char value) {
        this.value = value;
    }

    public CharSymbol(String name) {
        super(name, SymbolType.Char);
    }
}
