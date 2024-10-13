package frontend.parser.statement;

import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Block implements SyntaxNode<Block> {
    List<BlockItem> blockItems = new ArrayList<>();

    @Override
    public Block parse() throws IOException {
        assert Parser.currentSymbol().getTokenType() == Token.TokenType.LBRACE;

        while (true) {
            Parser.getSymbol();
            if (Parser.currentSymbol().getTokenType() == Token.TokenType.RBRACE) {
                break;
            } else {
                blockItems.add(new BlockItem().parse());
            }
        }

        return this;
    }

    @Override
    public String outputString() {
        StringBuilder sb = new StringBuilder();

        sb.append("LBRACE {");

        for(BlockItem blockItem: blockItems) {
            sb.append('\n');
            sb.append(blockItem.outputString());
        }

        sb.append("\nRBRACE }\n<Block>");

        return sb.toString();
    }
}
