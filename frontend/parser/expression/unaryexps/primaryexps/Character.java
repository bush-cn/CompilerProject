package frontend.parser.expression.unaryexps.primaryexps;

import frontend.parser.SyntaxNode;
import frontend.parser.expression.UnaryExp;
import frontend.parser.expression.unaryexps.PrimaryExp;
import frontend.parser.terminal.CharConst;

public class Character extends PrimaryExp implements SyntaxNode<UnaryExp> {
    CharConst charConst;

    public CharConst getCharConst() {
        return charConst;
    }

    @Override
    public Character parse() {
        charConst = new CharConst().parse();
        return this;
    }

    @Override
    public String outputString() {
        return charConst.outputString() + "\n<Character>" + "\n<PrimaryExp>";
    }
}
