package frontend.parser.declaration;

import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;
import frontend.parser.expression.ConstExp;
import frontend.parser.terminal.StringConst;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static frontend.lexer.Token.TokenType.*;

public class ConstInitVal implements SyntaxNode<ConstInitVal> {
    // 首先，若stringConst非null，则是StringConst；否则是ConstExp或其数组
    StringConst stringConst;
    List<ConstExp> constExps = new ArrayList<>();

    // 若有左大括号，则是数组；否则不是数组
    // 需要注意有可能是数组但长度为0，即constExps为空列表
    boolean isArray;

    public StringConst getStringConst() {
        return stringConst;
    }

    public List<ConstExp> getConstExps() {
        return constExps;
    }

    public boolean isArray() {
        return isArray;
    }

    @Override
    public ConstInitVal parse() throws IOException {
        if (Parser.currentSymbol().getTokenType() == Token.TokenType.STRCON) {
            stringConst = new StringConst().parse();
        } else if (Parser.currentSymbol().getTokenType() == LBRACE) {
            stringConst = null;
            // 数组
            isArray = true;
            // 不会出现缺失右大括号的错误
            while(true) {
                Parser.getSymbol();
                if (Parser.currentSymbol().getTokenType() == RBRACE) {
                    break;
                } else if (Parser.currentSymbol().getTokenType() == COMMA) {
                    continue;
                } else {
                    constExps.add(new ConstExp().parse());
                } // 不出现其他错误
            }
        } else {
            stringConst = null;
            isArray = false;
            constExps.add(new ConstExp().parse());
        }

        return this;
    }

    @Override
    public String outputString() {
        StringBuilder sb = new StringBuilder();

        if (stringConst != null) {
            sb.append(stringConst.outputString());
        } else {
            if (!isArray) {
                sb.append(constExps.get(0).outputString());
            } else {
                sb.append("LBRACE {");
                if (!constExps.isEmpty()) {
                    sb.append('\n');
                    sb.append(constExps.get(0).outputString());
                }
                for (int i = 1; i < constExps.size(); i++) {
                    sb.append('\n');
                    sb.append("COMMA ,");
                    sb.append('\n');
                    sb.append(constExps.get(i).outputString());
                }
                sb.append('\n');
                sb.append("RBRACE }");
            }
        }

        sb.append('\n');
        sb.append("<ConstInitVal>");

        return sb.toString();
    }
}
