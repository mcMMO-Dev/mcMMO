package com.gmail.nossr50.util;

import com.gmail.nossr50.events.items.McMMOItemSpawnEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.player.PlayerProfileLoadingTask;
import com.gmail.nossr50.util.player.UserManager;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Random;
import java.util.Set;

public final class Misc {
    private static Random random = new Random();

    public static final int TIME_CONVERSION_FACTOR = 1000;
    public static final int TICK_CONVERSION_FACTOR = 20;

    public static final int PLAYER_RESPAWN_COOLDOWN_SECONDS = 5;
    public static final double SKILL_MESSAGE_MAX_SENDING_DISTANCE = 10.0;

    // Sound Pitches & Volumes from CB
/*    public static final float ANVIL_USE_PITCH  = 0.3F;  // Not in CB directly, I went off the place sound values
    public static final float ANVIL_USE_VOLUME = 1.0F * Config.getInstance().getMasterVolume();  // Not in CB directly, I went off the place sound values
    public static final float FIZZ_VOLUME      = 0.5F * Config.getInstance().getMasterVolume();
    public static final float POP_VOLUME       = 0.2F * Config.getInstance().getMasterVolume();
    public static final float BAT_VOLUME       = 1.0F * Config.getInstance().getMasterVolume();
    public static final float BAT_PITCH        = 0.6F;
    public static final float GHAST_VOLUME     = 1.0F * Config.getInstance().getMasterVolume();
    public static final float LEVELUP_PITCH    = 0.5F;  // Reduced to differentiate between vanilla level-up
    public static final float LEVELUP_VOLUME   = 0.75F * Config.getInstance().getMasterVolume(); // Use max volume always*/

    public static final Set<String> modNames = ImmutableSet.of("LOTR", "BUILDCRAFT", "ENDERIO", "ENHANCEDBIOMES", "IC2", "METALLURGY", "FORESTRY", "GALACTICRAFT", "RAILCRAFT", "TWILIGHTFOREST", "THAUMCRAFT", "GRAVESTONEMOD", "GROWTHCRAFT", "ARCTICMOBS", "DEMONMOBS", "INFERNOMOBS", "SWAMPMOBS", "MARICULTURE", "MINESTRAPPOLATION");

    private Misc() {};

    /**
     * Determines if an entity is an NPC but not a villager
     * This method aims to establish compatibility between mcMMO and other plugins which create "NPCs"
     *
     * It does this by checking the following
     * 1) The entity is not a Villager
     * 2) The entity can be considered an NPC
     *
     * In this context, an NPC is a bit hard to define. Various plugins determine what an NPC is in different ways.
     * @see Misc::isNPCIncludingVillagers
     * @param entity target entity
     * @return true if the entity is not a Villager and is not a "NPC"
     */
    public static boolean isNPCEntityExcludingVillagers(Entity entity) {
        return (!isVillager(entity)
                && isNPCIncludingVillagers(entity)); //Compatibility with some mod..
    }

    public static boolean isNPCClassType(Entity entity) {
        return entity instanceof NPC;
    }

    public static boolean hasNPCMetadataTag(Entity entity) {
        return entity.hasMetadata("NPC");
    }

    public static boolean isVillager(Entity entity) {
        String entityType = entity.getType().toString();
        //This weird code is for 1.13 & 1.14 compatibility
        return entityType.equalsIgnoreCase("wandering_trader") || entity instanceof Villager;
    }

    public static boolean isNPCIncludingVillagers(Entity entity) {
        return (entity == null
                || (hasNPCMetadataTag(entity))
                || (isNPCClassType(entity))
                || entity.getClass().getName().equalsIgnoreCase("cofh.entity.PlayerFake"));
    }

    /**
     * Determine if two locations are near each other.
     *
     * @param first The first location
     * @param second The second location
     * @param maxDistance The max distance apart
     * @return true if the distance between {@code first} and {@code second} is less than {@code maxDistance}, false otherwise
     */
    public static boolean isNear(Location first, Location second, double maxDistance) {
        return (first.getWorld() == second.getWorld()) && (first.distanceSquared(second) < (maxDistance * maxDistance) || maxDistance == 0);
    }

