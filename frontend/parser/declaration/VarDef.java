package frontend.parser.declaration;

import error.CompileError;
import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;
import frontend.parser.expression.ConstExp;
import frontend.parser.terminal.Ident;

import java.io.IOException;

public class VarDef implements SyntaxNode<VarDef> {
    Ident ident;
    // 若为null则没有此项
    ConstExp constExp = null;
    // 若为null则没有此项
    InitVal initVal = null;

    boolean hasRBracket;

    @Override
    public VarDef parse() throws IOException {
        ident = new Ident().parse();

        // 类似于ConstDef，但是要注意后面不是一定跟等号，需要预读
        if (Parser.preReadNext().getTokenType() == Token.TokenType.LBRACK) {
            Parser.getSymbol(); // 吃掉此左中括号
            Parser.getSymbol();
            constExp = new ConstExp().parse();
            int line = Parser.currentLine();
            if (Parser.preReadNext().getTokenType() == Token.TokenType.RBRACK) {
                // 有右中括号
                hasRBracket = true;
                Parser.getSymbol();
            } else {
                hasRBracket = false;
                CompileError.raiseError(line, CompileError.ErrorType.k);
            }
        }

        if (Parser.preReadNext().getTokenType() == Token.TokenType.ASSIGN) {
            Parser.getSymbol(); // 吃掉此等号
            Parser.getSymbol();
            initVal = new InitVal().parse();
        }

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

        if (initVal != null) {
            sb.append('\n');
            sb.append("ASSIGN =");
            sb.append('\n');
            sb.append(initVal.outputString());
        }

        sb.append('\n');
        sb.append("<VarDef>");

        return sb.toString();
    }
}
