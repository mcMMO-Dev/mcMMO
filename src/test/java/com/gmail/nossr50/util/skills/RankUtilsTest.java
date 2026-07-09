package com.gmail.nossr50.util.skills;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.config.GeneralConfig;
import com.gmail.nossr50.config.RankConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.platform.MinecraftGameVersion;
import java.io.File;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;

/**
 * Pins the observable getRank behavior (rank table, rankless skills, unloaded players) so the
 * rank lookup internals can be reworked without changing results.
 */
class RankUtilsTest {
    private static final Logger logger = Logger.getLogger(RankUtilsTest.class.getName());

    private MockedStatic<mcMMO> mcMMOMock;
    private MockedStatic<RankConfig> rankConfigMock;
    private MockedStatic<com.gmail.nossr50.util.player.UserManager> userManagerMock;
    private Player player;
    private McMMOPlayer mmoPlayer;

    @BeforeEach
    void setUp() {
        mcMMOMock = mockStatic(mcMMO.class);
        mcMMO.p = mock(mcMMO.class);
        when(mcMMO.p.getLogger()).thenReturn(logger);
        mcMMOMock.when(mcMMO::getLocalesDirectory)
                .thenReturn(System.getProperty("java.io.tmpdir") + File.separator);
        // SkillTools checks the game version and locale while building its skill tables
        final MinecraftGameVersion gameVersion = mock(MinecraftGameVersion.class);
        when(gameVersion.isAtLeast(anyInt(), anyInt(), anyInt())).thenReturn(true);
        mcMMOMock.when(mcMMO::getMinecraftGameVersion).thenReturn(gameVersion);
        final GeneralConfig generalConfig = mock(GeneralConfig.class);
        when(generalConfig.getLocale()).thenReturn("en_US");
        when(mcMMO.p.getGeneralConfig()).thenReturn(generalConfig);
        // getParentSkill resolves through the real SkillTools tables
        final SkillTools skillTools = new SkillTools(mcMMO.p);
        when(mcMMO.p.getSkillTools()).thenReturn(skillTools);

        // Rupture has four ranks; unlock levels 5, 10, 20, 50
        final RankConfig rankConfig = mock(RankConfig.class);
        rankConfigMock = mockStatic(RankConfig.class);
        rankConfigMock.when(RankConfig::getInstance).thenReturn(rankConfig);
        when(rankConfig.getSubSkillUnlockLevel(SubSkillType.SWORDS_RUPTURE, 1)).thenReturn(5);
        when(rankConfig.getSubSkillUnlockLevel(SubSkillType.SWORDS_RUPTURE, 2)).thenReturn(10);
        when(rankConfig.getSubSkillUnlockLevel(SubSkillType.SWORDS_RUPTURE, 3)).thenReturn(20);
        when(rankConfig.getSubSkillUnlockLevel(SubSkillType.SWORDS_RUPTURE, 4)).thenReturn(50);

        player = mock(Player.class);
        mmoPlayer = mock(McMMOPlayer.class);
        userManagerMock = mockStatic(com.gmail.nossr50.util.player.UserManager.class);
        userManagerMock.when(
                () -> com.gmail.nossr50.util.player.UserManager.getPlayer(player))
                .thenReturn(mmoPlayer);
    }

    @AfterEach
    void tearDown() {
        if (userManagerMock != null) {
            userManagerMock.close();
        }
        if (rankConfigMock != null) {
            rankConfigMock.close();
        }
        if (mcMMOMock != null) {
            mcMMOMock.close();
        }
    }

    private static Stream<Arguments> ruptureRankTable() {
        return Stream.of(
                // skillLevel, expectedRank (unlock levels: 5, 10, 20, 50)
                Arguments.of(0, 0),
                Arguments.of(4, 0),
                Arguments.of(5, 1),
                Arguments.of(9, 1),
                Arguments.of(10, 2),
                Arguments.of(19, 2),
                Arguments.of(20, 3),
                Arguments.of(49, 3),
                Arguments.of(50, 4),
                Arguments.of(1000, 4)
        );
    }

    @ParameterizedTest
    @MethodSource("ruptureRankTable")
    void getRankShouldMatchUnlockLevelTable(int skillLevel, int expectedRank) {
        // Given - a player with the given Swords level
        when(mmoPlayer.getSkillLevel(PrimarySkillType.SWORDS)).thenReturn(skillLevel);

        // When / Then - the rank matches the unlock level table
        assertThat(RankUtils.getRank(player, SubSkillType.SWORDS_RUPTURE))
                .isEqualTo(expectedRank);
    }

    @Test
    void getRankShouldReturnMinusOneForRanklessSkills() {
        // Given / When / Then - skills without ranks report -1
        assertThat(RankUtils.getRank(player, SubSkillType.ARCHERY_DAZE)).isEqualTo(-1);
    }

    @Test
    void getRankShouldReturnZeroWhenPlayerDataIsNotLoaded() {
        // Given - a player whose mcMMO data is not loaded
        final Player unloadedPlayer = mock(Player.class);

        // When / Then - the rank defaults to zero
        assertThat(RankUtils.getRank(unloadedPlayer, SubSkillType.SWORDS_RUPTURE)).isZero();
    }

    @Test
    void hasUnlockedSubskillShouldBeTrueForRanklessSkills() {
        // Given / When / Then - rankless skills are always considered unlocked
        assertThat(RankUtils.hasUnlockedSubskill(player, SubSkillType.ARCHERY_DAZE)).isTrue();
    }

    @Test
    void hasReachedRankShouldCompareAgainstTheCurrentRank() {
        // Given - a Swords level that puts the player at rank 3
        when(mmoPlayer.getSkillLevel(PrimarySkillType.SWORDS)).thenReturn(20);

        // When / Then - reached ranks up to 3, not 4
        assertThat(RankUtils.hasReachedRank(3, player, SubSkillType.SWORDS_RUPTURE)).isTrue();
        assertThat(RankUtils.hasReachedRank(4, player, SubSkillType.SWORDS_RUPTURE)).isFalse();
    }

    @Test
    void isPlayerMaxRankInSubSkillShouldBeTrueAtTheHighestRank() {
        // Given - a Swords level at the final unlock level
        when(mmoPlayer.getSkillLevel(PrimarySkillType.SWORDS)).thenReturn(50);

        // When / Then - the player is max rank
        assertThat(RankUtils.isPlayerMaxRankInSubSkill(player, SubSkillType.SWORDS_RUPTURE))
                .isTrue();
    }
}
