package backend.optimizer;

import backend.Register;
import backend.RegisterPool;
import backend.StackFrame;
import midend.llvm.*;
import midend.llvm.Module;

import java.util.*;

/**
 * 冲突图的数据结构
 */
public class Graph {
    // 邻接表
    private final Map<Slot, Set<Slot>> adjacencyList;

    public Graph() {
        adjacencyList = new LinkedHashMap<>();
    }

    public void addNode(Slot slot) {
        adjacencyList.putIfAbsent(slot, new HashSet<>());
    }

    public void addEdge(Slot slot1, Slot slot2) {
        adjacencyList.putIfAbsent(slot1, new HashSet<>());
        adjacencyList.putIfAbsent(slot2, new HashSet<>());
        adjacencyList.get(slot1).add(slot2);
        adjacencyList.get(slot2).add(slot1);
    }

    public void removeNode(Slot slot) {
        adjacencyList.remove(slot);
        for (Map.Entry<Slot, Set<Slot>> entry: adjacencyList.entrySet()) {
            entry.getValue().remove(slot);
        }
    }

    public boolean areConnected(Slot slot1, Slot slot2) {
        return adjacencyList.containsKey(slot1) && adjacencyList.get(slot1).contains(slot2);
    }

    public int getDegree(Slot slot) {
        return adjacencyList.get(slot).size();
    }

    public int getNodesNum() {
        return adjacencyList.size();
    }

    /**
     * 【precondition】已进行活跃变量分析
     * 图着色算法，对冲突图进行着色以分配全局寄存器
     * @param function 以函数为单位进行着色
     * @param registerPool 寄存器池
     * @param stackFrame 栈帧
     */
    public static void graphColoring(Function function, RegisterPool registerPool, StackFrame stackFrame) {
        if (function.interBlockLive.isEmpty()) {
            return;
        }
        // 构造冲突图
        Graph graph = new Graph(); // 用于移除节点得到队列
        Graph conflict = new Graph(); // 副本，用于保存冲突图
        for (Slot node: function.interBlockLive) {
            graph.addNode(node);
            conflict.addNode(node);
        }
        // 在liveIn和liveOut中同时出现的变量，即为冲突变量，添加边
        for (BasicBlock block: function.basicBlocks) {
            for (Slot slot1: block.liveIn) {
                for (Slot slot2: block.liveIn) {
                    if (slot1 != slot2 &&
                        function.interBlockLive.contains(slot1) &&
                        function.interBlockLive.contains(slot2)) {
                        graph.addEdge(slot1, slot2);
                        conflict.addEdge(slot1, slot2);
                    }
                }
            }
            for (Slot slot1: block.liveOut) {
                for (Slot slot2: block.liveOut) {
                    if (slot1 != slot2 &&
                        function.interBlockLive.contains(slot1) &&
                        function.interBlockLive.contains(slot2)) {
                        graph.addEdge(slot1, slot2);
                        conflict.addEdge(slot1, slot2);
                    }
                }
            }
        }
        // 对每个基本块，检查只出现在 in 或 out 中的变量，若活性范围冲突则也添加冲突边（一开始没有考虑这个也能过测试点）
        for (BasicBlock block: function.basicBlocks) {
            Set<Slot> intersection = new HashSet<>(block.liveIn);   // 交集
            intersection.retainAll(block.liveOut);

            Set<Slot> liveInRemoveInter = new HashSet<>(block.liveIn);
            liveInRemoveInter.removeAll(intersection);
            Set<Slot> liveOutRemoveInter = new HashSet<>(block.liveOut);
            liveOutRemoveInter.removeAll(intersection);

            for (Slot slot1: liveInRemoveInter) {
                for (Slot slot2: liveOutRemoveInter) {
                    if (function.interBlockLive.contains(slot1) &&
                            function.interBlockLive.contains(slot2)) {
                        // 滤去函数参数等不跨块活跃的变量
                        Instruction defInst = null;
                        for (Instruction inst: block.instructions) {
                            if (inst.def.contains(slot2)) {
                                // slot2只出现在liveOut中，则有一条定义它的指令
                                defInst = inst;
                                break;
                            }
                        }
                        // 定义后，slot1仍活跃，即为冲突
                        if (defInst.liveOut.contains(slot1)) {
                            graph.addEdge(slot1, slot2);
                            conflict.addEdge(slot1, slot2);
                        }
                    }
                }
            }
        }
        System.out.println("conflict node num: " + graph.getNodesNum());

        LinkedList<Slot> queue = new LinkedList<>();
        // 执行图着色算法
        while (graph.getNodesNum() > 0) {
            boolean canRemove = false;
            // 【为保证移出顺序，先对邻接表排序】
            List<Map.Entry<Slot, Set<Slot>>> list = new ArrayList<>(graph.adjacencyList.entrySet());
            list.sort(Comparator.comparingInt(o -> o.getKey().slotId));
            for (Map.Entry<Slot, Set<Slot>> entry : list) {
                if (graph.getDegree(entry.getKey()) < RegisterPool.savedRegisters.size()) {
                    queue.addLast(entry.getKey());
                    graph.removeNode(entry.getKey());
                    canRemove = true;
                    System.out.println("remove " + entry.getKey().toText());
                    break;
                }
            }
            if (!canRemove) {
                // 若无法删除节点，即图中存在度数大于等于寄存器数量的节点，保存在栈上
                // TODO: 优化时，选择引用次数最少的节点进行合并
                Slot remove = list.get(0).getKey();
                graph.removeNode(remove);
                conflict.removeNode(remove);    // 【不分配，移出冲突图】
                System.out.println("spill " + remove.toText());
            }
        }
        // 按照结点移走的反向顺序将点和边添加回去，并分配颜色
        while (!queue.isEmpty()) {
            Slot slot = queue.removeLast();
            graph.addNode(slot);
            List<Register> available = new LinkedList<>(RegisterPool.savedRegisters);
            for (Map.Entry<Slot, Set<Slot>> entry: graph.adjacencyList.entrySet()) {
                if (conflict.adjacencyList.get(slot).contains(entry.getKey())) {
                    // 若已在图中的节点与此节点存在冲突
                    available.remove(registerPool.globalAllocation.get(entry.getKey()));
                    graph.addEdge(slot, entry.getKey());
                }
            }
            // 挑选一个可用的寄存器着色
            assert !available.isEmpty();
            registerPool.globalAllocation.put(slot, available.get(0));
            System.out.println(slot.toText() + " got " + registerPool.globalAllocation.get(slot));
        }

        for (Map.Entry<Slot, Register> entry1: registerPool.globalAllocation.entrySet()) {
            for (Map.Entry<Slot, Register> entry2: registerPool.globalAllocation.entrySet()) {
                if (entry1.getValue() == entry2.getValue()) {
                    assert !conflict.areConnected(entry1.getKey(), entry2.getKey());
                }
            }
        }
    }
}
