package backend.instructions;

public class SYSCALL extends MIPSInst {
    @Override
    public String toMIPS() {
        return "syscall";
    }
}
