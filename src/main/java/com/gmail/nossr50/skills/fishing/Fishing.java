package com.gmail.nossr50.skills.fishing;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import com.gmail.nossr50.config.AdvancedConfig;
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
    protected static void findPossibleDrops(LivingEntity target, Map<ItemStack, Integer> possibleDrops) {
        switch (target.getType()) {
            case BLAZE:
                possibleDrops.put(new ItemStack(Material.BLAZE_ROD), 100);
                break;

            case CAVE_SPIDER:
            case SPIDER:
                possibleDrops.put(new ItemStack(Material.SPIDER_EYE), 50);
                possibleDrops.put(new ItemStack(Material.STRING), 50);
                break;

            case CHICKEN:
                possibleDrops.put(new ItemStack(Material.FEATHER), 34);
                possibleDrops.put(new ItemStack(Material.RAW_CHICKEN), 33);
                possibleDrops.put(new ItemStack(Material.EGG), 33);
                break;

            case COW:
                possibleDrops.put(new ItemStack(Material.MILK_BUCKET), 2);
                possibleDrops.put(new ItemStack(Material.LEATHER), 49);
                possibleDrops.put(new ItemStack(Material.RAW_BEEF), 49);
                break;

            case CREEPER:
                possibleDrops.put(new ItemStack(Material.SKULL_ITEM, 1, (short) 4), 1);
                possibleDrops.put(new ItemStack(Material.SULPHUR), 99);
                break;

            case ENDERMAN:
                possibleDrops.put(new ItemStack(Material.ENDER_PEARL), 100);
                break;

            case GHAST:
                possibleDrops.put(new ItemStack(Material.SULPHUR), 50);
                possibleDrops.put(new ItemStack(Material.GHAST_TEAR), 50);
                break;

            case IRON_GOLEM:
                possibleDrops.put(new ItemStack(Material.PUMPKIN), 3);
                possibleDrops.put(new ItemStack(Material.IRON_INGOT), 12);
                possibleDrops.put(new ItemStack(Material.RED_ROSE), 85);
                break;

            case MAGMA_CUBE:
                possibleDrops.put(new ItemStack(Material.MAGMA_CREAM), 100);
                break;

            case MUSHROOM_COW:
                possibleDrops.put(new ItemStack(Material.MILK_BUCKET), 5);
                possibleDrops.put(new ItemStack(Material.MUSHROOM_SOUP), 5);
                possibleDrops.put(new ItemStack(Material.LEATHER), 30);
                possibleDrops.put(new ItemStack(Material.RAW_BEEF), 30);
                possibleDrops.put(new ItemStack(Material.RED_MUSHROOM, Misc.getRandom().nextInt(3) + 1), 30);
                break;

            case PIG:
                possibleDrops.put(new ItemStack(Material.PORK), 100);
                break;

            case PIG_ZOMBIE:
                possibleDrops.put(new ItemStack(Material.ROTTEN_FLESH), 50);
                possibleDrops.put(new ItemStack(Material.GOLD_NUGGET), 50);
                break;

            case SHEEP:
                possibleDrops.put(new ItemStack(Material.WOOL, Misc.getRandom().nextInt(6) + 1), 100);
                break;

            case SKELETON:
                possibleDrops.put(new ItemStack(Material.SKULL_ITEM, 1, (short) 0), 2);
                possibleDrops.put(new ItemStack(Material.BONE), 49);
                possibleDrops.put(new ItemStack(Material.ARROW, Misc.getRandom().nextInt(3) + 1), 49);
                break;

            case SLIME:
                possibleDrops.put(new ItemStack(Material.SLIME_BALL), 100);
                break;

            case SNOWMAN:
                possibleDrops.put(new ItemStack(Material.PUMPKIN), 3);
                possibleDrops.put(new ItemStack(Material.SNOW_BALL, Misc.getRandom().nextInt(4) + 1), 97);
                break;

            case SQUID:
                possibleDrops.put(new ItemStack(Material.INK_SACK, 1, DyeColor.BLACK.getDyeData()), 100);
                break;

            case WITCH:
                possibleDrops.put(new Potion(PotionType.INSTANT_HEAL).toItemStack(1), 1);
                possibleDrops.put(new Potion(PotionType.FIRE_RESISTANCE).toItemStack(1), 1);
                possibleDrops.put(new Potion(PotionType.SPEED).toItemStack(1), 1);
                possibleDrops.put(new ItemStack(Material.GLASS_BOTTLE), 9);
                possibleDrops.put(new ItemStack(Material.GLOWSTONE_DUST), 13);
                possibleDrops.put(new ItemStack(Material.SULPHUR), 12);
                possibleDrops.put(new ItemStack(Material.REDSTONE), 13);
                possibleDrops.put(new ItemStack(Material.SPIDER_EYE), 12);
                possibleDrops.put(new ItemStack(Material.STICK), 13);
                possibleDrops.put(new ItemStack(Material.SUGAR), 12);
                possibleDrops.put(new ItemStack(Material.POTION), 13);
                break;

            case ZOMBIE:
                possibleDrops.put(new ItemStack(Material.SKULL_ITEM, 1, (short) 2), 2);
                possibleDrops.put(new ItemStack(Material.ROTTEN_FLESH), 98);
                break;

            default:
                return;
        }
    }

    /**
     * Randomly chooses a drop among the list
     *
     * @param possibleDrops List of ItemStack that can be dropped
     * @return Chosen ItemStack
     */
    protected static ItemStack chooseDrop(Map<ItemStack, Integer> possibleDrops) {
        int dropProbability = Misc.getRandom().nextInt(100);
        int cumulatedProbability = 0;

        for (Entry<ItemStack, Integer> entry : possibleDrops.entrySet()) {
            cumulatedProbability += entry.getValue();

            if (dropProbability < cumulatedProbability) {
                return entry.getKey();
            }
        }

        return null;
    }
}
