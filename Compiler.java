import backend.MIPSCode;
import backend.Translator;
import error.CompileError;
import frontend.lexer.Lexer;
import frontend.lexer.Token;
import frontend.parser.CompUnit;
import frontend.parser.Parser;
import midend.Symbol;
import midend.SymbolTable;
import midend.Visitor;
import midend.llvm.Module;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Compiler {
    static Lexer lexer = Lexer.getInstance();
    static Parser parser = Parser.getInstance();
    static Visitor visitor = Visitor.getInstance();

    static Translator translator = Translator.getInstance();

    public static void main(String[] args) {
        try (BufferedReader br =
                     new BufferedReader(new FileReader("testfile.txt"));
                BufferedWriter bw =
                     new BufferedWriter(new FileWriter("llvm_ir.txt"));
                BufferedWriter bwMIPS =
                     new BufferedWriter(new FileWriter("mips.txt"));
                BufferedWriter bwErr =
                     new BufferedWriter(new FileWriter("error.txt"))){
            List<Token> tokenList = lexer.lex(br);              // 词法分析
            CompUnit compUnit = parser.parse(tokenList);        // 语法分析
            Module module = visitor.visitCompUnit(compUnit);    // 语义分析、中间代码生成
            // 输出结果
            bw.write(module.toText());          // 输出LLVM IR
            MIPSCode mipsCode = translator.translate(module);   // 目标代码生成
            bwMIPS.write(mipsCode.toString());    // 输出MIPS
            bwErr.write(CompileError.outputString());
        } catch (IOException e) {
            System.out.println("IOException: file operation failed");
        }
    }

    public static void outputSymbol(SymbolTable table, BufferedWriter bw) throws IOException {
        for (Symbol symbol: table.symbols) {
            bw.write(table.scopeId + " " + symbol.name + " " + symbol.type.toString());
            bw.newLine();
        }
        for (SymbolTable child: table.childrenTables) {
            outputSymbol(child, bw);
        }
    }
}
