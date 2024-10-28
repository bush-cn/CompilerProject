package frontend.parser.expression.unaryexps;

import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;
import frontend.parser.expression.UnaryExp;
import frontend.parser.expression.unaryexps.primaryexps.Character;
import frontend.parser.expression.unaryexps.primaryexps.LVal;
import frontend.parser.expression.unaryexps.primaryexps.Number;
import frontend.parser.expression.unaryexps.primaryexps.ParenthesisExp;

import java.io.IOException;
import java.util.Set;

public class PrimaryExp extends UnaryExp implements SyntaxNode<UnaryExp> {
    PrimaryExp primaryExp; // praimaryexps包里的子类之一

    public PrimaryExp getPrimaryExp() {
        return primaryExp;
    }

    @Override
    public PrimaryExp parse() throws IOException {
        Token.TokenType tokenType = Parser.currentSymbol().getTokenType();

        if (tokenType == Token.TokenType.LPARENT) {
            primaryExp = new ParenthesisExp().parse();
        } else if (tokenType == Token.TokenType.IDENFR) {
            primaryExp = new LVal().parse();
        } else if (tokenType == Token.TokenType.INTCON) {
            primaryExp = new Number().parse();
        } else if (tokenType == Token.TokenType.CHRCON) {
            primaryExp = new Character().parse();
        } // 不考虑其他情况

        return this;
    }

    @Override
    public String outputString() {
        return primaryExp.outputString() + "\n<PrimaryExp>";
    }
}
