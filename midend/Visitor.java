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
import midend.symbols.*;

import java.util.ArrayList;
import java.util.List;

public class Visitor {
    private Visitor() {}

    private static final Visitor visitor = new Visitor();

    public static Visitor getInstance() {
        return visitor;
    }

    private SymbolTable curSymbolTab = SymbolTable.ROOT;

    private boolean inLoop = false;

    // 当前所在的函数类型IntFunc/CharFunc/VoidFunc/null
    // 用来判断return语句是否造成f错误
    // null表示在最顶层的scope中，不在任何函数；在main函数中也是IntFunc
    private Symbol.SymbolType inFuncType = null;

    public void visitCompUnit(CompUnit compUnit) {
        for (Decl decl: compUnit.getDecls()) {
            visitDecl(decl);
        }
        for (FuncDef funcDef: compUnit.getFuncDefs()) {
            visitFuncDef(funcDef);
        }
        visitMainFuncDef(compUnit.getMainFuncDef());
    }

    private void visitDecl(Decl decl) {
        if (decl.getDecl() instanceof ConstDecl) {
            visitConstDecl((ConstDecl) decl.getDecl());
        } else {
            visitVarDecl((VarDecl) decl.getDecl());
        }
    }

    private void visitConstDecl(ConstDecl constDecl) {
        for (ConstDef constDef: constDecl.getConstDefs()) {
            visitConstDef(constDef, constDecl.getBtype());
        }
    }

    private void visitConstDef(ConstDef constDef, Btype btype) {
        // 检查是否重命名
        Token token = constDef.getIdent().getIdent();
        if (curSymbolTab.lookupCurSymTab(token.getValue()) != null) {
            CompileError.raiseError(token.getLine(), CompileError.ErrorType.b);
            return ;
        }

        visitConstInitVal(constDef.getConstInitVal());

        if (constDef.getConstExp() == null) {
            // 常量
            if (btype.getBtypeToken().getTokenType() == Token.TokenType.INTTK) {
                curSymbolTab.addSymbol(new ConstIntSymbol(token.getValue()));
            } else {
                curSymbolTab.addSymbol(new ConstCharSymbol(token.getValue()));
            }
        } else {
            // 常量数组
            visitConstExp(constDef.getConstExp());
            if (btype.getBtypeToken().getTokenType() == Token.TokenType.INTTK) {
                curSymbolTab.addSymbol(new ConstIntArraySymbol(token.getValue()));
            } else {
                curSymbolTab.addSymbol(new ConstCharArraySymbol(token.getValue()));
            }
        }
    }

    private void visitConstInitVal(ConstInitVal constInitVal) {
        if (constInitVal.getStringConst() == null) {
            for (ConstExp constExp: constInitVal.getConstExps()) {
                visitConstExp(constExp);
            }
        }
    }

    private void visitConstExp(ConstExp constExp) {
        visitAddExp(constExp.getAddExp());
    }

    private void visitExp(Exp exp) {
        visitAddExp(exp.getAddExp());
    }

    private void visitAddExp(AddExp addExp) {
//        int result = calMulExp(addExp.getMulExp());
//        for (AddExp.OpMulExp opMulExp: addExp.getOpMulExps()) {
//            int m = calMulExp(opMulExp.mulExp);
//            switch (opMulExp.op.getTokenType()) {
//                case PLUS -> result += m;
//                case MINU -> result -= m;
//            }
//        }
//        return result;
        visitMulExp(addExp.getMulExp());
        for (AddExp.OpMulExp opMulExp: addExp.getOpMulExps()) {
            visitMulExp(opMulExp.mulExp);
        }
    }

    private void visitMulExp(MulExp mulExp) {
//        int result = calUnaryExp(mulExp.getUnaryExpWithoutOp());
//        for (MulExp.OpUnaryExp opUnaryExp: mulExp.getOpUnaryExps()) {
//            int u = calUnaryExp(opUnaryExp.unaryExp);
//            switch (opUnaryExp.op.getTokenType()) {
//                case MULT -> result *= u;
//                case DIV -> result /= u;
//                case MOD -> result %= u;
//            }
//        }
//        return result;
        visitUnaryExp(mulExp.getUnaryExp());
        for (MulExp.OpUnaryExp opUnaryExp: mulExp.getOpUnaryExps()) {
            visitUnaryExp(opUnaryExp.unaryExp);
        }
    }

