package com.gmail.nossr50.datatypes.player;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.experience.FormulaManager;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/**
 * Covers the public XP and level adding methods on {@link McMMOPlayer}. Other plugins award
 * progression through these, so they must behave like any normal gain: XP that crosses a
 * threshold levels the player up, and added levels fire the level change events. Raw writes
 * that leave a skill sitting over its XP threshold must not be reachable here.
 */
class McMMOPlayerAddXpAndLevelsTest extends MMOTestEnvironment {
    private static final Logger logger = Logger.getLogger(
            McMMOPlayerAddXpAndLevelsTest.class.getName());
    private static final int XP_TO_NEXT_LEVEL = 10;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(logger);
        stubLevelUpEnvironment();
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    /**
     * Stubs everything the level-up path needs so gains translate into level-ups: skill
     * permissions, disabled level caps, and a flat XP curve.
     */
    private void stubLevelUpEnvironment() {
        when(Permissions.skillEnabled(any(Player.class), any(PrimarySkillType.class)))
                .thenReturn(true);
        when(generalConfig.getPowerLevelCap()).thenReturn(Integer.MAX_VALUE);
        when(generalConfig.getLevelCap(any(PrimarySkillType.class))).thenReturn(Integer.MAX_VALUE);
        when(ExperienceConfig.getInstance().getFormulaType()).thenReturn(FormulaType.LINEAR);

        final FormulaManager formulaManager = mock(FormulaManager.class);
        when(formulaManager.getXPtoNextLevel(anyInt(), any(FormulaType.class)))
                .thenReturn(XP_TO_NEXT_LEVEL);
        when(mcMMO.getFormulaManager()).thenReturn(formulaManager);
    }

    @Test
    void addXpShouldAwardALevelUpWhenTheGainCrossesTheThreshold() {
        // Given - a level 0 player on a flat 10-XP-per-level curve

        // When - another plugin adds 15 XP to Mining
        mmoPlayer.addXp(PrimarySkillType.MINING, XP_TO_NEXT_LEVEL + 5F);

        // Then - the player levels up and keeps the remainder instead of sitting at 15/10 XP
        assertThat(mmoPlayer.getSkillLevel(PrimarySkillType.MINING)).isEqualTo(1);
        assertThat(mmoPlayer.getProfile().getSkillXpLevelRaw(PrimarySkillType.MINING))
                .isEqualTo(5F);
    }

    @Test
    void addXpShouldAccrueTheExactAmountWhenBelowTheThreshold() {
        // Given - a level 0 player on a flat 10-XP-per-level curve

        // When - another plugin adds less XP than the next level needs
        mmoPlayer.addXp(PrimarySkillType.MINING, XP_TO_NEXT_LEVEL - 5F);

        // Then - the XP accrues unmodified by rates or perks and no level is awarded
        assertThat(mmoPlayer.getSkillLevel(PrimarySkillType.MINING)).isZero();
        assertThat(mmoPlayer.getProfile().getSkillXpLevelRaw(PrimarySkillType.MINING))
                .isEqualTo(5F);
    }

    @Test
    void addLevelsShouldFireTheLevelUpEventForTheAddedLevels() {
        // Given - a level 0 player

        // When - another plugin adds 3 Mining levels
        mmoPlayer.addLevels(PrimarySkillType.MINING, 3);

        // Then - the levels are applied and the level up event announces the change
        assertThat(mmoPlayer.getSkillLevel(PrimarySkillType.MINING)).isEqualTo(3);
        final ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(pluginManager, atLeastOnce()).callEvent(eventCaptor.capture());
        assertThat(eventCaptor.getAllValues())
                .filteredOn(McMMOPlayerLevelUpEvent.class::isInstance)
                .map(McMMOPlayerLevelUpEvent.class::cast)
                .singleElement()
                .satisfies(levelUpEvent -> {
                    assertThat(levelUpEvent.getSkill()).isEqualTo(PrimarySkillType.MINING);
                    assertThat(levelUpEvent.getLevelsGained()).isEqualTo(3);
                });
    }
}
