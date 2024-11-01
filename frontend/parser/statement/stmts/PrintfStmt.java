package frontend.parser.statement.stmts;

import error.CompileError;
import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;
import frontend.parser.expression.Exp;
import frontend.parser.statement.BlockItem;
import frontend.parser.statement.Stmt;
import frontend.parser.terminal.StringConst;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PrintfStmt extends Stmt implements SyntaxNode<BlockItem> {
    StringConst stringConst;

    List<Exp> exps = new ArrayList<>();
    int printfLine;

    boolean hasRParenthesis;

    boolean hasSemicolon;

    public StringConst getStringConst() {
        return stringConst;
    }

    public List<Exp> getExps() {
        return exps;
    }

    public int getPrintfLine() {
        return printfLine;
    }

    @Override
    public Stmt parse() throws IOException {
        assert Parser.currentSymbol().getTokenType() == Token.TokenType.PRINTFTK;
        printfLine = Parser.currentLine();

        Parser.getSymbol();
        assert Parser.currentSymbol().getTokenType() == Token.TokenType.LPARENT;

        Parser.getSymbol();
        stringConst = new StringConst().parse();
        int line = Parser.currentLine();

        while (true) {
            if (Parser.preReadNext().getTokenType() == Token.TokenType.COMMA) {
                Parser.getSymbol();
                Parser.getSymbol();
                exps.add(new Exp().parse());
                line = Parser.currentLine();
            } else {
                break;
            }
        }

        if (Parser.preReadNext().getTokenType() == Token.TokenType.RPARENT) {
            hasRParenthesis = true;
            Parser.getSymbol(); // 吃掉
            line = Parser.currentLine();
        } else {
            hasRParenthesis = false;
            CompileError.raiseError(line, CompileError.ErrorType.j);
        }
        line = Parser.currentLine();

        if (Parser.preReadNext().getTokenType() == Token.TokenType.SEMICN) {
            hasSemicolon = true;
            Parser.getSymbol();
        } else {
            hasSemicolon = false;
            CompileError.raiseError(line, CompileError.ErrorType.i);
        }

        return this;
    }

    @Override
    public String outputString() {
        StringBuilder sb = new StringBuilder();

        sb.append("PRINTFTK printf\nLPARENT (\n");

        sb.append(stringConst.outputString());

        for (Exp exp : exps) {
            sb.append("\nCOMMA ,\n");
            sb.append(exp.outputString());
        }

        if (hasRParenthesis) {
            sb.append("\nRPARENT )");
        }

        if (hasSemicolon) {
            sb.append("\nSEMICN ;");
        }

        return sb.toString();
    }
}
