package midend;

import java.util.ArrayList;
import java.util.List;

public class SymbolTable {
    private static int scopeIdCounter = 1;
    public static final SymbolTable ROOT = new SymbolTable(); // 根符号表，即全局符号表

    public SymbolTable fatherTable; // 上一层次的符号表。若为null则为根符号表。
    public List<SymbolTable> childrenTables = new ArrayList<>(); // 下一层次的符号表

    public int scopeId;

    public List<Symbol> symbols = new ArrayList<>();

    public void addSymbol(Symbol symbol) {
        symbols.add(symbol);
        symbol.tableIn = this;
    }

    /**
     * 递归检查符号在该符号表中或外层符号表是否存在
     * 用于使用该符号时判断是否产生使用未定义的符号（c错误）
     *
     * @param name 要检查的符号名
     * @return 若已有这个符号，则返回此符号；否则返回null
     */
    public Symbol lookupSymbol(String name) {
        for (Symbol symbol: symbols) {
            if (symbol.name.equals(name)) {
                return symbol;
            }
        }
        if (fatherTable != null) {
            return fatherTable.lookupSymbol(name);
        } else {
            return null;
        }
    }

    /**
     * 检查符号在当前符号表中是否存在
     * 用于声明该符号时判断是否产生重定义（b错误）
     * @param name 要检查的符号名
     * @return 若已有这个符号，则返回此符号；否则返回null
     */
    public Symbol lookupCurSymTab(String name) {
        for (Symbol symbol: symbols) {
            if (symbol.name.equals(name)) {
                return symbol;
            }
        }
        return null;
    }

    public SymbolTable(SymbolTable fatherTable) {
        this.fatherTable = fatherTable;
        this.scopeId = scopeIdCounter++;
        fatherTable.childrenTables.add(this);
    }
    private SymbolTable() {
        this.fatherTable = null;
        this.scopeId = scopeIdCounter++;
    }
}
