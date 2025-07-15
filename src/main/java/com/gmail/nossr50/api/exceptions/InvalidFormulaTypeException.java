package com.gmail.nossr50.api.exceptions;

import java.io.Serial;

public class InvalidFormulaTypeException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 3368670229490121886L;

    public InvalidFormulaTypeException() {
        super("That is not a valid FormulaType.");
    }
}
