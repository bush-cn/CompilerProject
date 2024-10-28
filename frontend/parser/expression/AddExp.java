package frontend.parser.expression;

import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * AddExp → MulExp | AddExp ('+' | '−') MulExp
 * <p>
 * 消除左递归得到：
 * AddExp → MulExp { ('+' | '−') MulExp }
 * <p>
 * 仅在outputString方法输出时，采用递归形式输出，但并不以递归形式储存
 */
public class AddExp implements SyntaxNode<AddExp> {
    public static Set<Token.TokenType> addOps =
            Set.of(Token.TokenType.PLUS, Token.TokenType.MINU);

    MulExp mulExp;
    public static class OpMulExp {
        public Token op;
        public MulExp mulExp;
    }

    List<OpMulExp> opMulExps = new ArrayList<>();

    public MulExp getMulExp() {
        return mulExp;
    }

    public List<OpMulExp> getOpMulExps() {
        return opMulExps;
    }

    @Override
    public AddExp parse() throws IOException {
        mulExp = new MulExp().parse();

        while (addOps.contains(Parser.preReadNext().getTokenType())) {
            OpMulExp opMulExp = new OpMulExp();

            Parser.getSymbol(); // 吃掉
            opMulExp.op = Parser.currentSymbol();

            Parser.getSymbol();
            opMulExp.mulExp = new MulExp().parse();

            opMulExps.add(opMulExp);
        }

        return this;
    }

    @Override
    public String outputString() {
        StringBuilder sb = new StringBuilder();

        sb.append(mulExp.outputString());

        for (OpMulExp opMulExp : opMulExps) {
            sb.append("\n<AddExp>");
            sb.append('\n')
                    .append(opMulExp.op.getTokenType().toString())
                    .append(" ")
                    .append(opMulExp.op.getValue());
            sb.append('\n')
                    .append(opMulExp.mulExp.outputString());
        }

        sb.append("\n<AddExp>");

        return sb.toString();
    }

    @Override
    public String toString() {
        return outputString(); // 便于Debug
    }
}
