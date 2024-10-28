package frontend.parser.statement.stmts;

import frontend.parser.SyntaxNode;
import frontend.parser.statement.Block;
import frontend.parser.statement.BlockItem;
import frontend.parser.statement.Stmt;

import java.io.IOException;

public class BlockStmt extends Stmt implements SyntaxNode<BlockItem> {
    Block block;

    public Block getBlock() {
        return block;
    }

    @Override
    public Stmt parse() throws IOException {
        block = new Block().parse();
        return this;
    }

    @Override
    public String outputString() {
        return block.outputString();
    }
}
