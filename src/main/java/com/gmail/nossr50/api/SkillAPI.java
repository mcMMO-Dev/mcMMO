package com.gmail.nossr50.api;

import java.util.ArrayList;
import java.util.List;

import com.gmail.nossr50.datatypes.skills.SkillType;

public final class SkillAPI {
    private SkillAPI() {}

    /**
     * Returns a list of strings with mcMMO's skills
     * This includes parent and child skills
     * </br>
     * This function is designed for API usage.
     *
     * @return a list of strings with valid skill names
     */
    public static List<String> getSkills() {
        return SkillType.skillNames;
    }

    /**
     * Returns a list of strings with mcMMO's skills
     * This only includes parent skills
     * </br>
     * This function is designed for API usage.
     *
     * @return a list of strings with valid skill names
     */
    public static List<String> getNonChildSkills() {
        return getStringListFromSkillList(SkillType.nonChildSkills);
    }

    /**
     * Returns a list of strings with mcMMO's skills
     * This only includes child skills
     * </br>
     * This function is designed for API usage.
     *
     * @return a list of strings with valid skill names
     */
    public static List<String> getChildSkills() {
        return getStringListFromSkillList(SkillType.childSkills);
    }

    /**
     * Returns a list of strings with mcMMO's skills
     * This only includes combat skills
     * </br>
     * This function is designed for API usage.
     *
     * @return a list of strings with valid skill names
     */
    public static List<String> getCombatSkills() {
        return getStringListFromSkillList(SkillType.combatSkills);
    }

    /**
     * Returns a list of strings with mcMMO's skills
     * This only includes gathering skills
     * </br>
     * This function is designed for API usage.
     *
     * @return a list of strings with valid skill names
     */
    public static List<String> getGatheringSkills() {
        return getStringListFromSkillList(SkillType.gatheringSkills);
    }

    /**
     * Returns a list of strings with mcMMO's skills
     * This only includes misc skills
     * </br>
     * This function is designed for API usage.
     *
     * @return a list of strings with valid skill names
     */
    public static List<String> getMiscSkills() {
        return getStringListFromSkillList(SkillType.miscSkills);
    }

    private static List<String> getStringListFromSkillList(List<SkillType> skillsTypes) {
        List<String> skills = new ArrayList<String>();

        for (SkillType skillType : skillsTypes) {
            skills.add(skillType.getName());
        }

        return skills;
    }
}
