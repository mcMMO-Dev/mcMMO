package com.gmail.nossr50.datatypes.player;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.experience.FormulaManager;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Covers the per-player level-up sound preference on {@link McMMOPlayer}. Players can silence
 * the level-up sound for themselves without touching the server-wide config toggle, and the
 * preference must gate only the sound: level-up notifications still go out either way.
 */
class McMMOPlayerLevelUpSoundTest extends MMOTestEnvironment {
    private static final Logger logger = Logger.getLogger(
            McMMOPlayerLevelUpSoundTest.class.getName());
    private static final int XP_TO_NEXT_LEVEL = 10;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    /**
     * Stubs everything the level-up path in {@link McMMOPlayer} needs so that gained XP
     * translates into a level-up: skill permissions, disabled level caps, a flat XP curve,
     * and server-wide level-up sounds left enabled.
     */
    private void stubLevelUpEnvironment() {
        when(Permissions.skillEnabled(any(Player.class), any(PrimarySkillType.class)))
                .thenReturn(true);
        when(generalConfig.getPowerLevelCap()).thenReturn(Integer.MAX_VALUE);
        when(generalConfig.getLevelCap(any(PrimarySkillType.class))).thenReturn(Integer.MAX_VALUE);
        when(generalConfig.getLevelUpSoundsEnabled()).thenReturn(true);
        when(ExperienceConfig.getInstance().getFormulaType()).thenReturn(FormulaType.LINEAR);

        final FormulaManager formulaManager = mock(FormulaManager.class);
        when(formulaManager.getXPtoNextLevel(anyInt(), any(FormulaType.class)))
                .thenReturn(XP_TO_NEXT_LEVEL);
        when(mcMMO.getFormulaManager()).thenReturn(formulaManager);
    }

    private void gainLevelUpWorthOfXp() {
        mmoPlayer.applyXpGain(PrimarySkillType.MINING, XP_TO_NEXT_LEVEL + 5F, XPGainReason.PVE,
                XPGainSource.SELF);
    }

    @Test
    void useLevelUpSoundsShouldDefaultToTrue() {
        // Given - a freshly created player with no preference changes
        // When - the level-up sound preference is read
        final boolean useLevelUpSounds = mmoPlayer.useLevelUpSounds();

        // Then - the sound is on by default
        assertThat(useLevelUpSounds).isTrue();
    }

    @Test
    void toggleLevelUpSoundsShouldFlipStateEachCall() {
        // Given - a player with the default preference
        // When - the preference is toggled once
        mmoPlayer.toggleLevelUpSounds();

        // Then - the sound is off
        assertThat(mmoPlayer.useLevelUpSounds()).isFalse();

        // When - the preference is toggled a second time
        mmoPlayer.toggleLevelUpSounds();

        // Then - the sound is back on
        assertThat(mmoPlayer.useLevelUpSounds()).isTrue();
    }

    @Test
    void levelUpShouldPlaySoundWhenPreferenceLeftOn() {
        // Given - a player who never toggled the level-up sound off
        stubLevelUpEnvironment();

        // When - the player gains enough XP to level up
        gainLevelUpWorthOfXp();

        // Then - the level-up sound is played for the player
        mockedSoundManager.verify(() -> SoundManager.sendSound(eq(player), any(Location.class),
                eq(SoundType.LEVEL_UP)));
    }

    @Test
    void levelUpShouldNotPlaySoundWhenPlayerToggledSoundOff() {
        // Given - a player who toggled the level-up sound off for themselves
        stubLevelUpEnvironment();
        mmoPlayer.toggleLevelUpSounds();

        // When - the player gains enough XP to level up
        gainLevelUpWorthOfXp();

        // Then - no level-up sound is played for the player
        mockedSoundManager.verify(() -> SoundManager.sendSound(eq(player), any(Location.class),
                eq(SoundType.LEVEL_UP)), never());
    }

    @Test
    void levelUpShouldStillSendNotificationWhenPlayerToggledSoundOff() {
        // Given - a player who toggled the level-up sound off for themselves
        stubLevelUpEnvironment();
        mmoPlayer.toggleLevelUpSounds();

        // When - the player gains enough XP to level up
        gainLevelUpWorthOfXp();

        // Then - the level-up message still goes out, only the sound is muted
        notificationManager.verify(() -> NotificationManager.sendPlayerLevelUpNotification(
                eq(mmoPlayer), eq(PrimarySkillType.MINING), anyInt(), anyInt()));
    }

    @Test
    void levelUpShouldNotPlaySoundWhenSoundsDisabledServerWide() {
        // Given - level-up sounds disabled in the server config despite the player preference
        stubLevelUpEnvironment();
        when(generalConfig.getLevelUpSoundsEnabled()).thenReturn(false);

        // When - the player gains enough XP to level up
        gainLevelUpWorthOfXp();

        // Then - no level-up sound is played for the player
        mockedSoundManager.verify(() -> SoundManager.sendSound(eq(player), any(Location.class),
                eq(SoundType.LEVEL_UP)), never());
    }

    @Test
    void xpGainBelowLevelUpThresholdShouldNotPlaySound() {
        // Given - a player with default preferences gaining too little XP to level up
        stubLevelUpEnvironment();

        // When - the player gains less XP than the next level requires
        mmoPlayer.applyXpGain(PrimarySkillType.MINING, XP_TO_NEXT_LEVEL - 5F, XPGainReason.PVE,
                XPGainSource.SELF);

        // Then - no level-up sound is played
        mockedSoundManager.verify(() -> SoundManager.sendSound(any(Player.class),
                any(Location.class), any(SoundType.class)), never());
    }
}
