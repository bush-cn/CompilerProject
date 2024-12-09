package midend.llvm;

import java.util.HashSet;
import java.util.Set;

public abstract class Instruction extends User {
    protected String comment;
    public Instruction setComment(String comment) {
        this.comment = comment;
        return this;
    }
    /*
    后端代码生成新增：活跃变量分析
     */
    public Set<Slot> use = new HashSet<>();
    public Set<Slot> def = new HashSet<>();
    public Set<Slot> liveIn = new HashSet<>();
    public Set<Slot> liveOut = new HashSet<>();
    public Set<Slot> reachIn = new HashSet<>();
    public Set<Slot> reachOut = new HashSet<>();
}
