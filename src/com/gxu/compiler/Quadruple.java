package com.gxu.compiler;

/**
 * 四元式类
 */
public class Quadruple {

    private int mId;    // 四元式序号
    private String mOp; // 操作符
    private String mArg1;   // 操作数1
    private String mArg2;   // 操作数2
    private String mResult; // 结果

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getOp() {
        return mOp;
    }

    public void setOp(String op) {
        mOp = op;
    }

    public String getArg1() {
        return mArg1;
    }

    public void setArg1(String arg1) {
        mArg1 = arg1;
    }

    public String getArg2() {
        return mArg2;
    }

    public void setArg2(String arg2) {
        mArg2 = arg2;
    }

    public String getResult() {
        return mResult;
    }

    public void setResult(String result) {
        mResult = result;
    }

    public Quadruple() {

    }

    public Quadruple(int id, String op, String arg1, String arg2, String result) {
        this.mId = id;
        this.mOp = op;
        this.mArg1 = arg1;
        this.mArg2 = arg2;
        this.mResult = result;
    }

    /**
     * 输出四元式字符串
     *
     * @return 四元式字符串
     */
    @Override
    public String toString() {
        return this.mId + ": (" + this.mOp + ", " + this.mArg1 + ", " + this.mArg2 + ", " + this.mResult + ")";
    }

}
