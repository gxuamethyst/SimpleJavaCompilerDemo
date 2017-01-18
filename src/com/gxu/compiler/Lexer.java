package com.gxu.compiler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 词法分析器
 * 使用：
 * Lexer lexer=new Lexer();
 * 调用lexer.lexicalAnalysisProcess(filePath);执行词法分析过程，需要传入文件路径
 * 调用lexer.getWords();获取单词表
 * 调用lexer.getErrorWords();获取错误的单词集合
 * 调用lexer.isLexicalCorrect();获取词法分析结果
 */
class Lexer {

    private List<Word> mWords;      // 单词表
    private List<Word> mErrorWords;     // 出现错误的单词集合
    private boolean isLexicalCorrect;   // 词法分析结果

    public List<Word> getWords() {
        return mWords;
    }

    public List<Word> getErrorWords() {
        return this.mErrorWords;
    }

    public boolean isLexicalCorrect() {
        return this.isLexicalCorrect;
    }

    /**
     * 构造函数，进行初始化操作
     */
    Lexer() {
        mWords = new ArrayList<Word>();
        mErrorWords = new ArrayList<Word>();
        isLexicalCorrect = false;
    }

    /**
     * 词法分析过程
     *
     * @param codeStr 需要分析的字符串
     * @param line    字符串所在行数
     */
    private void lexicalAnalysis(String codeStr, int line) {
        int currentIndex = 0;
        int endIndex = 0;
        while (currentIndex < codeStr.length()) {
            char ch = codeStr.charAt(currentIndex);
            // 跳过空白
            if (ch == ' ' || ch == '\t' || ch == '\n') {
                currentIndex++;
                endIndex++;
                continue;
            }
            /* 关键字/标识符 */
            if (isLetter(ch)) {
                while (true) {
                    if (endIndex == codeStr.length()) {
                        break;
                    }
                    if (isLetter(codeStr.charAt(endIndex)) || isNum(codeStr.charAt(endIndex))) {
                        endIndex++;
                    } else {
                        break;
                    }
                }
                String str = codeStr.substring(currentIndex, endIndex);
                int type;
                if (Word.keywords.contains(str)) {
                    type = Word.KEYWORDS;
                } else {
                    type = Word.IDENTIFIER;
                }
                mWords.add(new Word(type, str, line));
                currentIndex = endIndex;
            }
            // 整型常量
            else if (isNum(ch)) {
                // 以数字开头，但不一定以数字结尾，中间可能有字母，是错误的
                while (true) {
                    if (endIndex == codeStr.length()) {
                        break;
                    }
                    if (isLetter(codeStr.charAt(endIndex)) || isNum(codeStr.charAt(endIndex))) {
                        endIndex++;
                    } else {
                        break;
                    }
                }
                String str = codeStr.substring(currentIndex, endIndex);
                int type = Word.INT_CONST;
                for (int i = 0; i < str.length(); i++) {
                    if (isLetter(str.charAt(i))) {
                        type = Word.ERROR;
                    }
                }
                mWords.add(new Word(type, codeStr.substring(currentIndex, endIndex), line));
                currentIndex = endIndex;
            }
            // 其他字符
            else {
                int type;
                switch (ch) {
                    case '(':
                    case ')':
                    case '[':
                    case ']':
                    case '{':
                    case '}':
                    case ';':
                    case ',':
                        type = Word.DELIMITER;
                        break;
                    case '=':
                    case '>':
                    case '<':
                        if (codeStr.charAt(currentIndex + 1) == '=') {
                            // == >= <=
                            endIndex++;
                        }
                        type = Word.OPERATOR;
                        break;
                    case '!':
                        if (codeStr.charAt(currentIndex + 1) == '=') {
                            // !=
                            endIndex++;
                            type = Word.OPERATOR;
                        } else {
                            type = Word.ERROR;
                        }
                        break;
                    case '+':
                    case '-':
                    case '*':
                    case '/':
                        type = Word.OPERATOR;
                        break;
                    default:
                        type = Word.ERROR;
                }
                mWords.add(new Word(type, codeStr.substring(currentIndex, endIndex + 1), line));
                currentIndex = endIndex + 1;
                endIndex = currentIndex;
            }
        }
    }

    /**
     * 检查词法分析结果
     */
    private void checkError() {
        for (int i = 0; i < mWords.size(); i++) {
            if (mWords.get(i).getType() == Word.ERROR) {
                mErrorWords.add(mWords.get(i));
            }
        }
        isLexicalCorrect = mErrorWords.isEmpty();
    }

    /**
     * 执行词法分析过程
     *
     * @param filePath 文件路径
     */
    public void lexicalAnalysisProcess(String filePath) {
        try {
            // 文件读取操作
            FileInputStream fileInputStream = new FileInputStream(filePath);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            InputStreamReader inputStreamReader = new InputStreamReader(bufferedInputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String codeStr;
            int line = 1;
            while ((codeStr = bufferedReader.readLine()) != null) {
                lexicalAnalysis(codeStr, line);
                line++;
            }
            mWords.add(new Word(Word.EOF, "#", line));
            // 检查词法分析结果
            checkError();

            fileInputStream.close();
            bufferedInputStream.close();
            inputStreamReader.close();
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断给定字符是否是数字
     *
     * @param c 需要进行判断的字符
     * @return true/false
     */
    private boolean isNum(char c) {
        return Character.isDigit(c);
    }

    /**
     * 判断给定的字符是否是字母
     *
     * @param c 需要进行判断的字符
     * @return true/false
     */
    private boolean isLetter(char c) {
        return Character.isLetter(c);
    }

}
