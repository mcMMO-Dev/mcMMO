package com.gmail.nossr50.skills.salvage;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.skills.SkillUtils;

public class Salvage {
    // The order of the values is extremely important, a few methods depend on it to work properly
    public enum Tier {
        EIGHT(8),
        SEVEN(7),
        SIX(6),
        FIVE(5),
        FOUR(4),
        THREE(3),
        TWO(2),
        ONE(1);

        int numerical;

        private Tier(int numerical) {
            this.numerical = numerical;
        }

        public int toNumerical() {
            return numerical;
        }

        protected int getLevel() {
            return AdvancedConfig.getInstance().getArcaneSalvageRankLevel(this);
        }

        protected double getExtractFullEnchantChance() {
            return AdvancedConfig.getInstance().getArcaneSalvageExtractFullEnchantsChance(this);
        }

        protected double getExtractPartialEnchantChance() {
            return AdvancedConfig.getInstance().getArcaneSalvageExtractPartialEnchantsChance(this);
        }
    }

    public static Material anvilMaterial = Config.getInstance().getSalvageAnvilMaterial();

    public static int    salvageMaxPercentageLevel = AdvancedConfig.getInstance().getSalvageMaxPercentageLevel();
    public static double salvageMaxPercentage      = AdvancedConfig.getInstance().getSalvageMaxPercentage();

    public static int advancedSalvageUnlockLevel = AdvancedConfig.getInstance().getAdvancedSalvageUnlockLevel();

    public static boolean arcaneSalvageDowngrades  = AdvancedConfig.getInstance().getArcaneSalvageEnchantDowngradeEnabled();
    public static boolean arcaneSalvageEnchantLoss = AdvancedConfig.getInstance().getArcaneSalvageEnchantLossEnabled();

    /**
     * Checks if the item is salvageable.
     *
     * @param item Item to check
     *
     * @return true if the item is salvageable, false otherwise
     */
    public static boolean isSalvageable(ItemStack item) {
        if (Config.getInstance().getSalvageTools() && ItemUtils.isMinecraftTool(item)) {
            return true;
        }

        if (Config.getInstance().getSalvageArmor() && !ItemUtils.isChainmailArmor(item) && ItemUtils.isMinecraftArmor(item)) {
            return true;
        }

        return false;
    }

    protected static Material getSalvagedItem(ItemStack inHand) {
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

    protected static int getSalvagedAmount(ItemStack inHand) {
        return SkillUtils.getRepairAndSalvageQuantities(inHand, getSalvagedItem(inHand), (byte) -1);
    }

    protected static int calculateSalvageableAmount(short currentDurability, short maxDurability, int baseAmount) {
        double percentDamaged = (double) (maxDurability - currentDurability) / maxDurability;

        return (int) Math.floor(baseAmount * percentDamaged);
    }
}
