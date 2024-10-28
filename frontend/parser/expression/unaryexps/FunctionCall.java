package frontend.parser.expression.unaryexps;

import error.CompileError;
import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;
import frontend.parser.expression.Exp;
import frontend.parser.expression.UnaryExp;
import frontend.parser.function.FuncFParams;
import frontend.parser.function.FuncRParams;
import frontend.parser.terminal.Ident;

import java.io.IOException;

public class FunctionCall extends UnaryExp implements SyntaxNode<UnaryExp> {
    Ident ident;
    FuncRParams funcRParams = null; // null表示不存在

    boolean hasRParenthesis;

    public Ident getIdent() {
        return ident;
    }

    public FuncRParams getFuncRParams() {
        return funcRParams;
    }

    @Override
    public UnaryExp parse() throws IOException {
        ident = new Ident().parse();

        Parser.getSymbol();
        assert Parser.currentSymbol().getTokenType() == Token.TokenType.LPARENT;
        int line = Parser.currentLine();

        if (Exp.firstSetOfExp.contains(Parser.preReadNext().getTokenType())) {
            Parser.getSymbol();
            funcRParams = new FuncRParams().parse();
            line = Parser.currentLine();
        }

        if (Parser.preReadNext().getTokenType() == Token.TokenType.RPARENT) {
            hasRParenthesis = true;
            Parser.getSymbol();
        } else {
            hasRParenthesis = false;
            CompileError.raiseError(line, CompileError.ErrorType.j);
        }

        return this;
    }

    @Override
    public String outputString() {
        StringBuilder sb = new StringBuilder();

        sb.append(ident.outputString());

        sb.append("\nLPARENT (");

        if (funcRParams != null) {
            sb.append('\n');
            sb.append(funcRParams.outputString());
        }

        if (hasRParenthesis) {
            sb.append("\nRPARENT )");
        }

        return sb.toString();
    }
}