    // 当unaryExp是functionCall时，无法直接在语义分析中计算其值！
    private void visitUnaryExp(UnaryExp unaryExp) {
        if (unaryExp.getUnaryExpWithoutOp() instanceof FunctionCall) {
            FunctionCall functionCall = (FunctionCall) unaryExp.getUnaryExpWithoutOp();
            // 不会出现调用函数的标识符类型不匹配这种错误（“将变量当作函数调用”）
            // 检查使用未定义符号（错误c）
            Token funcName = functionCall.getIdent().getIdent();
            Symbol symbol = curSymbolTab.lookupSymbol(funcName.getValue());
            if (symbol == null) {
                CompileError.raiseError(funcName.getLine(), CompileError.ErrorType.c);
                return;
            }
            // 接着检查函数参数个数不匹配（错误d）
            assert symbol instanceof FuncSymbol;
            FuncSymbol funcSymbol = (FuncSymbol) symbol;
            if (functionCall.getFuncRParams() == null) {
                if (!funcSymbol.fParams.isEmpty()) {
                    CompileError.raiseError(funcName.getLine(), CompileError.ErrorType.d);
                    return;
                }
            }
            else {
                if (funcSymbol.fParams.size() != functionCall.getFuncRParams().getExps().size()) {
                    CompileError.raiseError(funcName.getLine(), CompileError.ErrorType.d);
                    return;
                }
            }
            // 最后检查参数类型不匹配（错误e）
            boolean raiseErrorE = false;  // 多个参数均不匹配时，只输出一条错误
            for (int i = 0; i < funcSymbol.fParams.size(); i++) {
                Symbol.SymbolType fParamType = funcSymbol.fParams.get(i); // 有且仅有四种
                Exp exp = functionCall.getFuncRParams().getExps().get(i);
                visitExp(exp);
                // 【如果传值为数组类型，则应由FuncRParam一路推导至Ident，不存在任何其他符号】
                if (exp.getAddExp().getMulExp().getUnaryExp().getUnaryExpWithoutOp() instanceof PrimaryExp primaryExp) {
                    if (primaryExp instanceof LVal lVal) {
                        Token t = lVal.getIdent().getIdent();
                        Symbol s = curSymbolTab.lookupSymbol(t.getValue());
                        // 常量数组不作为参数传入到函数中
                        // 那么有且仅有两种情况下传入的exp为数组，且符号一定已经填入
                        if (lVal.getExp() == null && s != null && s.type == Symbol.SymbolType.IntArray) {
                            // 传入的参数为int型数组
                            if (fParamType != Symbol.SymbolType.IntArray) {
                                raiseErrorE = true;
                            }
                        } else if (lVal.getExp() == null && s != null && s.type == Symbol.SymbolType.CharArray) {
                            // 传入的参数为char型数组
                            if (fParamType != Symbol.SymbolType.CharArray) {
                                raiseErrorE = true;
                            }
                        } else {
                            // 【剩余情况均为：传入的参数exp是变量而不是数组】
                            if (fParamType == Symbol.SymbolType.CharArray
                                    || fParamType == Symbol.SymbolType.IntArray) {
                                raiseErrorE = true;
                            }
                        }
                    } else {
                        // 【剩余情况均为：传入的参数exp是变量而不是数组】
                        if (fParamType == Symbol.SymbolType.CharArray
                                || fParamType == Symbol.SymbolType.IntArray) {
                            raiseErrorE = true;
                        }
                    }
                } // 【剩余情况均为：传入的参数exp是变量而不是数组】
                else {
                    if (fParamType == Symbol.SymbolType.CharArray
                            || fParamType == Symbol.SymbolType.IntArray) {
                        raiseErrorE = true;
                    }
                }
            }
            if (raiseErrorE) {
                CompileError.raiseError(funcName.getLine(), CompileError.ErrorType.e);
            }
        } else {
            // unaryExp instanceof PrimaryExp
            PrimaryExp primaryExp = (PrimaryExp) unaryExp.getUnaryExpWithoutOp();
            if (primaryExp instanceof ParenthesisExp parenthesisExp) {
                visitExp((parenthesisExp.getExp()));
            } else if (primaryExp instanceof LVal lVal) {
                visitLVal(lVal);
            }
        }
    }

    private void visitLVal(LVal lVal) {
        Token token = lVal.getIdent().getIdent();
        if (curSymbolTab.lookupSymbol(token.getValue()) == null) {
            CompileError.raiseError(token.getLine(), CompileError.ErrorType.c);
        }
        if (lVal.getExp() != null) {
            visitExp(lVal.getExp());
        }
    }

    private void visitVarDecl(VarDecl varDecl) {
        for (VarDef varDef: varDecl.getVarDefs()) {
            visitVarDef(varDef, varDecl.getBtype());
        }
    }

