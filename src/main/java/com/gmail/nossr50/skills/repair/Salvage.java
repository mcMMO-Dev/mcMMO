package com.gmail.nossr50.skills.repair;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.ItemChecks;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Users;

public class Salvage {
    private static Config configInstance = Config.getInstance();
    public static int salvageUnlockLevel = Config.getInstance().getSalvageUnlockLevel();
    public static int anvilID = Config.getInstance().getSalvageAnvilId();

    public static void handleSalvage(final Player player, final Location location, final ItemStack item) {
        if (!configInstance.getSalvageEnabled()) {
            return;
        }

        if (player.getGameMode() == GameMode.SURVIVAL) {
            final int skillLevel = Users.getPlayer(player).getProfile().getSkillLevel(SkillType.REPAIR);
            final int unlockLevel = configInstance.getSalvageUnlockLevel();

            if (skillLevel < unlockLevel) {
                player.sendMessage(LocaleLoader.getString("Repair.Skills.AdeptSalvage"));
                return;
            }

            final float currentDurability = item.getDurability();

            if (currentDurability == 0) {
                player.setItemInHand(new ItemStack(Material.AIR));
                location.setY(location.getY() + 1);

                Misc.dropItems(location, new ItemStack(getSalvagedItem(item)), getSalvagedAmount(item));

                player.playSound(player.getLocation(), Sound.ANVIL_USE, Misc.ANVIL_USE_VOLUME, Misc.ANVIL_USE_PITCH);
                player.sendMessage(LocaleLoader.getString("Repair.Skills.SalvageSuccess"));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Repair.Skills.NotFullDurability"));
            }
        }
        
    }

    /**
     * Handles notifications for placing an anvil.
     * 
     * @param player The player placing the anvil
     * @param anvilID The item ID of the anvil block
     */
    public static void placedAnvilCheck(final Player player, final int anvilID) {
        final PlayerProfile profile = Users.getPlayer(player).getProfile();

        if (!profile.getPlacedSalvageAnvil()) {
            if (mcMMO.spoutEnabled) {
                final SpoutPlayer spoutPlayer = SpoutManager.getPlayer(player);

                if (spoutPlayer.isSpoutCraftEnabled()) {
                    spoutPlayer.sendNotification("[mcMMO] Anvil Placed", "Right click to salvage!", Material.getMaterial(anvilID));
                }
            }
            else {
                player.sendMessage(LocaleLoader.getString("Repair.Listener.Anvil2"));
            }

            player.playSound(player.getLocation(), Sound.ANVIL_LAND, Misc.ANVIL_USE_VOLUME, Misc.ANVIL_USE_PITCH);
            profile.togglePlacedSalvageAnvil();
        }
    }

    private static Material getSalvagedItem(final ItemStack inHand) {
        if (ItemChecks.isDiamondTool(inHand) || ItemChecks.isDiamondArmor(inHand)) {
            return Material.DIAMOND;
        }
        else if (ItemChecks.isGoldTool(inHand) || ItemChecks.isGoldArmor(inHand)) {
            return Material.GOLD_INGOT;
        }
        else if (ItemChecks.isIronTool(inHand) || ItemChecks.isIronArmor(inHand)) {
            return Material.IRON_INGOT;
        }
        else if (ItemChecks.isStoneTool(inHand)) {
            return Material.COBBLESTONE;
        }
        else if (ItemChecks.isWoodTool(inHand)) {
            return Material.WOOD;
        }
        else if (ItemChecks.isLeatherArmor(inHand)) {
            return Material.LEATHER;
        }
        else if (ItemChecks.isStringTool(inHand)) {
            return Material.STRING;
        }
        else {
            return null;
        }
    }

    private static int getSalvagedAmount(final ItemStack inHand) {
        if (ItemChecks.isPickaxe(inHand) || ItemChecks.isAxe(inHand) || inHand.getType() == Material.BOW || inHand.getType() == Material.BUCKET) {
            return 3;
        }
        else if (ItemChecks.isShovel(inHand) || inHand.getType() == Material.FLINT_AND_STEEL) {
            return 1;
        }
        else if (ItemChecks.isSword(inHand) || ItemChecks.isHoe(inHand) || inHand.getType() == Material.CARROT_STICK || inHand.getType() == Material.FISHING_ROD || inHand.getType() == Material.SHEARS) {
            return 2;
        }
        else if (ItemChecks.isHelmet(inHand)) {
            return 5;
        }
        else if (ItemChecks.isChestplate(inHand)) {
            return 8;
        }
        else if (ItemChecks.isLeggings(inHand)) {
            return 7;
        }
        else if (ItemChecks.isBoots(inHand)) {
            return 4;
        }
        else {
            return 0;
        }
    }
    /**
     * Checks if the item is salvageable.
     * 
     * @param is Item to check
     * @return true if the item is salvageable, false otherwise
     */
    public static boolean isSalvageable(final ItemStack is) {
        if (configInstance.getSalvageTools() && (ItemChecks.isMinecraftTool(is) || ItemChecks.isStringTool(is) || is.getType() == Material.BUCKET)) {
            return true;
        }
        if (configInstance.getSalvageArmor() && ItemChecks.isMinecraftArmor(is)) {
            return true;
        }
        return false;
    }
}
