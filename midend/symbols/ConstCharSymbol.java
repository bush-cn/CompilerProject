package midend.symbols;

import frontend.parser.terminal.Ident;
import midend.Symbol;

public class ConstCharSymbol extends Symbol {
    public char value;

    public ConstCharSymbol(String name) {
        super(name, SymbolType.ConstChar);
    }
}
