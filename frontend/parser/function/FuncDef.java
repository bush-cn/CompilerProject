package frontend.parser.function;

import error.CompileError;
import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;
import frontend.parser.statement.Block;
import frontend.parser.terminal.Ident;

import java.io.IOException;

public class FuncDef implements SyntaxNode<FuncDef> {
    FuncType funcType;
    Ident ident;
    FuncFParams funcFParams = null; // 若为null则此项不存在
    Block block;

    boolean hasRParenthesis;

    public FuncType getFuncType() {
        return funcType;
    }

    public Ident getIdent() {
        return ident;
    }

    public FuncFParams getFuncFParams() {
        return funcFParams;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public FuncDef parse() throws IOException {
        funcType = new FuncType().parse();

        Parser.getSymbol();
        ident = new Ident().parse();

        Parser.getSymbol();
        assert Parser.currentSymbol().getTokenType() == Token.TokenType.LPARENT;
        int line = Parser.currentLine();

        Parser.getSymbol();
        if (Parser.currentSymbol().getTokenType() == Token.TokenType.RPARENT) {
            hasRParenthesis = true;
            Parser.getSymbol();
        } else if (Parser.currentSymbol().getTokenType() == Token.TokenType.INTTK
        || Parser.currentSymbol().getTokenType() == Token.TokenType.CHARTK) {
            // FuncParams部分
            funcFParams = new FuncFParams().parse();
            line = Parser.currentLine();
            Parser.getSymbol();
            if (Parser.currentSymbol().getTokenType() == Token.TokenType.RPARENT) {
                hasRParenthesis = true;
                Parser.getSymbol();
            } else {
                hasRParenthesis = false;
                CompileError.raiseError(line, CompileError.ErrorType.j);
            }
        } else {
            // 来到Block部分且无右括号
            hasRParenthesis = false;
            CompileError.raiseError(line, CompileError.ErrorType.j);
        }

        block = new Block().parse();

        return this;
    }

    @Override
    public String outputString() {
        StringBuilder sb = new StringBuilder();

        sb.append(funcType.outputString());

        sb.append('\n');
        sb.append(ident.outputString());

        sb.append('\n');
        sb.append("LPARENT (");

        if (funcFParams != null) {
            sb.append('\n');
            sb.append(funcFParams.outputString());
        }

        if (hasRParenthesis) {
            sb.append('\n');
            sb.append("RPARENT )");
        }

        sb.append('\n');
        sb.append(block.outputString());

        sb.append('\n');
        sb.append("<FuncDef>");

        return sb.toString();
    }
}
