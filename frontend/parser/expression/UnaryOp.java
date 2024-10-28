package frontend.parser.expression;

import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;

public class UnaryOp implements SyntaxNode<UnaryOp> {
    Token unaryToken; // +, -, !三者其一,  注：'!'仅出现在条件表达式中

    public Token getUnaryToken() {
        return unaryToken;
    }

    @Override
    public UnaryOp parse() {
        unaryToken = Parser.currentSymbol();
        assert unaryToken.getTokenType() == Token.TokenType.PLUS
                || unaryToken.getTokenType() == Token.TokenType.MINU
                || unaryToken.getTokenType() == Token.TokenType.NOT;
        return this;
    }

    @Override
    public String outputString() {
        return unaryToken.getTokenType().toString() + " "
                + unaryToken.getValue() + "\n<UnaryOp>";
    }
}
