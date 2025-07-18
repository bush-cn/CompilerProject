package frontend.parser.statement.stmts;

import error.CompileError;
import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;
import frontend.parser.statement.BlockItem;
import frontend.parser.statement.Stmt;

import java.io.IOException;

public class BreakContinueStmt extends Stmt implements SyntaxNode<BlockItem> {
    Token breakOrContinue;
    int breakOrContinueLine;

    boolean hasSemicolon;

    public Token getBreakOrContinue() {
        return breakOrContinue;
    }

    public int getBreakOrContinueLine() {
        return breakOrContinueLine;
    }

    @Override
    public Stmt parse() throws IOException {
        breakOrContinue = Parser.currentSymbol();
        breakOrContinueLine = Parser.currentLine();
        int line = Parser.currentLine();

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

        if (breakOrContinue.getTokenType() == Token.TokenType.BREAKTK) {
            sb.append("BREAKTK break");
        } else {
            sb.append("CONTINUETK continue");
        }

        if (hasSemicolon) {
            sb.append("\nSEMICN ;");
        }

        return sb.toString() + "\n<Stmt>";
    }
}
