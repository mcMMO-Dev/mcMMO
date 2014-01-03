package com.gmail.nossr50.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.events.items.McMMOItemSpawnEvent;
import com.gmail.nossr50.util.player.UserManager;

public final class Misc {
    private static Random random = new Random();

    public static final int TIME_CONVERSION_FACTOR = 1000;
    public static final int TICK_CONVERSION_FACTOR = 20;

    public static final long PLAYER_DATABASE_COOLDOWN_MILLIS = 1750;
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
        return (entity == null || entity.hasMetadata("NPC") || entity instanceof NPC || (mcMMO.isCombatTagEnabled() && entity instanceof HumanEntity && ((HumanEntity) entity).getName().contains("PvpLogger")) || entity.getClass().getName().equalsIgnoreCase("cofh.entity.PlayerFake"));
    }

    /**
     * Get the upgrade tier of the item in hand.
     *
     * @param inHand The item to check the tier of
     * @return the tier of the item
     */
    public static int getTier(ItemStack inHand) {
        int tier = 0;

        if (ItemUtils.isWoodTool(inHand)) {
            tier = 1;
        }
        else if (ItemUtils.isStoneTool(inHand)) {
            tier = 2;
        }
        else if (ItemUtils.isIronTool(inHand)) {
            tier = 3;
        }
        else if (ItemUtils.isGoldTool(inHand)) {
            tier = 1;
        }
        else if (ItemUtils.isDiamondTool(inHand)) {
            tier = 4;
        }
        else if (ModUtils.isCustomTool(inHand)) {
            tier = ModUtils.getToolFromItemStack(inHand).getTier();
        }

        return tier;
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
        if (first.getWorld() != second.getWorld()) {
            return false;
        }

        return first.distanceSquared(second) < (maxDistance * maxDistance) || maxDistance == 0;
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
     * Randomly drop an item at a given location.
     *
     * @param location The location to drop the items at
     * @param is The item to drop
     * @param chance The percentage chance for the item to drop
     */
    public static void randomDropItem(Location location, ItemStack is, double chance) {
        if (random.nextInt(100) < chance) {
            dropItem(location, is);
        }
    }

    /**
     * Drop items with random quantity at a given location.
     *
     * @param location The location to drop the items at
     * @param is The item to drop
     * @param quantity The amount of items to drop
     */
    public static void randomDropItems(Location location, ItemStack is, int quantity) {
        int dropCount = random.nextInt(quantity + 1);

        if (dropCount > 0) {
            is.setAmount(dropCount);
            dropItem(location, is);
        }
    }

    public static void randomDropItems(Location location, Collection<ItemStack> drops, double chance) {
        for (ItemStack item : drops) {
            randomDropItem(location, item, chance);
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
        UserManager.remove(playerName);

        Player player = mcMMO.p.getServer().getPlayerExact(playerName);

        if (player != null) {
            UserManager.addUser(player);
        }
    }

    public static void printProgress(int convertedUsers, int progressInterval, long startMillis) {
        if ((convertedUsers % progressInterval) == 0) {
            mcMMO.p.getLogger().info(String.format("Conversion progress: %d users at %.2f users/second", convertedUsers, convertedUsers / (double) ((System.currentTimeMillis() - startMillis) / TIME_CONVERSION_FACTOR)));
        }
    }

    public static void resendChunkRadiusAt(Player player, int radius) {
        Chunk chunk = player.getLocation().getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        for (int x = chunkX - radius; x < chunkX + radius; x++) {
            for (int z = chunkZ - radius; z < chunkZ + radius; z++) {
                player.getWorld().refreshChunk(x, z);
            }
        }
    }

    /**
     * Attempts to match any player names with the given name, and returns a list of all possibly matches.
     *
     * This list is not sorted in any particular order.
     * If an exact match is found, the returned list will only contain a single result.
     *
     * @param partialName Name to match
     * @return List of all possible names
     */
    public static List<String> matchPlayer(String partialName) {
        List<String> matchedPlayers = new ArrayList<String>();

        for (OfflinePlayer offlinePlayer : mcMMO.p.getServer().getOfflinePlayers()) {
            String playerName = offlinePlayer.getName();

            if (partialName.equalsIgnoreCase(playerName)) {
                // Exact match
                matchedPlayers.clear();
                matchedPlayers.add(playerName);
                break;
            }
            if (playerName.toLowerCase().contains(partialName.toLowerCase())) {
                // Partial match
                matchedPlayers.add(playerName);
            }
        }

        return matchedPlayers;
    }

    /**
     * Get a matched player name if one was found in the database.
     *
     * @param partialName Name to match
     *
     * @return Matched name or {@code partialName} if no match was found
     */
    public static String getMatchedPlayerName(String partialName) {
        if (Config.getInstance().getMatchOfflinePlayers()) {
            List<String> matches = matchPlayer(partialName);

            if (matches.size() == 1) {
                partialName = matches.get(0);
            }

        }
        else {
            Player player = mcMMO.p.getServer().getPlayer(partialName);
            if (player != null) {
                partialName = player.getName();
            }
        }

        return partialName;
    }

    public static Random getRandom() {
        return random;
    }
}
