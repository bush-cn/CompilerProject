package midend.llvm;

import midend.Visitor;
import midend.llvm.instructions.BrCondInst;
import midend.llvm.instructions.BrInst;
import midend.llvm.instructions.RetInst;

import java.util.ArrayList;
import java.util.List;

public class BasicBlock extends Value {
    public Slot label;  // 占一个slot
    public List<Instruction> instructions = new ArrayList<>();

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
                .append(":");
        for (Instruction instruction: instructions) {
            sb.append("\n\t")
                    .append(instruction.toText());
        }
        return sb.toString();
    }
}
