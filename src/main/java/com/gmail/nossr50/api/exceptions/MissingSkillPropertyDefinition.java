package com.gmail.nossr50.api.exceptions;

public class MissingSkillPropertyDefinition extends RuntimeException {
    public MissingSkillPropertyDefinition(String details) {
        super("A skill property is undefined! Details: " + details);
    }
}
