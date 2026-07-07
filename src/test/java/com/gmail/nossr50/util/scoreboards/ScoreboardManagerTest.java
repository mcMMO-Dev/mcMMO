package com.gmail.nossr50.util.scoreboards;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.config.GeneralConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.platform.MinecraftGameVersion;
import com.gmail.nossr50.util.skills.SkillTools;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Verifies the sidebar refresh decisions made by {@link ScoreboardManager#handleXp} and
 * {@link ScoreboardManager#handleLevelUp}.
 *
 * <p>Child skills (Salvage, Smelting) never gain XP or levels themselves - their XP is split
 * between parent skills and their displayed level is derived from parent levels. A sidebar
 * showing a child skill therefore only stays current if parent-skill gains also refresh it
 * (GitHub issue #5184: a kept /salvage board froze while the player fished and repaired).
 */
@TestInstance(Lifecycle.PER_CLASS)
class ScoreboardManagerTest {

    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final String PLAYER_NAME = "Herpington";

    private static MockedStatic<mcMMO> mockedMcMMO;
    private static MockedStatic<LocaleLoader> mockedLocaleLoader;

    @BeforeAll
    void setUpAll() {
        mockedMcMMO = Mockito.mockStatic(mcMMO.class);
        mockedLocaleLoader = Mockito.mockStatic(LocaleLoader.class);

        mcMMO.p = mock(mcMMO.class);
        when(mcMMO.p.getLogger()).thenReturn(logger);

        final GeneralConfig generalConfig = mock(GeneralConfig.class);
        when(mcMMO.p.getGeneralConfig()).thenReturn(generalConfig);
        when(generalConfig.getLocale()).thenReturn("en_US");
        when(generalConfig.getScoreboardRainbows()).thenReturn(false);
        when(generalConfig.getShowAbilityNames()).thenReturn(false);
        when(generalConfig.getPowerLevelTagsEnabled()).thenReturn(false);
        when(generalConfig.getSkillLevelUpBoard()).thenReturn(false);

        // Echo locale keys back so label building in ScoreboardManager's static init is
        // deterministic without touching real locale files
        mockedLocaleLoader.when(() -> LocaleLoader.getString(anyString()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final MinecraftGameVersion gameVersion = mock(MinecraftGameVersion.class);
        when(gameVersion.isAtLeast(anyInt(), anyInt(), anyInt())).thenReturn(true);
        when(mcMMO.getMinecraftGameVersion()).thenReturn(gameVersion);

        final SkillTools skillTools = new SkillTools(mcMMO.p);
        when(mcMMO.p.getSkillTools()).thenReturn(skillTools);
    }

    @AfterAll
    void tearDownAll() {
        mockedLocaleLoader.close();
        mockedMcMMO.close();
    }

    @AfterEach
    void clearRegisteredBoards() {
        ScoreboardManager.PLAYER_SCOREBOARDS.clear();
    }

    /**
     * Every (child skill, parent skill) pairing defined by {@link SkillTools}.
     */
    private static Stream<Arguments> childBoardParentSkillPairs() {
        return Stream.of(
                Arguments.of(PrimarySkillType.SALVAGE, PrimarySkillType.REPAIR),
                Arguments.of(PrimarySkillType.SALVAGE, PrimarySkillType.FISHING),
                Arguments.of(PrimarySkillType.SMELTING, PrimarySkillType.MINING),
                Arguments.of(PrimarySkillType.SMELTING, PrimarySkillType.REPAIR)
        );
    }

    /**
     * Child boards paired with skills that are NOT parents of that child - including skills
     * that parent the other child skill, the trickiest false-positive case.
     */
    private static Stream<Arguments> childBoardUnrelatedSkillPairs() {
        return Stream.of(
                Arguments.of(PrimarySkillType.SALVAGE, PrimarySkillType.MINING),
                Arguments.of(PrimarySkillType.SALVAGE, PrimarySkillType.EXCAVATION),
                Arguments.of(PrimarySkillType.SMELTING, PrimarySkillType.FISHING),
                Arguments.of(PrimarySkillType.SMELTING, PrimarySkillType.HERBALISM)
        );
    }

    @ParameterizedTest
    @MethodSource("childBoardParentSkillPairs")
    void handleXpShouldRefreshChildSkillBoardWhenParentSkillGainsXp(
            final PrimarySkillType childSkill, final PrimarySkillType parentSkill) {
        // Given - a player keeps a child-skill sidebar (e.g. /salvage then /mcsb keep) visible
        final Player player = mockPlayer();
        final ScoreboardWrapper wrapper = registerShownSkillBoard(childSkill);

        // When - the player earns XP in one of the child skill's parent skills
        ScoreboardManager.handleXp(player, parentSkill);

        // Then - the child-skill sidebar schedules a refresh
        verify(wrapper).doSidebarUpdateSoon();
    }

    @ParameterizedTest
    @MethodSource("childBoardParentSkillPairs")
    void handleLevelUpShouldRefreshChildSkillBoardWhenParentSkillLevelsUp(
            final PrimarySkillType childSkill, final PrimarySkillType parentSkill) {
        // Given - a player keeps a child-skill sidebar visible
        final Player player = mockPlayer();
        final ScoreboardWrapper wrapper = registerShownSkillBoard(childSkill);

        // When - the player levels up one of the child skill's parent skills
        ScoreboardManager.handleLevelUp(player, parentSkill);

        // Then - the child-skill sidebar schedules a refresh (its derived level just changed)
        verify(wrapper).doSidebarUpdateSoon();
    }

    @ParameterizedTest
    @MethodSource("childBoardUnrelatedSkillPairs")
    void handleXpShouldNotRefreshChildSkillBoardWhenUnrelatedSkillGainsXp(
            final PrimarySkillType childSkill, final PrimarySkillType unrelatedSkill) {
        // Given - a player keeps a child-skill sidebar visible
        final Player player = mockPlayer();
        final ScoreboardWrapper wrapper = registerShownSkillBoard(childSkill);

        // When - the player earns XP in a skill that is not a parent of the displayed child
        ScoreboardManager.handleXp(player, unrelatedSkill);

        // Then - no refresh is scheduled, keeping updates cheap
        verify(wrapper, never()).doSidebarUpdateSoon();
    }

    @ParameterizedTest
    @MethodSource("childBoardUnrelatedSkillPairs")
    void handleLevelUpShouldNotRefreshChildSkillBoardWhenUnrelatedSkillLevelsUp(
            final PrimarySkillType childSkill, final PrimarySkillType unrelatedSkill) {
        // Given - a player keeps a child-skill sidebar visible
        final Player player = mockPlayer();
        final ScoreboardWrapper wrapper = registerShownSkillBoard(childSkill);

        // When - the player levels up a skill that is not a parent of the displayed child
        ScoreboardManager.handleLevelUp(player, unrelatedSkill);

        // Then - no refresh is scheduled
        verify(wrapper, never()).doSidebarUpdateSoon();
    }

    @Test
    void handleXpShouldRefreshSkillBoardWhenDisplayedSkillGainsXp() {
        // Given - a player keeps a regular (non-child) skill sidebar visible
        final Player player = mockPlayer();
        final ScoreboardWrapper wrapper = registerShownSkillBoard(PrimarySkillType.EXCAVATION);

        // When - the player earns XP in that same skill
        ScoreboardManager.handleXp(player, PrimarySkillType.EXCAVATION);

        // Then - the sidebar schedules a refresh, as it always has
        verify(wrapper).doSidebarUpdateSoon();
    }

    @Test
    void handleXpShouldNotRefreshChildSkillBoardWhenBoardIsHidden() {
        // Given - a player has a child-skill sidebar that is currently not shown
        final Player player = mockPlayer();
        final ScoreboardWrapper wrapper = registerSkillBoard(PrimarySkillType.SALVAGE, false);

        // When - the player earns XP in a parent skill of the hidden child board
        ScoreboardManager.handleXp(player, PrimarySkillType.FISHING);

        // Then - no refresh is scheduled for the hidden board
        verify(wrapper, never()).doSidebarUpdateSoon();
    }

    private Player mockPlayer() {
        final Player player = mock(Player.class);
        when(player.getName()).thenReturn(PLAYER_NAME);
        return player;
    }

    private ScoreboardWrapper registerShownSkillBoard(final PrimarySkillType boardSkill) {
        return registerSkillBoard(boardSkill, true);
    }

    private ScoreboardWrapper registerSkillBoard(final PrimarySkillType boardSkill,
            final boolean shown) {
        final ScoreboardWrapper wrapper = mock(ScoreboardWrapper.class);
        when(wrapper.isSkillScoreboard()).thenReturn(true);
        when(wrapper.isStatsScoreboard()).thenReturn(false);
        when(wrapper.isBoardShown()).thenReturn(shown);
        wrapper.targetSkill = boardSkill;
        ScoreboardManager.PLAYER_SCOREBOARDS.put(PLAYER_NAME, wrapper);
        return wrapper;
    }
}
