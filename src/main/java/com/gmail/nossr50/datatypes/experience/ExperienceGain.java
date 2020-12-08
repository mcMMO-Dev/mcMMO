package com.gmail.nossr50.datatypes.experience;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface ExperienceGain {
    /**
     * Get the target skill for this XP gain
     * We define this by a String to allow for custom skills
     * @return The target skill
     */
    @NotNull UUID getTargetSkill();

    /**
     * Value of the experience gain, this is the raw value before any mutations are done via modifiers or otherwise
     * @return the value of this {@link ExperienceGain}
     */
    int getValue();
}
