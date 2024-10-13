package frontend.parser.terminal;

import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;

public class CharConst implements SyntaxNode<CharConst> {
    Token charConst;

    @Override
    public CharConst parse() {
        charConst = Parser.currentSymbol();
        assert charConst.getTokenType() == Token.TokenType.CHRCON;
        return this;
    }

    @Override
    public String outputString() {
        return charConst.getTokenType().toString() + " "
                + charConst.getValue();
    }
}
