/**
 * 分析栈节点类：
 * String type 节点类型
 * String name 节点名
 */
package com.gxu.compiler;

import java.util.ArrayList;

public class AnalyseNode {

    public final static String NONTERMINAL = "非终结符";
    public final static String TERMINAL = "终结符";
    public final static String EOF = "结束符";
    private static ArrayList<String> nonterminal = new ArrayList<String>();//非终结符集合

    private String type;    //节点类型
    private String name;    //节点名

    static {
        //添加“非终结符”进nonterminal
        //S,A,B,C,X,Y,R,Z,Z’,U,U’,E,D,L,L’,T,T’,F,P
        nonterminal.add("S");
        nonterminal.add("A");
        nonterminal.add("B");
        nonterminal.add("C");
        nonterminal.add("X");
        nonterminal.add("Y");
        nonterminal.add("R");
        nonterminal.add("Z");
        nonterminal.add("Z'");
        nonterminal.add("U");
        nonterminal.add("U'");
        nonterminal.add("E");
        nonterminal.add("D");
        nonterminal.add("L");
        nonterminal.add("L'");
        nonterminal.add("T");
        nonterminal.add("T'");
        nonterminal.add("F");
        nonterminal.add("P");

    }

    public AnalyseNode() {

    }

    //构造函数
    public AnalyseNode(String type, String name) {
        this.type = type;
        this.name = name;

    }

    //1.判断一个分析栈节点是否为"非终结符"
    public static boolean isNonterminal(AnalyseNode node) {
        return nonterminal.contains(node.name);
    }

    //2.判断一个分析栈节点是否为"终结符"
    public static boolean isTerminal(AnalyseNode node) {
        return Word.isKeyword(node.name) || Word.isOperator(node.name) ||
                Word.isDelimiter(node.name) || node.name.equals("identifier") ||
                node.name.equals("num");
    }

    //setter  getter
    public static ArrayList<String> getNonterminal() {
        return nonterminal;
    }

    public static void setNonterminal(ArrayList<String> nonterminal) {
        AnalyseNode.nonterminal = nonterminal;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