    /**
     * Get the center of the given block.
     * 
     * @param blockState The {@link BlockState} of the block
     * @return A {@link Location} lying at the center of the block
     */
    public static Location getBlockCenter(BlockState blockState) {
        return blockState.getLocation().add(0.5, 0.5, 0.5);
    }

    public static void dropItems(Location location, Collection<ItemStack> drops) {
        for (ItemStack drop : drops) {
            dropItem(location, drop);
        }
    }

    /**
     * Drop items at a given location.
     *
     * @param location The location to drop the items at
     * @param is The items to drop
     * @param quantity The amount of items to drop
     */
    public static void dropItems(Location location, ItemStack is, int quantity) {
        for (int i = 0; i < quantity; i++) {
            dropItem(location, is);
        }
    }

    /**
     * Drop an item at a given location.
     *
     * @param location The location to drop the item at
     * @param itemStack The item to drop
     * @return Dropped Item entity or null if invalid or cancelled
     */
    public static Item dropItem(Location location, ItemStack itemStack) {
        if (itemStack.getType() == Material.AIR) {
            return null;
        }

        // We can't get the item until we spawn it and we want to make it cancellable, so we have a custom event.
        McMMOItemSpawnEvent event = new McMMOItemSpawnEvent(location, itemStack);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return null;
        }

        return location.getWorld().dropItem(location, itemStack);
    }

    /**
     * Drop items at a given location.
     *
     * @param fromLocation The location to drop the items at
     * @param is The items to drop
     * @param speed the speed that the item should travel
     * @param quantity The amount of items to drop
     */
    public static void spawnItemsTowardsLocation(Location fromLocation, Location toLocation, ItemStack is, int quantity, double speed) {
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
    public static Item spawnItemTowardsLocation(Location fromLocation, Location toLocation, ItemStack itemToSpawn, double speed) {
        if (itemToSpawn.getType() == Material.AIR) {
            return null;
        }

        //Work with fresh copies of everything
        ItemStack clonedItem = itemToSpawn.clone();
        Location spawnLocation = fromLocation.clone();
        Location targetLocation = toLocation.clone();

        // We can't get the item until we spawn it and we want to make it cancellable, so we have a custom event.
        McMMOItemSpawnEvent event = new McMMOItemSpawnEvent(spawnLocation, clonedItem);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

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

    public static void profileCleanup(String playerName) {
        Player player = mcMMO.p.getServer().getPlayerExact(playerName);

        if (player != null) {
            UserManager.remove(player);
            new PlayerProfileLoadingTask(player).runTaskLaterAsynchronously(mcMMO.p, 1); // 1 Tick delay to ensure the player is marked as online before we begin loading
        }
    }

    public static void printProgress(int convertedUsers, int progressInterval, long startMillis) {
        if ((convertedUsers % progressInterval) == 0) {
            mcMMO.p.getLogger().info(String.format("Conversion progress: %d users at %.2f users/second", convertedUsers, convertedUsers / (double) ((System.currentTimeMillis() - startMillis) / TIME_CONVERSION_FACTOR)));
        }
    }

    public static String getModName(String materialName) {
        for (String mod : modNames) {
            if (materialName.contains(mod)) {
                return mod;
            }
        }

        String[] materialSplit = materialName.split("_");

        if (materialSplit.length > 1) {
            return materialSplit[0].toLowerCase();
        }

        return "UnknownMods";
    }

    /**
     * Gets a random location near the specified location
     */
    public static Location getLocationOffset(Location location, double strength) {
        double blockX = location.getBlockX();
        double blockZ = location.getBlockZ();

        double distance;
        distance = strength * random.nextDouble();
        blockX = (random.nextBoolean()) ? blockX + (distance) : blockX - (distance);

        distance = strength * random.nextDouble();
        blockZ = (random.nextBoolean()) ? blockZ + (distance) : blockZ - (distance);

        return new Location(location.getWorld(), blockX, location.getY(), blockZ);
    }

    public static Random getRandom() {
        return random;
    }
}
