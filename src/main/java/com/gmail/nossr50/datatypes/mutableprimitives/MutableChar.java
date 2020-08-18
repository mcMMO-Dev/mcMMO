package com.gmail.nossr50.datatypes.mutableprimitives;

public class MutableChar {

    private char charValue;

    public MutableChar(char charValue) {
        this.charValue = charValue;
    }

    public char getImmutableCopy() {
        return charValue;
    }

    public void setChar(char charValue) {
        this.charValue = charValue;
    }

}