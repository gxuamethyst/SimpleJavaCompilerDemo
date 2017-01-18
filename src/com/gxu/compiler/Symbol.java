package com.gxu.compiler;

/**
 * 符号类，用于符号表
 */
public class Symbol {

    private String mType;   // 符号类型
    private String mProperty;     // 符号值

    public String getType() {
        return mType;
    }

    public String getProperty() {
        return mProperty;
    }

    public Symbol(String type, String property) {
        this.mType = type;
        this.mProperty = property;
    }

}
