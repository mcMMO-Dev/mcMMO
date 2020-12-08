package com.gmail.nossr50.datatypes.experience.context;

import com.neetgames.jmal.Block;
import com.neetgames.mcmmo.experience.context.BlockExperienceContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockExperienceContextImpl implements BlockExperienceContext {
    @NotNull Block blockExperienceContext;

    public BlockExperienceContextImpl(@NotNull Block block) {
        this.blockExperienceContext = block;
    }

    @Nullable
    @Override
    public Object getContext() {
        return blockExperienceContext;
    }

    public @NotNull Block getBlockExperienceContext() {
        return blockExperienceContext;
    }
}
