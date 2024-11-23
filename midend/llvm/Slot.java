package midend.llvm;


import midend.Visitor;
import midend.llvm.instructions.BrCondInst;
import midend.llvm.instructions.BrInst;
import midend.llvm.instructions.RetInst;

import java.util.List;

public class Slot extends Value {
    public int slotId;

    public Slot() {}

    public Slot(Function scope) {
        if (!scope.basicBlocks.isEmpty()) {
            List<Instruction> instructions = scope.basicBlocks.get(scope.basicBlocks.size() - 1).instructions;
            if (!instructions.isEmpty()) {
                Instruction i = instructions.get(instructions.size() - 1);
                if (i instanceof RetInst || i instanceof BrInst || i instanceof BrCondInst) {
                    // 如果上一条指令为ret指令或br指令，则需新增一个基本块
                    // return、break、continue
                    BasicBlock newBlock = new BasicBlock(Visitor.getInstance().curFunction);
                    Visitor.getInstance().checkoutBlock(newBlock);
                }
            }
        }
        scope.addSlot(this);
    }

    @Override
    public String toText() {
        return "%" + slotId;
    }
}
