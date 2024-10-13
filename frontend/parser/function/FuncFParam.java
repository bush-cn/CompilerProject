package frontend.parser.function;

import error.CompileError;
import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;
import frontend.parser.declaration.Btype;
import frontend.parser.terminal.Ident;

import java.io.IOException;

public class FuncFParam implements SyntaxNode<FuncFParam> {
    Btype btype;
    Ident ident;

    boolean isArray; // 有中括号则为数组，否则不是
    boolean hasRBracket;

    @Override
    public FuncFParam parse() throws IOException {
        btype = new Btype().parse();

        Parser.getSymbol();
        ident = new Ident().parse();

        if (Parser.preReadNext().getTokenType() == Token.TokenType.LBRACK) {
            Parser.getSymbol(); // 吃掉
            int line = Parser.currentLine();
            isArray = true;
            if (Parser.preReadNext().getTokenType() == Token.TokenType.RBRACK) {
                hasRBracket = true;
                Parser.getSymbol();
            } else {
                hasRBracket = false;
                CompileError.raiseError(line, CompileError.ErrorType.k);
            }
        } else {
            isArray = false;
        }

        return this;
    }

    @Override
    public String outputString() {
        StringBuilder sb =  new StringBuilder();

        sb.append(btype.outputString());

        sb.append('\n');
        sb.append(ident.outputString());

        if (isArray) {
            sb.append('\n');
            sb.append("LBRACK [");
            if (hasRBracket) {
                sb.append('\n');
                sb.append("RBRACK ]");
            }
        }

        sb.append('\n');
        sb.append("<FuncFParam>");

        return sb.toString();
    }
}
