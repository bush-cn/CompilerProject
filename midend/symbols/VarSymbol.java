package midend.symbols;

import midend.Symbol;
import midend.llvm.Value;

import java.util.HashMap;

// 只包括IntSymbol和CharSymbol
// 用于在LVal赋值时，不能直接store，需要检查phi值
// 同时在读取时，也不能直接load，需要检查phi值
public abstract class VarSymbol extends Symbol {
    // TODO: 不每次都使用load获取值，并处理phi值
    // label和Value键值对
    protected final HashMap<Value, Value> valueLabel = new HashMap<>();
    protected boolean hasPhiValue = false;

    public abstract void storeValue(Value value);

    public abstract Value loadValue();
    public VarSymbol(String name, SymbolType type) {
        super(name, type);
    }
}
