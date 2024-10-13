package frontend.parser;

import error.CompileError;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 语法树的节点
 */
public interface SyntaxNode<T> {
    public T parse() throws IOException;

    public String outputString();
}
