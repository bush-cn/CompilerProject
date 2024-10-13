package frontend.parser;

import error.CompileError;
import frontend.lexer.Lexer;
import frontend.lexer.Token;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 语法分析器
 */
public class Parser {
    private Parser() {}

    private static final Parser parser = new Parser();

    public static Parser getInstance() {
        return parser;
    }

    private final Lexer lexer = Lexer.getInstance();

    // 用于i, j, k错误时返回前一个token所在行号
    public static int preTokenLine() {
        if (parser.symbolIndex > 1) {
            return tokenList.get(parser.symbolIndex - 2).getLine();
        } else {
            return 0; // 实际上不会发生
        }
    }

    public static int currentLine() {
        return parser.symbol.getLine();
    }

    // Token序列
    public static final List<Token> tokenList = new ArrayList<>();

    public void runLexer(BufferedReader br) throws IOException {
        lexer.setBufferedReader(br);
        Token token;
        while ((token = lexer.getToken()) != null) {
            tokenList.add(token);
        }
    }

    // 下一个要读取的符号token索引
    private int symbolIndex = 0;

    // 当前读取的符号
    public Token symbol = null;

    // 读入一个符号到symbol，且索引+1
    public static void getSymbol() {
        if (parser.symbolIndex < tokenList.size()) {
            parser.symbol = tokenList.get(parser.symbolIndex);
            parser.symbolIndex++;
        } else {
            parser.symbol = Token.EOF;
        }
    }

    public static Token currentSymbol() {
        return parser.symbol;
    }

    // 返回预读的下一个符号
    public static Token preReadNext() {
        if (parser.symbolIndex < tokenList.size()) {
            return tokenList.get(parser.symbolIndex);
        } else {
            return Token.EOF;
        }
    }

    // 返回预读的下下个符号
    public static Token preReadNextNext() {
        if ((parser.symbolIndex + 1) < tokenList.size()) {
            return tokenList.get(parser.symbolIndex + 1);
        } else {
            return Token.EOF;
        }
    }

    // 回溯功能，在Stmt中判断是ExpStmt还是LValStmt时用
    // 返回当前的symbolIndex
    public static int markIndex() {
        return parser.symbolIndex;
    }

    public static void reset(int markIndex) {
        parser.symbolIndex = markIndex;
        parser.symbol = tokenList.get(parser.symbolIndex - 1);
    }

    public CompUnit parse(BufferedReader br) throws IOException {
        runLexer(br);
        getSymbol();
        return new CompUnit().parse();
    }
}
