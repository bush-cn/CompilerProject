package backend;

import backend.instructions.DataPseudo;
import backend.instructions.MIPSInst;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于存放MIPS代码的数据结构，类似LLVM的Module
 */
public class MIPSCode {
    public DataSeg dataSeg = new DataSeg();
    public TextSeg textSeg = new TextSeg();

    public void addDataPseudo(DataPseudo dataPseudo) {
        dataSeg.dataPseudos.add(dataPseudo);
    }
    public void addMIPSInst(MIPSInst mipsInst) {
        textSeg.texts.add(mipsInst);
    }
    public void addLabel(Label label) {
        textSeg.texts.add(label);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(".data\n");
        for (DataPseudo dataPseudo: dataSeg.dataPseudos) {
            sb.append('\t')
                    .append (dataPseudo.toMIPS())
                    .append("\n");
        }     // 为了能够在MARS中运行，需要声明main函数为全局函数
        sb.append(".text\n");
        for (MIPSElement mipsElement: textSeg.texts) {
            if (mipsElement instanceof MIPSInst) {
                sb.append('\t');
            }
            sb.append(mipsElement.toMIPS());
            if (mipsElement instanceof MIPSInst mipsInst) {
                if (mipsInst.comment != null) {
                    sb.append("\t# ").append(mipsInst.comment);
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // data段内部类
    public static class DataSeg {
        List<DataPseudo> dataPseudos = new ArrayList<>();
    }
    // text段内部类
    public static class TextSeg {
        // 用于存放MIPS指令和label
        List<MIPSElement> texts = new ArrayList<>();
    }
}
