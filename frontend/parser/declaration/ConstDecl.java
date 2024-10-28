package frontend.parser.declaration;

import error.CompileError;
import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;
import frontend.parser.statement.BlockItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConstDecl extends Decl implements SyntaxNode<BlockItem> {
    Btype btype;
    List<ConstDef> constDefs = new ArrayList<>();

    boolean hasSemicolon;

    public Btype getBtype() {
        return btype;
    }

    public List<ConstDef> getConstDefs() {
        return constDefs;
    }

    @Override
    public ConstDecl parse() throws IOException {
        assert Parser.currentSymbol().getTokenType() == Token.TokenType.CONSTTK;

        Parser.getSymbol();
        btype = new Btype().parse();


        Parser.getSymbol();
        constDefs.add(new ConstDef().parse());
        int line = Parser.currentLine(); // 若缺少分号，报错行号

        while (true) {
            if (Parser.preReadNext().getTokenType() == Token.TokenType.COMMA) {
                // 重复constDef
                Parser.getSymbol(); // 吃掉这一逗号
                Parser.getSymbol();
                constDefs.add(new ConstDef().parse());
                line = Parser.currentLine();
            } else if (Parser.preReadNext().getTokenType() == Token.TokenType.SEMICN) {
                // 结束且带分号
                hasSemicolon = true;
                Parser.getSymbol();
                break;
            } else {
                // 不带分号
                hasSemicolon = false;
                CompileError.raiseError(line, CompileError.ErrorType.i);
                break;
            }
        }

        return this;
    }

    @Override
    public String outputString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CONSTTK const");

        sb.append('\n');
        sb.append(btype.outputString());

        sb.append('\n');
        sb.append(constDefs.get(0).outputString());

        for (int i = 1; i < constDefs.size(); i++) {
            sb.append('\n');
            sb.append("COMMA ,");
            sb.append('\n');
            sb.append(constDefs.get(i).outputString());
        }

        if (hasSemicolon) {
            sb.append('\n');
            sb.append("SEMICN ;");
        }

        sb.append('\n');
        sb.append("<ConstDecl>");

        return sb.toString();
    }
}
