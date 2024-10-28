package frontend.parser.function;

import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FuncFParams implements SyntaxNode<FuncFParams> {
    List<FuncFParam> funcFParams = new ArrayList<>();

    public List<FuncFParam> getFuncFParams() {
        return funcFParams;
    }

    @Override
    public FuncFParams parse() throws IOException {
        funcFParams.add(new FuncFParam().parse());
        while (Parser.preReadNext().getTokenType() == Token.TokenType.COMMA) {
            Parser.getSymbol(); // 吃掉此comma
            Parser.getSymbol();
            funcFParams.add(new FuncFParam().parse());
        }

        return this;
    }

    @Override
    public String outputString() {
        StringBuilder sb = new StringBuilder();

        sb.append(funcFParams.get(0).outputString());

        for(int i = 1; i < funcFParams.size(); i++) {
            sb.append('\n');
            sb.append("COMMA ,");
            sb.append('\n');
            sb.append(funcFParams.get(i).outputString());
        }

        sb.append('\n');
        sb.append("<FuncFParams>");

        return sb.toString();
    }
}
