package frontend.parser.expression;

import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * LOrExp → LAndExp | LOrExp  ‘||’ LAndExp
 * <p>
 * 消除左递归得到：
 * LOrExp → LAndExp {  ‘||’ LAndExp }
 * <p>
 * 仅在outputString方法输出时，采用递归形式输出，但并不以递归形式储存
 */
public class LOrExp implements SyntaxNode<LOrExp> {
    public static Set<Token.TokenType> lOrOps =
            Set.of(Token.TokenType.OR);

    LAndExp lAndExp;
    public static class OpLAndExp {
        public Token op;
        public LAndExp lAndExp;
    }

    List<OpLAndExp> opLAndExps = new ArrayList<>();

    @Override
    public LOrExp parse() throws IOException {
        lAndExp = new LAndExp().parse();

        while (lOrOps.contains(Parser.preReadNext().getTokenType())) {
            OpLAndExp opLAndExp = new OpLAndExp();

            Parser.getSymbol(); // 吃掉
            opLAndExp.op = Parser.currentSymbol();

            Parser.getSymbol();
            opLAndExp.lAndExp = new LAndExp().parse();

            opLAndExps.add(opLAndExp);
        }

        return this;
    }

    @Override
    public String outputString() {
        StringBuilder sb = new StringBuilder();

        sb.append(lAndExp.outputString());

        for (OpLAndExp opLAndExp : opLAndExps) {
            sb.append("\n<LOrExp>");
            sb.append('\n')
                    .append(opLAndExp.op.getTokenType().toString())
                    .append(" ")
                    .append(opLAndExp.op.getValue());
            sb.append('\n')
                    .append(opLAndExp.lAndExp.outputString());
        }

        sb.append("\n<LOrExp>");

        return sb.toString();
    }
}
