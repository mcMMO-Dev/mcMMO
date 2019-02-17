package com.gmail.nossr50;

import com.sk89q.worldedit.extension.platform.NoCapablePlatformException;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;

public abstract class VersionedHandler {
    private static VersionedHandler instance;

    private final mcMMO plugin;

    protected VersionedHandler(mcMMO plugin) {
        this.plugin = plugin;
    }

    /**
     * Gets the string value id of the BlockType, note that this is not the
     * blockstate.
     *
     * Examples include: "minecraft:stone", "minecraft:chest", "thaumcraft:log2"
     * BlockStates are more specific like "minecraft:log[variant=oak,axis=x]"
     *
     * This should be casting down and retrieving the id from the block registry maintained by vanilla.
     *
     * @param block The block instance (holds byte/numerical id or sometimes block state in newer versions)
     * @return The string id of the block type
     */
    public abstract String getIdFor(Block block);

    /**
     * Gets the string value id of the Entity, much like blocks, this is not
     * the full string representation of the entity, just the string id of the
     * TYPE.
     *
     * Examples include: "minecraft:creeeper", "minecraft:sheep", "thaumcraft:wisp"
     *
     * @param entity The entity instance
     * @return The string id of the entity's registered type.
     */
    public abstract String getIdFor(Entity entity);

    // Technically can be TileEntity snapshot references as well.
    public abstract String getIdFor(BlockState block);

    /**
     * Gets the {@link VersionedHandler} instance for this running platform.
     * Note that all the handler does is perform various operations necessitated
     * by either a lack of, or bridging an implementation of some API/core aspects
     * that are not available through Bukkit API (like getting BlockType id's, or
     * BlockState id's).
     *
     * @param plugin The mcmmo plugin
     * @return The version handler instance
     */
    public VersionedHandler getInstance(mcMMO plugin) {
        if (instance == null) {
            final String serverPackage = mcMMO.p.getServer().getClass().getPackage().getName();
            final String serverVersion = serverPackage.substring(serverPackage.lastIndexOf(".") + 1);
            try {
                final Class<?> clazz = Class.forName("com.gmail.nossr50.bukkit." + serverVersion + ".NmsHandler");
                if (VersionedHandler.class.isAssignableFrom(clazz)) {
                    instance = (VersionedHandler) clazz.getConstructor(mcMMO.class).newInstance(plugin);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new NoCapablePlatformException("Could not discover a valid mcMMO VersionedHandler for version:" + serverVersion);
            }
        }
        return instance;
    }


}
