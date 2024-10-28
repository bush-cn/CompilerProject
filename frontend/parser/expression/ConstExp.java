package frontend.parser.expression;

import frontend.parser.SyntaxNode;

import java.io.IOException;

public class ConstExp implements SyntaxNode<ConstExp> {
    AddExp addExp; // 注：使用的 Ident 必须是常量

    public AddExp getAddExp() {
        return addExp;
    }

    @Override
    public ConstExp parse() throws IOException {
        addExp = new AddExp().parse();
        return this;
    }

    @Override
    public String outputString() {
        return addExp.outputString() + "\n<ConstExp>";
    }
}
