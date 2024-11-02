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
    @Override
    public PrimaryExp parse() throws IOException {
        Token.TokenType tokenType = Parser.currentSymbol().getTokenType();

        if (tokenType == Token.TokenType.LPARENT) {
            return new ParenthesisExp().parse();
        } else if (tokenType == Token.TokenType.IDENFR) {
            return new LVal().parse();
        } else if (tokenType == Token.TokenType.INTCON) {
            return new Number().parse();
        } else if (tokenType == Token.TokenType.CHRCON) {
            return new Character().parse();
        } // 不考虑其他情况

        return this;
    }
}
