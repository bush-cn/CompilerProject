package error;

public class CompileError extends Exception{
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

    public ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }
}
