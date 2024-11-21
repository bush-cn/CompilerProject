package midend.symbols;

import midend.Symbol;
import midend.Visitor;
import midend.llvm.Slot;
import midend.llvm.Value;
import midend.llvm.instructions.LoadInst;
import midend.llvm.instructions.StoreInst;
import midend.llvm.types.PointerType;
import midend.llvm.types.Type;

public class ConstIntArraySymbol extends Symbol {
    public int[] values;
    public ConstIntArraySymbol(String name, int[] values) {
        super(name, SymbolType.ConstIntArray);
        this.values = values;
    }
}
