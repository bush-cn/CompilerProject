package midend.llvm;

import frontend.parser.statement.Block;
import midend.Visitor;
import midend.llvm.instructions.BrCondInst;
import midend.llvm.instructions.BrInst;
import midend.llvm.instructions.RetInst;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BasicBlock extends Value {
    public Slot label;  // 占一个slot
    public List<Instruction> instructions = new ArrayList<>();
    public Set<BasicBlock> preds = new HashSet<>(); // 前驱
    public Set<BasicBlock> succs = new HashSet<>(); // 后继
    public void linkTo(BasicBlock block) {
        succs.add(block);
        block.preds.add(this);
    }
    /*
    后端代码生成新增：数据流分析
     */
    public Set<Slot> liveIn = new HashSet<>();
    public Set<Slot> liveOut = new HashSet<>();
    public Set<Slot> reachIn = new HashSet<>();
    public Set<Slot> reachOut = new HashSet<>();
    public Set<Slot> use = new HashSet<>();
    public Set<Slot> def = new HashSet<>();

    public void addInst(Instruction instruction) {
        if (!instructions.isEmpty()) {
            Instruction i = instructions.get(instructions.size() - 1);
            if (i instanceof RetInst || i instanceof BrInst || i instanceof BrCondInst) {
                // 如果上一条指令为ret指令或br指令，则需新增一个基本块
                // return、break、continue
                BasicBlock newBlock = new BasicBlock(Visitor.getInstance().curFunction);
                Visitor.getInstance().checkoutBlock(newBlock);
                newBlock.addInst(instruction);
                return;
            }
        }
        instructions.add(instruction);
    }

    public BasicBlock(Function scope) {
        label = new Slot(); // 不能直接加入scope，需要进入基本块后再加入
        scope.addBasicBlock(this);
    }

    public BasicBlock() {
        label = new Slot();
    }

    @Override
    public String toText() {
        StringBuilder sb = new StringBuilder();
        sb.append(label.slotId)
                .append(":\t\t\t\t\t\t\t\t ; preds =");
        for (BasicBlock pred: preds) {
            sb.append(" ")
                    .append(pred.label.slotId);
        }
        for (Instruction instruction: instructions) {
            sb.append("\n\t")
                    .append(instruction.toText());
        }
        return sb.toString();
    }
}
