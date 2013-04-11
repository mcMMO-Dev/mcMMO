package com.gmail.nossr50.api.exceptions;

public class InvalidSkillException extends RuntimeException {
    private static final long serialVersionUID = 942705284195791157L;

    public InvalidSkillException() {
        super("That is not a valid skill.");
    }
}
