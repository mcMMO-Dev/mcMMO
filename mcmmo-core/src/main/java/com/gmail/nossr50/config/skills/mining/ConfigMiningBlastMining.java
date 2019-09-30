package com.gmail.nossr50.config.skills.mining;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;

@ConfigSerializable
public class ConfigMiningBlastMining {

    private static final ArrayList<String> DETONATORS_DEFAULT;
    private static final HashMap<Integer, Double> DAMAGE_DECREASE_RANK_MAP;
    private static final HashMap<Integer, Double> OREBONUS_RANK_MAP;
    private static final HashMap<Integer, Double> DEBRIS_REDUCTION_MAP;
    private static final HashMap<Integer, Integer> DROP_MULTIPLIER_MAP;
    private static final HashMap<Integer, Double> RADIUS_MAP;

    static {
        DETONATORS_DEFAULT = new ArrayList<>();
        DETONATORS_DEFAULT.add(Material.FLINT_AND_STEEL.getKey().toString());
        DETONATORS_DEFAULT.add(Material.DIAMOND_PICKAXE.getKey().toString());
        DETONATORS_DEFAULT.add(Material.GOLDEN_PICKAXE.getKey().toString());
        DETONATORS_DEFAULT.add(Material.IRON_PICKAXE.getKey().toString());
        DETONATORS_DEFAULT.add(Material.WOODEN_PICKAXE.getKey().toString());

        DAMAGE_DECREASE_RANK_MAP = new HashMap<>();
        DAMAGE_DECREASE_RANK_MAP.put(1, 5.0);
        DAMAGE_DECREASE_RANK_MAP.put(2, 10.0);
        DAMAGE_DECREASE_RANK_MAP.put(3, 15.0);
        DAMAGE_DECREASE_RANK_MAP.put(4, 25.0);
        DAMAGE_DECREASE_RANK_MAP.put(5, 35.0);
        DAMAGE_DECREASE_RANK_MAP.put(6, 50.0);
        DAMAGE_DECREASE_RANK_MAP.put(7, 75.0);
        DAMAGE_DECREASE_RANK_MAP.put(8, 100.0);

        OREBONUS_RANK_MAP = new HashMap<>();
        OREBONUS_RANK_MAP.put(1, 35.0);
        OREBONUS_RANK_MAP.put(2, 40.0);
        OREBONUS_RANK_MAP.put(3, 45.0);
        OREBONUS_RANK_MAP.put(4, 50.0);
        OREBONUS_RANK_MAP.put(5, 55.0);
        OREBONUS_RANK_MAP.put(6, 60.0);
        OREBONUS_RANK_MAP.put(7, 65.0);
        OREBONUS_RANK_MAP.put(8, 70.0);

        DEBRIS_REDUCTION_MAP = new HashMap<>();
        DEBRIS_REDUCTION_MAP.put(1, 5.0);
        DEBRIS_REDUCTION_MAP.put(2, 10.0);
        DEBRIS_REDUCTION_MAP.put(3, 15.0);
        DEBRIS_REDUCTION_MAP.put(4, 20.0);
        DEBRIS_REDUCTION_MAP.put(5, 25.0);
        DEBRIS_REDUCTION_MAP.put(6, 30.0);
        DEBRIS_REDUCTION_MAP.put(7, 35.0);
        DEBRIS_REDUCTION_MAP.put(8, 40.0);

        DROP_MULTIPLIER_MAP = new HashMap<>();
        DROP_MULTIPLIER_MAP.put(1, 1);
        DROP_MULTIPLIER_MAP.put(2, 1);
        DROP_MULTIPLIER_MAP.put(3, 1);
        DROP_MULTIPLIER_MAP.put(4, 1);
        DROP_MULTIPLIER_MAP.put(5, 2);
        DROP_MULTIPLIER_MAP.put(6, 2);
        DROP_MULTIPLIER_MAP.put(7, 3);
        DROP_MULTIPLIER_MAP.put(8, 3);

        RADIUS_MAP = new HashMap<>();
        RADIUS_MAP.put(1, 1.0);
        RADIUS_MAP.put(2, 1.0);
        RADIUS_MAP.put(3, 2.0);
        RADIUS_MAP.put(4, 2.0);
        RADIUS_MAP.put(5, 3.0);
        RADIUS_MAP.put(6, 3.0);
        RADIUS_MAP.put(7, 4.0);
        RADIUS_MAP.put(8, 4.0);
    }

    @Setting(value = "Detonators", comment = "Items that can be used to activate Blast-Mining")
    private ArrayList<String> detonators = DETONATORS_DEFAULT;

    @Setting(value = "Damage-Decrease-Per-Rank")
    private HashMap<Integer, Double> damageDecreaseMap = DAMAGE_DECREASE_RANK_MAP;

    @Setting(value = "Ore-Bonus-Per-Rank")
    private HashMap<Integer, Double> orebonusMap = OREBONUS_RANK_MAP;

    @Setting(value = "Debris-Decrease-Per-Rank")
    private HashMap<Integer, Double> debrisReductionMap = DEBRIS_REDUCTION_MAP;

    @Setting(value = "Radius-Increase-Per-Rank")
    private HashMap<Integer, Double> radiusMap = RADIUS_MAP;

    @Setting(value = "Drop-Multiplier-Per-Rank")
    private HashMap<Integer, Integer> dropMultiplierMap = DROP_MULTIPLIER_MAP;

    public ArrayList<String> getDetonators() {
        return detonators;
    }

    public double getDamageDecrease(int rank) {
        return damageDecreaseMap.get(rank);
    }

    public double getOreBonus(int rank) {
        return orebonusMap.get(rank);
    }

    public double getDebrisReduction(int rank) {
        return debrisReductionMap.get(rank);
    }

    public double getRadius(int rank) {
        return radiusMap.get(rank);
    }

    public int getDropMultiplier(int rank) {
        return dropMultiplierMap.get(rank);
    }

    public HashMap<Integer, Double> getDamageDecreaseMap() {
        return damageDecreaseMap;
    }

    public HashMap<Integer, Double> getOrebonusMap() {
        return orebonusMap;
    }

    public HashMap<Integer, Double> getDebrisReductionMap() {
        return debrisReductionMap;
    }

    public HashMap<Integer, Double> getRadiusMap() {
        return radiusMap;
    }

    public HashMap<Integer, Integer> getDropMultiplierMap() {
        return dropMultiplierMap;
    }
}