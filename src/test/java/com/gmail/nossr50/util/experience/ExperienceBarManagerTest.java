package com.gmail.nossr50.util.experience;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.experience.ExperienceBarManager.XPBarSettingTarget;
import com.gmail.nossr50.util.player.NotificationManager;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Covers the chat feedback sent by XP bar setting changes. '/mmoxpbar disable' toggles every
 * skill at once, so it must send a single summary line rather than one 'setting changed' line
 * per skill, while single-skill toggles keep their per-skill confirmation.
 */
class ExperienceBarManagerTest extends MMOTestEnvironment {
    private static final Logger logger = Logger.getLogger(
            ExperienceBarManagerTest.class.getName());

    private ExperienceBarManager experienceBarManager;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(logger);
        experienceBarManager = new ExperienceBarManager(mmoPlayer);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @Test
    void disableAllBarsShouldSendOnlyTheSummaryMessage() {
        // Given - a player with default XP bar settings

        // When - the player disables all XP bars at once (/mmoxpbar disable)
        experienceBarManager.disableAllBars();

        // Then - a single summary message is sent instead of one line per skill
        notificationManager.verify(() -> NotificationManager.sendPlayerInformationChatOnlyPrefixed(
                player, "Commands.XPBar.DisableAll"), times(1));
        notificationManager.verify(() -> NotificationManager.sendPlayerInformationChatOnlyPrefixed(
                eq(player), eq("Commands.XPBar.SettingChanged"), any(String[].class)),
                never());
    }

    @Test
    void xpBarSettingToggleShouldStillConfirmSingleSkillChanges() {
        // Given - a player with default XP bar settings

        // When - the player hides a single skill's XP bar (/mmoxpbar hide mining)
        experienceBarManager.xpBarSettingToggle(XPBarSettingTarget.HIDE, PrimarySkillType.MINING);

        // Then - the per-skill confirmation is still sent
        notificationManager.verify(() -> NotificationManager.sendPlayerInformationChatOnlyPrefixed(
                eq(player), eq("Commands.XPBar.SettingChanged"), any(String[].class)),
                times(1));
    }
}
