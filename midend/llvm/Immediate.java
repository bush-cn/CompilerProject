package midend.llvm;

import midend.llvm.Value;

/**
 * Immediate和Slot同为Value，都是Instruction的Use对象，但是不分配寄存器
 */
public class Immediate extends Value {
    public int immediate;

    public Immediate(int immediate) {
        this.immediate = immediate;
    }

    @Override
    public String toText() {
        return String.valueOf(immediate);
    }
}
