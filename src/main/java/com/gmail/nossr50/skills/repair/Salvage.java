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
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.player.UserManager;

public class Salvage {
    public static int salvageUnlockLevel = Config.getInstance().getSalvageUnlockLevel();
    public static int anvilID = Config.getInstance().getSalvageAnvilId();

    public static void handleSalvage(final Player player, final Location location, final ItemStack item) {
        if (!Config.getInstance().getSalvageEnabled()) {
            return;
        }

        if (player.getGameMode() == GameMode.SURVIVAL) {
            final int skillLevel = UserManager.getPlayer(player).getProfile().getSkillLevel(SkillType.REPAIR);

            if (skillLevel < salvageUnlockLevel) {
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
        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        if (!mcMMOPlayer.getPlacedSalvageAnvil()) {
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
            mcMMOPlayer.togglePlacedSalvageAnvil();
        }
    }

    /**
     * Checks if the item is salvageable.
     *
     * @param is Item to check
     * @return true if the item is salvageable, false otherwise
     */
    public static boolean isSalvageable(final ItemStack is) {
        if (Config.getInstance().getSalvageTools() && (ItemUtils.isMinecraftTool(is) || ItemUtils.isStringTool(is) || is.getType() == Material.BUCKET)) {
            return true;
        }

        if (Config.getInstance().getSalvageArmor() && ItemUtils.isMinecraftArmor(is)) {
            return true;
        }

        return false;
    }

    private static Material getSalvagedItem(final ItemStack inHand) {
        if (ItemUtils.isDiamondTool(inHand) || ItemUtils.isDiamondArmor(inHand)) {
            return Material.DIAMOND;
        }
        else if (ItemUtils.isGoldTool(inHand) || ItemUtils.isGoldArmor(inHand)) {
            return Material.GOLD_INGOT;
        }
        else if (ItemUtils.isIronTool(inHand) || ItemUtils.isIronArmor(inHand)) {
            return Material.IRON_INGOT;
        }
        else if (ItemUtils.isStoneTool(inHand)) {
            return Material.COBBLESTONE;
        }
        else if (ItemUtils.isWoodTool(inHand)) {
            return Material.WOOD;
        }
        else if (ItemUtils.isLeatherArmor(inHand)) {
            return Material.LEATHER;
        }
        else if (ItemUtils.isStringTool(inHand)) {
            return Material.STRING;
        }
        else {
            return null;
        }
    }

    private static int getSalvagedAmount(final ItemStack inHand) {
        if (ItemUtils.isPickaxe(inHand) || ItemUtils.isAxe(inHand) || inHand.getType() == Material.BOW || inHand.getType() == Material.BUCKET) {
            return 3;
        }
        else if (ItemUtils.isShovel(inHand) || inHand.getType() == Material.FLINT_AND_STEEL) {
            return 1;
        }
        else if (ItemUtils.isSword(inHand) || ItemUtils.isHoe(inHand) || inHand.getType() == Material.CARROT_STICK || inHand.getType() == Material.FISHING_ROD || inHand.getType() == Material.SHEARS) {
            return 2;
        }
        else if (ItemUtils.isHelmet(inHand)) {
            return 5;
        }
        else if (ItemUtils.isChestplate(inHand)) {
            return 8;
        }
        else if (ItemUtils.isLeggings(inHand)) {
            return 7;
        }
        else if (ItemUtils.isBoots(inHand)) {
            return 4;
        }
        else {
            return 0;
        }
    }
}
