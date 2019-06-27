package com.gmail.nossr50.api.exceptions;

public class InvalidFormulaTypeException extends RuntimeException {
    private static final long serialVersionUID = 3368670229490121886L;

    public InvalidFormulaTypeException() {
        super("That is not a valid FormulaType.");
    }
}
