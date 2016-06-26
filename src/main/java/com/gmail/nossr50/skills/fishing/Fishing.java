package com.gmail.nossr50.skills.fishing;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.treasure.TreasureConfig;
import com.gmail.nossr50.datatypes.treasure.ShakeTreasure;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.adapter.BiomeAdapter;

public final class Fishing {
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
            return AdvancedConfig.getInstance().getFishingTierLevel(this);
        }

        protected double getShakeChance() {
            return AdvancedConfig.getInstance().getShakeChance(this);
        }

        protected int getVanillaXPBoostModifier() {
            return AdvancedConfig.getInstance().getFishingVanillaXPModifier(this);
        }
    }

    protected static final HashMap<Material, List<Enchantment>> ENCHANTABLE_CACHE = new HashMap<Material, List<Enchantment>>();

    public static int fishermansDietRankLevel1 = AdvancedConfig.getInstance().getFishermanDietRankChange();
    public static int fishermansDietRankLevel2 = fishermansDietRankLevel1 * 2;
    public static int fishermansDietMaxLevel   = fishermansDietRankLevel1 * 5;

    public static Set<Biome> masterAnglerBiomes = BiomeAdapter.WATER_BIOMES;
    public static Set<Biome> iceFishingBiomes = BiomeAdapter.ICE_BIOMES;

    private Fishing() {}

    /**
     * Finds the possible drops of an entity
     *
     * @param target Targeted entity
     * @return possibleDrops List of ItemStack that can be dropped
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

		case PLAYER:
			return TreasureConfig.getInstance().shakeFromPlayer;

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
		case RABBIT:
			return TreasureConfig.getInstance().shakeFromRabbit;

		case GUARDIAN:
			if (((Guardian) target).isElder())
				return TreasureConfig.getInstance().shakeFromElderGuardian;
			return TreasureConfig.getInstance().shakeFromGuardian;

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
                return treasure.getDrop().clone();
            }
        }

        return null;
    }
}
