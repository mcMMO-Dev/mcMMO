package com.gmail.nossr50;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.events.FakeBlockBreakEvent;
import com.gmail.nossr50.events.McMMOItemSpawnEvent;

public class m {

    /**
     * Gets a capitalized version of the target string.
     *
     * @param target String to capitalize
     * @return the capitalized string
     */
    public static String getCapitalized(String target) {
        String firstLetter = target.substring(0,1);
        String remainder = target.substring(1);
        String capitalized = firstLetter.toUpperCase() + remainder.toLowerCase();

        return capitalized;
    }

    /**
     * Gets a nicely formatted string version of an item name from a given item ID.
     *
     * @param itemID The ID of the item to convert to string.
     * @return the nicely formatting string
     */
    public static String prettyItemString(int itemID) {
        String baseString = Material.getMaterial(itemID).toString();
        String[] substrings = baseString.split("_");
        String prettyString = "";
        int size = 1;

        for (String s : substrings) {
            prettyString = prettyString.concat(m.getCapitalized(s));

            if (size < substrings.length) {
                prettyString = prettyString.concat(" ");
            }

            size++;
        }

        return prettyString;
    }

    /**
     * Gets the int represented by this string.
     *
     * @param string The string to parse
     * @return the int represented by this string
     */
    public static int getInt(String string) {
        if (isInt(string)) {
            return Integer.parseInt(string);
        }
        else {
            return 0;
        }
    }

    /**
     * Checks to see if an entity is currently invincible.
     *
     * @param le The LivingEntity to check
     * @param event The event the entity is involved in
     * @return true if the entity is invincible, false otherwise
     */
    public static boolean isInvincible(LivingEntity le, EntityDamageEvent event) {

        /*
         * So apparently if you do more damage to a LivingEntity than its last damage int you bypass the invincibility.
         * So yeah, this is for that.
         */
        if (le.getNoDamageTicks() > le.getMaximumNoDamageTicks() / 2.0F && event.getDamage() <= le.getLastDamage()) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Gets the power level of a player.
     *
     * @param player The player to get the power level of
     * @param PP The profile of the player
     * @return the power level of the player
     */
    public static int getPowerLevel(Player player, PlayerProfile PP) {
        int powerLevel = 0;

        for (SkillType type : SkillType.values()) {
            if (type.getPermissions(player)) {
                powerLevel += PP.getSkillLevel(type);
            }
        }

        return powerLevel;
    }

    /**
     * Simulate a block break event.
     *
     * @param block The block to break
     * @param player The player breaking the block
     * @param shouldArmSwing true if an armswing event should be fired, false otherwise
     * @return true if the event wasn't cancelled, false otherwise
     */
    public static boolean blockBreakSimulate(Block block, Player player, Boolean shouldArmSwing) {

        //Support for NoCheat
        if (shouldArmSwing) {
            PlayerAnimationEvent armswing = new PlayerAnimationEvent(player);
            Bukkit.getPluginManager().callEvent(armswing);
        }

        FakeBlockBreakEvent event = new FakeBlockBreakEvent(block, player);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Get the upgrade tier of the item in hand.
     *
     * @param inHand The item to check the tier of
     * @return the tier of the item
     */
    public static Integer getTier(ItemStack inHand) {
        int tier = 0;

        if (ItemChecks.isWoodTool(inHand)) {
            tier = 1;
        }
        else if (ItemChecks.isStoneTool(inHand)) {
            tier = 2;
        }
        else if (ItemChecks.isIronTool(inHand)) {
            tier = 3;
        }
        else if(ItemChecks.isGoldTool(inHand)) {
            tier = 1;
        }
        else if(ItemChecks.isDiamondTool(inHand))
            tier = 4;

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
    public static boolean isNear(Location first, Location second, int maxDistance) {
        double relX = first.getX() - second.getX();
        double relY = first.getY() - second.getY();
        double relZ = first.getZ() - second.getZ();
        double dist = (relX * relX) + (relY * relY) + (relZ * relZ);

        if (dist < maxDistance * maxDistance) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Determine if a string represents an Integer
     *
     * @param string String to check
     * @return true if the string is an Integer, false otherwise
     */
    public static boolean isInt(String string) {
        try {
            Integer.parseInt(string);
            return true;
        }
        catch (NumberFormatException nFE) {
            return false;
        }
    }

    /**
     * Drop items at a given location.
     *
     * @param location The location to drop the items at
     * @param is The items to drop
     * @param quantity The amount of items to drop
     */
    public static void mcDropItems(Location location, ItemStack is, int quantity) {
        for (int i = 0; i < quantity; i++) {
            mcDropItem(location, is);
        }
    }

    /**
     * Randomly drop an item at a given location.
     *
     * @param location The location to drop the items at
     * @param is The item to drop
     * @param chance The percentage chance for the item to drop
     */
    public static void mcRandomDropItem(Location location, ItemStack is, double chance) {
        if (Math.random() * 100 < chance) {
            mcDropItem(location, is);
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
    public static void mcRandomDropItems(Location location, ItemStack is, int chance, int quantity) {
        for(int i = 0; i < quantity; i++) {
            mcRandomDropItem(location, is, chance);
        }
    }

    /**
     * Drop an item at a given location.
     *
     * @param location The location to drop the item at
     * @param itemStack The item to drop
     */
    public static void mcDropItem(Location location, ItemStack itemStack) {

        // We can't get the item until we spawn it and we want to make it cancellable, so we have a custom event.
        McMMOItemSpawnEvent event = new McMMOItemSpawnEvent(location, itemStack);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }
        else {
            location.getWorld().dropItemNaturally(location, itemStack);
        }
    }

    /**
     * Check if a skill level is higher than the max bonus level of the ability.
     *
     * @param skillLevel Skill level to check
     * @param maxLevel Max level of the ability
     * @return whichever value is lower
     */
    public static int skillCheck(int skillLevel, int maxLevel) {
        if (skillLevel > maxLevel) {
            return maxLevel;
        }
        else {
            return skillLevel;
        }
    }
}
