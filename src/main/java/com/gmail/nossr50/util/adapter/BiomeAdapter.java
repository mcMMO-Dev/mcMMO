package com.gmail.nossr50.util.adapter;

import org.bukkit.block.Biome;

import java.util.*;

public class BiomeAdapter {
    public static final Set<Biome> WATER_BIOMES;
    public static final Set<Biome> ICE_BIOMES;
    
    static {
        List<Biome> allBiomes = Arrays.asList(Biome.values());
        List<Biome> waterBiomes = new ArrayList<Biome>();
        List<Biome> iceBiomes = new ArrayList<Biome>();
        for (Biome biome : allBiomes) {
            if (isWater(biome.name()) && !isCold(biome.name())) {
                waterBiomes.add(biome);
            } else if (isCold(biome.name())) {
                iceBiomes.add(biome);
            }
        }
        WATER_BIOMES = EnumSet.copyOf(waterBiomes);
        ICE_BIOMES = EnumSet.copyOf(iceBiomes);
    }

    private static boolean isWater(String name) {
        return name.contains("RIVER") || name.contains("OCEAN");
    }
    private static boolean isCold(String name) {
        return (name.contains("COLD") || name.contains("ICE") || name.contains("FROZEN") || name.contains("TAIGA")) && !(name.contains("WARM"));
    }
}
