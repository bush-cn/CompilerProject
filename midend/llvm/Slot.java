package midend.llvm;


public class Slot extends Value {
    public int slotId;

    public Slot() {}

    public Slot(Function scope) {
        scope.addSlot(this);
    }

    @Override
    public String toText() {
        return "%" + slotId;
    }
}
