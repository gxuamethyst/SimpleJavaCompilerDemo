package com.gxu.compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 语义分析器
 */
public class SemanticAnalyzer {

    private Word mWord; // 当前正在分析的单词
    private List<Word> mWordList;  // 从词法分析获取的结果（单词表）
    private List<Symbol> mSymbolList;  // 符号表
    private boolean isSuccess;  // 分析是否成功/通过
    private int mWordListIndex; // 当前正在分析的单词的下标
    private int mQuadrupleIndex;     // 四元式序号
    private String mOp; // 四元式操作符
    private String mArg1;   // 四元式操作数1
    private String mArg2;   // 四元式操作数2
    private String mResult; // 四元式结果
    private int mTempIndex; // 临时变量T下标
    private int quadrupleIndex2;//用于if else语句的Rj跳转
    private List<Quadruple> mQuadrupleList;    // 四元式序列集合
    private List<String> mTempVarList;          // 四元式临时变量集合

    //获得经过排序后的四元式序列集合
    public List<Quadruple> getQuadrupleList() {
        sort();//四元式按序号排序
        return mQuadrupleList;
    }

    //获得四元式临时变量集合
    public List<String> getTempVarList() {
        return mTempVarList;
    }

    //获得符号表集合
    public List<Symbol> getSymbolList() {
        return mSymbolList;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public SemanticAnalyzer() {
        // 初始化一些信息
        mSymbolList = new ArrayList<Symbol>();
        isSuccess = true;
        mWordListIndex = 0;
        mQuadrupleIndex = 0;
        mTempIndex = 1;
        mQuadrupleList = new ArrayList<Quadruple>();
        mTempVarList = new ArrayList<String>();
    }

    /**
     * 语义分析过程
     *
     * @param words           词法分析的结果（单词表）
     * @param isSyntaxCorrect 语法分析的结果，通过/不通过（通过才能进行语义分析）
     */
    public void SemanticAnalysis(List<Word> words, boolean isSyntaxCorrect) {
        if (words.isEmpty()) {
            System.out.println("单词表为空，不能进入语义分析阶段！");
        } else if (isSyntaxCorrect) {
            this.mWordList = words;
            this.mWordList.remove(mWordList.size() - 1);  // 删除词法分析结果中单词表最后的"#"(语句末尾)
            mainTree();
        } else {
            System.out.println("语法分析未通过，不能进入语义分析阶段！");
        }
    }

    /**
     * 主函数public static void main(String[] args) {
     */
    private void mainTree() {
        getNextWord();
        Trip("block", "/", "/", "/");
        if (mWord.getType() == 1 && mWord.getProperty().equals("public")) {
            getNextWord();
            if (mWord.getType() == 1 && mWord.getProperty().equals("static")) {
                getNextWord();
                if (mWord.getType() == 1 && mWord.getProperty().equals("void")) {
                    getNextWord();
                    if (mWord.getType() == 1 && mWord.getProperty().equals("main")) {
                        getNextWord();
                        if (mWord.getType() == 5 && mWord.getProperty().equals("(")) {
                            getNextWord();
                            if (mWord.getType() == 1 && mWord.getProperty().equals("String")) {
                                getNextWord();
                                if (mWord.getType() == 5 && mWord.getProperty().equals("[")) {
                                    getNextWord();
                                    if (mWord.getType() == 5 && mWord.getProperty().equals("]")) {
                                        getNextWord();
                                        if (mWord.getType() == 1 && mWord.getProperty().equals("args")) {
                                            getNextWord();
                                            if (mWord.getType() == 5 && mWord.getProperty().equals(")")) {
                                                getNextWord();
                                                if (mWord.getType() == 5 && mWord.getProperty().equals("{")) {
                                                    complexSentence();
                                                    getNextWord();
                                                    if (mWord.getType() == 5 && mWord.getProperty().equals("}")) {
                                                        Trip("blockend", "/", "/", "/");
                                                    } else isSuccess = false;
                                                } else isSuccess = false;
                                            } else isSuccess = false;
                                        } else isSuccess = false;
                                    } else isSuccess = false;
                                } else isSuccess = false;
                            } else isSuccess = false;
                        } else isSuccess = false;
                    } else isSuccess = false;
                } else isSuccess = false;
            } else isSuccess = false;
        } else isSuccess = false;
    }

    /**
     * 复合语句
     */
    private void complexSentence() {
        getNextWord();
        while (true) {
            if (mWord.getType() == 1 && mWord.getProperty().equals("int")) {
                declareSentence();//跳转到声明语句
                getNextWord();
                continue;
            } else if (mWord.getType() == 2) {//当前符号为标识符，进行赋值语句
                assignmentSentence();
                if (mWord.getType() == 5 && mWord.getProperty().equals(";")) {

                } else {
                    isSuccess = false;
                    break;
                }
                getNextWord();
                continue;
            } else if (mWord.getType() == 1 && mWord.getProperty().equals("while")) {
                whileSentence();
                getNextWord();
                continue;
            } else if (mWord.getType() == 1 && mWord.getProperty().equals("if")) {
                ifSentence();
                getNextWord();
                continue;
            } else if (mWord.getType() == 1 && mWord.getProperty().equals("else")) {
                elseSentence();
                getNextWord();
                continue;
            } else if (mWord.getType() == 5 && mWord.getProperty().equals("}")) {
                break;
            } else {
                isSuccess = false;
                break;
            }
        }
    }

    /**
     * 声明语句
     */
    private void declareSentence() {
        String type = mWord.getProperty();    // 符号表类型
        getNextWord();
        if (mWord.getType() == 2) {
            if (!symbolContained(mWord.getProperty())) {     // 写入符号表前判断该id是否已经存在
                Entry(type, mWord.getProperty());
            } else {
                isSuccess = false;
                System.out.println("语义分析错误，变量" + mWord.getProperty() + "重复定义");
                return;
            }
//            mResult = mWord.getProperty();//用于四元序的临时变量结果保存
            String resultTemp = mWord.getProperty();
            getNextWord();
            while (true) {
                if (mWord.getType() == 5 && mWord.getProperty().equals(",")) {
                    getNextWord();
                    resultTemp = mWord.getProperty();
                    if (mWord.getType() == 2) {
                        if (!symbolContained(mWord.getProperty())) {     // 写入符号表前判断该id是否已经存在
                            Entry(type, mWord.getProperty());
                        } else {
                            isSuccess = false;
                            System.out.println("语义分析错误，变量" + mWord.getProperty() + "重复定义");
                            return;
                        }
//                        mResult = mWord.getProperty();
                        getNextWord();
                        continue;
                    } else {
                        isSuccess = false;
                        break;
                    }
                } else if (mWord.getType() == 4 && mWord.getProperty().equals("=")) {
                    mArg1 = opSentence();
                    mOp = ":=";
                    mArg2 = "/";
                    Trip(mOp, mArg1, mArg2, resultTemp);
                } else if (mWord.getType() == 5 && mWord.getProperty().equals(";")) {
                    break;
                } else {
                    isSuccess = false;
                    break;
                }
            }
        } else {
            isSuccess = false;
        }
    }

    /**
     * 赋值语句
     */
    private void assignmentSentence() {
        String resultTemp = mWord.getProperty();
        //是否已经声明变量
        if (symbolContained(resultTemp)) {
            getNextWord();
            if (mWord.getType() == 4 && mWord.getProperty().equals("=")) {
                mArg1 = opSentence();
                mOp = ":=";
                mArg2 = "/";
                Trip(mOp, mArg1, mArg2, resultTemp);
            } else {
                isSuccess = false;
            }
        } else {
            isSuccess = false;
            System.out.println("语义分析错误，变量" + resultTemp + "未定义");
        }
    }

    /**
     * while语句
     */
    private void whileSentence() {
        getNextWord();
        if (mWord.getType() == 5 && mWord.getProperty().equals("(")) {
            String arg2Temp = compareSentence();
            int quadrupleIndex1 = mQuadrupleIndex;
            mQuadrupleIndex++;
            if (mWord.getType() == 5 && mWord.getProperty().equals(")")) {
                getNextWord();
                if (mWord.getType() == 5 && mWord.getProperty().equals("{")) {
                    complexSentence();
                    if (mWord.getType() == 5 && mWord.getProperty().equals("}")) {
                        quadrupleIndex2 = mQuadrupleIndex + 1;
                        mQuadrupleIndex = quadrupleIndex1;
                        Trip("FJ", String.valueOf(quadrupleIndex2 + 1), arg2Temp, "/");
                        mQuadrupleIndex = quadrupleIndex2 - 1;
                        Trip("RJ", String.valueOf(quadrupleIndex1), arg2Temp, "/");

                        Trip("/", "/", "/", "/");
                        return;
                    } else isSuccess = false;
                } else isSuccess = false;
            } else isSuccess = false;
        } else isSuccess = false;
    }

    /**
     * if语句
     */
    private void ifSentence() {
        getNextWord();
        if (mWord.getType() == 5 && mWord.getProperty().equals("(")) {
            String arg2Temp = compareSentence();
            int quadrupleIndex1 = mQuadrupleIndex;
            mQuadrupleIndex++;
            if (mWord.getType() == 5 && mWord.getProperty().equals(")")) {
                getNextWord();
                if (mWord.getType() == 5 && mWord.getProperty().equals("{")) {
                    complexSentence();
                    if (mWord.getType() == 5 && mWord.getProperty().equals("}")) {
                        quadrupleIndex2 = mQuadrupleIndex + 1;
//                        Trip("RJ", String.valueOf(quadrupleIndex2 + 2), "/", "/");
                        mQuadrupleIndex = quadrupleIndex1;
                        Trip("FJ", String.valueOf(quadrupleIndex2 + 1), arg2Temp, "/");
                        mQuadrupleIndex = quadrupleIndex2;
                        return;
                    } else isSuccess = false;
                } else isSuccess = false;
            } else isSuccess = false;
        } else isSuccess = false;
    }

    /**
     * else语句
     */
    private void elseSentence() {
        getNextWord();
        if (mWord.getType() == 5 && mWord.getProperty().equals("{")) {
            complexSentence();
            if (mWord.getType() == 5 && mWord.getProperty().equals("}")) {
                int quadrupleIndex3 = mQuadrupleIndex;
                mQuadrupleIndex = quadrupleIndex2 - 1;
                Trip("RJ", String.valueOf(quadrupleIndex3 + 1), "/", "/");
                mQuadrupleIndex = quadrupleIndex3;
                Trip("/", "/", "/", "/");
                return;
            } else {
                isSuccess = false;
            }
        } else if (mWord.getType() == 1 && mWord.getProperty().equals("if")) {
            ifSentence();
        } else {
            isSuccess = false;
        }
    }

    /**
     * 比较语句,出口指向下一个匹配字符
     *
     * @return
     */
    private String compareSentence() {
        String arg1Temp = opSentence();
        if (mWord.getType() == 4 && Word.isRelationalOperator(mWord.getProperty())) {
            mOp = mWord.getProperty();
            mArg2 = opSentence();
        } else {
            isSuccess = false;
        }
        mResult = "T" + mTempIndex++;
        mTempVarList.add(mResult);
        Trip(mOp, arg1Temp, mArg2, mResult);
        return mResult;
    }

    /**
     * 运算式
     *
     * @return
     */
    private String opSentence() {
        String firstmOp;
        getNextWord();
        if (mWord.getType() == 2 || mWord.getType() == 3) {
            mArg1 = mWord.getProperty();
            boolean k = true;
            getNextWord();
            while (true) {
                if (mWord.getType() == 4 && Word.isArithmeticOperator(mWord.getProperty())) {    // 判断是否为运算符
                    k = false;
                    mOp = mWord.getProperty();
                    firstmOp = mOp;
                    getNextWord();
                    if (mWord.getType() == 2 || mWord.getType() == 3) {
                        mArg2 = mWord.getProperty();
                        getNextWord();
                        continue;
                    } else if (mWord.getType() == 5 && mWord.getProperty().equals("(")) {
                        mArg2 = opSentence();
                        mOp = firstmOp;
                        if (mWord.getType() == 5 && mWord.getProperty().equals(")")) {
                            getNextWord();
                            continue;
                        }
                    }
                } else {
                    break;
                }
            }
            if (k) {
                mResult = mArg1;
            } else {
                mResult = "T" + mTempIndex++;
                mTempVarList.add(mResult);
                Trip(mOp, mArg1, mArg2, mResult);
            }
        }
        return mResult;
    }

    /**
     * 获取下一个单词
     */
    private void getNextWord() {
        if (mWordListIndex < mWordList.size()) {
            mWord = mWordList.get(mWordListIndex++);
        }
    }

    /**
     * 生产/写入符号表
     *
     * @param type 符号类型
     * @param id   符号id
     */
    private void Entry(String type, String id) {
        mSymbolList.add(new Symbol(type, id));
    }

    /**
     * 生产/写入四元式序列
     *
     * @param op     操作符
     * @param arg1   操作数1
     * @param arg2   操作数2
     * @param result 结果
     */
    private void Trip(String op, String arg1, String arg2, String result) {
        mQuadrupleIndex++;
        mQuadrupleList.add(new Quadruple(mQuadrupleIndex, op, arg1, arg2, result));
    }

    /**
     * 检查给定id/标识符在符号表中是否存在
     *
     * @param id id/标识符
     * @return true/false
     */
    private boolean symbolContained(String Property) {
        boolean isSymbolContained = false;
        for (Symbol symbol : mSymbolList) {
            if (symbol.getProperty().equals(Property)) {
                isSymbolContained = true;
            }
        }
        return isSymbolContained;
    }

    /**
     * 给四元式序列排序
     */
    private void sort() {
        Collections.sort(mQuadrupleList, new Comparator<Quadruple>() {
            @Override
            public int compare(Quadruple a, Quadruple b) {
                return a.getId() - b.getId();       // 按序号升序排序
            }
        });
    }

}
