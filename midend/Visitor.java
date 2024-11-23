package midend;

import error.CompileError;
import frontend.lexer.Token;
import frontend.parser.CompUnit;
import frontend.parser.declaration.*;
import frontend.parser.expression.*;
import frontend.parser.expression.unaryexps.FunctionCall;
import frontend.parser.expression.unaryexps.PrimaryExp;
import frontend.parser.expression.unaryexps.primaryexps.LVal;
import frontend.parser.expression.unaryexps.primaryexps.ParenthesisExp;
import frontend.parser.function.FuncDef;
import frontend.parser.function.FuncFParam;
import frontend.parser.function.FuncFParams;
import frontend.parser.function.MainFuncDef;
import frontend.parser.statement.*;
import frontend.parser.statement.stmts.*;
import midend.llvm.BasicBlock;
import midend.llvm.Function;
import midend.llvm.Module;
import midend.llvm.Value;
import midend.symbols.*;
import midend.visitors.DeclVisitor;
import midend.visitors.ExpVisitor;
import midend.visitors.FuncVisitor;
import midend.visitors.StmtVisitor;

import java.util.ArrayList;
import java.util.List;

public class Visitor {
    private Visitor() {}

    private static final Visitor visitor = new Visitor();

    public static Visitor getInstance() {
        return visitor;
    }

    /**
     * 将综合属性、继承属性均放置在这里，可以避免在visit方法传参
     */
    public SymbolTable curSymbolTab = SymbolTable.ROOT;

    public boolean inLoop = false;
    public Value continueLabel, breakLabel;

    // 当前所在的函数类型IntFunc/CharFunc/VoidFunc/null
    // 用来判断return语句是否造成f错误
    // null表示在最顶层的scope中，不在任何函数；在main函数中也是IntFunc
    public Symbol.SymbolType inFuncType = null;

    public Function curFunction = null;
    private BasicBlock curBasicBlock = null;
    public BasicBlock curBasicBlock() {
        return curBasicBlock;
    }

    public void checkoutBlock(BasicBlock basicBlock) {
        curBasicBlock = basicBlock;
        curFunction.addSlot(basicBlock.label);
    }

    public boolean inBranch = false;  // 当有为true时，需记录重新赋值的变量
    // 当上一条指令为br、ret时，下一次实例化Slot（或添加到slots里）或添加指令时需要新建一个基本块
    public boolean shouldCreateNewBlock = false;

    public Module module = new Module(); // llvm模块

    public Module visitCompUnit(CompUnit compUnit) {
        for (Decl decl: compUnit.getDecls()) {
            DeclVisitor.visitDecl(decl);
        }
        for (FuncDef funcDef: compUnit.getFuncDefs()) {
            FuncVisitor.visitFuncDef(funcDef);
        }
        FuncVisitor.visitMainFuncDef(compUnit.getMainFuncDef());
        return module;
    }
}
