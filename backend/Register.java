package backend;

public enum Register {
    ZERO("$zero"), // Constant value 0
    AT("$at"),     // Assembler temporary
    V0("$v0"),     // Function result
    V1("$v1"),     // Function result
    A0("$a0"),     // Argument
    A1("$a1"),     // Argument
    A2("$a2"),     // Argument
    A3("$a3"),     // Argument
    T0("$t0"),     // Temporary
    T1("$t1"),     // Temporary
    T2("$t2"),     // Temporary
    T3("$t3"),     // Temporary
    T4("$t4"),     // Temporary
    T5("$t5"),     // Temporary
    T6("$t6"),     // Temporary
    T7("$t7"),     // Temporary
    S0("$s0"),     // Saved temporary
    S1("$s1"),     // Saved temporary
    S2("$s2"),     // Saved temporary
    S3("$s3"),     // Saved temporary
    S4("$s4"),     // Saved temporary
    S5("$s5"),     // Saved temporary
    S6("$s6"),     // Saved temporary
    S7("$s7"),     // Saved temporary
    T8("$t8"),     // Temporary
    T9("$t9"),     // Temporary
    K0("$k0"),     // Reserved for OS kernel
    K1("$k1"),     // Reserved for OS kernel
    GP("$gp"),     // Global pointer
    SP("$sp"),     // Stack pointer
    FP("$fp"),     // Frame pointer
    RA("$ra");     // Return address

    private final String name;

    Register(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}