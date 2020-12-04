package com.gmail.nossr50.datatypes.experience.context;

import javax.annotation.Nullable;

public interface ExperienceContext {
    /**
     * The source for this experience gain, can be anything from a block to an entity, etc
     * Context is available as long as it can be
     * @return the context (source) of this experience
     */
    @Nullable Object getContext();
}
