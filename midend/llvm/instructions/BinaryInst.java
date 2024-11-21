package midend.llvm.instructions;

import midend.llvm.Instruction;
import midend.llvm.Value;
import midend.llvm.types.Type;

public class BinaryInst extends Instruction {
    public Value result;
    public BinaryOp binaryOp;
    public Type type;
    public Value op1;
    public Value op2;

    @Override
    public String toText() {
        return result.toText() + " = " + binaryOp
                + " " + type + " "
                + op1.toText() + ", " + op2.toText();
    }

    public BinaryInst(Value result, BinaryOp binaryOp, Type type, Value op1, Value op2) {
        this.result = result;
        this.binaryOp = binaryOp;
        this.type = type;
        this.op1 = op1;
        this.op2 = op2;
    }

    public enum BinaryOp {
        add("add"),
        sub("sub"),
        mul("mul"),
        sdiv("sdiv"),
        srem("srem"),
        and("and"),
        or("or"),
        xor("xor"),     // 用于! 取反
        eq("icmp eq"),
        ne("icmp ne"),
        sgt("icmp sgt"),
        sge("icmp sge"),
        slt("icmp slt"),
        sle("icmp sle");

        private final String str;
        BinaryOp(String str) {
            this.str = str;
        }

        @Override
        public String toString() {
            return str;
        }
    }
}
