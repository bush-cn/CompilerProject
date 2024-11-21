package midend.symbols;

import midend.Symbol;
import midend.Visitor;
import midend.llvm.Slot;
import midend.llvm.Value;
import midend.llvm.instructions.LoadInst;
import midend.llvm.instructions.PhiInst;
import midend.llvm.instructions.StoreInst;
import midend.llvm.types.ArrayType;
import midend.llvm.types.PointerType;
import midend.llvm.types.Type;

import java.util.HashMap;

public class CharArraySymbol extends Symbol {
    public int length;
    public CharArraySymbol(String name, int length) {
        super(name, SymbolType.CharArray);
        this.length = length;
    }
    // 形参无长度
    public CharArraySymbol(String name) {
        super(name, SymbolType.CharArray);
    }
}
