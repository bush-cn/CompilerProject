package frontend.parser.expression;

import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
 * <p>
 * 消除左递归得到：
 * RelExp → AddExp { ('<' | '>' | '<=' | '>=') AddExp }
 * <p>
 * 仅在outputString方法输出时，采用递归形式输出，但并不以递归形式储存
 */
public class RelExp implements SyntaxNode<RelExp> {
    public static Set<Token.TokenType> relOps =
            Set.of(Token.TokenType.LSS,
                    Token.TokenType.GRE,
                    Token.TokenType.LEQ,
                    Token.TokenType.GEQ);

    AddExp addExp;
    public static class OpAddExp {
        public Token op;
        public AddExp addExp;
    }

    List<OpAddExp> opAddExps = new ArrayList<>();

    @Override
    public RelExp parse() throws IOException {
        addExp = new AddExp().parse();

        while (relOps.contains(Parser.preReadNext().getTokenType())) {
            OpAddExp opAddExp = new OpAddExp();

            Parser.getSymbol(); // 吃掉
            opAddExp.op = Parser.currentSymbol();

            Parser.getSymbol();
            opAddExp.addExp = new AddExp().parse();

            opAddExps.add(opAddExp);
        }

        return this;
    }

    @Override
    public String outputString() {
        StringBuilder sb = new StringBuilder();

        sb.append(addExp.outputString());

        for (OpAddExp opAddExp : opAddExps) {
            sb.append("\n<RelExp>");
            sb.append('\n')
                    .append(opAddExp.op.getTokenType().toString())
                    .append(" ")
                    .append(opAddExp.op.getValue());
            sb.append('\n')
                    .append(opAddExp.addExp.outputString());
        }

        sb.append("\n<RelExp>");

        return sb.toString();
    }
}
