package com.gmail.nossr50.skills.repair;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.repair.repairables.Repairable;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import com.gmail.nossr50.util.text.StringUtils;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class RepairManager extends SkillManager {
    private boolean placedAnvil;
    private int lastClick;

    public RepairManager(McMMOPlayer mmoPlayer) {
        super(mmoPlayer, PrimarySkillType.REPAIR);
    }

    /**
     * Handles notifications for placing an anvil.
     */
    public void placedAnvilCheck() {
        Player player = getPlayer();

        if (getPlacedAnvil()) {
            return;
        }

        if (mcMMO.p.getGeneralConfig().getRepairAnvilMessagesEnabled()) {
            NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE,
                    "Repair.Listener.Anvil");
        }

        if (mcMMO.p.getGeneralConfig().getRepairAnvilPlaceSoundsEnabled()) {
            SoundManager.sendCategorizedSound(player, player.getLocation(), SoundType.ANVIL,
                    SoundCategory.BLOCKS);
        }

        togglePlacedAnvil();
    }

    public void handleRepair(ItemStack item) {
        Player player = getPlayer();
        Repairable repairable = mcMMO.getRepairableManager().getRepairable(item.getType());
        if (item.getItemMeta() != null) {
            if (item.getItemMeta().hasCustomModelData()) {
                if (!mcMMO.p.getCustomItemSupportConfig().isCustomRepairAllowed()) {
                    NotificationManager.sendPlayerInformation(player,
                            NotificationType.SUBSKILL_MESSAGE_FAILED,
                            "Anvil.Repair.Reject.CustomModelData");
                    return;
                }
            }

            if (item.getItemMeta().isUnbreakable()) {
                NotificationManager.sendPlayerInformation(player,
                        NotificationType.SUBSKILL_MESSAGE_FAILED, "Anvil.Unbreakable");
                return;
            }
        }

        // Permissions checks on material and item types
        if (!Permissions.repairMaterialType(player, repairable.getRepairMaterialType())) {
            NotificationManager.sendPlayerInformation(player, NotificationType.NO_PERMISSION,
                    "mcMMO.NoPermission");
            return;
        }

        if (!Permissions.repairItemType(player, repairable.getRepairItemType())) {
            NotificationManager.sendPlayerInformation(player, NotificationType.NO_PERMISSION,
                    "mcMMO.NoPermission");
            return;
        }

        int skillLevel = getSkillLevel();
        int minimumRepairableLevel = repairable.getMinimumLevel();

        // Level check
        if (skillLevel < minimumRepairableLevel) {
            NotificationManager.sendPlayerInformation(player,
                    NotificationType.SUBSKILL_MESSAGE_FAILED, "Repair.Skills.Adept",
                    String.valueOf(minimumRepairableLevel),
                    StringUtils.getPrettyMaterialString(item.getType()));
            return;
        }

        PlayerInventory inventory = player.getInventory();

        Material repairMaterial = repairable.getRepairMaterial();
        ItemStack toRemove = new ItemStack(repairMaterial);

        short startDurability = item.getDurability();

        // Do not repair if at full durability
        if (startDurability <= 0) {
            NotificationManager.sendPlayerInformation(player,
                    NotificationType.SUBSKILL_MESSAGE_FAILED, "Repair.Skills.FullDurability");
            return;
        }

        // Check if they have the proper material to repair with
        if (!inventory.contains(repairMaterial)) {
            String prettyName = repairable.getRepairMaterialPrettyName() == null
                    ? StringUtils.getPrettyMaterialString(repairMaterial)
                    : repairable.getRepairMaterialPrettyName();

            String materialsNeeded = "";

            NotificationManager.sendPlayerInformation(player,
                    NotificationType.SUBSKILL_MESSAGE_FAILED, "Skills.NeedMore.Extra", prettyName,
                    materialsNeeded);
            return;
        }

        // Do not repair stacked items
        if (item.getAmount() != 1) {
            NotificationManager.sendPlayerInformation(player,
                    NotificationType.SUBSKILL_MESSAGE_FAILED, "Repair.Skills.StackedItems");
            return;
        }

        // Clear ability buffs before trying to repair.
        SkillUtils.removeAbilityBuff(item);

        // Lets get down to business,
        // To defeat, the huns.
        int baseRepairAmount = repairable.getBaseRepairDurability(
                item); // Did they send me daughters?
        short newDurability = repairCalculate(startDurability,
                baseRepairAmount); // When I asked for sons?

        // toRemove should be refreshed before the event call.
        toRemove = inventory.getItem(inventory.first(repairMaterial)).clone();

        // Check if we allow enchanted materials to be used to repair objects.
        // (Servers may provide enchanted items that don't follow their intended use)
        if (!mcMMO.p.getAdvancedConfig().getAllowEnchantedRepairMaterials()) {

            // See if our proposed item is even enchanted in the first place.
            if (toRemove.getEnchantments().size() > 0) {

                // Lots of array sorting to find a potential non-enchanted candidate item.
                Optional<ItemStack> possibleMaterial = Arrays.stream(inventory.getContents())
                        .filter(Objects::nonNull)
                        .filter(p -> p.getType() == repairMaterial)
                        .filter(p -> p.getEnchantments().isEmpty())
                        .findFirst();

                // Fail out with "you need material" if we don't find a suitable alternative.
                if (possibleMaterial.isEmpty()) {
                    String prettyName = repairable.getRepairMaterialPrettyName() == null
                            ? StringUtils.getPrettyMaterialString(repairMaterial)
                            : repairable.getRepairMaterialPrettyName();

                    String materialsNeeded = "";

                    NotificationManager.sendPlayerInformation(player,
                            NotificationType.SUBSKILL_MESSAGE_FAILED, "Skills.NeedMore.Extra",
                            prettyName, materialsNeeded);
                    return;
                }

                // Update our toRemove item to our suggested possible material.
                toRemove = possibleMaterial.get().clone();
            }
        }

        // Call event
        if (EventUtils.callRepairCheckEvent(player, (short) (startDurability - newDurability),
                toRemove, item).isCancelled()) {
            return;
        }

        // Handle the enchants
        if (ArcaneForging.arcaneForgingEnchantLoss && !Permissions.hasRepairEnchantBypassPerk(
                player)) {
            addEnchants(item);
        }

        // Remove the item
        toRemove.setAmount(1);

        inventory.removeItem(toRemove);

        // Give out XP like candy
        applyXpGain((float) ((getPercentageRepaired(startDurability, newDurability,
                        repairable.getMaximumDurability())
                        * repairable.getXpMultiplier())
                        * ExperienceConfig.getInstance().getRepairXPBase()
                        * ExperienceConfig.getInstance().getRepairXP(repairable.getRepairMaterialType())),
                XPGainReason.PVE, XPGainSource.SELF);

        // BWONG BWONG BWONG
        if (mcMMO.p.getGeneralConfig().getRepairAnvilUseSoundsEnabled()) {
            SoundManager.sendCategorizedSound(player, player.getLocation(), SoundType.ANVIL,
                    SoundCategory.BLOCKS);
            SoundManager.sendCategorizedSound(player, player.getLocation(), SoundType.ITEM_BREAK,
                    SoundCategory.PLAYERS);
        }

        // Repair the item!
        item.setDurability(newDurability);
    }

    private float getPercentageRepaired(short startDurability, short newDurability,
            short totalDurability) {
        return ((startDurability - newDurability) / (float) totalDurability);
    }

    /**
     * Check if the player has tried to use an Anvil before.
     *
     * @return true if the player has confirmed using an Anvil
     */
    public boolean checkConfirmation(boolean actualize) {
        Player player = getPlayer();
        long lastUse = getLastAnvilUse();

        if (!SkillUtils.cooldownExpired(lastUse, 3) || !mcMMO.p.getGeneralConfig()
                .getRepairConfirmRequired()) {
            return true;
        }

        if (!actualize) {
            return false;
        }

        actualizeLastAnvilUse();
        NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE,
                "Skills.ConfirmOrCancel", LocaleLoader.getString("Repair.Pretty.Name"));

        return false;
    }

    /**
     * Gets the Arcane Forging rank
     *
     * @return the current Arcane Forging rank
     */
    public int getArcaneForgingRank() {
        return RankUtils.getRank(getPlayer(), SubSkillType.REPAIR_ARCANE_FORGING);
    }

    /**
     * Gets chance of keeping enchantment during repair.
     *
     * @return The chance of keeping the enchantment
     */
    public double getKeepEnchantChance() {
        return mcMMO.p.getAdvancedConfig()
                .getArcaneForgingKeepEnchantsChance(getArcaneForgingRank());
    }

    /**
     * Gets chance of enchantment being downgraded during repair.
     *
     * @return The chance of the enchantment being downgraded
     */
    public double getDowngradeEnchantChance() {
        return mcMMO.p.getAdvancedConfig().getArcaneForgingDowngradeChance(getArcaneForgingRank());
    }

    /*
      Gets chance of keeping enchantment during repair.

      @return The chance of keeping the enchantment
     */
    /*public double getKeepEnchantChance() {
        int skillLevel = getSkillLevel();

        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.getKeepEnchantChance();
            }
        }

        return 0;
    }*/

    /*
      Gets chance of enchantment being downgraded during repair.

      @return The chance of the enchantment being downgraded
     */
    /*public double getDowngradeEnchantChance() {
        int skillLevel = getSkillLevel();

        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.getDowngradeEnchantChance();
            }
        }

        return 100;
    }*/

    /**
     * Computes repair bonuses.
     *
     * @param durability The durability of the item being repaired
     * @param repairAmount The base amount of durability repaired to the item
     * @return The final amount of durability repaired to the item
     */
    private short repairCalculate(short durability, int repairAmount) {
        Player player = getPlayer();

        if (Permissions.isSubSkillEnabled(player, SubSkillType.REPAIR_REPAIR_MASTERY)
                && RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.REPAIR_REPAIR_MASTERY)) {

            double maxBonusCalc = Repair.repairMasteryMaxBonus / 100.0D;
            double skillLevelBonusCalc =
                    (Repair.repairMasteryMaxBonus / Repair.repairMasteryMaxBonusLevel) * (
                            getSkillLevel() / 100.0D);
            double bonus = repairAmount * Math.min(skillLevelBonusCalc, maxBonusCalc);

            repairAmount += bonus;
        }

        if (Permissions.isSubSkillEnabled(player, SubSkillType.REPAIR_SUPER_REPAIR)
                && checkPlayerProcRepair()) {
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
        if (!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.REPAIR_SUPER_REPAIR)) {
            return false;
        }

        if (ProbabilityUtil.isSkillRNGSuccessful(SubSkillType.REPAIR_SUPER_REPAIR, mmoPlayer)) {
            NotificationManager.sendPlayerInformation(getPlayer(),
                    NotificationType.SUBSKILL_MESSAGE, "Repair.Skills.FeltEasy");
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
            NotificationManager.sendPlayerInformation(getPlayer(),
                    NotificationType.SUBSKILL_MESSAGE, "Repair.Arcane.Perfect");
            return;
        }

        if (getArcaneForgingRank() == 0 || !Permissions.isSubSkillEnabled(player,
                SubSkillType.REPAIR_ARCANE_FORGING)) {
            for (Enchantment enchant : enchants.keySet()) {
                item.removeEnchantment(enchant);
            }

            NotificationManager.sendPlayerInformation(getPlayer(),
                    NotificationType.SUBSKILL_MESSAGE_FAILED, "Repair.Arcane.Lost");
            return;
        }

        boolean downgraded = false;

        for (Entry<Enchantment, Integer> enchant : enchants.entrySet()) {
            int enchantLevel = enchant.getValue();

            if (!ExperienceConfig.getInstance().allowUnsafeEnchantments()) {
                if (enchantLevel > enchant.getKey().getMaxLevel()) {
                    enchantLevel = enchant.getKey().getMaxLevel();

                    item.addUnsafeEnchantment(enchant.getKey(), enchantLevel);
                }
            }

            Enchantment enchantment = enchant.getKey();

            if (ProbabilityUtil.isStaticSkillRNGSuccessful(PrimarySkillType.REPAIR, mmoPlayer,
                    getKeepEnchantChance())) {

                if (ArcaneForging.arcaneForgingDowngrades && enchantLevel > 1
                        && (!ProbabilityUtil.isStaticSkillRNGSuccessful(PrimarySkillType.REPAIR,
                        mmoPlayer, 100 - getDowngradeEnchantChance()))) {
                    item.addUnsafeEnchantment(enchantment, enchantLevel - 1);
                    downgraded = true;
                }
            } else {
                item.removeEnchantment(enchantment);
            }
        }

        Map<Enchantment, Integer> newEnchants = item.getEnchantments();

        if (newEnchants.isEmpty()) {
            NotificationManager.sendPlayerInformationChatOnly(getPlayer(), "Repair.Arcane.Fail");
        } else if (downgraded || newEnchants.size() < enchants.size()) {
            NotificationManager.sendPlayerInformationChatOnly(getPlayer(),
                    "Repair.Arcane.Downgrade");
        } else {
            NotificationManager.sendPlayerInformationChatOnly(getPlayer(), "Repair.Arcane.Perfect");
        }
    }

    /*
     * Repair Anvil Placement
     */

    public boolean getPlacedAnvil() {
        return placedAnvil;
    }

    public void togglePlacedAnvil() {
        placedAnvil = !placedAnvil;
    }

    /*
     * Repair Anvil Usage
     */

    public int getLastAnvilUse() {
        return lastClick;
    }

    public void setLastAnvilUse(int value) {
        lastClick = value;
    }

    public void actualizeLastAnvilUse() {
        lastClick = (int) (System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR);
    }
}
