package midend.symbols;

import midend.Symbol;
import midend.Visitor;
import midend.llvm.Slot;
import midend.llvm.Value;
import midend.llvm.instructions.LoadInst;
import midend.llvm.instructions.PhiInst;
import midend.llvm.instructions.StoreInst;
import midend.llvm.types.PointerType;
import midend.llvm.types.Type;

import java.util.HashMap;

public class CharSymbol extends VarSymbol {
    public void storeValue(Value value) {
        if (!Visitor.getInstance().inBranch) {
            hasPhiValue = false;
            Visitor.getInstance().curBasicBlock().addInst(
                    new StoreInst(Type.i8, value, new PointerType(Type.i8), address)
            );
        } else {
            hasPhiValue = true;
            valueLabel.put(Visitor.getInstance().curBasicBlock().label, value);
        }
    }

    public Value loadValue() {
        Value result = new Slot(Visitor.getInstance().curFunction);
        if (!hasPhiValue) {
            Visitor.getInstance().curBasicBlock().addInst(
                    new LoadInst(result, Type.i8, new PointerType(Type.i8), address)
            );
        } else {
            // 只考虑两个前驱的合并phi指令
            Visitor.getInstance().curBasicBlock().addInst(
                    new PhiInst(result, Type.i8, valueLabel)
            );
            Visitor.getInstance().curBasicBlock().addInst(
                    new StoreInst(Type.i8, result, new PointerType(Type.i8), address)
            );
            // 也可以再load一次，这里省略
        }
        return result;
    }
    public CharSymbol(String name) {
        super(name, SymbolType.Char);
    }
}
