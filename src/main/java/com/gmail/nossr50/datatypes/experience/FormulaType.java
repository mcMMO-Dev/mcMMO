package com.gmail.nossr50.datatypes.experience;

public enum FormulaType {
    LINEAR,
    EXPONENTIAL,
    UNKNOWN;

    public static FormulaType getFormulaType(String string) {
        try {
            return valueOf(string);
        } catch (IllegalArgumentException ex) {
            return UNKNOWN;
        }
    }
}
