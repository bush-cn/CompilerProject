package frontend.parser.expression.unaryexps.primaryexps;

import error.CompileError;
import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;
import frontend.parser.expression.Exp;
import frontend.parser.expression.UnaryExp;
import frontend.parser.expression.unaryexps.PrimaryExp;
import frontend.parser.terminal.Ident;

import java.io.IOException;

public class LVal extends PrimaryExp implements SyntaxNode<UnaryExp> {
    Ident ident;
    Exp exp = null; // null表示不存在，非null则为数组

    boolean hasRBracket;

    public Ident getIdent() {
        return ident;
    }

    public Exp getExp() {
        return exp;
    }

    @Override
    public LVal parse() throws IOException {
        ident = new Ident().parse();

        if (Parser.preReadNext().getTokenType() == Token.TokenType.LBRACK) {
            Parser.getSymbol(); // 吃掉此中括号
            Parser.getSymbol();
            exp = new Exp().parse();
            int line = Parser.currentLine();
            if (Parser.preReadNext().getTokenType() == Token.TokenType.RBRACK) {
                hasRBracket = true;
                Parser.getSymbol();
            } else {
                hasRBracket = false;
                CompileError.raiseError(line, CompileError.ErrorType.k);
            }
        }
        return this;
    }

    @Override
    public String outputString() {
        StringBuilder sb = new StringBuilder();

        sb.append(ident.outputString());

        if (exp != null) {
            sb.append("\nLBRACK [\n");
            sb.append(exp.outputString());
            if (hasRBracket) {
                sb.append("\nRBRACK ]");
            }
        }

        sb.append("\n<LVal>");

        return sb.toString();
    }
}
