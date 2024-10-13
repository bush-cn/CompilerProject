package frontend.parser.function;

import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;
import frontend.parser.expression.Exp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FuncRParams implements SyntaxNode<FuncRParams> {
    List<Exp> exps = new ArrayList<>();

    @Override
    public FuncRParams parse() throws IOException {
        exps.add(new Exp().parse());

        while (Parser.preReadNext().getTokenType() == Token.TokenType.COMMA) {
            Parser.getSymbol(); // 吃掉
            Parser.getSymbol();
            exps.add(new Exp().parse());
        }

        return this;
    }

    @Override
    public String outputString() {
        StringBuilder sb = new StringBuilder();

        sb.append(exps.get(0).outputString());

        for (int i = 1; i < exps.size(); i++) {
            sb.append("\nCOMMA ,\n");
            sb.append(exps.get(i).outputString());
        }

        sb.append("\n<FuncRParams>");

        return sb.toString();
    }
}
