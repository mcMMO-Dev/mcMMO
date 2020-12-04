package com.gmail.nossr50.datatypes.experience.capture;

import com.gmail.nossr50.datatypes.experience.context.ExperienceContext;
import com.neetgames.mcmmo.skill.SkillIdentity;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class ExperienceCapture {
    private @NotNull ExperienceContext experienceContext;
    private @NotNull HashSet<SkillIdentity> affectedSkills;

    public ExperienceCapture(@NotNull ExperienceContext experienceContext, @NotNull HashSet<SkillIdentity> affectedSkills) {
        this.experienceContext = experienceContext;
        this.affectedSkills = affectedSkills;
    }

    /**
     * Check whether or not a skill is targeted in this experience capture
     * @param skillIdentity target skill
     * @return true if this skill is targeted in this experience capture
     */
    public boolean isSkillAffected(@NotNull SkillIdentity skillIdentity) {
        return affectedSkills.contains(skillIdentity);
    }
}
