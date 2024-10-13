package frontend.parser.statement.stmts;

import error.CompileError;
import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;
import frontend.parser.expression.Exp;
import frontend.parser.statement.BlockItem;
import frontend.parser.statement.Stmt;

import java.io.IOException;

public class ExpStmt extends Stmt implements SyntaxNode<BlockItem> {
    Exp exp = null; // exp为null时代表此项不存在

    boolean hasSemicolon;

    @Override
    public Stmt parse() throws IOException {
        // 暂且认为不能既没有exp也没有semicolon，否则对应的是空串
        if (Parser.currentSymbol().getTokenType() == Token.TokenType.SEMICN) {
            hasSemicolon = true;
        } else {
            exp = new Exp().parse();
            int line = Parser.currentLine();
            if (Parser.preReadNext().getTokenType() == Token.TokenType.SEMICN) {
                hasSemicolon = true;
                Parser.getSymbol();
            } else {
                hasSemicolon = false;
                CompileError.raiseError(line, CompileError.ErrorType.i);
            }
        }

        return this;
    }

    @Override
    public String outputString() {
        if (exp != null && hasSemicolon) {
            return exp.outputString() + "\nSEMICN ;";
        } else if (exp != null && !hasSemicolon) {
            return exp.outputString();
        } else if (exp == null && hasSemicolon) {
            return "SEMICN ;";
        } else {
            return "";
        }
    }
}
