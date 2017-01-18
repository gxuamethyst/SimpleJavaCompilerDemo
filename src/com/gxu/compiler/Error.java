/*
 * 错误类
 */
package com.gxu.compiler;

public class Error {

    private int id;      //错误序号
    private String errorInfo;  //错误信息
    private int line;    //错误所在行
    private Word word;    //错误的单词

    public Error() {

    }

    public Error(int id, String errorInfo, int line, Word word) {
        this.id = id;
        this.errorInfo = errorInfo;
        this.line = line;
        this.word = word;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }

}
