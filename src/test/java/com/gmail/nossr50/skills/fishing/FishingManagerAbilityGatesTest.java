package com.gmail.nossr50.skills.fishing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.FakeBlockBreakEventType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.RankUtils;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Covers the eligibility gates for the Fishing subskills: Shake, Master Angler, Magic Hunter,
 * and Ice Fishing. Each gate mixes rank unlocks, permissions, and world state, and a gate that
 * wrongly passes hands out ability effects the player has not earned.
 */
class FishingManagerAbilityGatesTest extends MMOTestEnvironment {
    private static final Logger LOGGER =
            Logger.getLogger(FishingManagerAbilityGatesTest.class.getName());

    private FishingManager fishingManager;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(LOGGER);

        fishingManager = new FishingManager(mmoPlayer);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @Nested
    class ShakeGate {
        @Test
        void allowsLivingTargetsWithRankAndPermission() {
            // Given - Shake is unlocked and permitted
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.FISHING_SHAKE))
                    .thenReturn(true);

            // When/Then - a living target can be shaken
            assertThat(fishingManager.canShake(mock(LivingEntity.class))).isTrue();
        }

        @Test
        void rejectsTargetsThatAreNotLiving() {
            // Given - Shake is unlocked and permitted
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.FISHING_SHAKE))
                    .thenReturn(true);

            // When/Then - a hooked dropped item cannot be shaken
            assertThat(fishingManager.canShake(mock(Item.class))).isFalse();
        }

        @Test
        void rejectsPlayersWhoHaveNotUnlockedShake() {
            // Given - Shake has not been unlocked
            // When/Then - the gate stays closed
            assertThat(fishingManager.canShake(mock(LivingEntity.class))).isFalse();
        }

        @Test
        void rejectsPlayersWithoutTheShakePermission() {
            // Given - Shake is unlocked but not permitted
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.FISHING_SHAKE))
                    .thenReturn(true);
            when(Permissions.isSubSkillEnabled(player, SubSkillType.FISHING_SHAKE))
                    .thenReturn(false);

            // When/Then - the gate stays closed
            assertThat(fishingManager.canShake(mock(LivingEntity.class))).isFalse();
        }
    }

    @Nested
    class MasterAnglerGate {
        @Test
        void unlocksAtTheConfiguredLevel() {
            // Given - the player's skill level meets the unlock level
            when(RankUtils.getUnlockLevel(SubSkillType.FISHING_MASTER_ANGLER)).thenReturn(0);

            // When/Then - master angler is available
            assertThat(fishingManager.canMasterAngler()).isTrue();
        }

        @Test
        void staysLockedBelowTheUnlockLevel() {
            // Given - the unlock level is above the player's skill level
            when(RankUtils.getUnlockLevel(SubSkillType.FISHING_MASTER_ANGLER)).thenReturn(5);

            // When/Then - master angler stays locked
            assertThat(fishingManager.canMasterAngler()).isFalse();
        }

        @Test
        void staysLockedWithoutThePermission() {
            // Given - the level requirement is met but the permission is missing
            when(RankUtils.getUnlockLevel(SubSkillType.FISHING_MASTER_ANGLER)).thenReturn(0);
            when(Permissions.isSubSkillEnabled(player, SubSkillType.FISHING_MASTER_ANGLER))
                    .thenReturn(false);

            // When/Then - master angler stays locked
            assertThat(fishingManager.canMasterAngler()).isFalse();
        }
    }

    @Nested
    class MagicHunterGate {
        @ParameterizedTest
        @CsvSource({
                "true, true, true, true, true",
                "false, true, true, true, false",
                "true, false, true, true, false",
                "true, true, false, true, false",
                "true, true, true, false, false",
        })
        void requiresBothSubskillsUnlockedAndPermitted(boolean magicHunterUnlocked,
                boolean treasureHunterUnlocked, boolean magicHunterPermitted,
                boolean treasureHunterPermitted, boolean expected) {
            // Given - Magic Hunter piggybacks on Treasure Hunter, so both subskills must be
            // unlocked and permitted before enchanted finds can roll
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.FISHING_MAGIC_HUNTER))
                    .thenReturn(magicHunterUnlocked);
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.FISHING_TREASURE_HUNTER))
                    .thenReturn(treasureHunterUnlocked);
            when(Permissions.isSubSkillEnabled(player, SubSkillType.FISHING_MAGIC_HUNTER))
                    .thenReturn(magicHunterPermitted);
            when(Permissions.isSubSkillEnabled(player, SubSkillType.FISHING_TREASURE_HUNTER))
                    .thenReturn(treasureHunterPermitted);

            // When/Then - the gate only opens with all four requirements met
            assertThat(fishingManager.isMagicHunterEnabled()).isEqualTo(expected);
        }
    }

    @Nested
    class IceFishingGate {
        private Block iceBlock;
        private Block blockBelow;

        @BeforeEach
        void setUpIceOverWater() {
            iceBlock = mock(Block.class);
            when(iceBlock.getType()).thenReturn(Material.ICE);

            // Not an ice biome, so eligibility depends on the water three blocks below
            blockBelow = mock(Block.class);
            when(blockBelow.getType()).thenReturn(Material.WATER);
            when(iceBlock.getRelative(BlockFace.DOWN, 3)).thenReturn(blockBelow);

            mockedEventUtils = mockStatic(EventUtils.class);
            when(EventUtils.simulateBlockBreak(any(Block.class), any(Player.class),
                    any(FakeBlockBreakEventType.class))).thenReturn(true);
        }

        @Test
        void allowsIceFishingOnIceAboveWater() {
            // Given - unlocked ice fishing over a frozen body of water
            // When/Then - the gate opens
            assertThat(fishingManager.canIceFish(iceBlock)).isTrue();
        }

        @Test
        void staysLockedBelowTheUnlockLevel() {
            // Given - the unlock level is above the player's skill level
            when(RankUtils.getUnlockLevel(SubSkillType.FISHING_ICE_FISHING)).thenReturn(5);

            // When/Then - the gate stays closed
            assertThat(fishingManager.canIceFish(iceBlock)).isFalse();
        }

        @Test
        void rejectsBlocksThatAreNotIce() {
            // Given - the hook landed on stone
            when(iceBlock.getType()).thenReturn(Material.STONE);

            // When/Then - the gate stays closed
            assertThat(fishingManager.canIceFish(iceBlock)).isFalse();
        }

        @Test
        void rejectsLoneIceBlocksWithoutWaterBelow() {
            // Given - a decorative ice block with no water underneath outside an ice biome
            when(blockBelow.getType()).thenReturn(Material.STONE);

            // When/Then - the gate stays closed
            assertThat(fishingManager.canIceFish(iceBlock)).isFalse();
        }

        @Test
        void rejectsPlayersWithoutTheIceFishingPermission() {
            // Given - the permission is missing
            when(Permissions.isSubSkillEnabled(player, SubSkillType.FISHING_ICE_FISHING))
                    .thenReturn(false);

            // When/Then - the gate stays closed
            assertThat(fishingManager.canIceFish(iceBlock)).isFalse();
        }

        @Test
        void rejectsIceProtectedFromFakeBlockBreaks() {
            // Given - a protection plugin cancels the simulated block break
            when(EventUtils.simulateBlockBreak(any(Block.class), any(Player.class),
                    any(FakeBlockBreakEventType.class))).thenReturn(false);

            // When/Then - the gate stays closed
            assertThat(fishingManager.canIceFish(iceBlock)).isFalse();
        }
    }
}
