package frontend.parser.function;

import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;

public class FuncType implements SyntaxNode<FuncType> {
    Token funcTypeToken; // int, char, void三选一
    @Override
    public FuncType parse() {
        funcTypeToken = Parser.currentSymbol();
        return this;
    }

    @Override
    public String outputString() {
        return funcTypeToken.getTokenType().toString()
                + " " + funcTypeToken.getValue() + "\n<FuncType>";
    }
}
