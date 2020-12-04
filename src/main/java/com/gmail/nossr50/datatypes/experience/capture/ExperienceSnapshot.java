package com.gmail.nossr50.datatypes.experience.capture;

import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SkillIdentity;
import org.jetbrains.annotations.NotNull;

public interface ExperienceSnapshot {
    /**
     * Check whether or not a skill is targeted in this experience capture
     *
     * @param skillIdentity target skill
     * @return true if this skill is targeted in this experience capture
     */
    boolean isSkillAffected(@NotNull SkillIdentity skillIdentity);

    /**
     * Check whether or not a skill is targeted in this experience capture
     *
     * @param skillId target skill
     * @return true if this skill is targeted in this experience capture
     */
    boolean isSkillAffected(@NotNull String skillId);

    /**
     * Check whether or not a skill is targeted in this experience capture
     *
     * @param primarySkillType target skill
     * @return true if this skill is targeted in this experience capture
     * @deprecated the {@link PrimarySkillType} type is going to be phased out in favour of {@link SkillIdentity} at some point in the future
     */
    @Deprecated
    boolean isSkillAffected(@NotNull PrimarySkillType primarySkillType);

    @NotNull PlayerProfile[] getPlayers();
}
