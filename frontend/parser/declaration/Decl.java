package frontend.parser.declaration;

import error.CompileError;
import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;
import frontend.parser.statement.BlockItem;

import java.io.IOException;

public class Decl extends BlockItem implements SyntaxNode<BlockItem> {
    // Decl的子类ConstDecl或VarDecl之一，因此输出字符串时不用重复输出
    Decl decl;

    public Decl getDecl() {
        return decl;
    }

    @Override
    public Decl parse() throws IOException{
        if (Parser.currentSymbol().getTokenType() == Token.TokenType.CONSTTK) {
            // ConstDecl
            decl =  new ConstDecl().parse();
        } else {
            // VarDecl
            decl = new VarDecl().parse();
        } // 不考虑其他错误
        return this;
    }

    @Override
    public String outputString() {
        return decl.outputString();
    }
}
