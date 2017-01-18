/*
 * 工具类：语法分析器
 */
package com.gxu.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Parser {

    private List<Word> wordsList = new ArrayList<Word>();   //单词列表
    private Stack<AnalyseNode> analyseStack = new Stack<AnalyseNode>();  //分析栈
    private ArrayList<Error> errorList = new ArrayList<Error>();   //错误信息列表
    private StringBuffer bf;   //分析栈缓冲流
    private int errorCount = 0;  //统计错误个数
    private boolean graErrorFlag = false;  //语法分析出错标志
    private AnalyseNode S, A, B, C, X, Y, R, Z, Z1, U, U1, E, D, L, L1, T, T1, F, P;   //分析栈节点
    private AnalyseNode topNode;   //分析栈 当前栈顶元素节点
    private Word firstWord;    //待分析单词(即单词列表中的第一个单词)
    private Error error;   //错误信息

    public Parser() {

    }

    //构造函数
    public Parser(List<Word> wordsList, boolean isLexicalCorrect) {
        this.wordsList = wordsList;
        this.init(isLexicalCorrect);

    }

    /**
     * 初始化函数，完成功能有：1.创建非终结符元素节点；2.LL1分析方法进行语法分析
     */
    public void init(boolean isLexicalCorrect) {
        //1.创建非终结符元素节点
        //S,A,B,C,X,Y,R,Z,Z1,U,U1,E,D,L,L1,T,T1,F,P;

        S = new AnalyseNode(AnalyseNode.NONTERMINAL, "S");
        A = new AnalyseNode(AnalyseNode.NONTERMINAL, "A");
        B = new AnalyseNode(AnalyseNode.NONTERMINAL, "B");
        C = new AnalyseNode(AnalyseNode.NONTERMINAL, "C");
        X = new AnalyseNode(AnalyseNode.NONTERMINAL, "X");
        Y = new AnalyseNode(AnalyseNode.NONTERMINAL, "Y");

        R = new AnalyseNode(AnalyseNode.NONTERMINAL, "R");
        Z = new AnalyseNode(AnalyseNode.NONTERMINAL, "Z");
        Z1 = new AnalyseNode(AnalyseNode.NONTERMINAL, "Z'");
        U = new AnalyseNode(AnalyseNode.NONTERMINAL, "U");
        U1 = new AnalyseNode(AnalyseNode.NONTERMINAL, "U'");
        E = new AnalyseNode(AnalyseNode.NONTERMINAL, "E");

        D = new AnalyseNode(AnalyseNode.NONTERMINAL, "D");
        L = new AnalyseNode(AnalyseNode.NONTERMINAL, "L");
        L1 = new AnalyseNode(AnalyseNode.NONTERMINAL, "L'");
        T = new AnalyseNode(AnalyseNode.NONTERMINAL, "T");
        T1 = new AnalyseNode(AnalyseNode.NONTERMINAL, "T'");
        F = new AnalyseNode(AnalyseNode.NONTERMINAL, "F");
        P = new AnalyseNode(AnalyseNode.NONTERMINAL, "P");

        //2.LL1分析方法进行语法分析
        //先判断词法分析是否通过
        if (!isLexicalCorrect) {
            System.out.println("词法分析未通过！不能进行语法分析");
        } else {
            System.out.println();

            //词法分析通过，才能进行语法分析：
            this.syntaxAnalysis();

        }
    }


    /**
     * LL1分析方法进行语法分析
     */
    public void syntaxAnalysis() {

        // 进行语法分析
        bf = new StringBuffer();
        int gcount = 0;         //语法分析步骤计数
        error = null;

        //先添加步骤0  ，即 #S
        analyseStack.add(0, S);
        analyseStack.add(1, new AnalyseNode(AnalyseNode.EOF, "#"));

        System.out.println("语法分析过程如下：");
        //循环分析过程  ; 分析栈和单词表 不为空时
        while (!analyseStack.empty() && !wordsList.isEmpty()) {
            bf.append('\n');
            bf.append("步骤" + gcount + "\t");


            topNode = analyseStack.get(0);   //取得当前栈顶元素节点
            firstWord = wordsList.get(0);    //取得待分析单词

            //分析正常结束
            if (firstWord.getProperty().equals("#") && topNode.getName().equals("#")) {
                bf.append("\n");
                analyseStack.remove(0);
                wordsList.remove(0);
                break;

            } else if (topNode.getName().equals("#") && !firstWord.getProperty().equals("#")) {
                //不正常结束 ,分析栈为# ，但是单词表不为#
                analyseStack.remove(0);
                graErrorFlag = true;
                break;

            } else if (AnalyseNode.isTerminal(topNode)) {
                //分析栈 栈顶元素为 “终结符”时 的操作
                this.terminalOP(topNode.getName());
                if (graErrorFlag) {
                    break;
                }

            } else if (AnalyseNode.isNonterminal(topNode)) {
                //分析栈 栈顶元素为 “非终结符”时 的操作
                this.nonTerminalOP(topNode.getName());
                if (graErrorFlag) {
                    break;
                }
            }

            bf.append("当前分析栈:");

            //输出分析步骤
            for (int i = analyseStack.size() - 1; i >= 0; i--) {
                bf.append(analyseStack.get(i).getName());

            }

            bf.append("\t\t\t").append("余留符号串：");

            for (int j = 0; j < wordsList.size(); j++) {
                bf.append(wordsList.get(j).getProperty());
            }
            gcount++;

        }
        //输出bf
        System.out.println(bf.toString());

        if (this.graErrorFlag) {
            Error error;
            System.out.println("语法分析出错！！！！！！！！");
            System.out.println("错误信息如下：");
            System.out.println("错误序号\t错误信息\t错误所在行 \t错误单词");

            for (int i = 0; i < errorList.size(); i++) {
                error = errorList.get(i);
                System.out.println(error.getId() + "\t" + error.getErrorInfo() + "\t\t" +
                        error.getLine() + "\t" + error.getWord().getProperty());
            }

        } else {
            System.out.println("语法分析通过!");
        }

    }

    /**
     * 分析栈 栈顶元素为 “终结符”时 的操作
     *
     * @param terminal:终结符
     */
    public void terminalOP(String terminal) {
        if ((terminal.equals("num") && firstWord.getType() == Word.INT_CONST)
                || terminal.equals(firstWord.getProperty())
                || (terminal.equals("identifier") && firstWord.getType() == Word.IDENTIFIER)) {
            analyseStack.remove(0);
            wordsList.remove(0);

        } else {
            errorCount++;
            error = new Error(errorCount, "语法错误", firstWord.getLine(), firstWord);
            errorList.add(error);
            graErrorFlag = true;
        }
    }

    /**
     * 分析栈 栈顶元素为 “非终结符”时 的操作
     *
     * @param nonTerminal:非终结符
     */
    public void nonTerminalOP(String nonTerminal) {
        if (nonTerminal.equals("Z'")) {
            nonTerminal = "1";
        }
        if (nonTerminal.equals("U'")) {
            nonTerminal = "2";
        }
        if (nonTerminal.equals("L'")) {
            nonTerminal = "3";
        }
        if (nonTerminal.equals("T'")) {
            nonTerminal = "4";
        }

        //S,A,B,C,X,Y,R,Z,Z1,U,U1,E,D,L,L1,T,T1,F,P;

        switch (nonTerminal.charAt(0)) {
            case 'S':    //S->public static void main(String[] args){A}
                if (firstWord.getProperty().equals("public")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "public"));
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "static"));
                    analyseStack.add(2, new AnalyseNode(AnalyseNode.TERMINAL, "void"));
                    analyseStack.add(3, new AnalyseNode(AnalyseNode.TERMINAL, "main"));
                    analyseStack.add(4, new AnalyseNode(AnalyseNode.TERMINAL, "("));
                    analyseStack.add(5, new AnalyseNode(AnalyseNode.TERMINAL, "String"));
                    analyseStack.add(6, new AnalyseNode(AnalyseNode.TERMINAL, "["));
                    analyseStack.add(7, new AnalyseNode(AnalyseNode.TERMINAL, "]"));
                    analyseStack.add(8, new AnalyseNode(AnalyseNode.TERMINAL, "args"));
                    analyseStack.add(9, new AnalyseNode(AnalyseNode.TERMINAL, ")"));
                    analyseStack.add(10, new AnalyseNode(AnalyseNode.TERMINAL, "{"));
                    analyseStack.add(11, A);
                    analyseStack.add(12, new AnalyseNode(AnalyseNode.TERMINAL, "}"));
                } else {
                    errorCount++;
                    error = new Error(errorCount, "语法错误", firstWord.getLine(), firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;

            case 'A':   // A->CA
                if (firstWord.getProperty().equals("int")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, C);
                    analyseStack.add(1, A);

                } else if (firstWord.getProperty().equals("System.out.println")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, C);
                    analyseStack.add(1, A);

                } else if (firstWord.getProperty().equals("System.in.read")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, C);
                    analyseStack.add(1, A);

                } else if (firstWord.getProperty().equals("if")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, C);
                    analyseStack.add(1, A);

                } else if (firstWord.getProperty().equals("while")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, C);
                    analyseStack.add(1, A);

                } else if (firstWord.getType() == Word.IDENTIFIER) {
                    analyseStack.remove(0);
                    analyseStack.add(0, C);
                    analyseStack.add(1, A);

                } else {
                    // A -> ε
                    analyseStack.remove(0);
                }
                break;

            case 'B':
                if (firstWord.getProperty().equals("System.out.println")) {
                    // B->System.out.println(P);
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "System.out.println"));
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "("));
                    analyseStack.add(2, P);
                    analyseStack.add(3, new AnalyseNode(AnalyseNode.TERMINAL, ")"));
                    analyseStack.add(4, new AnalyseNode(AnalyseNode.TERMINAL, ";"));

                } else if (firstWord.getProperty().equals("System.in.read")) {
                    // B -> System.in.read(identifier);
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "System.in.read"));
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "("));
                    analyseStack.add(2, new AnalyseNode(AnalyseNode.TERMINAL, "identifier"));
                    analyseStack.add(3, new AnalyseNode(AnalyseNode.TERMINAL, ")"));
                    analyseStack.add(4, new AnalyseNode(AnalyseNode.TERMINAL, ";"));

                } else if (firstWord.getProperty().equals("if")) {
                    // B-> if(E){A}else{A}
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "if"));
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "("));
                    analyseStack.add(2, E);
                    analyseStack.add(3, new AnalyseNode(AnalyseNode.TERMINAL, ")"));
                    analyseStack.add(4, new AnalyseNode(AnalyseNode.TERMINAL, "{"));
                    analyseStack.add(5, A);
                    analyseStack.add(6, new AnalyseNode(AnalyseNode.TERMINAL, "}"));
                    analyseStack.add(7, new AnalyseNode(AnalyseNode.TERMINAL, "else"));
                    analyseStack.add(8, new AnalyseNode(AnalyseNode.TERMINAL, "{"));
                    analyseStack.add(9, A);
                    analyseStack.add(10, new AnalyseNode(AnalyseNode.TERMINAL, "}"));

                } else if (firstWord.getProperty().equals("while")) {
                    // B -> while(E){A}
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "while"));
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "("));
                    analyseStack.add(2, E);
                    analyseStack.add(3, new AnalyseNode(AnalyseNode.TERMINAL, ")"));
                    analyseStack.add(4, new AnalyseNode(AnalyseNode.TERMINAL, "{"));
                    analyseStack.add(5, A);
                    analyseStack.add(6, new AnalyseNode(AnalyseNode.TERMINAL, "}"));

                } else {
                    // B -> ε
                    analyseStack.remove(0);
                }
                break;

            case 'C':
                if (firstWord.getProperty().equals("int")) {
                    //C->X
                    analyseStack.remove(0);
                    analyseStack.add(0, X);

                } else if (firstWord.getType() == Word.IDENTIFIER) {
                    // C->R
                    analyseStack.remove(0);
                    analyseStack.add(0, R);

                } else if (firstWord.getProperty().equals("System.out.println") || firstWord.getProperty().equals("System.in.read")
                        || firstWord.getProperty().equals("if") || firstWord.getProperty().equals("else")
                        || firstWord.getProperty().equals("while")) {
                    // C->B
                    analyseStack.remove(0);
                    analyseStack.add(0, B);

                } else {
                    errorCount++;
                    error = new Error(errorCount, "非法标识符", firstWord.getLine(), firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }

                break;

            case 'X':
                if (firstWord.getProperty().equals("int")) {
                    // X -> YZ;
                    analyseStack.remove(0);
                    analyseStack.add(0, Y);
                    analyseStack.add(1, Z);
                    analyseStack.add(2, new AnalyseNode(AnalyseNode.TERMINAL, ";"));

                } else {
                    // X -> ε
                    analyseStack.remove(0);
                }
                break;

            case 'Y':
                if (firstWord.getProperty().equals("int")) {
                    // Y->int
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "int"));

                } else {
                    errorCount++;
                    error = new Error(errorCount, "非法数据类型", firstWord.getLine(), firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;

            case 'R':
                if (firstWord.getType() == Word.IDENTIFIER) {
                    // R -> identifier=L;
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "identifier"));
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "="));
                    analyseStack.add(2, L);
                    analyseStack.add(3, new AnalyseNode(AnalyseNode.TERMINAL, ";"));

                } else {
                    // R -> ε
                    analyseStack.remove(0);
                }
                break;

            case 'Z':
                if (firstWord.getType() == Word.IDENTIFIER) {
                    // Z -> UZ’
                    analyseStack.remove(0);
                    analyseStack.add(0, U);
                    analyseStack.add(1, Z1);

                } else {
                    errorCount++;
                    error = new Error(errorCount, "非法标识符", firstWord.getLine(), firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;

            case '1':  //  就是 z'
                if (firstWord.getProperty().equals(",")) {
                    // Z’ -> ,Z
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, ","));
                    analyseStack.add(1, Z);
                } else {
                    // Z’-> ε
                    analyseStack.remove(0);
                }
                break;

            case 'U':
                if (firstWord.getType() == Word.IDENTIFIER) {
                    // U-> identifierU’
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "identifier"));
                    analyseStack.add(1, U1);
                } else {
                    errorCount++;
                    error = new Error(errorCount, "非法标识符", firstWord.getLine(), firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;

            case '2':  // 就是 U'
                if (firstWord.getProperty().equals("=")) {
                    //U’-> =L
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "="));
                    analyseStack.add(1, L);

                } else {
                    // U’-> ε
                    analyseStack.remove(0);
                }
                break;

            case 'E':
                if (firstWord.getType() == Word.IDENTIFIER) {
                    // E-> FDF
                    analyseStack.remove(0);
                    analyseStack.add(0, F);
                    analyseStack.add(1, D);
                    analyseStack.add(2, F);

                } else if (firstWord.getType() == Word.INT_CONST) {
                    // E-> FDF
                    analyseStack.remove(0);
                    analyseStack.add(0, F);
                    analyseStack.add(1, D);
                    analyseStack.add(2, F);

                } else if (firstWord.getProperty().equals("(")) {
                    // E-> FDF
                    analyseStack.remove(0);
                    analyseStack.add(0, F);
                    analyseStack.add(1, D);
                    analyseStack.add(2, F);

                } else {
                    errorCount++;
                    error = new Error(errorCount, "不能进行运算", firstWord.getLine(), firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;

            case 'D':
                if (firstWord.getProperty().equals("==")) {
                    // D-> ==
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "=="));

                } else if (firstWord.getProperty().equals("!=")) {
                    // D-> ！=
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "!="));

                } else if (firstWord.getProperty().equals(">")) {
                    //D-> >
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, ">"));


                } else if (firstWord.getProperty().equals("<")) {
                    //D-> <
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "<"));


                } else if (firstWord.getProperty().equals(">=")) {
                    //D-> >=
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, ">="));


                } else if (firstWord.getProperty().equals("<=")) {
                    //D-> <=
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "<="));

                } else {
                    errorCount++;
                    error = new Error(errorCount, "非法运算符", firstWord.getLine(), firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;

            case 'L':
                if (firstWord.getType() == Word.IDENTIFIER) {
                    // L -> TL’
                    analyseStack.remove(0);
                    analyseStack.add(0, T);
                    analyseStack.add(1, L1);

                } else if (firstWord.getType() == Word.INT_CONST) {
                    // L -> TL’
                    analyseStack.remove(0);
                    analyseStack.add(0, T);
                    analyseStack.add(1, L1);

                } else if (firstWord.getProperty().equals("(")) {
                    // L -> TL’
                    analyseStack.remove(0);
                    analyseStack.add(0, T);
                    analyseStack.add(1, L1);

                } else {

                    errorCount++;
                    error = new Error(errorCount, "不能进行算术运算的数据类型或括号不匹配", firstWord.getLine(), firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;

            case '3': // 就是 L'
                if (firstWord.getProperty().equals("+")) {
                    // L’-> +L
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "+"));
                    analyseStack.add(1, L);

                } else if (firstWord.getProperty().equals("-")) {
                    // L’-> -L
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "-"));
                    analyseStack.add(1, L);

                } else {
                    // L’-> ε
                    analyseStack.remove(0);
                }
                break;

            case 'T':
                if (firstWord.getType() == Word.IDENTIFIER) {
                    // T -> FT’
                    analyseStack.remove(0);
                    analyseStack.add(0, F);
                    analyseStack.add(1, T1);

                } else if (firstWord.getType() == Word.INT_CONST) {
                    // T -> FT’
                    analyseStack.remove(0);
                    analyseStack.add(0, F);
                    analyseStack.add(1, T1);

                } else if (firstWord.getProperty().equals("(")) {
                    // T -> FT’
                    analyseStack.remove(0);
                    analyseStack.add(0, F);
                    analyseStack.add(1, T1);

                } else {
                    errorCount++;
                    error = new Error(errorCount, "不能进行算术运算的数据类型或括号不匹配", firstWord.getLine(), firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;

            case '4': // 就是 T'
                if (firstWord.getProperty().equals("*")) {
                    // T’ -> *T
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "*"));
                    analyseStack.add(1, T);

                } else if (firstWord.getProperty().equals("/")) {
                    // T’ -> *T
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "/"));
                    analyseStack.add(1, T);

                } else {
                    // T’-> ε
                    analyseStack.remove(0);
                }
                break;

            case 'F':
                if (firstWord.getType() == Word.IDENTIFIER) {
                    // F -> identifier
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "identifier"));

                } else if (firstWord.getType() == Word.INT_CONST) {
                    // F -> num
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "num"));

                } else if (firstWord.getProperty().equals("(")) {
                    // F -> (L)
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "("));
                    analyseStack.add(1, L);
                    analyseStack.add(2, new AnalyseNode(AnalyseNode.TERMINAL, ")"));

                } else {
                    errorCount++;
                    error = new Error(errorCount, "不能进行算术运算的数据类型或括号不匹配", firstWord.getLine(), firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;

            case 'P':
                if (firstWord.getType() == Word.IDENTIFIER) {
                    // P -> identifier
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "identifier"));


                } else if (firstWord.getType() == Word.INT_CONST) {
                    // P -> num
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "num"));


                } else {
                    errorCount++;
                    ;
                    error = new Error(errorCount, "不能输出的数据类型", firstWord.getLine(), firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;

            default:
                graErrorFlag = true;

        }

    }

    /**
     * 返回语法分析结果
     *
     * @return true false
     */
    public boolean isGraErrorFlag() {
        return graErrorFlag;
    }


}
