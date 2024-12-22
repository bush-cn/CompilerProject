package error;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CompileError extends Exception{
    // 在语法分析中，不能通过抛出处理，因此把错误都暂存到这里，最后统一输出
    public static List<CompileError> errors = new ArrayList<>();

    /**
     * 判断是否有语法错误
     * 【若有语法错误则不进行代码生成】
     * @return 是否有语法错误
     */
    public static boolean hasSemanticError() {
        for (CompileError error: errors) {
            if (!(error.errorType == ErrorType.a ||
                    error.errorType == ErrorType.i ||
                    error.errorType == ErrorType.j ||
                    error.errorType == ErrorType.k)) {
                // a: 词法错误
                // i, j, k: 语法错误
                // 其他错误都是语义错误
                return true;
            }
        }
        return false;
    }

    public static String outputString() {
        errors.sort(Comparator.comparingInt(o -> o.line));
        StringBuilder sb = new StringBuilder();
        for (CompileError error : errors) {
            sb.append(error.line)
                    .append(" ")
                    .append(error.errorType.toString())
                    .append('\n');
        }
        return sb.toString();
    }

    public static void raiseError(int line, ErrorType errorType) {
        errors.add(new CompileError(line, errorType));
    }

    // 在Stmt中分析LVal时，进行回溯时CompileError也需要一并回溯
    public static int mark() {
        return errors.size();
    }

    public static void reset(int size) {
        while (errors.size() > size) {
            errors.remove(errors.size() - 1);
        }
    }

    public int line;

    public enum ErrorType {
        a, b, c, d, e, f, g, h, i, j, k, l, m
    }

    public ErrorType errorType;

    public String errorInfo;

    public CompileError(int line, ErrorType errorType) {
        this.line = line;
        this.errorType = errorType;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }
}
