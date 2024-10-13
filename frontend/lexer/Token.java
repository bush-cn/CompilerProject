package frontend.lexer;

public class Token {
    public enum TokenType {
        IDENFR,
        INTCON,
        STRCON,
        CHRCON,
        MAINTK,
        CONSTTK,
        INTTK,
        CHARTK,
        BREAKTK,
        CONTINUETK,
        IFTK,
        ELSETK,
        NOT,
        AND,
        OR,
        FORTK,
        GETINTTK,
        GETCHARTK,
        PRINTFTK,
        RETURNTK,
        PLUS,
        MINU,
        VOIDTK,
        MULT,
        DIV,
        MOD,
        LSS,
        LEQ,
        GRE,
        GEQ,
        EQL,
        NEQ,
        ASSIGN,
        SEMICN,
        COMMA,
        LPARENT,
        RPARENT,
        LBRACK,
        RBRACK,
        LBRACE,
        RBRACE,
        EOF
        // 表示读到tokenList的末尾并且再无token，
        // 仅用于语法分析时不触发NullPointerException，不出现在tokenList中
    }

    public static Token EOF = new Token(TokenType.EOF, "", 0);

    TokenType tokenType;
    String value;

    // 语法分析新加：token最后一个非终结符所在行数
    // 从一遍变成多遍，因此Token必须包含行信息 （若以后需要输出列信息可再新加）
    int line;

    public Token(TokenType tokenType, String value, int line) {
        this.tokenType = tokenType;
        this.value = value;
        this.line = line;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public String getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }

    @Override
    public String toString() {
        return value;
    }
}
