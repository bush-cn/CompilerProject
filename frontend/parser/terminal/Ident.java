package frontend.parser.terminal;

import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;

public class Ident implements SyntaxNode<Ident> {
    Token ident;

    @Override
    public Ident parse() {
        ident = Parser.currentSymbol();
        assert ident.getTokenType() == Token.TokenType.IDENFR;
        return this;
    }

    @Override
    public String outputString() {
        return ident.getTokenType().toString() + " "
                + ident.getValue();
    }
}
