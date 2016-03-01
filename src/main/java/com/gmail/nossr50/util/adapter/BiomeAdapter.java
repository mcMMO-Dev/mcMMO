package com.gmail.nossr50.util.adapter;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.bukkit.block.Biome;

import com.gmail.nossr50.mcMMO;

public class BiomeAdapter {
    public static final Set<Biome> WATER_BIOMES;
    public static final Set<Biome> ICE_BIOMES;
    
    static {
        List<Biome> temp = new ArrayList<Biome>();
        EnumSet<Biome> set = null;
        try {
            temp.add(Biome.valueOf("RIVER"));
            temp.add(Biome.valueOf("OCEAN"));
            temp.add(Biome.valueOf("DEEP_OCEAN"));
        } catch (Exception e) {
            temp.clear();
        } finally {
            try {
                set = EnumSet.copyOf(temp);
            } catch (IllegalArgumentException e) {
                mcMMO.p.getLogger().severe("Biome enum mismatch");;
            }
            temp.clear();
        }
        WATER_BIOMES = set;
        set = null;
        try {
            temp.add(Biome.valueOf("FROZEN_OCEAN"));
            temp.add(Biome.valueOf("FROZEN_RIVER"));
            temp.add(Biome.valueOf("TAIGA"));
            temp.add(Biome.valueOf("TAIGA_HILLS"));
            temp.add(Biome.valueOf("TAIGA_COLD_HILLS"));
            temp.add(Biome.valueOf("TAIGA_COLD"));
            temp.add(Biome.valueOf("MUTATED_TAIGA_COLD"));
            temp.add(Biome.valueOf("ICE_MOUNTAINS"));
            temp.add(Biome.valueOf("ICE_FLATS"));
            temp.add(Biome.valueOf("MUTATED_ICE_FLATS"));
        } catch (Exception e) {
            temp.clear();
            try {
                temp.add(Biome.valueOf("FROZEN_OCEAN"));
                temp.add(Biome.valueOf("FROZEN_RIVER"));
                temp.add(Biome.valueOf("TAIGA"));
                temp.add(Biome.valueOf("TAIGA_HILLS"));
                temp.add(Biome.valueOf("TAIGA_MOUNTAINS"));
                temp.add(Biome.valueOf("COLD_TAIGA"));
                temp.add(Biome.valueOf("COLD_TAIGA_HILLS"));
                temp.add(Biome.valueOf("COLD_TAIGA_MOUNTAINS"));
                temp.add(Biome.valueOf("ICE_MOUNTAINS"));
                temp.add(Biome.valueOf("ICE_PLAINS"));
                temp.add(Biome.valueOf("ICE_PLAINS_SPIKES"));
            } catch (Exception e1) {
                temp.clear();
            }
        } finally {
            try {
                set = EnumSet.copyOf(temp);
            } catch (IllegalArgumentException e) {
                mcMMO.p.getLogger().severe("Biome enum mismatch");;
            }
            temp.clear();
        }
        ICE_BIOMES = set;
    }
}
