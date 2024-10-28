package frontend.parser.declaration;

import error.CompileError;
import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;
import frontend.parser.expression.ConstExp;
import frontend.parser.terminal.Ident;

import java.io.IOException;

public class ConstDef implements SyntaxNode<ConstDef> {
    Ident ident;
    // 若为null则没有此项
    ConstExp constExp = null;
    ConstInitVal constInitVal;

    boolean hasRBracket;

    public Ident getIdent() {
        return ident;
    }

    public ConstExp getConstExp() {
        return constExp;
    }

    public ConstInitVal getConstInitVal() {
        return constInitVal;
    }

    @Override
    public ConstDef parse() throws IOException {
        ident = new Ident().parse();

        Parser.getSymbol();
        if (Parser.currentSymbol().getTokenType() == Token.TokenType.LBRACK) {
            Parser.getSymbol();
            constExp = new ConstExp().parse();
            int line = Parser.currentLine();
            Parser.getSymbol();
            if (Parser.currentSymbol().getTokenType() == Token.TokenType.RBRACK) {
                // 有右中括号
                hasRBracket = true;
                Parser.getSymbol();
            } else {
                hasRBracket = false;
                CompileError.raiseError(line, CompileError.ErrorType.k);
            }
        }

        assert Parser.currentSymbol().getTokenType() == Token.TokenType.ASSIGN;

        Parser.getSymbol();
        constInitVal = new ConstInitVal().parse();

        return this;
    }

    @Override
    public String outputString() {
        StringBuilder sb = new StringBuilder();

        sb.append(ident.outputString());

        if (constExp != null) {
            sb.append('\n');
            sb.append("LBRACK [");
            sb.append('\n');
            sb.append(constExp.outputString());
            if (hasRBracket) {
                sb.append('\n');
                sb.append("RBRACK ]");
            }
        }

        sb.append('\n');
        sb.append("ASSIGN =");

        sb.append('\n');
        sb.append(constInitVal.outputString());

        sb.append('\n');
        sb.append("<ConstDef>");

        return sb.toString();
    }
}
