package com.gmail.nossr50.util.compat.layers.bungee;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class AbstractBungeeSerializerCompatibilityLayer {

    public abstract @NonNull Component deserialize(final @NonNull BaseComponent @NonNull [] input);

}
