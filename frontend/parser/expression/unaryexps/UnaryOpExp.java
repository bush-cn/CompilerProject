package frontend.parser.expression.unaryexps;

import frontend.parser.Parser;
import frontend.parser.SyntaxNode;
import frontend.parser.expression.UnaryExp;
import frontend.parser.expression.UnaryOp;

import java.io.IOException;

public class UnaryOpExp extends UnaryExp implements SyntaxNode<UnaryExp> {
    UnaryOp unaryOp;
    UnaryExp unaryExp;

    @Override
    public UnaryExp parse() throws IOException {
        unaryOp = new UnaryOp().parse();

        Parser.getSymbol();
        unaryExp = new UnaryExp().parse();

        return this;
    }

    @Override
    public String outputString() {
        return unaryOp.outputString() + "\n" + unaryExp.outputString();
    }
}
