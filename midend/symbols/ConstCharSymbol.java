package midend.symbols;

import frontend.parser.terminal.Ident;
import midend.Symbol;
import midend.Visitor;
import midend.llvm.Slot;
import midend.llvm.Value;
import midend.llvm.instructions.LoadInst;
import midend.llvm.instructions.StoreInst;
import midend.llvm.types.PointerType;
import midend.llvm.types.Type;

public class ConstCharSymbol extends Symbol {
    public char value;
    public ConstCharSymbol(String name, char value) {
        super(name, SymbolType.ConstChar);
        this.value = value;
    }
}
