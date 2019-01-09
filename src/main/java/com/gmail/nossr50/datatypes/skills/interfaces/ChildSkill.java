package com.gmail.nossr50.datatypes.skills.interfaces;

import com.gmail.nossr50.datatypes.skills.PrimarySkill;

public interface ChildSkill extends Skill {
    /**
     * Get's the other parent for this Skill
     * @return the other parent
     */
    PrimarySkill getSecondParent();
}
