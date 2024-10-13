package frontend.lexer;

import error.CompileError;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 词法分析器（单例模式）
 */
public class Lexer {
    private Lexer() {}

    private static final Lexer lexer = new Lexer();

    public static Lexer getInstance() {
        return lexer;
    }

    BufferedReader br;

    public void setBufferedReader(BufferedReader br) {
        this.br = br;
    }

    public BufferedReader getBufferedReader() {
        return br;
    }

    int line = 1;

    public int getLine() {
        return line;
    }

    char ch;

    /**
     * 当且仅当读到文件流末尾时返回null
     * @throws IOException 文件I/O错误
     * @return br流解析到的下一个Token
     *
     */
    public Token getToken() throws IOException {
        // 跳过空白符
        do {
            if(!readChar(br)) return null;
            if(ch == '\n') line++;
        } while (Character.isWhitespace(ch));

        if (Character.isLetter(ch) || ch == '_') {
            StringBuilder sb = new StringBuilder();
            sb.append(ch);
            while(true) {
                br.mark(1);
                if (!readChar(br)) {
                    break;
                }
                if (Character.isLetterOrDigit(ch) || ch == '_') {
                    sb.append(ch);
                    continue;
                } else {
                    br.reset();
                    break;
                }
            }
            String str = sb.toString();
            // 查询字符串是否为保留字
            Token.TokenType type = reservedWords.getOrDefault(str, Token.TokenType.IDENFR);
            return new Token(type, str, line);
        }
        else if (Character.isDigit(ch)) {
            if (ch == '0') {
                // 是否需要考虑'0'之后出现其他字符，并报出词法错误？应该不需要，词法分析错误只考虑一种情况
                // 或者在'0'之后出现的字符正常继续解析，在之后的语法分析中报错。
                return new Token(Token.TokenType.INTCON, String.valueOf(0), line);
            }
            else {
                StringBuilder sb = new StringBuilder();
                sb.append(ch);
                while(true) {
                    br.mark(1);
                    if (!readChar(br)) {
                        break;
                    }
                    if (Character.isDigit(ch)) {
                        sb.append(ch);
                        continue;
                    } else {
                        br.reset();
                        break;
                    }
                }
                String str = sb.toString();
                return new Token(Token.TokenType.INTCON, str, line);
            }
        }
        else if (ch == '\'') {
            StringBuilder sb = new StringBuilder();
            sb.append(ch); // 即'\''
            // 修改：要考虑转义字符，单引号间可能有两个字符
            // 不考虑错误情况：
            // 1. 单引号后有多个字符
            // 2. 单引号后读到EOF
            readChar(br);
            if (ch != '\\') {
                // 非转义字符
                sb.append(ch);
                readChar(br);
                assert ch == '\'';
                sb.append(ch);
            } else {
                // 转义字符
                sb.append(ch); // 即'\\'
                readChar(br);
                sb.append(ch);
                readChar(br);
                assert ch == '\'';
                sb.append(ch);
            }
            return new Token(Token.TokenType.CHRCON, sb.toString(), line);
        }
        else if (ch == '\"') {
            StringBuilder sb = new StringBuilder();
            sb.append(ch);
            while(readChar(br) && ch != '\"') {
                // 不考虑字符串未结束就读到EOF的错误情况
                sb.append(ch);
            }
            sb.append(ch);
            return new Token(Token.TokenType.STRCON, sb.toString(), line);
        }
        else if (ch == '+')
            return new Token(Token.TokenType.PLUS, String.valueOf((char)ch), line);
        else if (ch == '-')
            return new Token(Token.TokenType.MINU, String.valueOf((char)ch), line);
        else if (ch == '*')
            return new Token(Token.TokenType.MULT, String.valueOf((char)ch), line);
        else if (ch == '/') {
            br.mark(1);
            if (!readChar(br)) {
                return new Token(Token.TokenType.DIV, "/", line);
            }
            if (ch == '/') {
                // 单行注释
                while (true) {
                    if (readChar(br) && ch != '\n') {
                        continue;
                    } else {
                        line++;
                        break;
                    }
                } // 即while(readChar(br) && ch != '\n')但会警告
                // 嵌套调用，未读到EOF不能返回null
                return lexer.getToken();
            }
            else if (ch == '*') {
                // 多行注释
                // 不考虑注释不闭合的情况
                while (true) {
                    if (readChar(br) && ch != '*') {
                        if (ch == '\n') line++;
                        continue;
                    } else {
                        if (readChar(br) && ch != '/') {
                            if (ch == '\n') line++;
                            continue;
                        } else {
                            break;
                        }
                    }
                }
                // 嵌套调用，未读到EOF不能返回null
                return lexer.getToken();
            }
            else {
                br.reset();
                return new Token(Token.TokenType.DIV, "/", line);
            }
        }
        else if (ch == '%')
            return new Token(Token.TokenType.MOD, String.valueOf((char)ch), line);
        else if (ch == ';')
            return new Token(Token.TokenType.SEMICN, String.valueOf((char)ch), line);
        else if (ch == ',')
            return new Token(Token.TokenType.COMMA, String.valueOf((char)ch), line);
        else if (ch == '(')
            return new Token(Token.TokenType.LPARENT, String.valueOf((char)ch), line);
        else if (ch == ')')
            return new Token(Token.TokenType.RPARENT, String.valueOf((char)ch), line);
        else if (ch == '[')
            return new Token(Token.TokenType.LBRACK, String.valueOf((char)ch), line);
        else if (ch == ']')
            return new Token(Token.TokenType.RBRACK, String.valueOf((char)ch), line);
        else if (ch == '{')
            return new Token(Token.TokenType.LBRACE, String.valueOf((char)ch), line);
        else if (ch == '}')
            return new Token(Token.TokenType.RBRACE, String.valueOf((char)ch), line);
        else if (ch == '&') {
            br.mark(1);
            if (!readChar(br)) {
                CompileError.raiseError(line, CompileError.ErrorType.a);
                return new Token(Token.TokenType.AND, "&", line);
            }
            else if (ch != '&') {
                br.reset();
                CompileError.raiseError(line, CompileError.ErrorType.a);
                return new Token(Token.TokenType.AND, "&", line);
            }
            else {
                return new Token(Token.TokenType.AND, "&&", line);
            }
        }
        else if (ch == '|') {
            br.mark(1);
            if (!readChar(br)) {
                CompileError.raiseError(line, CompileError.ErrorType.a);
                return new Token(Token.TokenType.OR, "|", line);
            }
            else if (ch != '|') {
                br.reset();
                CompileError.raiseError(line, CompileError.ErrorType.a);
                return new Token(Token.TokenType.OR, "|", line);
            }
            else {
                return new Token(Token.TokenType.OR, "||", line);
            }
        }
        else if (ch == '=') {
            br.mark(1);
            if (!readChar(br)) {
                return new Token(Token.TokenType.ASSIGN, "=", line);
            }
            else if (ch != '=') {
                br.reset();
                return new Token(Token.TokenType.ASSIGN, "=", line);
            }
            else {
                return new Token(Token.TokenType.EQL, "==", line);
            }
        }
        else if (ch == '>') {
            br.mark(1);
            if (!readChar(br)) {
                return new Token(Token.TokenType.GRE, ">", line);
            }
            else if (ch != '=') {
                br.reset();
                return new Token(Token.TokenType.GRE, ">", line);
            }
            else {
                return new Token(Token.TokenType.GEQ, ">=", line);
            }
        }
        else if (ch == '<') {
            br.mark(1);
            if (!readChar(br)) {
                return new Token(Token.TokenType.LSS, "<", line);
            }
            else if (ch != '=') {
                br.reset();
                return new Token(Token.TokenType.LSS, "<", line);
            }
            else {
                return new Token(Token.TokenType.LEQ, "<=", line);
            }
        }
        else if (ch == '!') {
            br.mark(1);
            if (!readChar(br)) {
                return new Token(Token.TokenType.NOT, "!", line);
            }
            else if (ch != '=') {
                br.reset();
                return new Token(Token.TokenType.NOT, "!", line);
            }
            else {
                return new Token(Token.TokenType.NEQ, "!=", line);
            }
        }
        // 不考虑其他错误情况
        // else throw new CompileError(line, CompileError.ErrorType.e);
        return null;
    }

    // 若读到文件末尾，返回false，否则返回true
    private boolean readChar(BufferedReader br) throws IOException {
        int read = br.read();
        if (read == -1) {
            return false;
        }
        else {
            ch = (char)read;
            return true;
        }
    }

    private static final Map<String, Token.TokenType> reservedWords = new HashMap<>() {{
        put("main", Token.TokenType.MAINTK);
        put("const", Token.TokenType.CONSTTK);
        put("int", Token.TokenType.INTTK);
        put("char", Token.TokenType.CHARTK);
        put("break", Token.TokenType.BREAKTK);
        put("continue", Token.TokenType.CONTINUETK);
        put("if", Token.TokenType.IFTK);
        put("else", Token.TokenType.ELSETK);
        put("for", Token.TokenType.FORTK);
        put("getint", Token.TokenType.GETINTTK);
        put("getchar", Token.TokenType.GETCHARTK);
        put("printf", Token.TokenType.PRINTFTK);
        put("return", Token.TokenType.RETURNTK);
        put("void", Token.TokenType.VOIDTK);
    }};

}
