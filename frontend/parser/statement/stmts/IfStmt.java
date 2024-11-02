package frontend.parser.statement.stmts;

import error.CompileError;
import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;
import frontend.parser.statement.BlockItem;
import frontend.parser.statement.Cond;
import frontend.parser.statement.Stmt;

import java.io.IOException;

public class IfStmt extends Stmt implements SyntaxNode<BlockItem> {
    Cond cond;
    Stmt stmt;

    Stmt elseStmt = null; // 为null时代表没有else子句

    boolean hasRParenthesis;

    public Cond getCond() {
        return cond;
    }

    public Stmt getStmt() {
        return stmt;
    }

    public Stmt getElseStmt() {
        return elseStmt;
    }

    @Override
    public Stmt parse() throws IOException {
        assert Parser.currentSymbol().getTokenType() == Token.TokenType.IFTK;

        Parser.getSymbol();
        assert Parser.currentSymbol().getTokenType() == Token.TokenType.LPARENT;

        Parser.getSymbol();
        cond = new Cond().parse();
        int line = Parser.currentLine();

        Parser.getSymbol();
        if (Parser.currentSymbol().getTokenType() == Token.TokenType.RPARENT) {
            hasRParenthesis = true;
            Parser.getSymbol(); // 不是吃掉，而是转到Stmt
        } else {
            hasRParenthesis = false;
            CompileError.raiseError(line, CompileError.ErrorType.j);
        }

        stmt = new Stmt().parse();

        if (Parser.preReadNext().getTokenType() == Token.TokenType.ELSETK) {
            Parser.getSymbol(); // 吃掉这个else
            Parser.getSymbol();
            elseStmt = new Stmt().parse();
        }

        return this;
    }

    @Override
    public String outputString() {
        StringBuilder sb = new StringBuilder();

        sb.append("IFTK if\nLPARENT (");

        sb.append('\n');
        sb.append(cond.outputString());

        if (hasRParenthesis) {
            sb.append("\nRPARENT )");
        }

        sb.append('\n');
        sb.append(stmt.outputString());

        if (elseStmt != null) {
            sb.append("\nELSETK else\n");
            sb.append(elseStmt.outputString());
        }

        return sb.toString() + "\n<Stmt>";
    }
}
