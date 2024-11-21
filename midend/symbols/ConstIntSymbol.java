package midend.symbols;

import midend.Symbol;
import midend.Visitor;
import midend.llvm.Slot;
import midend.llvm.Value;
import midend.llvm.instructions.LoadInst;
import midend.llvm.instructions.StoreInst;
import midend.llvm.types.PointerType;
import midend.llvm.types.Type;

public class ConstIntSymbol extends Symbol {
    public int value;
    public ConstIntSymbol(String name, int value) {
        super(name, SymbolType.ConstInt);
        this.value = value;
    }
}
