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

import java.util.*;

public class IntSymbol extends VarSymbol {
    public void storeValue(Value value) {
        if (!Visitor.getInstance().inBranch) {
            hasPhiValue = false;
            Visitor.getInstance().curBasicBlock().addInst(
                    new StoreInst(Type.i32, value, new PointerType(Type.i32), address)
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
                    new LoadInst(result, Type.i32, new PointerType(Type.i32), address)
            );
        } else {
            // 只考虑两个前驱的合并phi指令
            Visitor.getInstance().curBasicBlock().addInst(
                    new PhiInst(result, Type.i32, valueLabel)
            );
            Visitor.getInstance().curBasicBlock().addInst(
                    new StoreInst(Type.i32, result, new PointerType(Type.i32), address)
            );
            // 也可以再load一次，这里省略
        }
        return result;
    }

    public IntSymbol(String name) {
        super(name, SymbolType.Int);
    }
}
