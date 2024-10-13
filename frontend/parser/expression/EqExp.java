package frontend.parser.expression;

import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * EqExp → RelExp | EqExp  ('==' | '!=') RelExp
 * <p>
 * 消除左递归得到：
 * EqExp → RelExp {  ('==' | '!=') RelExp }
 * <p>
 * 仅在outputString方法输出时，采用递归形式输出，但并不以递归形式储存
 */
public class EqExp implements SyntaxNode<EqExp> {
    public static Set<Token.TokenType> eqOps =
            Set.of(Token.TokenType.EQL, Token.TokenType.NEQ);

    RelExp relExp;
    public static class OpRelExp {
        public Token op;
        public RelExp relExp;
    }

    List<OpRelExp> opRelExps = new ArrayList<>();

    @Override
    public EqExp parse() throws IOException {
        relExp = new RelExp().parse();

        while (eqOps.contains(Parser.preReadNext().getTokenType())) {
            OpRelExp opRelExp = new OpRelExp();

            Parser.getSymbol(); // 吃掉
            opRelExp.op = Parser.currentSymbol();

            Parser.getSymbol();
            opRelExp.relExp = new RelExp().parse();

            opRelExps.add(opRelExp);
        }

        return this;
    }

    @Override
    public String outputString() {
        StringBuilder sb = new StringBuilder();

        sb.append(relExp.outputString());

        for (OpRelExp opRelExp : opRelExps) {
            sb.append("\n<EqExp>");
            sb.append('\n')
                    .append(opRelExp.op.getTokenType().toString())
                    .append(" ")
                    .append(opRelExp.op.getValue());
            sb.append('\n')
                    .append(opRelExp.relExp.outputString());
        }

        sb.append("\n<EqExp>");

        return sb.toString();
    }
}
