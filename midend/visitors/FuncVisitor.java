package midend.visitors;

import error.CompileError;
import frontend.lexer.Token;
import frontend.parser.function.FuncDef;
import frontend.parser.function.FuncFParam;
import frontend.parser.function.FuncFParams;
import frontend.parser.function.MainFuncDef;
import frontend.parser.statement.BlockItem;
import frontend.parser.statement.stmts.ReturnStmt;
import midend.Symbol;
import midend.SymbolTable;
import midend.Visitor;
import midend.llvm.BasicBlock;
import midend.llvm.Function;
import midend.llvm.Slot;
import midend.llvm.Value;
import midend.llvm.instructions.AllocaInst;
import midend.llvm.instructions.RetInst;
import midend.llvm.instructions.StoreInst;
import midend.llvm.types.PointerType;
import midend.llvm.types.Type;
import midend.symbols.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 函数传递的非指针参数不能修改，
 * 因此**在进入函数时**，需要为其alloca一个地址空间，并将传进的参数赋给它（相当于变量定义）。
 * **不能在第一次使用时再分配并赋值**，因为如果第一次使用在循环体内，则会导致死循环。
 */
public class FuncVisitor {
    static Visitor visitor = Visitor.getInstance();

    public static void visitFuncDef(FuncDef funcDef) {
        Token funcName = funcDef.getIdent().getIdent();
        if (visitor.curSymbolTab.lookupCurSymTab(funcName.getValue()) != null) {
            CompileError.raiseError(funcName.getLine(), CompileError.ErrorType.b);
        }

        List<BlockItem> blockItems = funcDef.getBlock().getBlockItems();
        if (funcDef.getFuncType().getFuncTypeToken().getTokenType() != Token.TokenType.VOIDTK) {
            if (blockItems.isEmpty()) {
                CompileError.raiseError(funcDef.getBlock().rBraceLine, CompileError.ErrorType.g);
            } else if (!(blockItems.get(blockItems.size() - 1) instanceof ReturnStmt)){
                CompileError.raiseError(funcDef.getBlock().rBraceLine, CompileError.ErrorType.g);
            }
        }

        List<Symbol.SymbolType> fParams = new ArrayList<>();
        Symbol.SymbolType funcType;
        // 代码生成新增
        Type retType;
        switch (funcDef.getFuncType().getFuncTypeToken().getTokenType()) {
            case INTTK -> {
                funcType = Symbol.SymbolType.IntFunc;
                retType = Type.i32;
            }
            case CHARTK -> {
                funcType =  Symbol.SymbolType.CharFunc;
                retType = Type.i8;
            }
            default -> {
                funcType =  Symbol.SymbolType.VoidFunc;
                retType = Type.Void;
            }
        }
        // 进入函数scope，形参slot需要在函数里
        visitor.curFunction = new Function(funcName.getValue(), retType);

        if (funcDef.getFuncFParams() != null) {
            for (FuncFParam funcFParam: funcDef.getFuncFParams().getFuncFParams()) {
                if (funcFParam.getBtype().getBtypeToken().getTokenType() == Token.TokenType.INTTK) {
                    if (funcFParam.isArray()) {
                        fParams.add(Symbol.SymbolType.IntArray);
                    } else {
                        fParams.add(Symbol.SymbolType.Int);
                    }
                } else {
                    if (funcFParam.isArray()) {
                        fParams.add(Symbol.SymbolType.CharArray);
                    } else {
                        fParams.add(Symbol.SymbolType.Char);
                    }
                }
            }
        }

        visitor.curSymbolTab.addSymbol(new FuncSymbol(funcName.getValue(), funcType, fParams));

        visitor.curSymbolTab = new SymbolTable(visitor.curSymbolTab);

        if (funcDef.getFuncFParams() != null) {
            visitor.curFunction.params = visitFuncFParams(funcDef.getFuncFParams());
        }


        visitor.inFuncType = funcType; // 进入函数block

        visitor.checkoutBlock(new BasicBlock(visitor.curFunction));
        // 为非指针型参数分配地址并赋值，指针型参数地址则直接等于Slot
        for (int i = 0; i < visitor.curFunction.params.size(); i++) {
            Function.Param param = visitor.curFunction.params.get(i);
            String name = funcDef.getFuncFParams().getFuncFParams().get(i).getIdent().getIdent().getValue();
            Symbol s = visitor.curSymbolTab.lookupSymbol(name);
            if (param.type.equals(Type.i32) || param.type.equals(Type.i8)) {
                Value addr = new Slot(visitor.curFunction);
                visitor.curBasicBlock().addInst(
                        new AllocaInst(addr, param.type).
                                setComment("alloc addr for fParam " + name)
                );
                s.address = addr;
                visitor.curBasicBlock().addInst(
                        new StoreInst(param.type, param.value, new PointerType(param.type), addr).
                                setComment("store fParam slot")
                );
            } else {
                s.address = param.value;
            }
        }
        // 解析函数体
        StmtVisitor.visitBlock(funcDef.getBlock());
        // 若void型函数无return语句，需要显式加上
        if (funcType == Symbol.SymbolType.VoidFunc)
            if (funcDef.getBlock().getBlockItems().isEmpty() ||
            !(funcDef.getBlock().getBlockItems().get(funcDef.getBlock().getBlockItems().size()-1) instanceof ReturnStmt))  {
            visitor.curBasicBlock().addInst(
                    new RetInst(Type.Void, null)
            );
        }
        visitor.curSymbolTab = visitor.curSymbolTab.fatherTable;

        visitor.inFuncType = null;
        visitor.module.addGlobalValue(visitor.curFunction);
        visitor.curFunction = null;
    }

