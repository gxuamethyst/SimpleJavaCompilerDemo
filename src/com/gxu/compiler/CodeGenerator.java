package com.gxu.compiler;

import java.util.List;

/**
 * 目标代码/汇编代码生成模块
 */
public class CodeGenerator {

    private String mAssemblyCodeString;    // 生成的汇编代码
    private StringBuffer mTempAssemblyCodeStringBuffer;     // 临时StringBuffer对象

    private List<Quadruple> mQuadruplesList;    // 四元式集合
    private List<Symbol> mSymbolsList;      // 符号表
    private List<String> mTempVarsList;  // 临时变量集合
    private String mInstruction;                // 转向语句四元式对应的操作指令

    /**
     * 获取生成的汇编代码字符串
     *
     * @param quadruplesList  从语义分析器传入的四元式集合
     * @param symbolsList     从语义分析器传入的符号集合
     * @param tempVarsList 从语义分析器传入的四元式临时变量集合
     * @return 汇编代码字符串
     */
    public String getAssemblyCodeStr(List<Quadruple> quadruplesList, List<Symbol> symbolsList, List<String> tempVarsList) {
        this.mQuadruplesList = quadruplesList;
        this.mSymbolsList = symbolsList;
        this.mTempVarsList = tempVarsList;
        GenerateAssembly();
        return mAssemblyCodeString;
    }

    /**
     * 构造函数，执行一些初始化操作
     */
    public CodeGenerator() {
        mAssemblyCodeString = "";
        mTempAssemblyCodeStringBuffer = new StringBuffer();
    }

    /**
     * 执行 生成汇编代码
     */
    private void GenerateAssembly() {
        GenerateDataSeg();
        GenerateCodeSeg();
        mAssemblyCodeString = mTempAssemblyCodeStringBuffer.toString();
    }

    /**
     * data segment
     */
    private void GenerateDataSeg() {
        mTempAssemblyCodeStringBuffer.append("data segment\n");
        for (Symbol symbol : mSymbolsList) {
            mTempAssemblyCodeStringBuffer.append(symbol.getProperty()).append(" dw 0\n");
        }
        for (String tempVar : mTempVarsList) {
            mTempAssemblyCodeStringBuffer.append(tempVar).append(" dw 0\n");
        }
        mTempAssemblyCodeStringBuffer.append("data ends\n");
    }

