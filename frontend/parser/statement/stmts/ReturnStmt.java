package frontend.parser.statement.stmts;

import error.CompileError;
import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;
import frontend.parser.expression.Exp;
import frontend.parser.statement.BlockItem;
import frontend.parser.statement.Stmt;

import java.io.IOException;

public class ReturnStmt extends Stmt implements SyntaxNode<BlockItem> {
    Exp exp = null; // 值为null时代表无Exp

    boolean hasSemicolon;

    public Exp getExp() {
        return exp;
    }

    @Override
    public Stmt parse() throws IOException {
        assert Parser.currentSymbol().getTokenType() == Token.TokenType.RETURNTK;
        int line = Parser.currentLine();

        if (Exp.firstSetOfExp.contains(Parser.preReadNext().getTokenType())) {
            // Exp的FIRST集，则解析一个Exp
            Parser.getSymbol();
            exp = new Exp().parse();
            line = Parser.currentLine();
        }

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

        sb.append("RETURNTK return");

        if (exp != null) {
            sb.append('\n');
            sb.append(exp.outputString());
        }

        if (hasSemicolon) {
            sb.append("\nSEMICN ;");
        }

        return sb.toString();
    }
}
