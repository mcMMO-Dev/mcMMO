package com.gmail.nossr50.util.compat.layers.world;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class WorldCompatibilityLayer_1_16_4 implements WorldCompatibilityLayer {
    @Override
    public int getMinWorldHeight(@NotNull World world) {
        return world.getMinHeight();
    }

    @Override
    public int getMaxWorldHeight(@NotNull World world) {
        return world.getMaxHeight();
    }
}
