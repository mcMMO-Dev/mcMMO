package com.gmail.nossr50.util.compat.layers.world;

import com.gmail.nossr50.util.compat.CompatibilityLayer;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public interface WorldCompatibilityLayer extends CompatibilityLayer {
    default int getMinWorldHeight(@NotNull World world) { return 0; }

    default int getMaxWorldHeight(@NotNull World world) { return 255; }
}
