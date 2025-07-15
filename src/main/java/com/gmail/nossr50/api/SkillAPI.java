package com.gmail.nossr50.api;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.skills.SkillTools;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SkillAPI {
    private SkillAPI() {
    }

    /**
     * Returns a list of strings with mcMMO's skills This includes parent and child skills
     * </br>
     * This function is designed for API usage.
     *
     * @return a list of strings with valid skill names
     */
    public static List<String> getSkills() {
        return getListFromEnum(Arrays.asList(PrimarySkillType.values()));
    }

    /**
     * Returns a list of strings with mcMMO's skills This only includes parent skills
     * </br>
     * This function is designed for API usage.
     *
     * @return a list of strings with valid skill names
     */
    public static List<String> getNonChildSkills() {
        return getListFromEnum(SkillTools.NON_CHILD_SKILLS);
    }

    /**
     * Returns a list of strings with mcMMO's skills This only includes child skills
     * </br>
     * This function is designed for API usage.
     *
     * @return a list of strings with valid skill names
     */
    public static List<String> getChildSkills() {
        return getListFromEnum(mcMMO.p.getSkillTools().CHILD_SKILLS);
    }

    /**
     * Returns a list of strings with mcMMO's skills This only includes combat skills
     * </br>
     * This function is designed for API usage.
     *
     * @return a list of strings with valid skill names
     */
    public static List<String> getCombatSkills() {
        return getListFromEnum(mcMMO.p.getSkillTools().COMBAT_SKILLS);
    }

    /**
     * Returns a list of strings with mcMMO's skills This only includes gathering skills
     * </br>
     * This function is designed for API usage.
     *
     * @return a list of strings with valid skill names
     */
    public static List<String> getGatheringSkills() {
        return getListFromEnum(mcMMO.p.getSkillTools().GATHERING_SKILLS);
    }

    /**
     * Returns a list of strings with mcMMO's skills This only includes misc skills
     * </br>
     * This function is designed for API usage.
     *
     * @return a list of strings with valid skill names
     */
    public static List<String> getMiscSkills() {
        return getListFromEnum(mcMMO.p.getSkillTools().MISC_SKILLS);
    }

    private static List<String> getListFromEnum(List<PrimarySkillType> skillsTypes) {
        List<String> skills = new ArrayList<>();

        for (PrimarySkillType primarySkillType : skillsTypes) {
            skills.add(primarySkillType.name());
        }

        return skills;
    }
}
