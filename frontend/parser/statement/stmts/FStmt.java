package frontend.parser.statement.stmts;

import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;
import frontend.parser.statement.BlockItem;
import frontend.parser.statement.Cond;
import frontend.parser.statement.ForStmt;
import frontend.parser.statement.Stmt;

import java.io.IOException;

public class FStmt extends Stmt implements SyntaxNode<BlockItem> {
    ForStmt preForStmt = null, postForStmt = null; // null代表此项不存在
    Cond cond = null; // null代表此项不存在
    Stmt stmt;

    public ForStmt getPreForStmt() {
        return preForStmt;
    }

    public ForStmt getPostForStmt() {
        return postForStmt;
    }

    public Cond getCond() {
        return cond;
    }

    public Stmt getStmt() {
        return stmt;
    }

    @Override
    public Stmt parse() throws IOException {
        // for语句不会出现分号或括号缺失
        assert Parser.currentSymbol().getTokenType() == Token.TokenType.FORTK;

        Parser.getSymbol();
        assert Parser.currentSymbol().getTokenType() == Token.TokenType.LPARENT;

        Parser.getSymbol();
        if (Parser.currentSymbol().getTokenType() != Token.TokenType.SEMICN) {
            preForStmt = new ForStmt().parse();
            Parser.getSymbol();
            assert Parser.currentSymbol().getTokenType() == Token.TokenType.SEMICN;
        }

        Parser.getSymbol();
        if (Parser.currentSymbol().getTokenType() != Token.TokenType.SEMICN) {
            cond = new Cond().parse();
            Parser.getSymbol();
            assert Parser.currentSymbol().getTokenType() == Token.TokenType.SEMICN;
        }

        Parser.getSymbol();
        if (Parser.currentSymbol().getTokenType() != Token.TokenType.RPARENT) {
            postForStmt = new ForStmt().parse();
            Parser.getSymbol();
            assert Parser.currentSymbol().getTokenType() == Token.TokenType.SEMICN;
        }

        Parser.getSymbol();
        stmt = new Stmt().parse();

        return this;
    }

    @Override
    public String outputString() {
        StringBuilder sb = new StringBuilder();

        sb.append("FORTK for\nLPARENT (");

        if (preForStmt != null) {
            sb.append('\n');
            sb.append(preForStmt.outputString());
        }

        sb.append("\nSEMICN ;");

        if (cond != null) {
            sb.append('\n');
            sb.append(cond.outputString());
        }

        sb.append("\nSEMICN ;");

        if (postForStmt != null) {
            sb.append('\n');
            sb.append(postForStmt.outputString());
        }

        sb.append("\nRPARENT )");

        sb.append('\n');
        sb.append(stmt.outputString());

        return sb.toString() + "\n<Stmt>";
    }
}
