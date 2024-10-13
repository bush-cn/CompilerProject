package frontend.parser.statement;

import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;
import frontend.parser.expression.unaryexps.primaryexps.LVal;
import frontend.parser.statement.stmts.*;

import java.io.IOException;

public class Stmt extends BlockItem implements SyntaxNode<BlockItem> {
    Stmt stmt; // stmts包里的子类其中一个

    @Override
    public Stmt parse() throws IOException {
        Token.TokenType tokenType = Parser.currentSymbol().getTokenType();

        if (tokenType == Token.TokenType.IFTK) {
            stmt = new IfStmt().parse();
        } else if (tokenType == Token.TokenType.FORTK) {
            stmt = new FStmt().parse();
        } else if (tokenType == Token.TokenType.RETURNTK) {
            stmt = new ReturnStmt().parse();
        } else if (tokenType == Token.TokenType.PRINTFTK) {
            stmt = new PrintfStmt().parse();
        } else if (tokenType == Token.TokenType.BREAKTK || tokenType == Token.TokenType.CONTINUETK) {
            stmt = new BreakContinueStmt().parse();
        } else if (tokenType == Token.TokenType.LBRACE) {
            stmt = new BlockStmt().parse();
        } else if (tokenType == Token.TokenType.IDENFR
        && Parser.preReadNext().getTokenType() != Token.TokenType.LPARENT) {
            // 接下来区分LValStmt和ExpStmt
            // LValStmt只以LVal开头，但Exp也有可能以LVal开头
            // 排除调用函数Exp这一情况，这样只会是LVal造成IDENFR
            int mark = Parser.markIndex();
            new LVal().parse(); // 吃掉一个LVal再判断
            Parser.getSymbol();
            if (Parser.currentSymbol().getTokenType() == Token.TokenType.ASSIGN) {
                // 但LValStmt在LVal后会紧跟ASSIGN
                Parser.reset(mark); // 回溯
                stmt = new LValStmt().parse();
            } else {
                Parser.reset(mark);
                stmt = new ExpStmt().parse();
            }
        } else {
            // 其余情况均为ExpStmt
            // 包括不以IDENFR开头和以IDENFR开头但紧跟(
            stmt = new ExpStmt().parse();
        }

        return this;
    }

    @Override
    public String outputString() {
        return stmt.outputString() + "\n<Stmt>";
    }
}
