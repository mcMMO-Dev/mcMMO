package com.gmail.nossr50.util.compat.layers.bungee;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.checkerframework.checker.nullness.qual.NonNull;

public class BungeeModernSerializerCompatibilityLayer extends
        AbstractBungeeSerializerCompatibilityLayer {
    @Override
    public @NonNull Component deserialize(@NonNull BaseComponent @NonNull [] input) {
        return BungeeComponentSerializer.get().deserialize(input);
    }
}
