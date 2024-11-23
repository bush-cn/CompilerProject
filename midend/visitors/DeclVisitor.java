package midend.visitors;

import error.CompileError;
import frontend.lexer.Token;
import frontend.parser.declaration.*;
import frontend.parser.expression.ConstExp;
import frontend.parser.expression.Exp;
import midend.Symbol;
import midend.Visitor;
import midend.llvm.GlobalValue;
import midend.llvm.Slot;
import midend.llvm.Value;
import midend.llvm.globalvalues.GlobalArrayVar;
import midend.llvm.globalvalues.GlobalVariable;
import midend.llvm.instructions.AllocaInst;
import midend.llvm.instructions.GEPInst;
import midend.llvm.Immediate;
import midend.llvm.instructions.StoreInst;
import midend.llvm.instructions.TruncInst;
import midend.llvm.types.ArrayType;
import midend.llvm.types.PointerType;
import midend.llvm.types.Type;
import midend.symbols.*;

import java.util.List;

public class DeclVisitor {
    static Visitor visitor = Visitor.getInstance();

    public static void visitDecl(Decl decl) {
        if (decl.getDecl() instanceof ConstDecl) {
            visitConstDecl((ConstDecl) decl.getDecl());
        } else {
            visitVarDecl((VarDecl) decl.getDecl());
        }
    }

    public static void visitConstDecl(ConstDecl constDecl) {
        for (ConstDef constDef: constDecl.getConstDefs()) {
            visitConstDef(constDef, constDecl.getBtype());
        }
    }

    public static void visitConstDef(ConstDef constDef, Btype btype) {
        // 检查是否重命名
        Token token = constDef.getIdent().getIdent();
        if (visitor.curSymbolTab.lookupCurSymTab(token.getValue()) != null) {
            CompileError.raiseError(token.getLine(), CompileError.ErrorType.b);
            return ;
        }

        // visitConstInitVal(constDef.getConstInitVal());

        if (constDef.getConstExp() == null) {
            // 单个常量
            assert !constDef.getConstInitVal().isArray();
            int initValue = 0;
            if (!constDef.getConstInitVal().getConstExps().isEmpty()) {
                initValue = ExpVisitor.visitConstExp(constDef.getConstInitVal().getConstExps().get(0).getAddExp());
            }

            Type type;
            Symbol newSymbol;
            if (btype.getBtypeToken().getTokenType() == Token.TokenType.INTTK) {
                newSymbol = new ConstIntSymbol(token.getValue(), initValue);
                type = Type.i32;
            } else {
                newSymbol = new ConstCharSymbol(token.getValue(), (char) initValue);
                type = Type.i8;
            }


            Value newValue;
            if (visitor.curFunction == null) {
                // 全局作用域
                newValue = new GlobalVariable(token.getValue(), type, initValue, true);
                visitor.module.addGlobalValue((GlobalValue) newValue);
            } else {
                // 局部作用域
                newValue = new Slot(visitor.curFunction);
                visitor.curBasicBlock().addInst(
                        new AllocaInst(newValue, type)
                );
                visitor.curBasicBlock().addInst(
                        new StoreInst(type, new Immediate(initValue), new PointerType(type), newValue)
                );
            }
            newSymbol.address = newValue; // 符号表存地址
            visitor.curSymbolTab.addSymbol(newSymbol);
        } else {
            // 常量数组
            int length = ExpVisitor.visitConstExp(constDef.getConstExp().getAddExp());

            int[] initValues = new int[length];
            if (constDef.getConstInitVal().getStringConst() != null) {
                // 字符串赋值
                String str = constDef.getConstInitVal().getStringConst().getStringConst().getValue();
                int i = 0;
                for (; i < length; i++) {
                    if (i + 1 < str.length() - 1) {
                        initValues[i] = str.charAt(i + 1);
                    } else {
                        initValues[i] = 0;
                    }
                }
            } else {
                List<ConstExp> constExpList = constDef.getConstInitVal().getConstExps();
                int i = 0;
                for (; i < length; i++) {
                    if (i < constExpList.size()) {
                        initValues[i] = ExpVisitor.visitConstExp(constExpList.get(i).getAddExp());
                    } else {
                        initValues[i] = 0;
                    }
                }
            }

            ArrayType type;
            Symbol newSymbol;
            if (btype.getBtypeToken().getTokenType() == Token.TokenType.INTTK) {
                newSymbol = new ConstIntArraySymbol(token.getValue(), initValues);
                type = new ArrayType(length, Type.i32);
            } else {
                char[] charInitValues = new char[initValues.length];
                for (int i = 0; i < initValues.length; i++) {
                    charInitValues[i] = (char)initValues[i];
                }
                newSymbol = new ConstCharArraySymbol(token.getValue(), charInitValues);
                type = new ArrayType(length, Type.i8);
            }

            // 局部常量也用alloca指令声明
            Value newValue;
            if (visitor.curFunction == null) {
                // 全局作用域
                newValue = new GlobalArrayVar(token.getValue(), type, initValues, true);
                visitor.module.addGlobalValue((GlobalValue) newValue);
            } else {
                // 局部作用域
                newValue = new Slot(visitor.curFunction);
                visitor.curBasicBlock().addInst(
                        new AllocaInst(newValue, type)
                );
                // 依次生成赋值指令
                for (int i = 0; i < length; i++) {
                    Slot s = new Slot(visitor.curFunction);
                    visitor.curBasicBlock().addInst(
                            new GEPInst(s, type, newValue, new Immediate(i))
                    );
                    visitor.curBasicBlock().addInst(
                            new StoreInst(type.eleType, new Immediate(initValues[i]), new PointerType(type.eleType), s)
                    );
                }
            }
            newSymbol.address = newValue;
            visitor.curSymbolTab.addSymbol(newSymbol);
        }
    }

