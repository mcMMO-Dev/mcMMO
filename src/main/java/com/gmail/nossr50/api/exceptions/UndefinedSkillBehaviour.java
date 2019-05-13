package com.gmail.nossr50.api.exceptions;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;

public class UndefinedSkillBehaviour extends RuntimeException {
    public UndefinedSkillBehaviour(PrimarySkillType primarySkillType) {
        super("Undefined behaviour for skill! - "+primarySkillType.toString());
    }
}
