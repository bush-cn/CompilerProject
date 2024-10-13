package frontend.parser.expression;

import frontend.lexer.Token;
import frontend.parser.SyntaxNode;

import java.io.IOException;
import java.util.Set;

public class Exp implements SyntaxNode<Exp> {
    public static Set<Token.TokenType> firstSetOfExp
            = Set.of(Token.TokenType.LPARENT,
            Token.TokenType.IDENFR,
            Token.TokenType.INTCON,
            Token.TokenType.CHRCON,
            Token.TokenType.PLUS,
            Token.TokenType.MINU,
            Token.TokenType.NOT);

    AddExp addExp;

    @Override
    public Exp parse() throws IOException {
        addExp = new AddExp().parse();
        return this;
    }

    @Override
    public String outputString() {
        return addExp.outputString() + "\n<Exp>";
    }
}
