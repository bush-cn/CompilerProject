package midend.visitors;

import error.CompileError;
import frontend.lexer.Token;
import frontend.parser.declaration.Decl;
import frontend.parser.expression.Exp;
import frontend.parser.statement.*;
import frontend.parser.statement.stmts.*;
import midend.Symbol;
import midend.SymbolTable;
import midend.Visitor;
import midend.llvm.*;
import midend.llvm.globalvalues.ConstString;
import midend.llvm.instructions.*;
import midend.llvm.types.PointerType;
import midend.llvm.types.Type;

import java.util.ArrayList;
import java.util.List;

public class StmtVisitor {
    static Visitor visitor = Visitor.getInstance();

    public static void visitBlock(Block block) {
        for (BlockItem blockItem : block.getBlockItems()) {
            visitBlockItem(blockItem);
        }
    }

    public static void visitBlockItem(BlockItem blockItem) {
        if (blockItem instanceof Decl decl) {
            DeclVisitor.visitDecl(decl);
        } else {
            visitStmt((Stmt) blockItem);
        }
    }

    /**
     * - 一个if语句将当前基本块分为4个基本块（有else）或3个基本块（无else）
     * - 一个for语句将当前基本块分为5块
     * | B1   | preForStmt（若无preForStmt，则无B1）                   |
     * | ---- | ------------------------------------------------------ |
     * | B2   | Cond，条件跳转指令B3、B5（若无Cond，则是无条件跳转B3） |
     * | B3   | Stmt，无条件跳转B4                                     |
     * | B4   | postStmt，无条件跳转B2 （**continue语句跳点**）        |
     * | B5   | （end）（**break语句跳点**）                           |
     *
     *   在visitCond里一次将跳转基本块和分裂的基本块加入函数，手动加入时同时加入label的slot
     */
    public static void visitStmt(Stmt s) {
        if (s instanceof IfStmt ifStmt) {
            // 一个if语句将当前基本块分为4个基本块（有else）或3个基本块（无else）
            // 先创建条件跳转基本块，但先不加入函数
            BasicBlock trueJump = new BasicBlock();
            BasicBlock falseJump = new BasicBlock();
            BasicBlock end;
            if (ifStmt.getElseStmt() != null) {
                end = new BasicBlock();
            } else {
                end = falseJump;
            }
            // 在visitCond里一次将跳转基本块和分裂的基本块加入函数，手动加入时同时加入label的slot
            visitCond(ifStmt.getCond(), trueJump, falseJump);

            // 跳转到之前创建的基本块
            boolean mark = visitor.inBranch;
            visitor.inBranch = true;
            visitor.curFunction.addBasicBlock(trueJump);
            visitor.checkoutBlock(trueJump);
            visitStmt(ifStmt.getStmt());
            visitor.curBasicBlock().addInst(
                    new BrInst(end.label)
            ); // if.then跳转到end，不可使用trueJump添加指令，因为可能因为ret指令已经分裂
            visitor.curBasicBlock().linkTo(end);
            visitor.inBranch = mark;

            if (ifStmt.getElseStmt() != null) {
                boolean mark1 = visitor.inBranch;
                visitor.inBranch = true;
                visitor.curFunction.addBasicBlock(falseJump);
                visitor.checkoutBlock(falseJump);
                visitStmt(ifStmt.getElseStmt());
                visitor.curBasicBlock().addInst(
                        new BrInst(end.label)
                );// 同理，不可使用trueJump添加指令，因为可能因为ret指令已经分裂
                visitor.curBasicBlock().linkTo(end);
                visitor.inBranch = mark1;
            }

            visitor.curFunction.addBasicBlock(end);
            visitor.checkoutBlock(end);
        }
        else if (s instanceof FStmt fStmt) { // for循环
            if (fStmt.getPreForStmt() != null) {
                visitForStmt(fStmt.getPreForStmt()); // preForStmt不可放在condBlock
            }
            // 必须分裂一个condBlock，trueJump会跳转到此
            BasicBlock condBlock = new BasicBlock();
            // 不可省略跳转到cond的指令，因为一个基本块必须以ret或br结尾
            visitor.curBasicBlock().addInst(
                    new BrInst(condBlock.label)
            );
            visitor.curBasicBlock().linkTo(condBlock);
            visitor.curFunction.addBasicBlock(condBlock);
            visitor.checkoutBlock(condBlock);

            BasicBlock loopBlock = new BasicBlock(); // Loop
            BasicBlock postStmtBlock = new BasicBlock();    // continue跳点
            BasicBlock endBlock = new BasicBlock(); // end
            boolean mark = visitor.inBranch;
            visitor.inBranch = true;
            if (fStmt.getCond() != null) {
                visitCond(fStmt.getCond(), loopBlock, endBlock);
            } else {
                visitor.curBasicBlock().addInst(
                        new BrInst(loopBlock.label)
                );
                visitor.curBasicBlock().linkTo(loopBlock);
            }
            visitor.inBranch = mark;

            visitor.curFunction.addBasicBlock(loopBlock);
            visitor.checkoutBlock(loopBlock);
            boolean curInLoop = visitor.inLoop;
            visitor.inLoop = true;
            visitor.breakHop = endBlock;
            visitor.continueHop = postStmtBlock;  // continue跳点是postStmt而不是cond
            visitStmt(fStmt.getStmt()); // 循环体stmt
            visitor.inLoop = curInLoop;
            visitor.curBasicBlock().addInst(
                    new BrInst(postStmtBlock.label)
            );
            visitor.curBasicBlock().linkTo(postStmtBlock);

            visitor.curFunction.addBasicBlock(postStmtBlock);
            visitor.checkoutBlock(postStmtBlock);
            if (fStmt.getPostForStmt() != null) {
                visitForStmt(fStmt.getPostForStmt());
            }
            visitor.curBasicBlock().addInst(
                    new BrInst(condBlock.label)
            );
            visitor.curBasicBlock().linkTo(condBlock);

            visitor.curFunction.addBasicBlock(endBlock);
            visitor.checkoutBlock(endBlock);
        }
        else if (s instanceof ReturnStmt returnStmt) {
            if (visitor.inFuncType == Symbol.SymbolType.VoidFunc || visitor.inFuncType == null) {
                if (returnStmt.getExp() != null) {
                    CompileError.raiseError(returnStmt.getReturnLine(), CompileError.ErrorType.f);
                }
            }

            if (visitor.inFuncType == Symbol.SymbolType.VoidFunc) {
                visitor.curBasicBlock().addInst(
                        new RetInst(Type.Void, null)
                );
            } else {
                if (returnStmt.getExp() != null) {
                    // 不考虑returnStmt.getExp()为null的情况（？）
                    Value value =  ExpVisitor.visitExp(returnStmt.getExp());
                    if (visitor.inFuncType == Symbol.SymbolType.IntFunc) {
                        visitor.curBasicBlock().addInst(
                                new RetInst(Type.i32, value)
                        );
                    } else {
                        if (value instanceof Slot) {
                            Value truc = new Slot(visitor.curFunction);
                            visitor.curBasicBlock().addInst(
                                    new TruncInst(truc, Type.i32, value, Type.i8)
                            );
                            value = truc;
                        }
                        visitor.curBasicBlock().addInst(
                                new RetInst(Type.i8, value)
                        );
                    }
                }
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

            string = string.substring(1, string.length() - 1);
            List<Exp> exps = printfStmt.getExps();
            int eIndex = 0;
            for(int i = 0; i < string.length(); i++) {
                if (string.charAt(i) == '%') {
                    Value v = ExpVisitor.visitExp(exps.get(eIndex++));
                    if (string.charAt(i+1) == 'c') {
                        visitor.curBasicBlock().addInst(
                                new CallInst(null,
                                        Type.Void,
                                        "putch",
                                        List.of(new Function.Param(Type.i32, v)))
                        );
                    } else {
                        visitor.curBasicBlock().addInst(
                                new CallInst(null,
                                        Type.Void,
                                        "putint",
                                        List.of(new Function.Param(Type.i32, v)))
                        );
                    }
                    i++; // 跳过'd'和'c'
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (int j = i; j < string.length() && string.charAt(j) != '%'; j++) {
                        sb.append(string.charAt(j));
                    }
                    ConstString constString = new ConstString(sb.toString());
                    visitor.module.addGlobalValue(constString);
                    visitor.curBasicBlock().addInst(
                            new PutStrInst(constString)
                    );
                    i+=(sb.length()-1);
                }
            }
        }
        else if (s instanceof BreakContinueStmt breakContinueStmt) {
            if (!visitor.inLoop) {
                CompileError.raiseError(breakContinueStmt.getBreakOrContinueLine(), CompileError.ErrorType.m);
            }

            if (breakContinueStmt.getBreakOrContinue().getTokenType() == Token.TokenType.BREAKTK) {
                visitor.curBasicBlock().addInst(
                        new BrInst(visitor.breakHop.label)
                );
                visitor.curBasicBlock().linkTo(visitor.breakHop);
            } else {
                visitor.curBasicBlock().addInst(
                        new BrInst(visitor.continueHop.label)
                );
                visitor.curBasicBlock().linkTo(visitor.continueHop);
            }
        }
        else if (s instanceof BlockStmt blockStmt) {
            visitor.curSymbolTab = new SymbolTable(visitor.curSymbolTab);
            visitBlock(blockStmt.getBlock());
            visitor.curSymbolTab = visitor.curSymbolTab.fatherTable;
        }
        else if (s instanceof ExpStmt expStmt) {
            if (expStmt.getExp() != null) {
                // TODO: 代码优化中可去掉此调用？
                Value value = ExpVisitor.visitExp(expStmt.getExp());
            }
        }
        else {
            // LVal赋值语句
            LValStmt lValStmt = (LValStmt) s;
            Symbol symbol = visitor.curSymbolTab.lookupSymbol(lValStmt.getlVal().getIdent().getIdent().getValue());
            if (symbol != null) {
                Symbol.SymbolType type = symbol.type;
                if (type == Symbol.SymbolType.ConstInt
                        || type == Symbol.SymbolType.ConstChar
                        || type == Symbol.SymbolType.ConstIntArray
                        || type == Symbol.SymbolType.ConstCharArray) {
                    CompileError.raiseError(lValStmt.getLValLine(), CompileError.ErrorType.h);
                }
            }

            // 计算右值【先计算！！再赋值】
            Value right;
            if (lValStmt.isAssignment()) {
                right = ExpVisitor.visitExp(lValStmt.getAssgnExp());
            } else if (lValStmt.getGetIntOrGetChar().getTokenType() == Token.TokenType.GETINTTK) {
                right = new Slot(visitor.curFunction);
                visitor.curBasicBlock().addInst(
                        new CallInst(right, Type.i32, "getint", new ArrayList<>())
                );
            } else {
                right = new Slot(visitor.curFunction);
                visitor.curBasicBlock().addInst(
                        new CallInst(right, Type.i32, "getchar", new ArrayList<>())
                );
            }
            // 若所赋值类型为i8则truc
            Type type = switch (visitor.curSymbolTab.lookupSymbol(lValStmt.getlVal().getIdent().getIdent().getValue()).type) {
                case Int, IntArray, ConstInt, ConstIntArray -> Type.i32;
                default -> Type.i8;
            };
            if (type.equals(Type.i8) && right instanceof Slot) {
                Value truc = new Slot(visitor.curFunction);
                visitor.curBasicBlock().addInst(
                        new TruncInst(truc, Type.i32, right, Type.i8)
                );
                right = truc;
            }
            Value addr = ExpVisitor.visitLValPointer(lValStmt.getlVal());
            visitor.curBasicBlock().addInst(
                    new StoreInst(type, right, new PointerType(type), addr)
            );
        }
    }

    // condBlock不需要传入，只需要在调用前切换到该块，condBlock自然就确定了
    public static void visitCond(Cond cond,
                                 BasicBlock trueJump,
                                 BasicBlock falseJump) {
        ExpVisitor.visitLOrExp(cond.getlOrExp(),
                trueJump,
                falseJump);
    }

    public static void visitForStmt(ForStmt forStmt) {
        Value address = ExpVisitor.visitLValPointer(forStmt.getlVal());
        if (visitor.curSymbolTab.lookupSymbol(forStmt.getlVal().getIdent().getIdent().getValue()) != null) {
            Symbol.SymbolType type = visitor.curSymbolTab.lookupSymbol(forStmt.getlVal().getIdent().getIdent().getValue()).type;
            if (type == Symbol.SymbolType.ConstInt
                    || type == Symbol.SymbolType.ConstChar
                    || type == Symbol.SymbolType.ConstIntArray
                    || type == Symbol.SymbolType.ConstCharArray) {
                CompileError.raiseError(forStmt.getLValLine(), CompileError.ErrorType.h);
            }
        }
        Type type = switch(visitor.curSymbolTab.lookupSymbol(forStmt.getlVal().getIdent().getIdent().getValue()).type) {
            case Int, IntArray, ConstInt, ConstIntArray ->  Type.i32;
            default -> Type.i8;
        };
        Value value = ExpVisitor.visitExp(forStmt.getExp());
        // 若为i8则truc
        if (type.equals(Type.i8)) {
            Value truc = new Slot(visitor.curFunction);
            visitor.curBasicBlock().addInst(
                    new TruncInst(truc, Type.i32, value, Type.i8)
            );
            value = truc;
        }
        visitor.curBasicBlock().addInst(
                new StoreInst(type, value, new PointerType(type), address)
        );
    }
}