    public static void visitVarDecl(VarDecl varDecl) {
        for (VarDef varDef: varDecl.getVarDefs()) {
            visitVarDef(varDef, varDecl.getBtype());
        }
    }

    public static void visitVarDef(VarDef varDef, Btype btype) {
        // 检查是否重命名
        Token token = varDef.getIdent().getIdent();
        if (visitor.curSymbolTab.lookupCurSymTab(token.getValue()) != null) {
            CompileError.raiseError(token.getLine(), CompileError.ErrorType.b);
            return ;
        }

        /*
        变量与常量不同，初始值可能无法计算，因此不可以先计算初始值
        这里直接分为全局和局部大类，全局变量初始值一定可计算
         */
        if (varDef.getConstExp() == null) {
            // 单个变量
            Type type;
            Symbol newSymbol;
            if (btype.getBtypeToken().getTokenType() == Token.TokenType.INTTK) {
                newSymbol = new IntSymbol(token.getValue());
                type = Type.i32;
            } else {
                newSymbol = new CharSymbol(token.getValue());
                type = Type.i8;
            }

            if (visitor.curFunction == null) {
                // 全局作用域
                int initValue = 0;
                if (varDef.getInitVal() != null) {
                    initValue = ExpVisitor.visitConstExp(varDef.getInitVal().getExps().get(0).getAddExp());
                }
                GlobalValue newValue = new GlobalVariable(token.getValue(), type, initValue, false);
                visitor.module.addGlobalValue(newValue);
                newSymbol.address = newValue;
                visitor.curSymbolTab.addSymbol(newSymbol);
            } else {
                // 局部作用域
                Value newValue = new Slot(visitor.curFunction);
                visitor.curBasicBlock().addInst(
                        new AllocaInst(newValue, type)
                );
                if (varDef.getInitVal() != null) {
                    Value initValue = ExpVisitor.visitExp(varDef.getInitVal().getExps().get(0));
                    if (type.equals(Type.i8)) {
                        Value truc = new Slot(visitor.curFunction);
                        visitor.curBasicBlock().addInst(
                                new TruncInst(truc, Type.i32, initValue, Type.i8)
                        );
                        initValue = truc;
                    }
                    visitor.curBasicBlock().addInst(
                            new StoreInst(type, initValue, new PointerType(type), newValue)
                    );
                }
                newSymbol.address = newValue;
                visitor.curSymbolTab.addSymbol(newSymbol);
            }
        } else {
            // 变量数组
            int length = ExpVisitor.visitConstExp(varDef.getConstExp().getAddExp());
            ArrayType type;
            Symbol newSymbol;
            if (btype.getBtypeToken().getTokenType() == Token.TokenType.INTTK) {
                newSymbol = new IntArraySymbol(token.getValue(), length);
                type = new ArrayType(length, Type.i32);
            } else {
                newSymbol = new CharArraySymbol(token.getValue(), length);
                type = new ArrayType(length, Type.i8);
            }

            if (visitor.curFunction == null) {
                // 全局作用域
                int[] initValues = new int[length]; // Java默认初始化为0
                if (varDef.getInitVal() != null) {
                    if (varDef.getInitVal().getStringConst() != null) {
                        // 字符串赋值
                        String str = varDef.getInitVal().getStringConst().getStringConst().getValue();
                        int i = 0;
                        for (; i < length; i++) {
                            if (i + 1 < str.length()) {
                                initValues[i] = str.charAt(i + 1);
                            } else {
                                initValues[i] = 0;
                            }
                        }
                    } else {
                        // 全局变量的初始值均为可直接计算值
                        List<Exp> expList = varDef.getInitVal().getExps();
                        for (int i = 0; i < expList.size(); i++) {
                            initValues[i] = ExpVisitor.visitConstExp(expList.get(i).getAddExp());
                        }
                    }
                } else {
                    initValues = null; // 无初始值则用zeroinitializer初始化
                }
                GlobalValue globalValue = new GlobalArrayVar(
                        token.getValue(), type, initValues, false
                );
                visitor.module.addGlobalValue(globalValue);
                newSymbol.address = globalValue;
                visitor.curSymbolTab.addSymbol(newSymbol);
            } else {
                // 局部作用域
                Value arrayValue = new Slot(visitor.curFunction);
                visitor.curBasicBlock().addInst(
                        new AllocaInst(arrayValue, type)
                );
                newSymbol.address = arrayValue;
                visitor.curSymbolTab.addSymbol(newSymbol);

                // 依次生成赋值指令（如果有）
                if (varDef.getInitVal() != null) {
                    if (varDef.getInitVal().getStringConst() != null) {
                        // TODO: 代码优化中，可以使用全局字符串拷贝优化赋值指令？
                        String str = varDef.getInitVal().getStringConst().getStringConst().getValue();
                        for (int i = 1; i < str.length() - 1; i++) {
                            Slot s = new Slot(visitor.curFunction);
                            visitor.curBasicBlock().addInst(
                                    new GEPInst(s, type, arrayValue, new Immediate(i - 1))
                            );
                            visitor.curBasicBlock().addInst(
                                    new StoreInst(type.eleType, new Immediate(str.charAt(i)), new PointerType(type.eleType), s)
                            );
                        }
                        // 若字符串长度不够，后面的不补0？
                    } else {
                        for (int i = 0; i < varDef.getInitVal().getExps().size(); i++) {
                            Exp exp = varDef.getInitVal().getExps().get(i);
                            Value initValue = ExpVisitor.visitExp(exp);
                            if (type.eleType.equals(Type.i8)) {
                                Value truc = new Slot(visitor.curFunction);
                                visitor.curBasicBlock().addInst(
                                        new TruncInst(truc, Type.i32, initValue, Type.i8)
                                );
                                initValue = truc;
                            }
                            Value address = new Slot(visitor.curFunction);
                            visitor.curBasicBlock().addInst(
                                    new GEPInst(address, type, arrayValue, new Immediate(i))
                            );
                            visitor.curBasicBlock().addInst(
                                    new StoreInst(type.eleType, initValue, new PointerType(type.eleType), address)
                            );
                        }
                    }
                }
            }
        }
    }
}
