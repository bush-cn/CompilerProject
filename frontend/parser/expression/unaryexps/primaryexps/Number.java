package frontend.parser.expression.unaryexps.primaryexps;

import frontend.parser.SyntaxNode;
import frontend.parser.expression.UnaryExp;
import frontend.parser.expression.unaryexps.PrimaryExp;
import frontend.parser.terminal.IntConst;

public class Number extends PrimaryExp implements SyntaxNode<UnaryExp> {
    IntConst intConst;

    public IntConst getIntConst() {
        return intConst;
    }

    @Override
    public Number parse() {
        intConst = new IntConst().parse();
        return this;
    }

    @Override
    public String outputString() {
        return intConst.outputString() + "\n<Number>" + "\n<PrimaryExp>";
    }
}
