package com.gmail.nossr50.datatypes.experience.context;

import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockExperienceContext implements ExperienceContext {

    @NotNull Block blockExperienceContext;

    public BlockExperienceContext(@NotNull Block block) {
        this.blockExperienceContext = block;
    }

    @Nullable
    @Override
    public Object getContext() {
        return blockExperienceContext;
    }

    /**
     * Get the Block involved in this experience context
     *
     * @return the {@link Block} involved in this experience context
     */
    public @NotNull Block getBlockExperienceContext() {
        return blockExperienceContext;
    }
}
