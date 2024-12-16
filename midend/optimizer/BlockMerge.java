package midend.optimizer;

import midend.llvm.*;
import midend.llvm.Module;
import midend.llvm.instructions.BrInst;

public class BlockMerge {
    public static void MergeBlock(Module module) {
        for (GlobalValue globalValue: module.globalValues) {
            if (globalValue instanceof Function function) {
                boolean changed = true;
                while (changed) {
                    changed = false;
                    for (int i = 0; i < function.basicBlocks.size() - 1; i++) {
                        BasicBlock pred = function.basicBlocks.get(i);
                        BasicBlock succ = function.basicBlocks.get(i + 1);
                        // 若pred的最后一条语句是跳转到succ的无条件跳转，则合并
                        if (pred.instructions.get(pred.instructions.size() - 1) instanceof BrInst brInst
                                && brInst.label == succ.label
                                && succ.preds.size() == 1 && succ.preds.contains(pred)) {
                            changed = true;
                            // 将succ的指令加入pred
                            function.removeSlot(succ.label);
                            pred.instructions.remove(brInst);
                            pred.instructions.addAll(succ.instructions);
                            // 更新pred的后继
                            pred.succs = succ.succs;
                            function.basicBlocks.remove(succ);
                            function.reorderSlot();
                            break;
                        }
                    }
                }
            }
        }
    }
}
