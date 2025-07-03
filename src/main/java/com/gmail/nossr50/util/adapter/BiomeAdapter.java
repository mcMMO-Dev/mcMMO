package com.gmail.nossr50.util.adapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;

public class BiomeAdapter {
    public static final Set<Biome> ICE_BIOMES;

    static final List<String> knownColdBiomes = Arrays.asList("COLD_OCEAN", "DEEP_COLD_OCEAN",
            "ICE_SPIKES",
            "FROZEN_PEAKS", "FROZEN_OCEAN", "FROZEN_RIVER", "DEEP_FROZEN_OCEAN", "SNOWY_TAIGA",
            "OLD_GROWTH_PINE_TAIGA", "OLD_GROWTH_SPRUCE_TAIGA", "TAIGA", "SNOWY_SLOPES",
            "SNOWY_BEACH");

    static {
        final Set<Biome> iceBiomes = new HashSet<>();
        knownColdBiomes.stream()
                .map(biomeFromString())
                .filter(Objects::nonNull)
                .forEach(iceBiomes::add);
        ICE_BIOMES = Collections.unmodifiableSet(iceBiomes);
    }

    private static @NotNull Function<String, Biome> biomeFromString() {
        return potentialBiome -> {
            try {
                Class<?> biomeClass = Class.forName("org.bukkit.block.Biome");
                Method methodValueOf = biomeClass.getMethod("valueOf", String.class);
                return methodValueOf.invoke(null, potentialBiome) == null
                        ? null
                        : (Biome) methodValueOf.invoke(null, potentialBiome);
            } catch (IllegalArgumentException | NullPointerException e) {
                return null;
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException
                     | IllegalAccessException e) {
                // Thrown when the method is not found or the class is not found
                throw new RuntimeException(e);
            }
        };
    }
}
