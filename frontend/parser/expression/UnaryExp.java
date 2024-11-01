package frontend.parser.expression;

import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;
import frontend.parser.expression.unaryexps.FunctionCall;
import frontend.parser.expression.unaryexps.PrimaryExp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UnaryExp implements SyntaxNode<UnaryExp> {
    // 相邻两个 UnaryOp 不能相同
    // UnaryOp 为 '!' 只能出现在条件表达式中
    List<UnaryOp> unaryOps = new ArrayList<>();
    UnaryExp unaryExp; // unaryexps包里的子类之一

    public UnaryExp getUnaryExp() {
        return unaryExp;
    }

    @Override
    public UnaryExp parse() throws IOException {
        Token.TokenType tokenType = Parser.currentSymbol().getTokenType();

        while (tokenType == Token.TokenType.PLUS
                || tokenType == Token.TokenType.MINU
                || tokenType == Token.TokenType.NOT) {
            unaryOps.add(new UnaryOp().parse());
            Parser.getSymbol();
            tokenType = Parser.currentSymbol().getTokenType();
        }

        if (tokenType == Token.TokenType.LPARENT
        || tokenType == Token.TokenType.INTCON
        || tokenType == Token.TokenType.CHRCON) {
            unaryExp = new PrimaryExp().parse();
        }
        // 此外tokenType只会是IDENFR，但要通过超前看两个token区分为两种情况
        else if (tokenType == Token.TokenType.IDENFR) {
            if (Parser.preReadNext().getTokenType() == Token.TokenType.LPARENT) {
                unaryExp = new FunctionCall().parse();
            } else {
                unaryExp = new PrimaryExp().parse();
            }
        } // 不考虑其他错误
        else {
            System.out.println("Should Never Reach this Statement.x");
            System.out.println("UnaryExp.java: 一元表达式FIRST集不含有：" + tokenType);
            System.out.println("token行数：" + Parser.currentSymbol().getLine());
        }

        return this;
    }

    @Override
    public String outputString() {
        return unaryExp.outputString() + "\n<UnaryExp>";
    }
}
