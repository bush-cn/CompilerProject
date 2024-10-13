import error.CompileError;
import frontend.lexer.Lexer;
import frontend.lexer.Token;
import frontend.parser.CompUnit;
import frontend.parser.Parser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Compiler {
    static Parser parser = Parser.getInstance();

    public static void main(String[] args) {
        try (BufferedReader br =
                     new BufferedReader(new FileReader("testfile.txt"));
                BufferedWriter bw =
                     new BufferedWriter(new FileWriter("parser.txt"));
                BufferedWriter bwErr =
                     new BufferedWriter(new FileWriter("error.txt"))){
            CompUnit compUnit = parser.parse(br);

            // 输出结果
            bw.write(compUnit.outputString());
            bwErr.write(CompileError.outputString());

        } catch (IOException e) {
            System.out.println("IOException: file operation failed");
        }
    }
}
