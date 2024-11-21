package midend.llvm;

import java.util.ArrayList;
import java.util.List;

public class Module {
    public List<GlobalValue> globalValues = new ArrayList<>();

    public void addGlobalValue(GlobalValue globalValue) {
        globalValues.add(globalValue);
    }

    public String toText() {
        StringBuilder sb = new StringBuilder();

        sb.append("""
                declare i32 @getint()
                declare i32 @getchar()
                declare void @putint(i32)
                declare void @putch(i32)
                declare void @putstr(i8*)
                
                """);

        for (GlobalValue globalValue: globalValues) {
            if (globalValue instanceof Function) {
                sb.append('\n');
            }
            sb.append(globalValue.globalText())
                    .append("\n");
            if (globalValue instanceof Function) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }
}
