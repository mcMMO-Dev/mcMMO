package com.gmail.nossr50.listeners;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

/**
 * Contract coverage for the bonus-drop trust analysis. Tile entities drop their contents when
 * broken, so drop lists that look like container spills must not be doubled.
 */
class BonusDropsTest {

    @Test
    void singleOreDropShouldBeRewardableWithoutRestrictions() {
        // Given - a coal ore dropping a single coal item
        // When - the drops are analyzed
        final BonusDrops.Analysis analysis = BonusDrops.analyze(Material.COAL_ORE,
                List.of(Material.COAL));

        // Then - bonus drops apply with no restriction
        assertThat(analysis.rewardable()).isTrue();
        assertThat(analysis.onlyRewardBlocks()).isFalse();
    }

    @Test
    void beetrootsDroppingCropAndSeedsShouldNotTriggerTileEntitySuspicion() {
        // Given - beetroots legitimately dropping beetroot plus seeds (two distinct materials)
        // When - the drops are analyzed
        final BonusDrops.Analysis analysis = BonusDrops.analyze(Material.BEETROOTS,
                List.of(Material.BEETROOT, Material.BEETROOT_SEEDS));

        // Then - the raised tolerance keeps the drop fully rewardable
        assertThat(analysis.rewardable()).isTrue();
        assertThat(analysis.onlyRewardBlocks()).isFalse();
    }

    @Test
    void mixedMaterialsShouldRestrictBonusDropsToBlocks() {
        // Given - a break dropping several distinct materials, like a container spilling contents
        // When - the drops are analyzed
        final BonusDrops.Analysis analysis = BonusDrops.analyze(Material.STONE,
                List.of(Material.STONE, Material.DIAMOND, Material.BREAD));

        // Then - only block items may receive bonus drops
        assertThat(analysis.rewardable()).isTrue();
        assertThat(analysis.onlyRewardBlocks()).isTrue();
    }

    @Test
    void multipleBlockDropsShouldNotBeRewardableAtAll() {
        // Given - two block items dropping from one break (untrustworthy drop list)
        // When - the drops are analyzed
        final BonusDrops.Analysis analysis = BonusDrops.analyze(Material.STONE,
                List.of(Material.STONE, Material.CHEST));

        // Then - bonus drops are rejected entirely
        assertThat(analysis.rewardable()).isFalse();
    }

    @Test
    void emptyDropListShouldRemainRewardable() {
        // Given - a break dropping nothing (e.g. silk-touch edge cases)
        // When - the drops are analyzed
        final BonusDrops.Analysis analysis = BonusDrops.analyze(Material.STONE, List.of());

        // Then - nothing restricts the (empty) reward loop
        assertThat(analysis.rewardable()).isTrue();
        assertThat(analysis.onlyRewardBlocks()).isFalse();
    }
}
