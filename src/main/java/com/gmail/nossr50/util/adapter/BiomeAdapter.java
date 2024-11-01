package com.gmail.nossr50.util.adapter;

import org.bukkit.block.Biome;

import java.util.*;

public class BiomeAdapter {
    public static final Set<Biome> WATER_BIOMES;
    public static final Set<Biome> ICE_BIOMES;

    static {
        final List<Biome> allBiomes = getAllBiomes();
        final Set<Biome> waterBiomes = new HashSet<>();
        final Set<Biome> iceBiomes = new HashSet<>();
        for (Biome biome : allBiomes) {
            String biomeName = getBiomeName(biome);
            if (isWater(biomeName) && !isCold(biomeName)) {
                waterBiomes.add(biome);
            } else if (isCold(biomeName)) {
                iceBiomes.add(biome);
            }
        }
        WATER_BIOMES = Collections.unmodifiableSet(waterBiomes);
        ICE_BIOMES = Collections.unmodifiableSet(iceBiomes);
    }

    @SuppressWarnings("deprecation")
    private static List<Biome> getAllBiomes() {
        return Arrays.asList(Biome.values());
    }

    @SuppressWarnings("deprecation")
    private static String getBiomeName(Biome biome) {
        return biome.name();
    }

    private static boolean isWater(String name) {
        return name.contains("RIVER") || name.contains("OCEAN");
    }

    private static boolean isCold(String name) {
        return (name.contains("COLD") || name.contains("ICE")
                || name.contains("FROZEN") || name.contains("TAIGA")) && !name.contains("WARM");
    }
}
