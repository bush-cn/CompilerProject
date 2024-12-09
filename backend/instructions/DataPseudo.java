package backend.instructions;

public class DataPseudo extends MIPSInst {
    public String name;
    public DataType dataType;
    public String value;

    public DataPseudo(String name, DataType dataType, String value) {
        this.name = name;
        this.dataType = dataType;
        this.value = value;
    }
    @Override
    public String toMIPS() {
        return name + ": " + dataType + " " + value;
    }

    public enum DataType {
        ASCIIZ(".asciiz"),
        WORD(".word"),
        BYTE(".byte"),
        SPACE(".space");
        private String type;
        DataType(String type) {
            this.type = type;
        }
        public String toString() {
            return type;
        }
    }
}
