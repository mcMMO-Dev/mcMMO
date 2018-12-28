package com.gmail.nossr50.datatypes.skills;

/**
 * This class represents a gated subskill
 * A gated subskill is a subskill that requires a certain level to unlock
 * This class is mostly to make it easier to grab information about subskills for a player
 */
public class SkillMilestone {
    private int unlockLevel; //Level that grants access to this skill
    private SecondaryAbility subskill; //Subskill that this milestone belongs to
    private SkillMilestone childMilestone; //Next rank in the milestone

    public SkillMilestone(SecondaryAbility subskill, int unlockLevel, SkillMilestone childMilestone)
    {
        this.subskill = subskill;
        this.unlockLevel = unlockLevel;

        //Assign a child subskill if it exists
        if(childMilestone != null)
            this.childMilestone = childMilestone;
    }

    public SkillMilestone(SecondaryAbility subskill, int unlockLevel)
    {
        this(subskill, unlockLevel, null);
    }

    public int getUnlockLevel() {
        return unlockLevel;
    }

    public SecondaryAbility getSubskill() {
        return subskill;
    }

    public SkillMilestone getChildMilestone() {
        return childMilestone;
    }
}
