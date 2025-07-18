package frontend.parser.statement;

import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;
import frontend.parser.expression.Exp;
import frontend.parser.expression.unaryexps.primaryexps.LVal;

import java.io.IOException;

public class ForStmt implements SyntaxNode<ForStmt> {
    LVal lVal;
    int lValLine;
    public int getLValLine() {
        return lValLine;
    }
    Exp exp;

    public LVal getlVal() {
        return lVal;
    }

    public Exp getExp() {
        return exp;
    }

    @Override
    public ForStmt parse() throws IOException {
        lVal = new LVal().parse();
        lValLine = Parser.currentLine();

        Parser.getSymbol();
        assert Parser.currentSymbol().getTokenType() == Token.TokenType.ASSIGN;

        Parser.getSymbol();
        exp = new Exp().parse();

        return this;
    }

    @Override
    public String outputString() {
        return lVal.outputString() +
                "\nASSIGN =\n" +
                exp.outputString() +
                "\n<ForStmt>";
    }
}
