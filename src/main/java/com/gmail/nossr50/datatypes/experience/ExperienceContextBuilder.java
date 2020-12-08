package com.gmail.nossr50.datatypes.experience;

import com.neetgames.mcmmo.experience.context.NullExperienceContext;
import org.jetbrains.annotations.NotNull;

public class ExperienceContextBuilder {

    private static final @NotNull NullExperienceContext nullExperienceContext = new NullExperienceContext();

    /**
     * Return a null experience context
     * @return a null experience context
     */
    public static NullExperienceContext nullContext() {
        return nullExperienceContext;
    }

}
