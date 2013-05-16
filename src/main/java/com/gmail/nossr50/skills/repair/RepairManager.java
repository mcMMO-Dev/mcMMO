package com.gmail.nossr50.skills.repair;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.skills.repair.McMMOPlayerRepairCheckEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.repair.ArcaneForging.Tier;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.gmail.nossr50.util.spout.SpoutUtils;

public class RepairManager extends SkillManager {
    public RepairManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, SkillType.REPAIR);
    }

    /**
     * Handles notifications for placing an anvil.
     *
     * @param anvilID The item ID of the anvil block
     */
    public void placedAnvilCheck(int anvilId) {
        Player player = getPlayer();

        if (mcMMOPlayer.getPlacedAnvil(anvilId)) {
            return;
        }

        if (mcMMO.isSpoutEnabled()) {
            SpoutUtils.sendRepairNotifications(player, anvilId);
        }
        else {
            player.sendMessage(Repair.getAnvilMessage(anvilId));
        }

        player.playSound(player.getLocation(), Sound.ANVIL_LAND, Misc.ANVIL_USE_VOLUME, Misc.ANVIL_USE_PITCH);
        mcMMOPlayer.togglePlacedAnvil(anvilId);
    }

    public void handleRepair(ItemStack item) {
        Player player = getPlayer();
        int itemId = item.getTypeId();

        Repairable repairable = mcMMO.getRepairableManager().getRepairable(itemId);

        // Permissions checks on material and item types
        if (!repairable.getRepairItemType().getPermissions(player)) {
            player.sendMessage(LocaleLoader.getString("mcMMO.NoPermission"));
            return;
        }

        if (!repairable.getRepairMaterialType().getPermissions(player)) {
            player.sendMessage(LocaleLoader.getString("mcMMO.NoPermission"));
            return;
        }

        int skillLevel = getSkillLevel();
        int minimumRepairableLevel = repairable.getMinimumLevel();

        // Level check
        if (skillLevel < minimumRepairableLevel) {
            player.sendMessage(LocaleLoader.getString("Repair.Skills.Adept", minimumRepairableLevel, StringUtils.getPrettyItemString(itemId)));
            return;
        }

        PlayerInventory inventory = player.getInventory();

        int repairMaterialId = repairable.getRepairMaterialId();
        byte repairMaterialMetadata = repairable.getRepairMaterialMetadata();

        // Check if they have the proper material to repair with
        if (!inventory.contains(repairMaterialId)) {
            String message = LocaleLoader.getString("Skills.NeedMore", StringUtils.getPrettyItemString(repairMaterialId));

            if (repairMaterialMetadata != (byte) -1) {
                // TODO: Do something nicer than append the metadata as a :# ?
                if (Repair.findInInventory(inventory, repairMaterialId, repairMaterialMetadata) == -1) {
                    message += ":" + repairMaterialMetadata;
                }
            }

            player.sendMessage(message);
            return;
        }

        short startDurability = item.getDurability();

        // Do not repair if at full durability
        if (startDurability <= 0) {
            player.sendMessage(LocaleLoader.getString("Repair.Skills.FullDurability"));
            return;
        }

        // Do not repair stacked items
        if (item.getAmount() != 1) {
            player.sendMessage(LocaleLoader.getString("Repair.Skills.StackedItems"));
            return;
        }

        // Clear ability buffs before trying to repair.
        SkillUtils.removeAbilityBuff(item);

        // Lets get down to business,
        // To defeat, the huns.
        int baseRepairAmount = repairable.getBaseRepairDurability(); // Did they send me daughters?
        short newDurability = repairCalculate(startDurability, baseRepairAmount); // When I asked for sons?

        // We're going to hold onto our repair item location
        int repairItemLocation;
        if (repairable.getRepairMaterialMetadata() == (byte) -1) {
            repairItemLocation = Repair.findInInventory(inventory, repairMaterialId);
        }
        else {
            // Special case for when the repairable has metadata that must be addressed
            repairItemLocation = Repair.findInInventory(inventory, repairMaterialId, repairMaterialMetadata);
        }

        // This should never happen, but if it does we need to complain loudly about it.
        if (repairItemLocation == -1) {
            player.sendMessage(LocaleLoader.getString("Repair.Error"));
            return;
        }

        // Call event
        McMMOPlayerRepairCheckEvent event = new McMMOPlayerRepairCheckEvent(player, (short) (startDurability - newDurability), inventory.getItem(repairItemLocation), item);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        // Handle the enchants
        if (ArcaneForging.arcaneForgingEnchantLoss) {
            addEnchants(item);
        }

        // Remove the item
        Repair.removeOneFrom(inventory, repairItemLocation);

        // Give out XP like candy
        applyXpGain((int) ((startDurability - newDurability) * repairable.getXpMultiplier()) * 10);

        // BWONG BWONG BWONG
        player.playSound(player.getLocation(), Sound.ANVIL_USE, Misc.ANVIL_USE_VOLUME, Misc.ANVIL_USE_PITCH);

        // Repair the item!
        item.setDurability(newDurability);
    }

    public void handleSalvage(Location location, ItemStack item) {
        Player player = getPlayer();

        if (getSkillLevel() < Repair.salvageUnlockLevel) {
            player.sendMessage(LocaleLoader.getString("Repair.Skills.AdeptSalvage"));
            return;
        }

        if (item.getDurability() == 0) {
            player.setItemInHand(new ItemStack(Material.AIR));
            location.setY(location.getY() + 1);

            Misc.dropItems(location, new ItemStack(Repair.getSalvagedItem(item)), Repair.getSalvagedAmount(item) * item.getAmount());

            player.playSound(player.getLocation(), Sound.ANVIL_USE, Misc.ANVIL_USE_VOLUME, Misc.ANVIL_USE_PITCH);
            player.sendMessage(LocaleLoader.getString("Repair.Skills.SalvageSuccess"));
        }
        else {
            player.sendMessage(LocaleLoader.getString("Repair.Skills.NotFullDurability"));
        }
    }

    /**
     * Check if the player has tried to use an Anvil before.
     *
     * @return true if the player has confirmed using an Anvil
     */
    public boolean checkConfirmation(int anvilId, boolean actualize) {
        Player player = getPlayer();
        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        long LastUse =  mcMMOPlayer.getLastAnvilUse(anvilId);

        // Don't use SkillUtils.cooldownOver() here since that also accounts for the cooldown perks
        if ((((LastUse + 3) * Misc.TIME_CONVERSION_FACTOR) >= System.currentTimeMillis()) || !Config.getInstance().getRepairConfirmRequired()) {
            return true;
        }

        if (!actualize) {
            return false;
        }

        mcMMOPlayer.actualizeLastAnvilUse(anvilId);

        if (anvilId == Repair.repairAnvilId) {
            player.sendMessage(LocaleLoader.getString("Skills.ConfirmOrCancel", LocaleLoader.getString("Repair.Pretty.Name")));
        }

        if (anvilId == Repair.salvageAnvilId) {
            player.sendMessage(LocaleLoader.getString("Skills.ConfirmOrCancel", LocaleLoader.getString("Salvage.Pretty.Name")));
        }
        return false;
    }

    /**
     * Gets the Arcane Forging rank
     *
     * @return the current Arcane Forging rank
     */
    public int getArcaneForgingRank() {
        int skillLevel = getSkillLevel();

        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.toNumerical();
            }
        }

        return 0;
    }

    /**
     * Gets chance of keeping enchantment during repair.
     *
     * @return The chance of keeping the enchantment
     */
    public int getKeepEnchantChance() {
        int skillLevel = getSkillLevel();

        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.getKeepEnchantChance();
            }
        }

        return 0;
    }

    /**
     * Gets chance of enchantment being downgraded during repair.
     *
     * @return The chance of the enchantment being downgraded
     */
    public int getDowngradeEnchantChance() {
        int skillLevel = getSkillLevel();

        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.getDowngradeEnchantChance();
            }
        }

        return 100;
    }

    /**
     * Computes repair bonuses.
     *
     * @param durability The durability of the item being repaired
     * @param repairAmount The base amount of durability repaired to the item
     * @return The final amount of durability repaired to the item
     */
    private short repairCalculate(short durability, int repairAmount) {
        Player player = getPlayer();

        if (Permissions.repairMastery(player)) {
            double bonus = repairAmount * Math.min((((Repair.repairMasteryMaxBonus / Repair.repairMasteryMaxBonusLevel) * getSkillLevel()) / 100.0D), Repair.repairMasteryMaxBonus / 100.0D);
            repairAmount += bonus;
        }

        if (Permissions.superRepair(player) && checkPlayerProcRepair()) {
            repairAmount *= 2.0D;
        }

        if (repairAmount <= 0 || repairAmount > Short.MAX_VALUE) {
            repairAmount = Short.MAX_VALUE;
        }

        return (short) Math.max(durability - repairAmount, 0);
    }

    /**
     * Checks for Super Repair bonus.
     *
     * @return true if bonus granted, false otherwise
     */
    private boolean checkPlayerProcRepair() {
        if (SkillUtils.activationSuccessful(getSkillLevel(), getActivationChance(), Repair.superRepairMaxChance, Repair.superRepairMaxBonusLevel)) {
            getPlayer().sendMessage(LocaleLoader.getString("Repair.Skills.FeltEasy"));
            return true;
        }

        return false;
    }

    /**
     * Handles removing & downgrading enchants.
     *
     * @param item Item being repaired
     */
    private void addEnchants(ItemStack item) {
        Player player = getPlayer();

        Map<Enchantment, Integer> enchants = item.getEnchantments();

        if (enchants.isEmpty()) {
            return;
        }

        if (Permissions.arcaneBypass(player)) {
            player.sendMessage(LocaleLoader.getString("Repair.Arcane.Perfect"));
            return;
        }

        if (getArcaneForgingRank() == 0 || !Permissions.arcaneForging(player)) {
            for (Enchantment enchant : enchants.keySet()) {
                item.removeEnchantment(enchant);
            }

            player.sendMessage(LocaleLoader.getString("Repair.Arcane.Lost"));
            return;
        }

        boolean downgraded = false;

        for (Entry<Enchantment, Integer> enchant : enchants.entrySet()) {
            Enchantment enchantment = enchant.getKey();

            if (getKeepEnchantChance() > Misc.getRandom().nextInt(activationChance)) {
                int enchantLevel = enchant.getValue();

                if (ArcaneForging.arcaneForgingDowngrades && enchantLevel > 1 && getDowngradeEnchantChance() > Misc.getRandom().nextInt(activationChance)) {
                    item.addEnchantment(enchantment, enchantLevel - 1);
                    downgraded = true;
                }
            }
            else {
                item.removeEnchantment(enchantment);
            }
        }

        Map<Enchantment, Integer> newEnchants = item.getEnchantments();

        if (newEnchants.isEmpty()) {
            player.sendMessage(LocaleLoader.getString("Repair.Arcane.Fail"));
        }
        else if (downgraded || newEnchants.size() < enchants.size()) {
            player.sendMessage(LocaleLoader.getString("Repair.Arcane.Downgrade"));
        }
        else {
            player.sendMessage(LocaleLoader.getString("Repair.Arcane.Perfect"));
        }
    }
}
