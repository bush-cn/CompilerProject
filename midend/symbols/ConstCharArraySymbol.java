package midend.symbols;

import midend.Symbol;
import midend.llvm.Value;

public class ConstCharArraySymbol extends Symbol {
    public char[] values;
    public ConstCharArraySymbol(String name, char[] values) {
        super(name, SymbolType.ConstCharArray);
        this.values = values;
    }
}
