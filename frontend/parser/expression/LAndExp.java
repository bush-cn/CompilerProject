package frontend.parser.expression;

import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * LAndExp → EqExp | LAndExp  ‘&&’ EqExp
 * <p>
 * 消除左递归得到：
 * LAndExp → EqExp {  ‘&&’ EqExp }
 * <p>
 * 仅在outputString方法输出时，采用递归形式输出，但并不以递归形式储存
 */
public class LAndExp implements SyntaxNode<LAndExp> {
    public static Set<Token.TokenType> lAndOps =
            Set.of(Token.TokenType.AND);

    EqExp eqExp;
    public static class OpEqExp {
        public Token op;
        public EqExp eqExp;
    }

    List<OpEqExp> opEqExps = new ArrayList<>();

    @Override
    public LAndExp parse() throws IOException {
        eqExp = new EqExp().parse();

        while (lAndOps.contains(Parser.preReadNext().getTokenType())) {
            OpEqExp opEqExp = new OpEqExp();

            Parser.getSymbol(); // 吃掉
            opEqExp.op = Parser.currentSymbol();

            Parser.getSymbol();
            opEqExp.eqExp = new EqExp().parse();

            opEqExps.add(opEqExp);
        }

        return this;
    }

    @Override
    public String outputString() {
        StringBuilder sb = new StringBuilder();

        sb.append(eqExp.outputString());

        for (OpEqExp opEqExp : opEqExps) {
            sb.append("\n<LAndExp>");
            sb.append('\n')
                    .append(opEqExp.op.getTokenType().toString())
                    .append(" ")
                    .append(opEqExp.op.getValue());
            sb.append('\n')
                    .append(opEqExp.eqExp.outputString());
        }

        sb.append("\n<LAndExp>");

        return sb.toString();
    }
}
