package com.gmail.nossr50.datatypes.skills.interfaces;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;

public interface ChildSkill extends Skill {
    /**
     * Get's the other parent for this Skill
     *
     * @return the other parent
     */
    PrimarySkillType getSecondParent();
}
