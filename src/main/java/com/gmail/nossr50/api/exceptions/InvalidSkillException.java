package com.gmail.nossr50.api.exceptions;

import java.io.Serial;

public class InvalidSkillException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 942705284195791157L;

    public InvalidSkillException() {
        super("That is not a valid skill.");
    }

    public InvalidSkillException(String msg) {
        super(msg);
    }
}
