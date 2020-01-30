package com.gmail.nossr50.util;

import com.gmail.nossr50.events.items.McMMOItemSpawnEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.player.PlayerProfileLoadingTask;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public final class MiscTools {
    public final int TIME_CONVERSION_FACTOR = 1000;
    public final int TICK_CONVERSION_FACTOR = 20;
    public final int PLAYER_RESPAWN_COOLDOWN_SECONDS = 5;
    public final double SKILL_MESSAGE_MAX_SENDING_DISTANCE = 10.0;
    public final Set<String> modNames = ImmutableSet.of("LOTR", "BUILDCRAFT", "ENDERIO", "ENHANCEDBIOMES", "IC2", "METALLURGY", "FORESTRY", "GALACTICRAFT", "RAILCRAFT", "TWILIGHTFOREST", "THAUMCRAFT", "GRAVESTONEMOD", "GROWTHCRAFT", "ARCTICMOBS", "DEMONMOBS", "INFERNOMOBS", "SWAMPMOBS", "MARICULTURE", "MINESTRAPPOLATION");
    private final mcMMO pluginRef;

    private Random random = new Random();

    public MiscTools(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    /**
     * Determines if an entity is an NPC but not a villager
     * This method aims to establish compatibility between mcMMO and other plugins which create "NPCs"
     *
     * It does this by checking the following
     * 1) The entity is not a Villager
     * 2) The entity can be considered an NPC
     *
     * In this context, an NPC is a bit hard to define. Various plugins determine what an NPC is in different ways.
     * @see MiscTools ::isNPCIncludingVillagers
     * @param entity target entity
     * @return true if the entity is not a Villager and is not a "NPC"
     */
    public boolean isNPCEntityExcludingVillagers(Entity entity) {
        return (!isVillager(entity)
                && isNPCIncludingVillagers(entity)); //Compatibility with some mod..
    }

    public boolean isNPCClassType(Entity entity) {
        return entity instanceof NPC;
    }

    public boolean hasNPCMetadataTag(Entity entity) {
        return entity.hasMetadata("NPC");
    }

    public boolean isVillager(Entity entity) {
        String entityType = entity.getType().toString();
        //This weird code is for 1.13 & 1.14 compatibility
        return entityType.equalsIgnoreCase("wandering_trader") || entity instanceof Villager;
    }

    public boolean isNPCIncludingVillagers(Entity entity) {
        return (entity == null
                || (hasNPCMetadataTag(entity))
                || (isNPCClassType(entity))
                || entity.getClass().getName().equalsIgnoreCase("cofh.entity.PlayerFake"));
    }

    /**
     * Determine if two locations are near each other.
     *
     * @param first       The first location
     * @param second      The second location
     * @param maxDistance The max distance apart
     * @return true if the distance between {@code first} and {@code second} is less than {@code maxDistance}, false otherwise
     */
    public boolean isNear(Location first, Location second, double maxDistance) {
        return (first.getWorld() == second.getWorld()) && (first.distanceSquared(second) < (maxDistance * maxDistance) || maxDistance == 0);
    }

    /**
     * Get the center of the given block.
     *
     * @param blockState The {@link BlockState} of the block
     * @return A {@link Location} lying at the center of the block
     */
    public Location getBlockCenter(BlockState blockState) {
        return blockState.getLocation().add(0.5, 0.5, 0.5);
    }

    public void dropItems(Location location, Collection<ItemStack> drops) {
        for (ItemStack drop : drops) {
            dropItem(location, drop);
        }
    }

    /**
     * Drop items at a given location.
     *
     * @param location The location to drop the items at
     * @param is       The items to drop
     * @param quantity The amount of items to drop
     */
    public void dropItems(Location location, ItemStack is, int quantity) {
        for (int i = 0; i < quantity; i++) {
            dropItem(location, is);
        }
    }

    /**
     * Drop an item at a given location.
     *
     * @param location  The location to drop the item at
     * @param itemStack The item to drop
     * @return Dropped Item entity or null if invalid or cancelled
     */
    public Item dropItem(Location location, ItemStack itemStack) {
        if (itemStack.getType() == Material.AIR) {
            return null;
        }

        // We can't get the item until we spawn it and we want to make it cancellable, so we have a custom event.
        McMMOItemSpawnEvent event = new McMMOItemSpawnEvent(location, itemStack);
        pluginRef.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return null;
        }

        return location.getWorld().dropItemNaturally(location, itemStack);
    }

    /**
     * Drop items at a given location.
     *
     * @param fromLocation The location to drop the items at
     * @param is The items to drop
     * @param speed the speed that the item should travel
     * @param quantity The amount of items to drop
     */
    public void spawnItemsTowardsLocation(Location fromLocation, Location toLocation, ItemStack is, int quantity, double speed) {
        for (int i = 0; i < quantity; i++) {
            spawnItemTowardsLocation(fromLocation, toLocation, is, speed);
        }
    }

    /**
     * Drop an item at a given location.
     * This method is fairly expensive as it creates clones of everything passed to itself since they are mutable objects
     *
     * @param fromLocation The location to drop the item at
     * @param toLocation The location the item will travel towards
     * @param itemToSpawn The item to spawn
     * @param speed the speed that the item should travel
     * @return Dropped Item entity or null if invalid or cancelled
     */
    public Item spawnItemTowardsLocation(Location fromLocation, Location toLocation, ItemStack itemToSpawn, double speed) {
        if (itemToSpawn.getType() == Material.AIR) {
            return null;
        }

        //Work with fresh copies of everything
        ItemStack clonedItem = itemToSpawn.clone();
        Location spawnLocation = fromLocation.clone();
        Location targetLocation = toLocation.clone();

        // We can't get the item until we spawn it and we want to make it cancellable, so we have a custom event.
        McMMOItemSpawnEvent event = new McMMOItemSpawnEvent(spawnLocation, clonedItem);
        pluginRef.getServer().getPluginManager().callEvent(event);

        //Something cancelled the event so back out
        if (event.isCancelled() || event.getItemStack() == null) {
            return null;
        }

        //Use the item from the event
        Item spawnedItem = spawnLocation.getWorld().dropItem(spawnLocation, clonedItem);
        Vector vecFrom = spawnLocation.clone().toVector().clone();
        Vector vecTo = targetLocation.clone().toVector().clone();

        //Vector which is pointing towards out target location
        Vector direction = vecTo.subtract(vecFrom).normalize();

        //Modify the speed of the vector
        direction = direction.multiply(speed);
        spawnedItem.setVelocity(direction);
        return spawnedItem;
    }

    public void profileCleanup(String playerName) {
        Player player = pluginRef.getServer().getPlayerExact(playerName);

        if (player != null) {
            pluginRef.getUserManager().remove(player);
            new PlayerProfileLoadingTask(pluginRef, player).runTaskLaterAsynchronously(pluginRef, 1); // 1 Tick delay to ensure the player is marked as online before we begin loading
        }
    }

    public String getModName(String materialName) {
        for (String mod : modNames) {
            if (materialName.contains(mod)) {
                return mod;
            }
        }

        String[] materialSplit = materialName.split("_");

        if (materialSplit.length > 1) {
            return materialSplit[0].toLowerCase(Locale.ENGLISH);
        }

        return "UnknownMods";
    }

    /**
     * Gets a random location near the specified location
     */
    public Location getLocationOffset(Location location, double strength) {
        double blockX = location.getBlockX();
        double blockZ = location.getBlockZ();

        double distance;
        distance = strength * random.nextDouble();
        blockX = (random.nextBoolean()) ? blockX + (distance) : blockX - (distance);

        distance = strength * random.nextDouble();
        blockZ = (random.nextBoolean()) ? blockZ + (distance) : blockZ - (distance);

        return new Location(location.getWorld(), blockX, location.getY(), blockZ);
    }

    public Random getRandom() {
        return random;
    }
}