    public static List<Function.Param> visitFuncFParams(FuncFParams funcFParams) {
        List<Function.Param> params = new ArrayList<>();
        for (FuncFParam funcFParam: funcFParams.getFuncFParams()) {
            params.add(visitFuncFParam(funcFParam));
        }
        return params;
    }

    public static Function.Param visitFuncFParam(FuncFParam funcFParam) {
        Token token = funcFParam.getIdent().getIdent();
        if (visitor.curSymbolTab.lookupCurSymTab(token.getValue()) != null) {
            CompileError.raiseError(token.getLine(), CompileError.ErrorType.b);
        }

        Value paramValue = new Slot(visitor.curFunction);
        Type type;
        Symbol newSymbol;
        if (funcFParam.getBtype().getBtypeToken().getTokenType() == Token.TokenType.INTTK) {
            if (funcFParam.isArray()) {
                newSymbol = new IntArraySymbol(token.getValue());
                type = new PointerType(Type.i32);
            } else {
                newSymbol = new IntSymbol(token.getValue());
                type = Type.i32;
            }
        } else {
            if (funcFParam.isArray()) {
                newSymbol = new CharArraySymbol(token.getValue());
                type = new PointerType(Type.i8);
            } else {
                newSymbol = new CharSymbol(token.getValue());
                type = Type.i8;
            }
        }
        visitor.curSymbolTab.addSymbol(newSymbol);
        return new Function.Param(type, paramValue);
    }

    public static void visitMainFuncDef(MainFuncDef mainFuncDef) {
        List<BlockItem> blockItems = mainFuncDef.getBlock().getBlockItems();
        if (blockItems.isEmpty()) {
            CompileError.raiseError(mainFuncDef.getBlock().rBraceLine, CompileError.ErrorType.g);
        }
        else if (!(blockItems.get(blockItems.size() - 1) instanceof ReturnStmt)) {
            CompileError.raiseError(mainFuncDef.getBlock().rBraceLine, CompileError.ErrorType.g);
        }

        visitor.curSymbolTab = new SymbolTable(visitor.curSymbolTab);
        visitor.inFuncType = Symbol.SymbolType.IntFunc;
        visitor.curFunction = new Function("main", Type.i32);
        visitor.checkoutBlock(new BasicBlock(visitor.curFunction));

        StmtVisitor.visitBlock(mainFuncDef.getBlock());

        visitor.curSymbolTab = visitor.curSymbolTab.fatherTable;
        visitor.inFuncType = null; // 其实也可以不要因为main函数是结尾
        visitor.module.addGlobalValue(visitor.curFunction);
        visitor.curFunction = null;
    }
}
