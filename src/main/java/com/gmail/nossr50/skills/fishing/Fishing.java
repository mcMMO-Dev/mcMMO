package com.gmail.nossr50.skills.fishing;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.treasure.TreasureConfig;
import com.gmail.nossr50.datatypes.treasure.ShakeTreasure;
import com.gmail.nossr50.util.Misc;

public final class Fishing {
    // The order of the values is extremely important, a few methods depend on it to work properly
    protected enum Tier {
        FIVE(5) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getFishingTierLevelsTier5(); }
            @Override public int getShakeChance() { return AdvancedConfig.getInstance().getShakeChanceRank5(); }
            @Override public int getVanillaXPBoostModifier() { return AdvancedConfig.getInstance().getFishingVanillaXPModifierRank5(); }},
        FOUR(4) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getFishingTierLevelsTier4(); }
            @Override public int getShakeChance() { return AdvancedConfig.getInstance().getShakeChanceRank4(); }
            @Override public int getVanillaXPBoostModifier() { return AdvancedConfig.getInstance().getFishingVanillaXPModifierRank4(); }},
        THREE(3) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getFishingTierLevelsTier3(); }
            @Override public int getShakeChance() { return AdvancedConfig.getInstance().getShakeChanceRank3(); }
            @Override public int getVanillaXPBoostModifier() { return AdvancedConfig.getInstance().getFishingVanillaXPModifierRank3(); }},
        TWO(2) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getFishingTierLevelsTier2(); }
            @Override public int getShakeChance() { return AdvancedConfig.getInstance().getShakeChanceRank2(); }
            @Override public int getVanillaXPBoostModifier() { return AdvancedConfig.getInstance().getFishingVanillaXPModifierRank2(); }},
        ONE(1) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getFishingTierLevelsTier1(); }
            @Override public int getShakeChance() { return AdvancedConfig.getInstance().getShakeChanceRank1(); }
            @Override public int getVanillaXPBoostModifier() { return AdvancedConfig.getInstance().getFishingVanillaXPModifierRank1(); }};

        int numerical;

        private Tier(int numerical) {
            this.numerical = numerical;
        }

        public int toNumerical() {
            return numerical;
        }

        abstract protected int getLevel();
        abstract protected int getShakeChance();
        abstract protected int getVanillaXPBoostModifier();
    }

    protected static final HashMap<Material, List<Enchantment>> ENCHANTABLE_CACHE = new HashMap<Material, List<Enchantment>>();

    public static int fishermansDietRankLevel1 = AdvancedConfig.getInstance().getFishermanDietRankChange();
    public static int fishermansDietRankLevel2 = fishermansDietRankLevel1 * 2;
    public static int fishermansDietMaxLevel   = fishermansDietRankLevel1 * 5;

    public static final double STORM_MODIFIER = 0.909;

    private Fishing() {}

    /**
     * Finds the possible drops of an entity
     *
     * @param target Targeted entity
     * @param possibleDrops List of ItemStack that can be dropped
     */
    protected static List<ShakeTreasure> findPossibleDrops(LivingEntity target) {
        switch (target.getType()) {
            case BLAZE:
                return TreasureConfig.getInstance().shakeFromBlaze;

            case CAVE_SPIDER:
                return TreasureConfig.getInstance().shakeFromCaveSpider;

            case CHICKEN:
                return TreasureConfig.getInstance().shakeFromChicken;

            case COW:
                return TreasureConfig.getInstance().shakeFromCow;

            case CREEPER:
                return TreasureConfig.getInstance().shakeFromCreeper;

            case ENDERMAN:
                return TreasureConfig.getInstance().shakeFromEnderman;

            case GHAST:
                return TreasureConfig.getInstance().shakeFromGhast;

            case IRON_GOLEM:
                return TreasureConfig.getInstance().shakeFromIronGolem;

            case MAGMA_CUBE:
                return TreasureConfig.getInstance().shakeFromMagmaCube;

            case MUSHROOM_COW:
                return TreasureConfig.getInstance().shakeFromMushroomCow;

            case PIG:
                return TreasureConfig.getInstance().shakeFromPig;

            case PIG_ZOMBIE:
                return TreasureConfig.getInstance().shakeFromPigZombie;

            case SHEEP:
                return TreasureConfig.getInstance().shakeFromSheep;

            case SKELETON:
                return TreasureConfig.getInstance().shakeFromSkeleton;

            case SLIME:
                return TreasureConfig.getInstance().shakeFromSlime;

            case SNOWMAN:
                return TreasureConfig.getInstance().shakeFromSnowman;

            case SPIDER:
                return TreasureConfig.getInstance().shakeFromSpider;

            case SQUID:
                return TreasureConfig.getInstance().shakeFromSquid;

            case WITCH:
                return TreasureConfig.getInstance().shakeFromWitch;

            case ZOMBIE:
                return TreasureConfig.getInstance().shakeFromZombie;

            default:
                return null;
        }
    }

    /**
     * Randomly chooses a drop among the list
     *
     * @param possibleDrops List of ItemStack that can be dropped
     * @return Chosen ItemStack
     */
    protected static ItemStack chooseDrop(List<ShakeTreasure> possibleDrops) {
        int dropProbability = Misc.getRandom().nextInt(100);
        double cumulatedProbability = 0;

        for (ShakeTreasure treasure : possibleDrops) {
            cumulatedProbability += treasure.getDropChance();

            if (dropProbability < cumulatedProbability) {
                return treasure.getDrop();
            }
        }

        return null;
    }
}
