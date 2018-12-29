package com.gmail.nossr50.util.skills;

import com.gmail.nossr50.datatypes.skills.SubSkill;
import com.gmail.nossr50.datatypes.skills.PrimarySkill;

import java.util.HashMap;

/**
 * This class will handle the following things
 * 1) Informing the player of important milestones in a skill (Every 5 levels)
 * 2) Getting lists of milestones (skill unlocks) a player has
 * 3) Propagating events for milestones (API)
 *
 * By setting up a skill milestone system it will make managing the audio/visual feedback for progression a lot easier
 *
 */
//TODO: Inform players of milestones
//TODO: Helper methods for getting milestones
//TODO: Propagate events when milestones are achieved
//TODO: Update existing parts of the codebase to use this where appropriate
public class SkillMilestoneManager {
    public static final HashMap<PrimarySkill, SubSkill> subskillMilestones;

    static {
        //Init our maps
        subskillMilestones = new HashMap<>();

        for(PrimarySkill primarySkill : PrimarySkill.values())
        {
            //TODO: Setup these values
        }
    }
}
