package midend.symbols;

import midend.Symbol;

public class CharArraySymbol extends Symbol {
    public char[] value;

    public void assign(char[] value) {
        this.value = value;
    }

    public CharArraySymbol(String name) {
        super(name, SymbolType.CharArray);
    }
}
