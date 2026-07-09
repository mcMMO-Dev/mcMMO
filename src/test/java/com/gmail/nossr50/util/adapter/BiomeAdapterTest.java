package com.gmail.nossr50.util.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.bukkit.block.Biome;
import org.junit.jupiter.api.Test;

class BiomeAdapterTest {

    /**
     * Regression coverage for biome resolution: an unknown biome name used to escape as a
     * RuntimeException, which would abort BiomeAdapter's class initialization and break mcMMO
     * entirely if a known cold biome is ever renamed or removed in a Minecraft update.
     */
    @Test
    void unknownBiomeNamesShouldResolveToNullInsteadOfCrashing() {
        // Given - a biome name that doesn't exist in this Minecraft version
        // When - the name is resolved
        // Then - the result is null and no exception escapes
        assertThatCode(() -> assertThat(
                BiomeAdapter.biomeFromString().apply("NOT_A_REAL_BIOME")).isNull())
                .doesNotThrowAnyException();
    }

    /** Guard: the resolvable cold biomes must still be collected at class load. */
    @Test
    void knownColdBiomesShouldBeCollected() {
        // Given / When - the ICE_BIOMES set built when the class loads
        // Then - it contains a well-known cold biome
        assertThat(BiomeAdapter.ICE_BIOMES).contains(Biome.TAIGA);
    }
}
