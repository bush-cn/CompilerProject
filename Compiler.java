import error.CompileError;
import frontend.Lexer;
import frontend.Token;

import java.io.*;

public class Compiler {
    static Lexer lexer = Lexer.getInstance();

    public static void main(String[] args) {
        try (BufferedReader br =
                     new BufferedReader(new FileReader("testfile.txt"));
                BufferedWriter bw =
                     new BufferedWriter(new FileWriter("lexer.txt"));
                BufferedWriter bwErr =
                     new BufferedWriter(new FileWriter("error.txt"))){
            Token token = null;
            while (true) {
                try {
                    if ((token = lexer.getToken(br)) == null) break;
                    bw.write(token.getTokenType().toString());
                    bw.write(' ');
                    bw.write(token.getValue());
                    bw.newLine();
                } catch (CompileError error) {
                    bwErr.write(String.valueOf(error.getLine()));
                    bwErr.write(' ');
                    bwErr.write(error.getErrorType().toString());
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("IOException: file operation failed");
        }
    }

}
