# 参考编译器介绍

主要参考了教材上PL/0以及tolang（但tolang没有直接说明完整的体系结构）

## 总体结构

PL/0是一个编译-解释执行程序，总体结构分为两个部分：

1. 先把PL/0编译成目标程序（P-code指令）
2. 再对目标程序进行解释执行，得到运行结果

## 接口设计

PL/0编译程序采用一遍扫描，以语法分析为核心，由它调用词法分析程序取单词，在语法分析过程中同时进行语义分析处理，并生成目标指令。

如遇有语法、语义错误，则随时调用出错处理程序，打印出错信息。

## 文件组织

![image-20241009204436442](C:\Users\吴自强\AppData\Roaming\Typora\typora-user-images\image-20241009204436442.png)

# 编译器总体设计

## 总体结构

总体结构与PL/0不同，是编译程序。

1. 先编译成LLVM IR

## 接口设计

## 文件组织



# 词法分析设计

## 编码前的设计

主要参考的是教材上的PL/0，以及第三章的词法分析示例。



代码大致流程图如上，编码时，主要依靠这张图，其他细节在编码时实现。

`Compiler.java`这一主程序调用`frontedn/Lexer.java`中的`getSymbol`方法。在此次作业中主逻辑还未实现，因此可以在`Compiler.java`中暂时编写满足本次作业的测试逻辑。

## 编码完成后的修改

### 设计模式的选择、类的设计

- 词法分析器`Lexer.java`采用单例模式（饿汉式）的设计模式，作为一个静态类存放到主类`Compiler.java`中，其方法也多是静态方法，最主要的是`getToken`方法；
  ![image-20240924220504532](C:\Users\吴自强\AppData\Roaming\Typora\typora-user-images\image-20240924220504532.png)
- 单词token作为一个类`Token`，类型作为其内部枚举类`TokenType`；
- 编译错误的处理：`CompilerError`是一个`Exception`的子类，在`Lexer`等各个组件的方法可以被抛出，在主类`Compiler`中被`try-catch`处理。

### 其他更改或BUG

1. 最大的疏忽是忘记添加处理注释的代码了，应该把读入`/`符号后的逻辑单列出来。
2. 另外，只考虑到字符常量中只有一个字符的情况了，没有考虑到转义字符

# 语法分析设计

## 编码前的设计

- 与词法分析类似，使用单例模式实现`Parser.java`
- 主要采用递归下降程序实现
- 此外通过预读解决多个规则匹配问题

| 非终结符     | FIRST集                                          | 完成 |
| ------------ | ------------------------------------------------ | ---- |
| CompUnit     | 'const', 'int', 'char', 'void'                   | V    |
| Decl         | 'const', 'int', 'char'                           | V    |
| FuncDef      | 'void', 'int', 'char'                            | V    |
| MainFuncDef  | 'int'                                            | V    |
| ConstDecl    | 'const'                                          | V    |
| VarDecl      | 'int', 'char'                                    | V    |
| FuncType     | 'void', 'int', 'char'                            | V    |
| BType        | INTTK, CHARTK                                    | V    |
| ConstDef     |                                                  | V    |
| Ident        | Vt                                               | V    |
| ConstInitVal |                                                  | V    |
| ConstExp     |                                                  |      |
| StringConst  | Vt                                               | V    |
| AddExp       |                                                  | V    |
| MulExp       |                                                  | V    |
| UnaryExp     | LPARENT, IDENFR, INTCON, CHRCON, PLUS, MINU, NOT | V    |
| PrimaryExp   | LPARENT, IDENFR, INTCON, CHRCON                  | V    |
| FuncRParams  |                                                  | V    |
| UnaryOp      | PLUS, MINU, NOT                                  | V    |
| LVal         | IDENFR                                           | V    |
| Number       | INTCON                                           | V    |
| Character    | CHRCON                                           | V    |
| IntConst     | Vt                                               | V    |
| CharConst    | Vt                                               | V    |
| VarDef       |                                                  | V    |
| InitVal      |                                                  | V    |
| Exp          | LPARENT, IDENFR, INTCON, CHRCON, PLUS, MINU, NOT | V    |
| FuncFParams  | INTTK, CHARTK                                    | V    |
| FuncFParam   | INTTK, CHARTK                                    | V    |
| Block        | LPARENT                                          | V    |
| BlockItem    |                                                  | V    |
| Stmt         |                                                  | V    |
| ForStmt      |                                                  | V    |
| Cond         |                                                  | V    |
| LOrExp       |                                                  | V    |
| LAndExp      |                                                  | V    |
| EqExp        |                                                  | V    |
| RelExp       |                                                  | V    |

## 编码后的设计

- 递归下降读取逻辑：

  - 在一个程序要调用一个分析子程序前，需要使用`Parser.getSymbol()`读取一个token
  - 在分析子程序里，分析子程序会递归匹配所有token，但并不会超前读取（例如分析子程序只匹配一个终结符即结束，则不会调用`Parser.getSymbol()`，因此一个子成分分析完后，`symbol`停留在最后匹配到的那个token处
  - 接着继续使用`Parser.getSymbol()`读取一个token并调用剩下的分析子程序
  - 在遇到终结符时，匹配后不需要使用`Parser.getSymbol()`，因为递归过程相当于在匹配之前已经执行一次

- 在之前的词法分析设计时，错误通过throw抛出自定义的`CompileError`处理并在上层解决，但在此次编码时发现：

  - 语法分析与词法分析不同，词法分析是顺序执行，可以使用throw抛出异常，在顶层记录错误并继续执行 
  - 而语法分析是递归调用的，不能够被中断，因此把错误暂存而不是抛出

  因此，修改处理逻辑，将所有错误先暂存并直接处理，最后一起输出。


# 错误处理设计

# 代码生成设计

# 代码优化设计