    /**
     * code segment
     */
    private void GenerateCodeSeg() {
        mTempAssemblyCodeStringBuffer.append("code segment\n");
        mTempAssemblyCodeStringBuffer.append("assume cs:code, ds:data\n");
        for (Quadruple quadruple : mQuadruplesList) {
            // （block,/,/,/)
            if (quadruple.getOp().equals("block")) {
                mTempAssemblyCodeStringBuffer.append("start:\n");
                mTempAssemblyCodeStringBuffer.append("\tmov ax, data\n");
                mTempAssemblyCodeStringBuffer.append("\tmov ds, ax\n");
            }
            // （blockend,/,/,/)
            else if (quadruple.getOp().equals("blockend")) {
                mTempAssemblyCodeStringBuffer.append("\tmov ax, 4c00h\n");
                mTempAssemblyCodeStringBuffer.append("\tint 21h\n");
                mTempAssemblyCodeStringBuffer.append("code ends\n");
                mTempAssemblyCodeStringBuffer.append("end start\n");
            }
            // (/,/,/,/)
            else if (quadruple.getOp().equals("/") && quadruple.getResult().equals("/")) {
                mTempAssemblyCodeStringBuffer.append("S" + quadruple.getId() + ":\n");
            }
            // (:=,arg1,/,result)
            else if (quadruple.getOp().equals(":=")) {
                mTempAssemblyCodeStringBuffer.append("\tmov ax, " + quadruple.getArg1() + "\n");
                mTempAssemblyCodeStringBuffer.append("\tmov " + quadruple.getResult() + ", ax" + "\n");
            }
            // (+,arg1,arg2,result)
            else if (quadruple.getOp().equals("+")) {
                mTempAssemblyCodeStringBuffer.append("\tmov ax, " + quadruple.getArg1() + "\n");
                mTempAssemblyCodeStringBuffer.append("\tadd ax, " + quadruple.getArg2() + "\n");
                mTempAssemblyCodeStringBuffer.append("\tmov " + quadruple.getResult() + ", ax" + "\n");
            }
            // (-,arg1,arg2,result)
            else if (quadruple.getOp().equals("-")) {
                mTempAssemblyCodeStringBuffer.append("\tmov ax, " + quadruple.getArg1() + "\n");
                mTempAssemblyCodeStringBuffer.append("\tsub ax, " + quadruple.getArg2() + "\n");
                mTempAssemblyCodeStringBuffer.append("\tmov " + quadruple.getResult() + ", ax" + "\n");
            }
            // (*,arg1,arg2,result)
            else if (quadruple.getOp().equals("*")) {
                mTempAssemblyCodeStringBuffer.append("\tmov ax, " + quadruple.getArg1() + "\n");
                mTempAssemblyCodeStringBuffer.append("\tmov bx, " + quadruple.getArg2() + "\n");
                mTempAssemblyCodeStringBuffer.append("\tmul bx\n");
                mTempAssemblyCodeStringBuffer.append("\tmov " + quadruple.getResult() + ", ax" + "\n");
            }
            // (/,arg1,arg2,result)
            else if (quadruple.getOp().equals("/")) {
                mTempAssemblyCodeStringBuffer.append("\tmov ax, " + quadruple.getArg1() + "\n");
                mTempAssemblyCodeStringBuffer.append("\tmov bx, " + quadruple.getArg2() + "\n");
                mTempAssemblyCodeStringBuffer.append("\tdiv bl\n");
                mTempAssemblyCodeStringBuffer.append("\tmov ah,0h\n");
                mTempAssemblyCodeStringBuffer.append("\tmov " + quadruple.getResult() + ", ax" + "\n");
            }
            // 关系运算四元式
            else if (quadruple.getOp().equals("<") || quadruple.getOp().equals("<=") ||
                    quadruple.getOp().equals(">") || quadruple.getOp().equals(">=") ||
                    quadruple.getOp().equals("==") || quadruple.getOp().equals("!=")) {
                mTempAssemblyCodeStringBuffer.append("S" + quadruple.getId() + ": \n");
                mTempAssemblyCodeStringBuffer.append("\tmov ax, " + quadruple.getArg1() + "\n");
                mTempAssemblyCodeStringBuffer.append("\tmov bx, " + quadruple.getArg2() + "\n");
                mTempAssemblyCodeStringBuffer.append("\tcmp ax,bx" + "\n");
                mInstruction = getInstruction(quadruple.getOp());
            }
            // (FJ,A,B,/)
            else if (quadruple.getOp().equals("FJ")) {
                mTempAssemblyCodeStringBuffer.append("\t" + mInstruction + " S" + quadruple.getArg1() + "\n");
            }
            // (RJ,A,/,/)
            else if (quadruple.getOp().equals("RJ")) {
                mTempAssemblyCodeStringBuffer.append("\tjmp " + "short S" + quadruple.getArg1() + "\n");
            }
        }
    }

    /**
     * 根据给定的关系运算符返回正确的转移指令
     * 因为四元式中为(FJ,A,B,/)即当为假时跳转，
     * 故给定关系运算符返回其相反意义的转移指令
     *
     * @param operator 关系运算符
     * @return 转移指令
     */
    private String getInstruction(String operator) {
        String instruction = "";
        switch (operator) {
            case "<":
                instruction = "jae ";   // 无符号大于或等于则跳转
                break;
            case "<=":
                instruction = "ja ";    // 无符号大于则跳转
                break;
            case ">":
                instruction = "jbe ";   // 无符号小于或等于则跳转
                break;
            case ">=":
                instruction = "jb ";    // 无符号小于则跳转
                break;
            case "==":
                instruction = "jne ";   // 不等于则跳转
                break;
            case "!=":
                instruction = "je ";    // 等于则跳转
                break;
        }
        return instruction;
    }

}