    private void visitVarDef(VarDef varDef, Btype btype) {
        // 检查是否重命名
        Token token = varDef.getIdent().getIdent();
        if (curSymbolTab.lookupCurSymTab(token.getValue()) != null) {
            CompileError.raiseError(token.getLine(), CompileError.ErrorType.b);
            return ;
        }

        if (varDef.getInitVal() != null) {
            visitInitVal(varDef.getInitVal());
        }


        if (varDef.getConstExp() == null) {
            // 变量
            if (btype.getBtypeToken().getTokenType() == Token.TokenType.INTTK) {
                curSymbolTab.addSymbol(new IntSymbol(token.getValue()));
            } else {
                curSymbolTab.addSymbol(new CharSymbol(token.getValue()));
            }
        } else {
            // 变量数组
            visitConstExp(varDef.getConstExp());
            if (btype.getBtypeToken().getTokenType() == Token.TokenType.INTTK) {
                curSymbolTab.addSymbol(new IntArraySymbol(token.getValue()));
            } else {
                curSymbolTab.addSymbol(new CharArraySymbol(token.getValue()));
            }
        }
    }

    private void visitInitVal(InitVal initVal) {
        if (initVal.getStringConst() == null) {
            for (Exp exp: initVal.getExps()) {
                visitExp(exp);
            }
        }
    }

