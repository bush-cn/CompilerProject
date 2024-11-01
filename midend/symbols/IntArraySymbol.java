package midend.symbols;

import midend.Symbol;

public class IntArraySymbol extends Symbol {
    public int[] value;

    public void assign(int[] value) {
        this.value = value;
    }

    public IntArraySymbol(String name) {
        super(name, SymbolType.IntArray);
    }
}
