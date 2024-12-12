package midend.llvm;

import midend.llvm.types.Type;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Function extends GlobalValue {
    Type retType;
    public List<Param> params = new ArrayList<>(); // 形参
    public List<BasicBlock> basicBlocks = new ArrayList<>();
    public void addBasicBlock(BasicBlock block) {
        basicBlocks.add(block);
    }

    public Set<Slot> interBlockLive = new HashSet<>();  // 函数内部的跨块活跃变量集合

    /**
     * 一个函数作用域中，需要分配虚拟寄存器的Value
     *      1. 每个临时寄存器和基本块占用一个编号，
     *      所以没有参数的函数的第一个临时寄存器的编号应该从 1 开始，因为函数体入口占用了一个编号 0。
     *      2. 而有参数的函数，参数编号从 0 开始，进入 Block 后需要跳过一个基本块入口的编号
     */
    public List<Slot> slots = new ArrayList<>();  // 包括指令里出现的slot和参数，以及函数体入口this（但不输出）

    // TODO: 在优化中，对slots增删后，需要重新编号
    private int slotTracker = 0;
    public void addSlot(Slot slot) {
        slots.add(slot);
        slot.slotId = slotTracker++;    // 在加入的时候分配编号
    }

    /**
     * 构造函数（形参和自身slot在定义时再插入）
     * @param name
     * @param retType
     */
    public Function(String name, Type retType) {
        this.name = name;
        this.retType = retType;
    }

    @Override
    public String globalText() {
        StringBuilder sb = new StringBuilder();
        sb.append("define dso_local ")
                .append(retType)
                .append(" @")
                .append(name)
                .append("(");

        for (int i = 0; i < params.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(params.get(i).type)
                    .append(" ")
                    .append(params.get(i).value.toText());
        }
        sb.append(") {\n");

        for (BasicBlock block: basicBlocks) {
            sb.append(block.toText())
                    .append("\n");
        }

        sb.append("}");

        return sb.toString();
    }

    @Override
    public String toText() {
        return "@" + name;
    }

    public static class Param {
        public Type type;
        public Value value; // 形参，因此都是Slot

        public Param(Type type, Value value) {
            this.type = type;
            this.value = value;
        }
    }
}
