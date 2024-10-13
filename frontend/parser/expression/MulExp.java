package frontend.parser.expression;

import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
 * <p>
 * 消除左递归得到：
 * MulExp → UnaryExp { ('*' | '/' | '%') UnaryExp }
 * <p>
 * 仅在outputString方法输出时，采用递归形式输出，但并不以递归形式储存
 */
public class MulExp implements SyntaxNode<MulExp> {
    public static Set<Token.TokenType> mulOps =
            Set.of(Token.TokenType.MULT, Token.TokenType.DIV, Token.TokenType.MOD);

    UnaryExp unaryExp;
    public static class OpUnaryExp {
        public Token op;
        public UnaryExp unaryExp;
    }

    List<OpUnaryExp> opUnaryExps = new ArrayList<>();

    @Override
    public MulExp parse() throws IOException {
        unaryExp = new UnaryExp().parse();

        while (mulOps.contains(Parser.preReadNext().getTokenType())) {
            OpUnaryExp opUnaryExp = new OpUnaryExp();

            Parser.getSymbol(); // 吃掉
            opUnaryExp.op = Parser.currentSymbol();

            Parser.getSymbol();
            opUnaryExp.unaryExp = new UnaryExp().parse();

            opUnaryExps.add(opUnaryExp);
        }

        return this;
    }

    @Override
    public String outputString() {
        StringBuilder sb = new StringBuilder();

        sb.append(unaryExp.outputString());

        for (OpUnaryExp opUnaryExp : opUnaryExps) {
            sb.append("\n<MulExp>");
            sb.append('\n')
                    .append(opUnaryExp.op.getTokenType().toString())
                    .append(" ")
                    .append(opUnaryExp.op.getValue());
            sb.append('\n')
                    .append(opUnaryExp.unaryExp.outputString());
        }

        sb.append("\n<MulExp>");

        return sb.toString();
    }
}
