package com.gmail.nossr50.util;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.spout.huds.McMMOHud;
import com.gmail.nossr50.events.items.McMMOItemSpawnEvent;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.spout.SpoutUtils;

public final class Misc {
    private static Random random = new Random();
    public static boolean isSpawnerXPEnabled = Config.getInstance().getExperienceGainsMobspawnersEnabled();
    public static final int PLAYER_RESPAWN_COOLDOWN_SECONDS = 5;
    public static final int TIME_CONVERSION_FACTOR = 1000;
    public static final double SKILL_MESSAGE_MAX_SENDING_DISTANCE = 10.0;

    // Sound Pitches & Volumes from CB
    public static final float ANVIL_USE_PITCH  = 0.3F; // Not in CB directly, I went off the place sound values
    public static final float ANVIL_USE_VOLUME = 1.0F; // Not in CB directly, I went off the place sound values
    public static final float FIZZ_PITCH       = 2.6F + (Misc.getRandom().nextFloat() - Misc.getRandom().nextFloat()) * 0.8F;
    public static final float FIZZ_VOLUME      = 0.5F;
    public static final float POP_PITCH        = ((getRandom().nextFloat() - getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F;
    public static final float POP_VOLUME       = 0.2F;
    public static final float BAT_VOLUME       = 1.0F;
    public static final float BAT_PITCH        = 0.6F;

    private Misc() {};

    public static boolean isNPCEntity(Entity entity) {
        if (entity == null || entity.hasMetadata("NPC")) {
            return true;
        }

        return false;
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
     * @return true if the distance between <code>first</code> and <code>second</code> is less than <code>maxDistance</code>, false otherwise
     */
    public static boolean isNear(Location first, Location second, double maxDistance) {
        if (first.getWorld() != second.getWorld()) {
            return false;
        }

        if (first.distanceSquared(second) < (maxDistance * maxDistance)) {
            return true;
        }

        return false;
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
    public static void randomDropItem(Location location, ItemStack is, int chance) {
        if (random.nextInt(100) < chance) {
            dropItem(location, is);
        }
    }

    /**
     * Randomly drop items at a given location.
     *
     * @param location The location to drop the items at
     * @param is The item to drop
     * @param chance The percentage chance for the item to drop
     * @param quantity The amount of items to drop
     */
    public static void randomDropItems(Location location, ItemStack is, int quantity) {
        int dropCount = random.nextInt(quantity + 1);

        if (dropCount > 0) {
            is.setAmount(dropCount);
            dropItem(location, is);
        }
    }

    /**
     * Drop an item at a given location.
     *
     * @param location The location to drop the item at
     * @param itemStack The item to drop
     */
    public static void dropItem(Location location, ItemStack itemStack) {

        if (itemStack.getType() == Material.AIR) {
            return;
        }

        // We can't get the item until we spawn it and we want to make it cancellable, so we have a custom event.
        McMMOItemSpawnEvent event = new McMMOItemSpawnEvent(location, itemStack);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        Item newItem = location.getWorld().dropItemNaturally(location, itemStack);

        ItemStack cloned = itemStack.clone();
        cloned.setAmount(newItem.getItemStack().getAmount());

        newItem.setItemStack(cloned);
    }

    public static void profileCleanup(String playerName) {
        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(playerName);

        if (mcMMOPlayer != null) {
            Player player = mcMMOPlayer.getPlayer();
            McMMOHud spoutHud = mcMMOPlayer.getProfile().getSpoutHud();

            if (spoutHud != null) {
                spoutHud.removeWidgets();
            }

            UserManager.remove(playerName);

            if (player.isOnline()) {
                UserManager.addUser(player);

                if (mcMMO.spoutEnabled) {
                    SpoutUtils.reloadSpoutPlayer(player);
                }
            }
        }
    }

    public static Random getRandom() {
        return random;
    }
}
