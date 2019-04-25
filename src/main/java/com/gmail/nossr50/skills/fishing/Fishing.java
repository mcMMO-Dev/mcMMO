package com.gmail.nossr50.skills.fishing;

import com.gmail.nossr50.config.treasure.FishingTreasureConfig;
import com.gmail.nossr50.datatypes.treasure.ShakeTreasure;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.adapter.BiomeAdapter;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public final class Fishing {

    public static Fishing instance;
    private final long fishingRodCastCdMilliseconds;
    private final int overfishLimit;
    private final float boundingBoxSize;
    private HashMap<Material, List<Enchantment>> enchantableCache = new HashMap<>();
    private HashMap<Material, Integer> fishingXpRewardMap;
    private Set<Biome> masterAnglerBiomes = BiomeAdapter.WATER_BIOMES;
    private Set<Biome> iceFishingBiomes = BiomeAdapter.ICE_BIOMES;

    public Fishing() {
        overfishLimit = mcMMO.getConfigManager().getConfigExploitPrevention().getOverfishingLimit() + 1;
        fishingRodCastCdMilliseconds = mcMMO.getConfigManager().getConfigExploitPrevention().getFishingRodSpamMilliseconds();
        boundingBoxSize = mcMMO.getConfigManager().getConfigExploitPrevention().getOverFishingAreaSize();
        initFishingXPRewardMap();
    }

    public static Fishing getInstance() {
        if (instance == null)
            instance = new Fishing();

        return instance;
    }

    /**
     * Inits the Fishing Catch -> XP Reward map
     */
    private void initFishingXPRewardMap() {
        fishingXpRewardMap = new HashMap<>();
        HashMap<String, Integer> nameRegisterMap = mcMMO.getConfigManager().getConfigExperience().getFishingXPMap();

        for (String qualifiedName : nameRegisterMap.keySet()) {
            Material material = Material.matchMaterial(qualifiedName);

            if (material == null) {
                mcMMO.p.getLogger().info("Unable to match qualified name to item for fishing xp map: " + qualifiedName);
                continue;
            }

            fishingXpRewardMap.putIfAbsent(material, nameRegisterMap.get(qualifiedName));
        }
    }

    /**
     * Finds the possible drops of an entity
     *
     * @param target Targeted entity
     * @return possibleDrops List of ItemStack that can be dropped
     */
    public List<ShakeTreasure> findPossibleDrops(LivingEntity target) {
        if (FishingTreasureConfig.getInstance().shakeMap.containsKey(target.getType()))
            return FishingTreasureConfig.getInstance().shakeMap.get(target.getType());

        return null;
    }

    /**
     * Randomly chooses a drop among the list
     *
     * @param possibleDrops List of ItemStack that can be dropped
     * @return Chosen ItemStack
     */
    public ItemStack chooseDrop(List<ShakeTreasure> possibleDrops) {
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

    public HashMap<Material, List<Enchantment>> getEnchantableCache() {
        return enchantableCache;
    }

    public HashMap<Material, Integer> getFishingXpRewardMap() {
        return fishingXpRewardMap;
    }

    public Set<Biome> getMasterAnglerBiomes() {
        return masterAnglerBiomes;
    }

    public Set<Biome> getIceFishingBiomes() {
        return iceFishingBiomes;
    }

    public int getFishXPValue(Material material) {
        return fishingXpRewardMap.get(material);
    }

    public long getFishingRodCastCdMilliseconds() {
        return fishingRodCastCdMilliseconds;
    }

    public int getOverfishLimit() {
        return overfishLimit;
    }

    public float getBoundingBoxSize() {
        return boundingBoxSize;
    }
}
