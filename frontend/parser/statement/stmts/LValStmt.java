package frontend.parser.statement.stmts;

import error.CompileError;
import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;
import frontend.parser.expression.Exp;
import frontend.parser.statement.BlockItem;
import frontend.parser.expression.unaryexps.primaryexps.LVal;
import frontend.parser.statement.Stmt;

import java.io.IOException;

/**
 * 此子类FIRST集为<IDENFR>，包含赋值语句、getint语句和getchar语句
 */
public class LValStmt extends Stmt implements SyntaxNode<BlockItem> {
    boolean isAssignment; // 若为true则是赋值语句，否则是getint或getchar语句
    LVal lVal; // 两者公用

    int lValLine;// 语义分析新增
    public int getLValLine() {
        return lValLine;
    }

    Exp assgnExp; // 赋值用
    boolean assignHasSemicolon; // 赋值用

    Token getIntOrGetChar; // 输入用，getint或getchar二选一

    boolean getHasRParenthesis; // 输入用

    boolean getHasSemicolon; // 输入用

    public boolean isAssignment() {
        return isAssignment;
    }

    public LVal getlVal() {
        return lVal;
    }

    public Exp getAssgnExp() {
        return assgnExp;
    }

    public Token getGetIntOrGetChar() {
        return getIntOrGetChar;
    }

    @Override
    public Stmt parse() throws IOException {
        lVal = new LVal().parse();
        lValLine = Parser.currentLine();

        Parser.getSymbol();
        assert Parser.currentSymbol().getTokenType() == Token.TokenType.ASSIGN;

        Parser.getSymbol();

        if (Parser.currentSymbol().getTokenType() != Token.TokenType.GETINTTK
        && Parser.currentSymbol().getTokenType() != Token.TokenType.GETCHARTK) {
            // 赋值语句
            isAssignment = true;
            assgnExp = new Exp().parse();
            int line = Parser.currentLine();

            if (Parser.preReadNext().getTokenType() == Token.TokenType.SEMICN) {
                assignHasSemicolon = true;
                Parser.getSymbol();
            } else {
                assignHasSemicolon = false;
                CompileError.raiseError(line, CompileError.ErrorType.i);
            }
        } else {
            // getint或getchar语句
            isAssignment = false;
            getIntOrGetChar = Parser.currentSymbol();
            assert getIntOrGetChar.getTokenType() == Token.TokenType.GETINTTK ||
                    getIntOrGetChar.getTokenType() == Token.TokenType.GETCHARTK;

            Parser.getSymbol();
            assert Parser.currentSymbol().getTokenType() == Token.TokenType.LPARENT;
            int line = Parser.currentLine();

            if (Parser.preReadNext().getTokenType() == Token.TokenType.RPARENT) {
                getHasRParenthesis = true;
                Parser.getSymbol(); // 吃掉
                line = Parser.currentLine();
            } else {
                getHasRParenthesis = false;
                CompileError.raiseError(line, CompileError.ErrorType.j);
            }
            line = Parser.currentLine();

            if (Parser.preReadNext().getTokenType() == Token.TokenType.SEMICN) {
                getHasSemicolon = true;
                Parser.getSymbol();
            } else {
                getHasSemicolon = false;
                CompileError.raiseError(line, CompileError.ErrorType.i);
            }
        }

        return this;
    }

    @Override
    public String outputString() {
        StringBuilder sb = new StringBuilder();

        sb.append(lVal.outputString());

        sb.append("\nASSIGN =\n");

        if (isAssignment) {
            sb.append(assgnExp.outputString());

            if (assignHasSemicolon) {
                sb.append("\nSEMICN ;");
            }
        }
        else {
            sb.append(getIntOrGetChar.getTokenType().toString())
                    .append(" ")
                    .append(getIntOrGetChar.getValue())
                    .append("\nLPARENT (");

            if (getHasRParenthesis) {
                sb.append("\nRPARENT )");
            }

            if (getHasSemicolon) {
                sb.append("\nSEMICN ;");
            }
        }

        return sb.toString() + "\n<Stmt>";
    }
}
