package com.gmail.nossr50.util;

import java.util.Collection;
import java.util.Random;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.events.items.McMMOItemSpawnEvent;
import com.gmail.nossr50.runnables.player.PlayerProfileLoadingTask;
import com.gmail.nossr50.util.player.UserManager;

import com.google.common.collect.ImmutableSet;

public final class Misc {
    private static Random random = new Random();

    public static final int TIME_CONVERSION_FACTOR = 1000;
    public static final int TICK_CONVERSION_FACTOR = 20;

    public static final int PLAYER_RESPAWN_COOLDOWN_SECONDS = 5;
    public static final double SKILL_MESSAGE_MAX_SENDING_DISTANCE = 10.0;

    // Sound Pitches & Volumes from CB
    public static final float ANVIL_USE_PITCH  = 0.3F;  // Not in CB directly, I went off the place sound values
    public static final float ANVIL_USE_VOLUME = 1.0F;  // Not in CB directly, I went off the place sound values
    public static final float FIZZ_VOLUME      = 0.5F;
    public static final float POP_VOLUME       = 0.2F;
    public static final float BAT_VOLUME       = 1.0F;
    public static final float BAT_PITCH        = 0.6F;
    public static final float GHAST_VOLUME     = 1.0F;
    public static final float LEVELUP_PITCH    = 0.5F;  // Reduced to differentiate between vanilla level-up
    public static final float LEVELUP_VOLUME   = 0.75F; // Use max volume always

    public static final Set<String> modNames = ImmutableSet.of("LOTR", "BUILDCRAFT", "ENDERIO", "ENHANCEDBIOMES", "IC2", "METALLURGY", "FORESTRY", "GALACTICRAFT", "RAILCRAFT", "TWILIGHTFOREST", "THAUMCRAFT", "GRAVESTONEMOD", "GROWTHCRAFT", "ARCTICMOBS", "DEMONMOBS", "INFERNOMOBS", "SWAMPMOBS", "MARICULTURE", "MINESTRAPPOLATION");

    private Misc() {};

    public static float getFizzPitch() {
        return 2.6F + (getRandom().nextFloat() - getRandom().nextFloat()) * 0.8F;
    }

    public static float getPopPitch() {
        return ((getRandom().nextFloat() - getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F;
    }

    public static float getGhastPitch() {
        return (getRandom().nextFloat() - getRandom().nextFloat()) * 0.2F + 1.0F;
    }

    public static boolean isNPCEntity(Entity entity) {
        return (entity == null || entity.hasMetadata("NPC") || entity instanceof NPC || entity.getClass().getName().equalsIgnoreCase("cofh.entity.PlayerFake"));
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

        return location.getWorld().dropItemNaturally(location, itemStack);
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
