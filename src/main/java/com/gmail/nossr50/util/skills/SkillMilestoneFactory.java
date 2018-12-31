package com.gmail.nossr50.util.skills;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.skills.SkillMilestone;
import com.gmail.nossr50.datatypes.skills.SubSkill;

import java.util.HashMap;

/**
 * This Factory class builds SkillMilestone chains as needed
 * SkillMilestones are stored in a hash map
 */
public class SkillMilestoneFactory {

    private static HashMap<SubSkill, SkillMilestone> skillMilestoneMap;

    /**
     * Gets a the SkillMilestone chain for this subskill
     * Builds that chain if it doesn't exist before returning the parent node
     * @param subSkill The SubSkill to get the SkillMilestone chain for
     * @return The parent node of the SkillMilestone chain for the target subskill
     */
    public static SkillMilestone getSkillMilestone(SubSkill subSkill)
    {
        //Init the map
        if(skillMilestoneMap == null)
            skillMilestoneMap = new HashMap<>();

        if(skillMilestoneMap.get(subSkill) == null)
            return buildSkillMilestone(subSkill);
        else
            return skillMilestoneMap.get(subSkill);
    }

    /**
     * Constructs a SkillMilestone chain for a given subskill
     * @param subSkill The subskill to build the SkillMilestone chain for
     * @return The base node of the SkillMilestone chain
     */
    private static SkillMilestone buildSkillMilestone(SubSkill subSkill)
    {
        //Init our parent node
        SkillMilestone newSkillMilestone = new SkillMilestone(subSkill, AdvancedConfig.getInstance().getSubSkillUnlockLevel(subSkill, 1));

        //There's probably a better way to do this
        for(int x = 0; x < subSkill.getNumRanks()-1; x++)
        {
            newSkillMilestone.addChildMilestone();
        }

        //DEBUG
        System.out.println("Milestone constructed for "+subSkill);
        return skillMilestoneMap.put(subSkill, newSkillMilestone);
    }
}