package com.gmail.nossr50.datatypes.skills.interfaces;

import com.gmail.nossr50.datatypes.skills.SubSkillType;

/**
 * This interface is mostly here to maintain backwards compatibility with other mcMMO plugins Only
 * Core Skills will make use of this Previously in mcMMO subskills were basically defined by the
 * SecondaryAbility ENUM In the new system which I'm gradually converting all the existing skills
 * to, skills instead are unique instances of AbstractSubSkill
 */
public interface CoreSkill {
    /**
     * Gets the associated SubSkillType for this subskill
     *
     * @return the associated SubSkillType ENUM definition
     */
    SubSkillType getSubSkillType();
}
