package frontend.parser.statement;

import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;
import frontend.parser.declaration.Decl;

import java.io.IOException;

public class BlockItem implements SyntaxNode<BlockItem> {
    BlockItem blockItem; // Decl或Stmt，且Decl和Stmt均为BlockItem子类
    @Override
    public BlockItem parse() throws IOException {
        if (Parser.currentSymbol().getTokenType() == Token.TokenType.INTTK
        || Parser.currentSymbol().getTokenType() == Token.TokenType.CHARTK
        || Parser.currentSymbol().getTokenType() == Token.TokenType.CONSTTK) {
            // Decl
            blockItem = new Decl().parse();
        } else {
            blockItem = new Stmt().parse();
        }
        return this;
    }

    @Override
    public String outputString() {
        // 不输出<BlockItem>
        return blockItem.outputString();
    }
}
