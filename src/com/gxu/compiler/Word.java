package com.gxu.compiler;

import java.util.ArrayList;
import java.util.List;

/**
 * 单词类
 */
public class Word {
    // 单词类别
    public static final int KEYWORDS = 1;  // 关键字
    public static final int IDENTIFIER = 2;     // 标识符
    public static final int INT_CONST = 3;     // 整型常量
    public static final int OPERATOR = 4;       // 操作符
    public static final int DELIMITER = 5;      // 界符
    public static final int ERROR = 6;      // 未知错误 类型
    public static final int EOF = 7;          // 结束符
    public static List<String> keywords = new ArrayList<String>();      // 关键字集合
    public static List<String> operators = new ArrayList<String>();     // 操作符集合
    public static List<String> delimiters = new ArrayList<String>();    // 界符集合

    // 初始化操作
    static {
        keywords.add("public");
        keywords.add("static");
        keywords.add("void");
        keywords.add("main");
        keywords.add("String");
        keywords.add("args");
        keywords.add("if");
        keywords.add("else");
        keywords.add("int");
        keywords.add("while");

        operators.add("+");
        operators.add("-");
        operators.add("*");
        operators.add("/");
        operators.add("=");
        operators.add("<");
        operators.add(">");
        operators.add("<=");
        operators.add(">=");
        operators.add("==");
        operators.add("!=");

        delimiters.add("(");
        delimiters.add(")");
        delimiters.add("[");
        delimiters.add("]");
        delimiters.add("{");
        delimiters.add("}");
        delimiters.add(",");
        delimiters.add(";");
    }

    private int mType;   // 单词类型
    private String mProperty;  // 单词属性
    private int mLine;  // 单词所在行

    public int getType() {
        return mType;
    }

    public String getProperty() {
        return mProperty;
    }

    public int getLine() {
        return mLine;
    }

    public Word(int type, String property, int line) {
        this.mType = type;
        this.mProperty = property;
        this.mLine = line;
    }

    /**
     * 根据类型值获取类型名
     *
     * @param type 类型值
     * @return 类型名
     */
    public static String getTypeName(int type) {
        String str = "";
        switch (type) {
            case 1:
                str = "关键字";
                break;
            case 2:
                str = "标识符";
                break;
            case 3:
                str = "整型常量";
                break;
            case 4:
                str = "操作符";
                break;
            case 5:
                str = "界符";
                break;
            case 6:
                str = "未知错误类型";
                break;
            case 7:
                str = "结束符";
                break;
            default:
                str = "";
        }
        return str;
    }

    /**
     * 判断给定的单词属性字符串是否是关键字(供语法分析调用)
     *
     * @param word 属性字符串
     * @return true/false
     */
    public static boolean isKeyword(String word) {
        return keywords.contains(word);
    }

    /**
     * 判断给定的单词属性字符串是否是运算符(供语法分析调用)
     *
     * @param word 属性字符串
     * @return true/false
     */
    public static boolean isOperator(String word) {
        return operators.contains(word);
    }

    /**
     * 判断给定的单词属性字符串是否是界符(供语法分析调用)
     *
     * @param word 属性字符串
     * @return true/false
     */
    public static boolean isDelimiter(String word) {
        return delimiters.contains(word);
    }

    /**
     * 判断给定的单词属性字符串是否是算术运算符(供语义分析调用)
     *
     * @param word 属性字符串
     * @return true/false
     */
    public static boolean isArithmeticOperator(String word) {
        return word.equals("+") || word.equals("-") || word.equals("*") || word.equals("/");
    }

    /**
     * 判断给定的单词属性字符串是否是关系运算符(供语义分析调用)
     *
     * @param word 属性字符串
     * @return true/false
     */
    public static boolean isRelationalOperator(String word) {
        return word.equals("<") || word.equals(">") || word.equals(">=") || word.equals("<=") || word.equals("==") || word.equals("!=");
    }

}
