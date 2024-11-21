package midend.llvm.globalvalues;

import midend.llvm.GlobalValue;
import midend.llvm.types.ArrayType;
import midend.llvm.types.Type;

// Hello: %d, %c
public class ConstString extends GlobalValue {
    private static int counter = 0;
    public String name;
    public String constString;
    public String escapeString;
    public int length = 0;

    public ConstString(String constString) {
        this.name = ".str." + counter++;
        this.constString = constString;
        this.escapeString = escape(constString);
    }

    @Override
    public String globalText() {
        return "@" + name + " = private unnamed_addr constant "
                + new ArrayType(length, Type.i8)
                + " c\"" + escapeString + "\", align 1";
    }





    public String escape(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && s.charAt(i+1) == 'n') {
                sb.append("\\0A");
                i++;
            } else {
                sb.append(c);
            }
            length ++;
        }
        sb.append("\\00");
        length ++;
        return sb.toString();
    }

    @Override
    public String toText() {
        return "@" + name;
    }
}
