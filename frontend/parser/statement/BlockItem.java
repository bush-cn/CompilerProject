package frontend.parser.statement;

import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;
import frontend.parser.declaration.Decl;

import java.io.IOException;

public class BlockItem implements SyntaxNode<BlockItem> {

    @Override
    public BlockItem parse() throws IOException {
        if (Parser.currentSymbol().getTokenType() == Token.TokenType.INTTK
        || Parser.currentSymbol().getTokenType() == Token.TokenType.CHARTK
        || Parser.currentSymbol().getTokenType() == Token.TokenType.CONSTTK) {
            // Decl
            return new Decl().parse();
        } else {
            return new Stmt().parse();
        }
    }

    @Override
    public String outputString() {
        // 不输出<BlockItem>
        // 永远不会调用此输出因为在子类中已重写
        return "Should Never Output This Item.";
    }
}
