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
import org.bukkit.material.MaterialData;

import com.gmail.nossr50.datatypes.skills.XPGainReason;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.repair.ArcaneForging.Tier;
import com.gmail.nossr50.skills.repair.repairables.Repairable;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.skills.SkillUtils;

public class RepairManager extends SkillManager {
    private boolean placedRepairAnvil;
    private int     lastRepairClick;
    private boolean placedSalvageAnvil;
    private int     lastSalvageClick;

    public RepairManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, SkillType.REPAIR);
    }

    /**
     * Handles notifications for placing an anvil.
     *
     * @param anvilType The {@link Material} of the anvil block
     */
    public void placedAnvilCheck(Material anvilType) {
        Player player = getPlayer();

        if (getPlacedAnvil(anvilType)) {
            return;
        }

        if (Repair.anvilMessagesEnabled) {
            player.sendMessage(Repair.getAnvilMessage(anvilType));
        }

        if (Config.getInstance().getRepairAnvilPlaceSoundsEnabled()) {
            player.playSound(player.getLocation(), Sound.ANVIL_LAND, Misc.ANVIL_USE_VOLUME, Misc.ANVIL_USE_PITCH);
        }

        togglePlacedAnvil(anvilType);
    }

    public void handleRepair(ItemStack item) {
        Player player = getPlayer();
        Repairable repairable = mcMMO.getRepairableManager().getRepairable(item.getType());

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
            player.sendMessage(LocaleLoader.getString("Repair.Skills.Adept", minimumRepairableLevel, StringUtils.getPrettyItemString(item.getType())));
            return;
        }

        PlayerInventory inventory = player.getInventory();

        Material repairMaterial = repairable.getRepairMaterial();
        byte repairMaterialMetadata = repairable.getRepairMaterialMetadata();
        ItemStack toRemove = new MaterialData(repairMaterial, repairMaterialMetadata).toItemStack(1);
        
        short startDurability = item.getDurability();

        // Do not repair if at full durability
        if (startDurability <= 0) {
            player.sendMessage(LocaleLoader.getString("Repair.Skills.FullDurability"));
            return;
        }
        
        // Check if they have the proper material to repair with
        if (!inventory.contains(repairMaterial)) {
            String message = LocaleLoader.getString("Skills.NeedMore", StringUtils.getPrettyItemString(repairMaterial));

            if (repairMaterialMetadata != (byte) -1 && !inventory.containsAtLeast(toRemove, 1)) {
                message += ":" + repairMaterialMetadata;
            }

            player.sendMessage(message);
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

        // Call event
        if (EventUtils.callRepairCheckEvent(player, (short) (startDurability - newDurability), toRemove, item).isCancelled()) {
            return;
        }

        // Handle the enchants
        if (ArcaneForging.arcaneForgingEnchantLoss) {
            addEnchants(item);
        }

        // Remove the item
        if (repairMaterialMetadata == -1) {
            toRemove = inventory.getItem(inventory.first(repairMaterial)).clone();
            toRemove.setAmount(1);
        }

        inventory.removeItem(toRemove);

        // Give out XP like candy
        applyXpGain((float) ((getPercentageRepaired(startDurability, newDurability, repairable.getMaximumDurability()) * repairable.getXpMultiplier()) * ExperienceConfig.getInstance().getRepairXPBase() * ExperienceConfig.getInstance().getRepairXP(repairable.getRepairMaterialType())), XPGainReason.PVE);

        // BWONG BWONG BWONG
        if (Config.getInstance().getRepairAnvilUseSoundsEnabled()) {
            player.playSound(player.getLocation(), Sound.ANVIL_USE, Misc.ANVIL_USE_VOLUME, Misc.ANVIL_USE_PITCH);
        }

        // Repair the item!
        item.setDurability(newDurability);
    }

    private float getPercentageRepaired(short startDurability, short newDurability, short totalDurability) {
        return ((startDurability - newDurability) / (float) totalDurability);
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

            Misc.dropItems(location, new ItemStack(Repair.getRepairAndSalvageItem(item)), Repair.getRepairAndSalvageQuantities(item) * item.getAmount());

            if (Config.getInstance().getRepairAnvilUseSoundsEnabled()) {
                player.playSound(player.getLocation(), Sound.ANVIL_USE, Misc.ANVIL_USE_VOLUME, Misc.ANVIL_USE_PITCH);
            }

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
    public boolean checkConfirmation(Material anvilType, boolean actualize) {
        Player player = getPlayer();
        long lastUse = getLastAnvilUse(anvilType);

        if (!SkillUtils.cooldownExpired(lastUse, 3) || !Config.getInstance().getRepairConfirmRequired()) {
            return true;
        }

        if (!actualize) {
            return false;
        }

        actualizeLastAnvilUse(anvilType);

        if (anvilType == Repair.repairAnvilMaterial) {
            player.sendMessage(LocaleLoader.getString("Skills.ConfirmOrCancel", LocaleLoader.getString("Repair.Pretty.Name")));
        }
        else if (anvilType == Repair.salvageAnvilMaterial) {
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
    public double getKeepEnchantChance() {
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
    public double getDowngradeEnchantChance() {
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

        if (Permissions.secondaryAbilityEnabled(player, SecondaryAbility.REPAIR_MASTERY)) {
            double bonus = repairAmount * Math.min((((Repair.repairMasteryMaxBonus / Repair.repairMasteryMaxBonusLevel) * getSkillLevel()) / 100.0D), Repair.repairMasteryMaxBonus / 100.0D);
            repairAmount += bonus;
        }

        if (Permissions.secondaryAbilityEnabled(player, SecondaryAbility.SUPER_REPAIR) && checkPlayerProcRepair()) {
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
        if (SkillUtils.activationSuccessful(SecondaryAbility.SUPER_REPAIR, getPlayer(), getSkillLevel(), activationChance)) {
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

        if (getArcaneForgingRank() == 0 || !Permissions.secondaryAbilityEnabled(player, SecondaryAbility.ARCANE_FORGING)) {
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

                if (ArcaneForging.arcaneForgingDowngrades && enchantLevel > 1 && (100 - getDowngradeEnchantChance()) <= Misc.getRandom().nextInt(activationChance)) {
                    item.addUnsafeEnchantment(enchantment, enchantLevel - 1);
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

    /*
     * Repair Anvil Placement
     */

    public boolean getPlacedAnvil(Material anvilType) {
        if (anvilType == Repair.repairAnvilMaterial) {
            return placedRepairAnvil;
        }

        if (anvilType == Repair.salvageAnvilMaterial) {
            return placedSalvageAnvil;
        }

        return true;
    }

    public void togglePlacedAnvil(Material anvilType) {
        if (anvilType == Repair.repairAnvilMaterial) {
            placedRepairAnvil = !placedRepairAnvil;
        }

        if (anvilType == Repair.salvageAnvilMaterial) {
            placedSalvageAnvil = !placedSalvageAnvil;
        }
    }

    /*
     * Repair Anvil Usage
     */

    public int getLastAnvilUse(Material anvilType) {
        if (anvilType == Repair.repairAnvilMaterial) {
            return lastRepairClick;
        }

        if (anvilType == Repair.salvageAnvilMaterial) {
            return lastSalvageClick;
        }

        return 0;
    }

    public void setLastAnvilUse(Material anvilType, int value) {
        if (anvilType == Repair.repairAnvilMaterial) {
            lastRepairClick = value;
        }

        if (anvilType == Repair.salvageAnvilMaterial) {
            lastSalvageClick = value;
        }
    }

    public void actualizeLastAnvilUse(Material anvilType) {
        if (anvilType == Repair.repairAnvilMaterial) {
            lastRepairClick = (int) (System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR);
        }

        if (anvilType == Repair.salvageAnvilMaterial) {
            lastSalvageClick = (int) (System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR);
        }
    }
}
