package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.sounds.SkillUnlockSoundThrottle;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Covers the unlock sound behavior of batched sub-skill unlock notifications. Mass level
 * changes (for example /mmoedit all) queue dozens of staggered unlock notifications; only
 * the first notification of such a batch may request the unlock sound, or the player hears
 * a long stream of unlock sounds while the batch drains.
 */
class SkillUnlockNotificationTaskTest extends MMOTestEnvironment {
    private static final Logger logger = Logger.getLogger(
            SkillUnlockNotificationTaskTest.class.getName());

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(logger);
        SkillUnlockSoundThrottle.clearAll();
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
        SkillUnlockSoundThrottle.clearAll();
    }

    @Test
    void firstUnlockNotificationShouldRequestTheUnlockSound() {
        // Given - no unlock notification was shown recently

        // When - a single unlock notification task runs
        new SkillUnlockNotificationTask(mmoPlayer, SubSkillType.MINING_MOTHER_LODE, 10).run();

        // Then - the notification requests the unlock sound
        notificationManager.verify(() -> NotificationManager.sendPlayerUnlockNotification(
                mmoPlayer, SubSkillType.MINING_MOTHER_LODE, true));
    }

    @Test
    void unlockNotificationsInQuickSuccessionShouldRequestTheSoundOnlyOnce() {
        // Given - a batch of unlock notifications, like a mass level change queues

        // When - the batch runs back to back
        new SkillUnlockNotificationTask(mmoPlayer, SubSkillType.MINING_MOTHER_LODE, 10).run();
        new SkillUnlockNotificationTask(mmoPlayer, SubSkillType.MINING_SUPER_BREAKER, 5).run();
        new SkillUnlockNotificationTask(mmoPlayer, SubSkillType.MINING_BLAST_MINING, 1).run();

        // Then - only the first notification requested the sound
        notificationManager.verify(() -> NotificationManager.sendPlayerUnlockNotification(
                mmoPlayer, SubSkillType.MINING_MOTHER_LODE, true));
        notificationManager.verify(() -> NotificationManager.sendPlayerUnlockNotification(
                mmoPlayer, SubSkillType.MINING_SUPER_BREAKER, false));
        notificationManager.verify(() -> NotificationManager.sendPlayerUnlockNotification(
                mmoPlayer, SubSkillType.MINING_BLAST_MINING, false));
    }
}
