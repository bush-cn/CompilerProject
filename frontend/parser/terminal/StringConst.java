package frontend.parser.terminal;

import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;

public class StringConst implements SyntaxNode<StringConst> {
    Token stringConst;
    @Override
    public StringConst parse() {
        stringConst = Parser.currentSymbol();
        assert stringConst.getTokenType() == Token.TokenType.STRCON;
        return this;
    }

    @Override
    public String outputString() {
        return stringConst.getTokenType().toString() + " "
                + stringConst.getValue();
    }
}
