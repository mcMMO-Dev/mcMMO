package com.gmail.nossr50.skills.repair;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillType;
import com.gmail.nossr50.util.ItemChecks;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class Salvage {
    private static Config configInstance = Config.getInstance();
    public static int salvageUnlockLevel = Config.getInstance().getSalvageUnlockLevel();

    public static void handleSalvage(final Player player, final Location location, final ItemStack inHand) {
        if (!Permissions.salvage(player) || !configInstance.getSalvageEnabled()) {
            return;
        }

        if (player.getGameMode() == GameMode.SURVIVAL) {
            final PlayerProfile profile = Users.getProfile(player);
            final int skillLevel = profile.getSkillLevel(SkillType.REPAIR);
            final int unlockLevel = configInstance.getSalvageUnlockLevel();

            if (skillLevel >= unlockLevel) {
                final float currentdura = inHand.getDurability();

                if (currentdura == 0) {
                    final int salvagedAmount = getSalvagedAmount(inHand);
                    final int itemID = getSalvagedItemID(inHand);

                    player.setItemInHand(new ItemStack(Material.AIR));
                    location.setY(location.getY() + 1);
                    Misc.dropItem(location, new ItemStack(itemID, salvagedAmount));
                    player.sendMessage(LocaleLoader.getString("Repair.Skills.SalvageSuccess"));
                } else {
                    player.sendMessage(LocaleLoader.getString("Repair.Skills.NotFullDurability"));
                }
            } else {
                player.sendMessage(LocaleLoader.getString("Repair.Skills.AdeptSalvage"));
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
        final PlayerProfile profile = Users.getProfile(player);

        if (!profile.getPlacedSalvageAnvil()) {
            if (mcMMO.spoutEnabled) {
                final SpoutPlayer spoutPlayer = SpoutManager.getPlayer(player);

                if (spoutPlayer.isSpoutCraftEnabled()) {
                    spoutPlayer.sendNotification("[mcMMO] Anvil Placed", "Right click to salvage!", Material.getMaterial(anvilID));
                }
            } else {
                player.sendMessage(LocaleLoader.getString("Repair.Listener.Anvil2"));
            }

            profile.togglePlacedSalvageAnvil();
        }
    }

    public static int getSalvagedItemID(final ItemStack inHand) {
        int salvagedItem = 0;
        if (ItemChecks.isDiamondTool(inHand) || ItemChecks.isDiamondArmor(inHand)) salvagedItem = 264;
        else if (ItemChecks.isGoldTool(inHand) || ItemChecks.isGoldArmor(inHand)) salvagedItem = 266;
        else if (ItemChecks.isIronTool(inHand) || ItemChecks.isIronArmor(inHand)) salvagedItem = 265;
        else if (ItemChecks.isStoneTool(inHand)) salvagedItem = 4;
        else if (ItemChecks.isWoodTool(inHand)) salvagedItem = 5;
        else if ( ItemChecks.isLeatherArmor(inHand)) salvagedItem = 334;
        return salvagedItem;
    }

    public static int getSalvagedAmount(final ItemStack inHand) {
        int salvagedAmount = 0;
        if (ItemChecks.isPickaxe(inHand) || ItemChecks.isAxe(inHand)) salvagedAmount = 3;
        else if (ItemChecks.isShovel(inHand)) salvagedAmount = 1;
        else if (ItemChecks.isSword(inHand) || ItemChecks.isHoe(inHand)) salvagedAmount = 2;
        else if (ItemChecks.isHelmet(inHand)) salvagedAmount = 5;
        else if (ItemChecks.isChestplate(inHand)) salvagedAmount = 8;
        else if (ItemChecks.isPants(inHand)) salvagedAmount = 7;
        else if (ItemChecks.isBoots(inHand)) salvagedAmount = 4;
        return salvagedAmount;
    }
    /**
     * Checks if the item is salvageable.
     * 
     * @param is Item to check
     * @return true if the item is salvageable, false otherwise
     */
    public static boolean isSalvageable(final ItemStack is) {
        if (configInstance.getSalvageTools() && ItemChecks.isTool(is)) {
            return true;
        }
        if (configInstance.getSalvageArmor() && ItemChecks.isArmor(is)) {
            return true;
        }
        return false;
    }
}
