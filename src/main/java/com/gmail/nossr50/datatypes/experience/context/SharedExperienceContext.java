package com.gmail.nossr50.datatypes.experience.context;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SharedExperienceContext {
    /**
     * The {@link McMMOPlayer} who originally gained the XP that was then shared
     * @return the {@link McMMOPlayer} to which this experience context originates
     */
    @NotNull McMMOPlayer getSharedContextSource();
}
