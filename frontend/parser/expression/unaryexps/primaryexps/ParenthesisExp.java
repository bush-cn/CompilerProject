package frontend.parser.expression.unaryexps.primaryexps;

import error.CompileError;
import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;
import frontend.parser.expression.Exp;
import frontend.parser.expression.UnaryExp;
import frontend.parser.expression.unaryexps.PrimaryExp;

import java.io.IOException;

public class ParenthesisExp extends PrimaryExp implements SyntaxNode<UnaryExp> {
    Exp exp;

    boolean hasRParenthesis;

    public Exp getExp() {
        return exp;
    }

    @Override
    public ParenthesisExp parse() throws IOException {
        assert Parser.currentSymbol().getTokenType() == Token.TokenType.LPARENT;

        Parser.getSymbol();
        exp = new Exp().parse();
        int line = Parser.currentLine();

        if (Parser.preReadNext().getTokenType() == Token.TokenType.RPARENT) {
            hasRParenthesis = true;
            Parser.getSymbol();
        } else {
            hasRParenthesis = false;
            CompileError.raiseError(line, CompileError.ErrorType.j);
        }

        return this;
    }

    @Override
    public String outputString() {
        StringBuilder sb = new StringBuilder();

        sb.append("LPARENT (\n");

        sb.append(exp.outputString());

        if (hasRParenthesis) {
            sb.append("\nRPARENT )");
        }

//        sb.append("\n<PrimaryExp>");
        // 在PrimaryExp里输出

        return sb.toString() + "\n<PrimaryExp>";
    }
}
