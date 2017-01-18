package com.gxu.compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 测试编译器的功能
 */
public class CompilerTester {

    private static final String filePath = "D:\\Projects\\Java\\CompilerDemo\\testCase\\TestCase.java";     // 待分析的源代码文件路径
    private static List<Word> wordList = new ArrayList<>();  // 单词表
    private static boolean isLexicalCorrect = false;  // 词法分析通过标志

    private static boolean isSyntaxCorrect = false;   // 语法分析通过标志

    private static boolean isSemanticCorrect = false;     // 语义分析通过标志
    private static List<Quadruple> quadrupleList = new ArrayList<>(); // 四元式序列
    private static List<Symbol> sSymbols = new ArrayList<>();         // 符号表
    private static List<String> tempVarList = new ArrayList<>();      // 四元式临时变量集合

    public static void main(String[] args) {
        testLexer();
        testSyntaxAnalyzer();
        testSemanticAnalyzer();
        testCodeGenerator();
    }

    private static void testLexer() {
        System.out.println("----------- 词法分析过程 -----------");
        Lexer lexer = new Lexer();
        lexer.lexicalAnalysisProcess(filePath);
        wordList = lexer.getWords();
        System.out.println("单词序列：");
        for (Word word : wordList) {
            System.out.println(word.getType() + "\t\t" + Word.getTypeName(word.getType()) + "\t\t" + word.getProperty());
        }
        List<Word> errors = lexer.getErrorWords();
        if (!errors.isEmpty()) {
            System.out.println("词法分析结果：词法分析失败!");
            System.out.println("错误信息：");
            for (Word error : errors) {
                System.out.println("单词" + error.getProperty() + "\t\t所在行：line " + error.getLine());
            }
        } else {
            System.out.println("词法分析结果：词法分析通过!");
            isLexicalCorrect = true;
        }
    }

    private static void testSyntaxAnalyzer() {
        if (!isLexicalCorrect) {
            System.out.println("词法分析未通过，不能进入语法分析阶段!");
        } else if (wordList.isEmpty()) {
            System.out.println("未进行词法分析，不能进入语法分析阶段!");
        } else {
            List<Word> words = new ArrayList<>();
            words.addAll(wordList);
            Parser parser = new Parser(words, isLexicalCorrect);
            isSyntaxCorrect = !parser.isGraErrorFlag();
        }
    }

    private static void testSemanticAnalyzer() {
        if (wordList.isEmpty()) {
            System.out.println("单词表为空，不能进入语义分析阶段！");
            return;
        } else if (!isSyntaxCorrect) {
            System.out.println("语法分析未通过，不能进入语义分析阶段！");
            return;
        }

        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
        semanticAnalyzer.SemanticAnalysis(wordList, isSyntaxCorrect);
        System.out.println();
        if (semanticAnalyzer.isSuccess()) {
            System.out.println("语义分析成功，生成的四元式序列如下：");
            isSemanticCorrect = true;
            quadrupleList = semanticAnalyzer.getQuadrupleList();
            tempVarList = semanticAnalyzer.getTempVarList();
            sSymbols = semanticAnalyzer.getSymbolList();
            for (Quadruple quadruple : quadrupleList) {
                System.out.println(quadruple.toString());
            }
        } else {
            System.out.println("语义分析失败！");
        }
    }

    private static void testCodeGenerator() {
        if (isSemanticCorrect) {
            CodeGenerator codeGenerator = new CodeGenerator();
            String assemblyCodeStr = codeGenerator.getAssemblyCodeStr(quadrupleList, sSymbols, tempVarList);
            System.out.println("\n代码生成成功，生成的汇编代码如下：");
            System.out.println(assemblyCodeStr);
        } else {
            System.out.println("语义分析失败，不能进行汇编代码生成！");
        }
    }

}
