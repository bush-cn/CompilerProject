package frontend.parser.terminal;

import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;

public class IntConst implements SyntaxNode<IntConst> {
    Token intConst;

    public Token getIntConst() {
        return intConst;
    }

    @Override
    public IntConst parse() {
        intConst = Parser.currentSymbol();
        assert intConst.getTokenType() == Token.TokenType.INTCON;
        return this;
    }

    @Override
    public String outputString() {
        return intConst.getTokenType().toString() + " "
                + intConst.getValue();
    }
}
