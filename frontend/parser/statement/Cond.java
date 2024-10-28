package frontend.parser.statement;

import frontend.parser.SyntaxNode;
import frontend.parser.expression.LOrExp;

import java.io.IOException;

public class Cond implements SyntaxNode<Cond> {
    LOrExp lOrExp;

    public LOrExp getlOrExp() {
        return lOrExp;
    }

    @Override
    public Cond parse() throws IOException {
        lOrExp = new LOrExp().parse();
        return this;
    }

    @Override
    public String outputString() {
        return lOrExp.outputString() + "\n<Cond>";
    }
}
