package backend.optimizer;

import backend.*;
import backend.instructions.BranchInst.BNEZ;
import backend.instructions.MFInst.MFHI;
import backend.instructions.MemInst.LI;
import backend.instructions.RIInst.*;
import backend.instructions.RRInst.*;

import java.math.BigInteger;
import java.util.HexFormat;

public class MulDivOptimizer {
    private static int count = 0;
    public static boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    public static int log2(int n) {
        if (!isPowerOfTwo(n)) {
            throw new IllegalArgumentException(n + " is not a power of two");
        }
        return Integer.numberOfTrailingZeros(n);
    }

    public static void mulOptimize(MIPSCode mipsCode, Register dest, Register src, int imm, RegisterPool registerPool, StackFrame stackFrame) {
        if (isPowerOfTwo(imm)) {
            mipsCode.addMIPSInst(new SLL(dest, src, log2(imm)));
        }
        else if (isPowerOfTwo(-imm)) {
            mipsCode.addMIPSInst(new SLL(dest, src, log2(-imm)));
            mipsCode.addMIPSInst(new SUBU(dest, Register.ZERO, dest));
        }
        else if (imm == 0) {
            mipsCode.addMIPSInst(new LI(dest, 0));
        }
        else if (imm == 1) {
            if (dest != src) {
                mipsCode.addMIPSInst(new MOVE(dest, src));
            } // else pass
        } else if (imm == 3) {
            if (dest != src) {
                mipsCode.addMIPSInst(new ADDU(dest, src, src));
                mipsCode.addMIPSInst(new ADDU(dest, dest, src));
            } else {
                Register temp = registerPool.allocTemp(stackFrame);
                mipsCode.addMIPSInst(new ADDU(temp, src, src));
                mipsCode.addMIPSInst(new ADDU(dest, temp, src));
            }
        }
        else if (isPowerOfTwo(imm - 1)) {
            if (dest != src) {
                mipsCode.addMIPSInst(new SLL(dest, src, log2(imm - 1)));
                mipsCode.addMIPSInst(new ADDU(dest, dest, src));
            } else {
                Register temp = registerPool.allocTemp(stackFrame);
                mipsCode.addMIPSInst(new SLL(temp, src, log2(imm - 1)));
                mipsCode.addMIPSInst(new ADDU(dest, temp, src));
            }
        } else if (isPowerOfTwo(imm - 2)) {
            if (dest != src) {
                mipsCode.addMIPSInst(new SLL(dest, src, log2(imm - 2)));
                mipsCode.addMIPSInst(new ADDU(dest, dest, src));
            } else {
                Register temp = registerPool.allocTemp(stackFrame);
                mipsCode.addMIPSInst(new SLL(temp, src, log2(imm - 2)));
                mipsCode.addMIPSInst(new ADDU(temp, temp, src));
                mipsCode.addMIPSInst(new ADDU(dest, temp, src));
            }
        }
        else if (isPowerOfTwo(imm + 1)) {
            if (dest != src) {
                mipsCode.addMIPSInst(new SLL(dest, src, log2(imm + 1)));
                mipsCode.addMIPSInst(new SUBU(dest, dest, src));
            } else {
                Register temp = registerPool.allocTemp(stackFrame);
                mipsCode.addMIPSInst(new SLL(temp, src, log2(imm + 1)));
                mipsCode.addMIPSInst(new SUBU(dest, temp, src));
            }
        }
        else {
            // 其他情况直接乘法，无法优化
            mipsCode.addMIPSInst(new MULI(dest, src, imm));
        }
    }

    public static void divOptimize(MIPSCode mipsCode, Register dest, Register src, int imm, RegisterPool registerPool, StackFrame stackFrame) {
        if (isPowerOfTwo(imm)) {
            Register ifPosi = registerPool.allocTemp(stackFrame);
            mipsCode.addMIPSInst(new SLT(ifPosi, Register.ZERO, src));
            int label1 = count++, label2 = count++;
            mipsCode.addMIPSInst(new BNEZ(ifPosi, new Label("_L_divOptimize_" + label1)));
            mipsCode.addMIPSInst(new SUBU(src, Register.ZERO, src));    // 若src为负数，转为正数
            mipsCode.addLabel(new Label("_L_divOptimize_" + label1));

            mipsCode.addMIPSInst(new SRA(dest, src, log2(imm)));    // 【右移】

            mipsCode.addMIPSInst(new BNEZ(ifPosi, new Label("_L_divOptimize_" + label2)));
            mipsCode.addMIPSInst(new SUBU(dest, Register.ZERO, dest));    // 若src为负数，dest转为负数
            mipsCode.addLabel(new Label("_L_divOptimize_" + label2));
        }   // -imm 为整数幂？
        else if (isPowerOfTwo(-imm)) {
            Register temp = registerPool.allocTemp(stackFrame);
            mipsCode.addMIPSInst(new SUBU(temp, Register.ZERO, src));
            divOptimize(mipsCode, dest, temp, -imm, registerPool, stackFrame);
        }
        else if (imm == 1) {
            if (dest != src) {
                mipsCode.addMIPSInst(new MOVE(dest, src));
            }
        } else {
            // TODO: 除法优化为乘法和移位指令
            boolean isNeg = false;
            if (imm < 0) {
                imm = -imm;
                isNeg = true;
            }
            int l;
            long m = 0;// 需要求的移位和乘数
            boolean hasResult = false;
            for (l = 0; l < 32; l++) {
                BigInteger low = BigInteger.valueOf(2).pow(32 + l);
                BigInteger high = BigInteger.valueOf(2).pow(32 + l).add(BigInteger.valueOf(2).pow(l));
                BigInteger start = low.divide(BigInteger.valueOf(imm));
                for (m = start.longValue(); BigInteger.valueOf(m).multiply(BigInteger.valueOf(imm)).compareTo(high) <= 0 && m <= 0xffffffffL; m++) {
                    if (BigInteger.valueOf(m).multiply(BigInteger.valueOf(imm)).compareTo(low) >= 0
                            && BigInteger.valueOf(m).multiply(BigInteger.valueOf(imm)).compareTo(high) <= 0) {
                        hasResult = true;
                        break;
                    }
                }
                if (hasResult) {
                    break;
                }
            }
            if (hasResult) {
                System.out.println("Multiplier = 0x" + Long.toHexString(m));
                System.out.println("Shift = " + l);
                Register ifPosi = registerPool.allocTemp(stackFrame);
                mipsCode.addMIPSInst(new SLT(ifPosi, Register.ZERO, src));
                int label1 = count++, label2 = count++;
                mipsCode.addMIPSInst(new BNEZ(ifPosi, new Label("_L_divOptimize_" + label1)));
                mipsCode.addMIPSInst(new SUBU(src, Register.ZERO, src));    // 若src为负数，转为正数
                mipsCode.addLabel(new Label("_L_divOptimize_" + label1));

                Register temp = registerPool.allocTemp(stackFrame);
                mipsCode.addMIPSInst(new LI(temp, m));
                mipsCode.addMIPSInst(new MULTU(src, temp));
                mipsCode.addMIPSInst(new MFHI(dest));
                mipsCode.addMIPSInst(new SRA(dest, dest, l));

                mipsCode.addMIPSInst(new BNEZ(ifPosi, new Label("_L_divOptimize_" + label2)));
                mipsCode.addMIPSInst(new SUBU(dest, Register.ZERO, dest));    // 若src为负数，dest转为负数
                mipsCode.addLabel(new Label("_L_divOptimize_" + label2));
            } else {
                mipsCode.addMIPSInst(new DIVI(dest, src, imm));
            }
        }
    }
}
