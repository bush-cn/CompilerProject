package frontend.parser.function;

import error.CompileError;
import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;
import frontend.parser.statement.Block;

import java.io.IOException;

public class MainFuncDef implements SyntaxNode<MainFuncDef> {
    Block block;

    boolean hasRParenthesis;

    public Block getBlock() {
        return block;
    }

    @Override
    public MainFuncDef parse() throws IOException {
        assert Parser.currentSymbol().getTokenType() == Token.TokenType.INTTK;

        Parser.getSymbol();
        assert Parser.currentSymbol().getTokenType() == Token.TokenType.MAINTK;

        Parser.getSymbol();
        assert Parser.currentSymbol().getTokenType() == Token.TokenType.LPARENT;

        Parser.getSymbol();
        int line = Parser.currentLine();
        if (Parser.currentSymbol().getTokenType() == Token.TokenType.RPARENT) {
            hasRParenthesis = true;
            Parser.getSymbol();
        } else {
            hasRParenthesis = false;
            CompileError.raiseError(line, CompileError.ErrorType.j);
        }

        block = new Block().parse();

        return this;
    }

    @Override
    public String outputString() {
        StringBuilder sb = new StringBuilder();
        sb.append("INTTK int\nMAINTK main\nLPARENT (\n");

        if (hasRParenthesis) {
            sb.append("RPARENT )\n");
        }

        sb.append(block.outputString());

        sb.append("\n<MainFuncDef>");

        return sb.toString();
    }
}
