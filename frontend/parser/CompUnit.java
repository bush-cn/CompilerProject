package frontend.parser;

import error.CompileError;
import frontend.lexer.Token;
import frontend.parser.declaration.Decl;
import frontend.parser.function.FuncDef;
import frontend.parser.function.MainFuncDef;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CompUnit implements SyntaxNode<CompUnit>{
    List<Decl> decls = new ArrayList<>();
    List<FuncDef> funcDefs = new ArrayList<>();
    MainFuncDef mainFuncDef;

    public List<Decl> getDecls() {
        return decls;
    }

    public List<FuncDef> getFuncDefs() {
        return funcDefs;
    }

    public MainFuncDef getMainFuncDef() {
        return mainFuncDef;
    }

    @Override
    public CompUnit parse() throws IOException {
        // {Decl}部分
        while(true) {
            if (Parser.currentSymbol().getTokenType() == Token.TokenType.INTTK) {
                // 可能是Decl, FuncDef, MainFunDef
                if (Parser.preReadNext().getTokenType() == Token.TokenType.MAINTK) {
                    // 若预读一个token为MAINTK，则直接跳转到MainFunDef
                    mainFuncDef = new MainFuncDef().parse();
                    return this;
                } else if (Parser.preReadNextNext().getTokenType()
                        == Token.TokenType.LPARENT) {
                    // 预读下下个token为左括号，则继续推导FuncDef
                    break;
                } else {
                    // 继续添加Decl
                    decls.add(new Decl().parse());
                    Parser.getSymbol();
                }
            } else if (Parser.currentSymbol().getTokenType() == Token.TokenType.CHARTK) {
                // 可能是Decl, FuncDef
                if (Parser.preReadNextNext().getTokenType()
                        == Token.TokenType.LPARENT) {
                    // 预读下下个token为左括号，则继续推导FuncDef
                    break;
                } else {
                    // 继续添加Decl
                    decls.add(new Decl().parse());
                    Parser.getSymbol();
                }
            } else if (Parser.currentSymbol().getTokenType() == Token.TokenType.VOIDTK) {
                // 跳转到FuncDef
                break;
            } else if (Parser.currentSymbol().getTokenType() == Token.TokenType.CONSTTK) {
                decls.add(new Decl().parse());
                Parser.getSymbol();
            } // 暂不考虑其他错误
        }

        // {FuncDef}部分，此时便不再区分Decl
        while (true) {
            if (Parser.currentSymbol().getTokenType() == Token.TokenType.INTTK) {
                // 可能为MainFuncDef
                if (Parser.preReadNext().getTokenType() == Token.TokenType.MAINTK) {
                    // 若预读一个token为MAINTK，则继续推导MainFunDef
                    break;
                } else {
                    funcDefs.add(new FuncDef().parse());
                    Parser.getSymbol();
                }
            } else {
                funcDefs.add(new FuncDef().parse());
                Parser.getSymbol();
            } // 暂不考虑其他错误
        }

        mainFuncDef = new MainFuncDef().parse();
        return this;
    }

    @Override
    public String outputString() {
        // 约定：每个语法成分SyntaxNode在自己的此方法里添加<SyntaxNode>字段
        StringBuilder sb = new StringBuilder();
        decls.forEach(decl -> {
            sb.append(decl.outputString());
            sb.append('\n');
        });
        funcDefs.forEach(funcDef -> {
            sb.append(funcDef.outputString());
            sb.append('\n');
        });
        sb.append(mainFuncDef.outputString());
        sb.append('\n');
        sb.append("<CompUnit>");
        return sb.toString();
    }
}
