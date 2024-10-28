package frontend.parser.declaration;

import error.CompileError;
import frontend.lexer.Token;
import frontend.parser.Parser;
import frontend.parser.SyntaxNode;
import frontend.parser.statement.BlockItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VarDecl extends Decl implements SyntaxNode<BlockItem> {
    Btype btype;
    List<VarDef> varDefs = new ArrayList<>();

    boolean hasSemicolon;

    public Btype getBtype() {
        return btype;
    }

    public List<VarDef> getVarDefs() {
        return varDefs;
    }

    @Override
    public VarDecl parse() throws IOException {
        btype = new Btype().parse();


        Parser.getSymbol();
        varDefs.add(new VarDef().parse());
        int line = Parser.currentLine(); // 若缺少分号，报错行号

        while (true) {
            if (Parser.preReadNext().getTokenType() == Token.TokenType.COMMA) {
                // 重复VarDef
                Parser.getSymbol(); // 吃掉这一逗号
                Parser.getSymbol();
                varDefs.add(new VarDef().parse());
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

        sb.append(btype.outputString());

        sb.append('\n');
        sb.append(varDefs.get(0).outputString());

        for (int i = 1; i < varDefs.size(); i++) {
            sb.append('\n');
            sb.append("COMMA ,");
            sb.append('\n');
            sb.append(varDefs.get(i).outputString());
        }

        if (hasSemicolon) {
            sb.append('\n');
            sb.append("SEMICN ;");
        }

        sb.append('\n');
        sb.append("<VarDecl>");

        return sb.toString();
    }
}