    private void visitFuncDef(FuncDef funcDef) {
        Token funcName = funcDef.getIdent().getIdent();
        if (curSymbolTab.lookupCurSymTab(funcName.getValue()) != null) {
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

        Symbol.SymbolType funcType = switch (funcDef.getFuncType().getFuncTypeToken().getTokenType()) {
            case INTTK -> Symbol.SymbolType.IntFunc;
            case CHARTK -> Symbol.SymbolType.CharFunc;
            default -> Symbol.SymbolType.VoidFunc;
        };
        curSymbolTab.addSymbol(new FuncSymbol(funcName.getValue(), funcType, fParams));

        curSymbolTab = new SymbolTable(curSymbolTab);

        if (funcDef.getFuncFParams() != null) {
            visitFuncFParams(funcDef.getFuncFParams());
        }

        inFuncType = funcType; // 进入函数scope

        visitBlock(funcDef.getBlock());
        curSymbolTab = curSymbolTab.fatherTable;

        inFuncType = null;
    }

    private void visitFuncFParams(FuncFParams funcFParams) {
        for (FuncFParam funcFParam: funcFParams.getFuncFParams()) {
            visitFuncFParam(funcFParam);
        }
    }

    private void visitFuncFParam(FuncFParam funcFParam) {
        Token token = funcFParam.getIdent().getIdent();
        if (curSymbolTab.lookupCurSymTab(token.getValue()) != null) {
            CompileError.raiseError(token.getLine(), CompileError.ErrorType.b);
            return ;
        }

        if (funcFParam.getBtype().getBtypeToken().getTokenType() == Token.TokenType.INTTK) {
            if (funcFParam.isArray()) {
                curSymbolTab.addSymbol(new IntArraySymbol(token.getValue()));
            } else {
                curSymbolTab.addSymbol(new IntSymbol(token.getValue()));
            }
        } else {
            if (funcFParam.isArray()) {
                curSymbolTab.addSymbol(new CharArraySymbol(token.getValue()));
            } else {
                curSymbolTab.addSymbol(new CharSymbol(token.getValue()));
            }
        }
    }

    private void visitBlock(Block block) {
        for (BlockItem blockItem: block.getBlockItems()) {
            visitBlockItem(blockItem);
        }
    }

    private void visitBlockItem(BlockItem blockItem) {
        if (blockItem instanceof Decl decl) {
            visitDecl(decl);
        } else {
            visitStmt((Stmt) blockItem);
        }
    }

    private void visitStmt(Stmt s) {
        if (s instanceof IfStmt ifStmt) {
            visitCond(ifStmt.getCond());
            visitStmt(ifStmt.getStmt());
            if (ifStmt.getElseStmt() != null) {
                visitStmt(ifStmt.getElseStmt());
            }
        }
        else if (s instanceof FStmt fStmt) {
            if (fStmt.getPreForStmt() != null) {
                visitForStmt(fStmt.getPreForStmt());
            }
            if (fStmt.getCond() != null) {
                visitCond(fStmt.getCond());
            }
            if (fStmt.getPostForStmt() != null) {
                visitForStmt(fStmt.getPostForStmt());
            }
            boolean curInLoop = inLoop;
            inLoop = true;
            visitStmt(fStmt.getStmt());
            inLoop = curInLoop;
        }
        else if (s instanceof ReturnStmt returnStmt) {
            if (inFuncType == Symbol.SymbolType.VoidFunc || inFuncType == null) {
                if (returnStmt.getExp() != null) {
                    CompileError.raiseError(returnStmt.getReturnLine(), CompileError.ErrorType.f);
                }
            }

            if (returnStmt.getExp() != null) {
                visitExp(returnStmt.getExp());
            }
        }
        else if (s instanceof PrintfStmt printfStmt) {
            int paramNum = 0;
            String string = printfStmt.getStringConst().getStringConst().getValue();
            for(int i = 0; i < string.length() - 1; i++) {
                if (string.charAt(i) == '%' && (string.charAt(i+1) == 'd' || string.charAt(i+1) == 'c')) {
                    paramNum ++;
                }
            }
            if (paramNum != printfStmt.getExps().size()) {
                CompileError.raiseError(printfStmt.getPrintfLine(), CompileError.ErrorType.l);
            }
            for (Exp e: printfStmt.getExps()) {
                visitExp(e);
            }
        }
        else if (s instanceof BreakContinueStmt breakContinueStmt) {
            if (!inLoop) {
                CompileError.raiseError(breakContinueStmt.getBreakOrContinueLine(), CompileError.ErrorType.m);
            }
        }
        else if (s instanceof BlockStmt blockStmt) {
            curSymbolTab = new SymbolTable(curSymbolTab);
            visitBlock(blockStmt.getBlock());
            curSymbolTab = curSymbolTab.fatherTable;
        }
        else if (s instanceof ExpStmt expStmt) {
            if (expStmt.getExp() != null) {
                visitExp(expStmt.getExp());
            }
        }
        else {
            LValStmt lValStmt = (LValStmt) s;
            visitLVal(lValStmt.getlVal());
            if (curSymbolTab.lookupSymbol(lValStmt.getlVal().getIdent().getIdent().getValue()) != null) {
                Symbol.SymbolType type = curSymbolTab.lookupSymbol(lValStmt.getlVal().getIdent().getIdent().getValue()).type;
                if (type == Symbol.SymbolType.ConstInt
                        || type == Symbol.SymbolType.ConstChar
                        || type == Symbol.SymbolType.ConstIntArray
                        || type == Symbol.SymbolType.ConstCharArray) {
                    CompileError.raiseError(lValStmt.getLValLine(), CompileError.ErrorType.h);
                }
            }


            if (lValStmt.isAssignment()) {
                visitExp(lValStmt.getAssgnExp());
            }
        }
    }

    private void visitCond(Cond cond) {
        visitLOrExp(cond.getlOrExp());
    }
    private void visitLOrExp(LOrExp lOrExp) {
        visitLAndExp(lOrExp.getlAndExp());
        for (LOrExp.OpLAndExp opLAndExp: lOrExp.getOpLAndExps()) {
            visitLAndExp(opLAndExp.lAndExp);
        }
    }
    private void visitLAndExp(LAndExp lAndExp) {
        visitEqExp(lAndExp.getEqExp());
        for (LAndExp.OpEqExp opEqExp: lAndExp.getOpEqExps()) {
            visitEqExp(opEqExp.eqExp);
        }
    }
    private void visitEqExp(EqExp eqExp) {
        visitRelExp(eqExp.getRelExp());
        for (EqExp.OpRelExp opRelExp: eqExp.getOpRelExps()) {
            visitRelExp(opRelExp.relExp);
        }
    }
    private void visitRelExp(RelExp relExp) {
        visitAddExp(relExp.getAddExp());
        for (RelExp.OpAddExp opAddExp: relExp.getOpAddExps()) {
            visitAddExp(opAddExp.addExp);
        }
    }

    private void visitForStmt(ForStmt forStmt) {
        visitLVal(forStmt.getlVal());
        if (curSymbolTab.lookupSymbol(forStmt.getlVal().getIdent().getIdent().getValue()) != null) {
            Symbol.SymbolType type = curSymbolTab.lookupSymbol(forStmt.getlVal().getIdent().getIdent().getValue()).type;
            if (type == Symbol.SymbolType.ConstInt
                    || type == Symbol.SymbolType.ConstChar
                    || type == Symbol.SymbolType.ConstIntArray
                    || type == Symbol.SymbolType.ConstCharArray) {
                CompileError.raiseError(forStmt.getLValLine(), CompileError.ErrorType.h);
            }
        }
        visitExp(forStmt.getExp());
    }

    private void visitMainFuncDef(MainFuncDef mainFuncDef) {
        List<BlockItem> blockItems = mainFuncDef.getBlock().getBlockItems();
        if (blockItems.isEmpty()) {
            CompileError.raiseError(mainFuncDef.getBlock().rBraceLine, CompileError.ErrorType.g);
        }
        else if (!(blockItems.get(blockItems.size() - 1) instanceof ReturnStmt)) {
            CompileError.raiseError(mainFuncDef.getBlock().rBraceLine, CompileError.ErrorType.g);
        }

        curSymbolTab = new SymbolTable(curSymbolTab);
        inFuncType = Symbol.SymbolType.IntFunc;
        visitBlock(mainFuncDef.getBlock());
        curSymbolTab = curSymbolTab.fatherTable;
        inFuncType = null; // 其实也可以不要因为main函数是结尾
    }
}
