package frontend.parser.declaration;

import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;

public class Btype implements SyntaxNode<Btype> {
    // Btype为INTTK或CHARTK二选一，也不用出现在outputString里
    Token btypeToken;

    public Token getBtypeToken() {
        return btypeToken;
    }

    @Override
    public Btype parse() {
        btypeToken = Parser.currentSymbol();
        return this;
    }

    @Override
    public String outputString() {
        if (btypeToken.getTokenType() == Token.TokenType.INTTK) {
            return "INTTK int";
        } else {
            return "CHARTK char";
        }
    }
}
