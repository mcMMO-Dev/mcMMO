package com.gmail.nossr50.api;

import java.util.ArrayList;
import java.util.Arrays;
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
        return getListFromEnum(Arrays.asList(SkillType.values()));
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
        return getListFromEnum(SkillType.NON_CHILD_SKILLS);
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
        return getListFromEnum(SkillType.CHILD_SKILLS);
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
        return getListFromEnum(SkillType.COMBAT_SKILLS);
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
        return getListFromEnum(SkillType.GATHERING_SKILLS);
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
        return getListFromEnum(SkillType.MISC_SKILLS);
    }

    private static List<String> getListFromEnum(List<SkillType> skillsTypes) {
        List<String> skills = new ArrayList<String>();

        for (SkillType skillType : skillsTypes) {
            skills.add(skillType.name());
        }

        return skills;
    }
}
