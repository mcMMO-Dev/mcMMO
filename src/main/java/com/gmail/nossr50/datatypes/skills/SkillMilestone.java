package com.gmail.nossr50.datatypes.skills;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.player.PlayerProfile;

/**
 * This class represents a gated subskill
 * A SkillMilestone is a representation of a specific rank for a subskill
 * A SkillMilestone may contain a child, the child represents the next rank of the subskill
 * This class is mostly to make it easier to grab information about subskills for a player
 */
public class SkillMilestone {
    private final int unlockLevel; //Level that grants access to this skill
    private final SubSkill subskill; //Subskill that this milestone belongs to
    private SkillMilestone childMilestone; //Next rank in the milestone
    private SkillMilestone lastChild; //The final child milestone in this chain
    private final int curRank; //The current rank of this SkillMilestone

    public SkillMilestone(SubSkill subskill, int curRank)
    {
        this.subskill = subskill;
        this.curRank = curRank;
        this.unlockLevel = AdvancedConfig.getInstance().getSubSkillUnlockLevel(subskill, curRank);
    }

    /**
     * Gets the level requirement for this subskill's rank
     * @return The level required to use this subskill
     */
    public int getUnlockLevel() {
        return unlockLevel;
    }

    /**
     * Get's the current milestone the player is working towards
     * @param playerProfile
     * @return
     */
    public SkillMilestone getCurrentMilestoneForPlayer(PlayerProfile playerProfile)
    {
        if(playerProfile.getSkillLevel(subskill.getParentSkill()) >= unlockLevel)
        {
            if(childMilestone != null)
                return childMilestone.getCurrentMilestoneForPlayer(playerProfile);

            return this;
        } else {
            return this;
        }
    }

    /**
     * Gets the associated SubSkill for this milestone
     * @return
     */
    public SubSkill getSubskill() {
        return subskill;
    }

    /**
     * Gets the child milestone, which is the next rank of this skill
     * @return The child milestone of this skill
     */
    public SkillMilestone getChildMilestone() {
        return childMilestone;
    }

    /**
     * Adds a child milestone, which represents the next rank of this skill
     */
    public void addChildMilestone()
    {
        childMilestone = new SkillMilestone(this.subskill, curRank+1);
    }

    /**
     * This grabs the final child in the chain of child milestones, which represents the last rank of this subskill
     * @return The final child for this SkillMilestone, which is the final rank for the associated subskill. Null if this Milestone has no children.
     */
    public SkillMilestone getFinalChild()
    {
        //Return lastchild if we already have the ref stored
        if(lastChild != null)
            return lastChild;

        //If the next child doesn't exist return this
        if(childMilestone == null) {
            return this;
        }

        //If we have children, find their children until the chain stops and store that reference and return it
        return lastChild = childMilestone.getFinalChild();
    }

    /**
     * Gets the current rank of this SkillMilestone for the associated subskill
     * @return The current rank of this subskill
     */
    public int getCurRank() { return curRank; }

    /**
     * The requirement for the next rank of this subskill
     * @return The level requirement for the next rank of this SkillMilestone chain
     */
    public int getNextRankReq() { return childMilestone.unlockLevel; }
}
