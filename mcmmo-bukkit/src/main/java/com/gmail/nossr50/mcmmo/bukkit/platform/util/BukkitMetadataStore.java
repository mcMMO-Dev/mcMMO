package com.gmail.nossr50.mcmmo.bukkit.platform.util;

import com.gmail.nossr50.mcmmo.api.data.MMOEntity;
import com.gmail.nossr50.mcmmo.api.platform.util.MetadataKey;
import com.gmail.nossr50.mcmmo.api.platform.util.MetadataStore;
import com.gmail.nossr50.mcmmo.bukkit.BukkitBootstrap;

import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unchecked")
public class BukkitMetadataStore implements MetadataStore<MMOEntity<Entity>> {
    private final BukkitBootstrap bukkitBootstrap;

    public BukkitMetadataStore(BukkitBootstrap bukkitBootstrap) {
        this.bukkitBootstrap = bukkitBootstrap;
    }

    @Override
    public <V> @Nullable V getMetadata(@NotNull MMOEntity<Entity> holder, @NotNull MetadataKey<V> key) {
        final List<MetadataValue> metadata = holder.getNative().getMetadata(key.getKey());
        if (!metadata.isEmpty()) {
            return (V) metadata.get(0);
        }
        return null;
    }

    @Override
    public <V> void setMetadata(@NotNull MMOEntity<Entity> holder, @NotNull MetadataKey<V> key, @NotNull V value) {
        holder.getNative().setMetadata(key.getKey(), new FixedMetadataValue(bukkitBootstrap, (V) value));
    }

    @Override
    public <V> @Nullable V removeMetadata(@NotNull MMOEntity<Entity> holder, @NotNull MetadataKey<V> key) {
        final List<MetadataValue> metadata = holder.getNative().getMetadata(key.getKey());
        if (!metadata.isEmpty()) {
            holder.getNative().removeMetadata(key.getKey(), bukkitBootstrap);
        }
        return (V) metadata.get(0);
    }
}
