package com.gmail.nossr50.core.skills.interfaces;

import com.gmail.nossr50.core.skills.PrimarySkillType;

public interface ChildSkill extends Skill {
    /**
     * Get's the other parent for this Skill
     *
     * @return the other parent
     */
    PrimarySkillType getSecondParent();
}
