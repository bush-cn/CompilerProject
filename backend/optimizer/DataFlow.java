package backend.optimizer;

import midend.llvm.*;
import midend.llvm.Module;
import midend.llvm.globalvalues.GlobalVariable;
import midend.llvm.types.PointerType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 用以数据流分析，包括活跃变量分析和可达定义分析
 */
public class DataFlow {
    public static void liveVariable(Module module) {
        for (GlobalValue globalValue: module.globalValues) {
            if (globalValue instanceof Function function) {
                List<BasicBlock> basicBlocks = function.basicBlocks;
                // 当有集合的in集发生变化时，继续迭代
                boolean changed = true;
                while(changed) {
                    changed = false;
                    for (int i = basicBlocks.size() - 1; i >= 0; i--) {
                        BasicBlock basicBlock = basicBlocks.get(i);
                        // BasicBlock的liveOut集合为其所有后继的liveIn集合的并集
                        for (BasicBlock succ: basicBlock.succs) {
                            basicBlock.liveOut.addAll(succ.liveIn);
                        }
                        // 计算每条指令的in集和out集
                        List<Instruction> instructions = basicBlock.instructions;
                        // BasicBlock的最后一条指令的liveOut集合即为liveOut集合
                        Set<Slot> succsLiveIn = new HashSet<>(basicBlock.liveOut);
                        for (int j = instructions.size() - 1; j >= 0; j--) {
                            Instruction instruction = instructions.get(j);
                            instruction.liveOut = new HashSet<>(succsLiveIn);   // 每条指令的liveOut集合为其后继的liveIn集合
                            instruction.liveIn = new HashSet<>(instruction.liveOut);
                            instruction.liveIn.removeAll(instruction.def);
                            instruction.liveIn.addAll(instruction.use);
                            succsLiveIn = new HashSet<>(instruction.liveIn);
                        }
                        // 最后一条指令的liveIn集合即为BasicBlock的liveIn集合
                        if (!basicBlock.liveIn.equals(succsLiveIn)) {
                            changed = true;
                            basicBlock.liveIn = new HashSet<>(succsLiveIn);
                        }
                    }
                }

                // 计算函数内部的跨块活跃变量集合
                for (BasicBlock basicBlock: basicBlocks) {
                    function.interBlockLive.addAll(basicBlock.liveIn);
                    function.interBlockLive.addAll(basicBlock.liveOut);
                    // 函数参数一概不分配寄存器，因此尽管在参数中出现，也不加入跨块活跃变量集合
                    // 【指针参数也不分配】，因为参数值都在栈上呢
                    for (Function.Param param: function.params) {
                        function.interBlockLive.remove((Slot)param.value);
                    }
                }
            }
        }
    }

    public static void reachingDefinition(Module module) {
        for (GlobalValue globalValue: module.globalValues) {
            if (globalValue instanceof Function function) {
                for (BasicBlock basicBlock: function.basicBlocks) {
                    // BasicBlock的reachIn集合为其所有前驱的reachOut集合的并集
                    for (BasicBlock pred: basicBlock.preds) {
                        basicBlock.reachIn.addAll(pred.reachOut);
                    }
                    // TODO: 计算Block的kill集、gen集、reachOut集
                }
            }
        }
    }
}
