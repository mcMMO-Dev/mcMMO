package com.gmail.nossr50.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.notifications.SensitiveCommandType;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.player.NotificationManager;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

/**
 * Covers /xprate argument handling: decimal and integer rates must reach the global XP
 * multiplier unchanged, non-positive or non-finite rates must never touch it, whole-number
 * rates must be announced without a trailing ".0", and reset must restore the multiplier
 * captured when the command was created.
 */
class XprateCommandTest extends MMOTestEnvironment {
    private static final Logger logger = Logger.getLogger(XprateCommandTest.class.getName());
    private static final double ORIGINAL_RATE = 1.0;

    private XprateCommand xprateCommand;
    private CommandSender sender;
    private Command command;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(logger);
        when(ExperienceConfig.getInstance().getExperienceGainsGlobalMultiplier())
                .thenReturn(ORIGINAL_RATE);

        sender = mock(CommandSender.class);
        command = mock(Command.class);
        when(command.getPermissionMessage()).thenReturn("permission-denied");
        when(Permissions.xprateSet(sender)).thenReturn(true);
        when(Permissions.xprateReset(sender)).thenReturn(true);

        xprateCommand = new XprateCommand();
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    private boolean runCommand(String... args) {
        return xprateCommand.onCommand(sender, command, "xprate", args);
    }

    @ParameterizedTest
    @CsvSource({
            "1.5, 1.5",
            "2.25, 2.25",
            "10.75, 10.75",
            "2, 2.0",
            "100, 100.0",
            "1.0, 1.0",
    })
    void onCommandShouldApplyPositiveRateWhenStartingEvent(String input, double expected) {
        // Given - a sender with permission to start an XP event

        // When - the sender starts an event with a positive decimal or integer rate
        final boolean handled = runCommand(input, "true");

        // Then - the exact rate reaches the global XP multiplier
        assertThat(handled).isTrue();
        verify(ExperienceConfig.getInstance()).setExperienceGainsGlobalMultiplier(expected);
        verify(mcMMO.p).setXPEventEnabled(true);
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "-0.5", "0", "0.0", "NaN", "Infinity", "-Infinity", "abc",
            "1.2.3", ""})
    void onCommandShouldRejectRateWhenValueIsNotAPositiveFiniteNumber(String input) {
        // Given - a sender with permission to start an XP event

        // When - the sender supplies a rate that is negative, zero, non-finite, or garbage
        final boolean handled = runCommand(input, "true");

        // Then - the command is handled with feedback and the multiplier is never touched
        assertThat(handled).isTrue();
        verify(sender, atLeastOnce()).sendMessage(anyString());
        verify(ExperienceConfig.getInstance(), never())
                .setExperienceGainsGlobalMultiplier(anyDouble());
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "0", "NaN"})
    void onCommandShouldNotToggleXpEventWhenRateIsInvalid(String input) {
        // Given - a sender with permission to start an XP event

        // When - the sender supplies an invalid rate together with a valid toggle
        runCommand(input, "true");

        // Then - the XP event state is left untouched despite the valid toggle argument
        verify(mcMMO.p, never()).setXPEventEnabled(true);
    }

    @Test
    void onCommandShouldShowUsageWhenToggleArgumentIsInvalid() {
        // Given - a sender with permission and a valid rate

        // When - the toggle argument is neither an enable nor a disable keyword
        final boolean handled = runCommand("2", "maybe");

        // Then - usage is requested and the multiplier is never touched
        assertThat(handled).isFalse();
        verify(ExperienceConfig.getInstance(), never())
                .setExperienceGainsGlobalMultiplier(anyDouble());
    }

    @Test
    void onCommandShouldAnnounceWholeRateWithoutTrailingZero() {
        // Given - event broadcasts are enabled and the sender sets a whole-number decimal rate
        when(mcMMO.p.getGeneralConfig().broadcastEventMessages()).thenReturn(true);

        // When - the rate is set to 2.0
        runCommand("2.0", "true");

        // Then - every broadcast and notification shows "2" rather than "2.0"
        final ArgumentCaptor<String> broadcastCaptor = ArgumentCaptor.forClass(String.class);
        verify(mcMMO.p.getServer(), atLeastOnce()).broadcastMessage(broadcastCaptor.capture());
        assertThat(broadcastCaptor.getAllValues())
                .anySatisfy(message -> assertThat(message).contains("2"))
                .allSatisfy(message -> assertThat(message).doesNotContain("2.0"));
        notificationManager.verify(() -> NotificationManager.processSensitiveCommandNotification(
                sender, SensitiveCommandType.XPRATE_MODIFY, "2"));
    }

    @Test
    void onCommandShouldAnnounceFractionalRateWithDecimals() {
        // Given - event broadcasts are enabled and the sender sets a fractional rate
        when(mcMMO.p.getGeneralConfig().broadcastEventMessages()).thenReturn(true);

        // When - the rate is set to 1.5
        runCommand("1.5", "true");

        // Then - the broadcast and the admin notification both show the decimal rate
        final ArgumentCaptor<String> broadcastCaptor = ArgumentCaptor.forClass(String.class);
        verify(mcMMO.p.getServer(), atLeastOnce()).broadcastMessage(broadcastCaptor.capture());
        assertThat(broadcastCaptor.getAllValues())
                .anySatisfy(message -> assertThat(message).contains("1.5"));
        notificationManager.verify(() -> NotificationManager.processSensitiveCommandNotification(
                sender, SensitiveCommandType.XPRATE_MODIFY, "1.5"));
    }

    @Test
    void onCommandShouldRestoreOriginalRateWhenReset() {
        // Given - the command captured the multiplier that was active when it was created

        // When - the sender resets the XP rate
        final boolean handled = runCommand("reset");

        // Then - the multiplier captured at construction time is restored
        assertThat(handled).isTrue();
        verify(ExperienceConfig.getInstance())
                .setExperienceGainsGlobalMultiplier(ORIGINAL_RATE);
    }

    @Test
    void onCommandShouldShowCurrentRateWhenCalledWithNoArgs() {
        // Given - a fractional rate is active and the sender may view the rate
        when(ExperienceConfig.getInstance().getExperienceGainsGlobalMultiplier())
                .thenReturn(2.5);
        when(Permissions.xprateShow(sender)).thenReturn(true);

        // When - the sender runs /xprate with no arguments
        final boolean handled = runCommand();

        // Then - the sender is told the current rate
        assertThat(handled).isTrue();
        final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(sender).sendMessage(messageCaptor.capture());
        assertThat(messageCaptor.getValue()).contains("2.5");
    }

    @Test
    void onCommandShouldShowWholeRateWithoutTrailingZeroWhenCalledWithNoArgs() {
        // Given - a whole-number rate is active and the sender may view the rate
        when(ExperienceConfig.getInstance().getExperienceGainsGlobalMultiplier())
                .thenReturn(2.0);
        when(Permissions.xprateShow(sender)).thenReturn(true);

        // When - the sender runs /xprate with no arguments
        final boolean handled = runCommand();

        // Then - the rate is shown as "2" rather than "2.0"
        assertThat(handled).isTrue();
        final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(sender).sendMessage(messageCaptor.capture());
        assertThat(messageCaptor.getValue()).contains("2").doesNotContain("2.0");
    }

    @Test
    void onCommandShouldDenyRateViewWithoutShowPermission() {
        // Given - a sender lacking the show permission
        when(Permissions.xprateShow(sender)).thenReturn(false);

        // When - the sender runs /xprate with no arguments
        final boolean handled = runCommand();

        // Then - only the permission message is sent
        assertThat(handled).isTrue();
        verify(sender).sendMessage("permission-denied");
    }

    /**
     * Folia runs player commands on independent region threads, and every player holds the
     * show permission by default, so the rate formatting used by /xprate must not garble its
     * output when several senders format a rate at the same moment.
     */
    @Test
    void formatXpRateShouldStayWellFormedUnderConcurrentUse() throws Exception {
        // Given - several threads formatting the same grouping-heavy rate simultaneously
        final int threadCount = 8;
        final int iterationsPerThread = 20_000;
        final ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        final CyclicBarrier startBarrier = new CyclicBarrier(threadCount);
        final Queue<String> garbledResults = new ConcurrentLinkedQueue<>();

        try {
            // When - every thread formats the rate repeatedly at the same time
            final List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < threadCount; i++) {
                futures.add(pool.submit(() -> {
                    startBarrier.await();
                    for (int iteration = 0; iteration < iterationsPerThread; iteration++) {
                        final String formatted = XprateCommand.formatXpRate(1234567.89);
                        if (!"1,234,567.89".equals(formatted)) {
                            garbledResults.add(formatted);
                        }
                    }
                    return null;
                }));
            }

            for (final Future<?> future : futures) {
                future.get(30, TimeUnit.SECONDS);
            }
        } finally {
            pool.shutdownNow();
        }

        // Then - no call ever observes a garbled rate
        assertThat(garbledResults).isEmpty();
    }

    @Test
    void onCommandShouldDenyRateChangeWithoutSetPermission() {
        // Given - a sender lacking the set permission
        when(Permissions.xprateSet(sender)).thenReturn(false);

        // When - the sender tries to change the rate
        final boolean handled = runCommand("1.5", "true");

        // Then - the permission message is sent and the multiplier is never touched
        assertThat(handled).isTrue();
        verify(sender).sendMessage("permission-denied");
        verify(ExperienceConfig.getInstance(), never())
                .setExperienceGainsGlobalMultiplier(anyDouble());
    }

    /**
     * Gotcha coverage: /xprate rates never reduce XP below the configured baseline (max wins),
     * so a below-baseline rate would silently do nothing at best and is almost always a typo.
     * The command must reject it and tell the sender instead of quietly accepting it.
     */
    @ParameterizedTest(name = "rate {0} below the configured 1.0 should be rejected")
    @ValueSource(strings = {"0.5", "0.99"})
    void onCommandShouldRejectRateBelowConfiguredBaseline(String belowBaseline) {
        // Given - the configured baseline rate is 1.0

        // When - the sender requests a global rate below the baseline
        final boolean handledGlobal = runCommand(belowBaseline);

        // And - a per-skill rate below the baseline
        final boolean handledSkill = runCommand("mining", belowBaseline);

        // Then - both are handled with feedback and no rate or event state changes
        assertThat(handledGlobal).isTrue();
        assertThat(handledSkill).isTrue();
        verify(sender, atLeastOnce()).sendMessage(anyString());
        verify(ExperienceConfig.getInstance(), never())
                .setExperienceGainsGlobalMultiplier(anyDouble());
        verify(ExperienceConfig.getInstance(), never())
                .setExperienceGainsSkillMultiplier(any(PrimarySkillType.class), anyDouble());
        verify(mcMMO.p, never()).setXPEventEnabled(anyBoolean());
    }

    @Test
    void onCommandShouldStartEventWhenBareRateIsGiven() {
        // Given - a sender with permission to start an XP event

        // When - the sender runs /xprate with just a rate and no toggle
        final boolean handled = runCommand("1.5");

        // Then - the rate applies and the change counts as an XP event without needing 'true'
        assertThat(handled).isTrue();
        verify(ExperienceConfig.getInstance()).setExperienceGainsGlobalMultiplier(1.5);
        verify(mcMMO.p).setXPEventEnabled(true);
    }

    @ParameterizedTest(name = "\"{0}\" should set the mining rate")
    @ValueSource(strings = {"mining", "Mining", "MINING"})
    void onCommandShouldSetSkillRateWhenSkillIsGiven(String skillArgument) {
        // Given - a sender with permission to start an XP event

        // When - the sender sets a per-skill rate without a toggle
        final boolean handled = runCommand(skillArgument, "5.3");

        // Then - the rate reaches that skill only, as an XP event, and admins are notified
        assertThat(handled).isTrue();
        verify(ExperienceConfig.getInstance())
                .setExperienceGainsSkillMultiplier(PrimarySkillType.MINING, 5.3);
        verify(ExperienceConfig.getInstance(), never())
                .setExperienceGainsGlobalMultiplier(anyDouble());
        verify(mcMMO.p).setXPEventEnabled(true);
        notificationManager.verify(() -> NotificationManager.processSensitiveCommandNotification(
                sender, SensitiveCommandType.XPRATE_MODIFY_SKILL, "Mining", "5.3"));
    }

    /**
     * Gotcha coverage: a quiet per-skill change must not end an event that other rates are
     * part of, so 'false' leaves the event flag alone instead of switching it off.
     */
    @Test
    void onCommandShouldLeaveEventFlagUntouchedWhenSkillRateToggleIsFalse() {
        // Given - a sender with permission to change rates

        // When - the sender sets a per-skill rate explicitly outside an event
        final boolean handled = runCommand("mining", "2", "false");

        // Then - the rate applies but the event flag is neither enabled nor disabled
        assertThat(handled).isTrue();
        verify(ExperienceConfig.getInstance())
                .setExperienceGainsSkillMultiplier(PrimarySkillType.MINING, 2.0);
        verify(mcMMO.p, never()).setXPEventEnabled(anyBoolean());
    }

    /**
     * A quiet per-skill change is still announced, but without the event banner, and when a
     * higher global event rate keeps applying to the skill the broadcast must say so instead
     * of implying the skill's rate change took effect or that any event ended.
     */
    @Test
    void onCommandShouldAnnounceQuietSkillChangeWithGlobalStillApplyingDuringEvent() {
        // Given - broadcasts are on and a 3x global XP event is active
        when(mcMMO.p.getGeneralConfig().broadcastEventMessages()).thenReturn(true);
        when(mcMMO.p.isXPEventEnabled()).thenReturn(true);
        when(ExperienceConfig.getInstance().getExperienceGainsGlobalMultiplier())
                .thenReturn(3.0);

        // When - mining is set to a lower rate outside the event
        runCommand("mining", "2", "false");

        // Then - the change and the still-applying global rate are announced without a banner
        final ArgumentCaptor<String> broadcastCaptor = ArgumentCaptor.forClass(String.class);
        verify(mcMMO.p.getServer(), times(2)).broadcastMessage(broadcastCaptor.capture());
        assertThat(broadcastCaptor.getAllValues())
                .anySatisfy(message -> assertThat(message).contains("Mining").contains("2"))
                .anySatisfy(message -> assertThat(message).contains("Mining").contains("3"))
                .noneSatisfy(message -> assertThat(message).contains("Event!"));
    }

    @Test
    void onCommandShouldAnnounceQuietSkillChangeWithoutBannerOrGlobalLineOutsideEvent() {
        // Given - broadcasts are on and no XP event is active
        when(mcMMO.p.getGeneralConfig().broadcastEventMessages()).thenReturn(true);

        // When - mining is set to a rate outside an event
        runCommand("mining", "2", "false");

        // Then - only the rate change itself is announced, with no event banner
        final ArgumentCaptor<String> broadcastCaptor = ArgumentCaptor.forClass(String.class);
        verify(mcMMO.p.getServer(), times(1)).broadcastMessage(broadcastCaptor.capture());
        assertThat(broadcastCaptor.getValue()).contains("Mining").contains("2")
                .doesNotContain("Event!");
    }

    @Test
    void onCommandShouldAnnounceQuietGlobalChangeWithoutBanner() {
        // Given - broadcasts are on

        // When - the global rate is changed outside an event
        when(mcMMO.p.getGeneralConfig().broadcastEventMessages()).thenReturn(true);
        runCommand("2", "false");

        // Then - the rate change is announced without the event banner
        final ArgumentCaptor<String> broadcastCaptor = ArgumentCaptor.forClass(String.class);
        verify(mcMMO.p.getServer(), times(1)).broadcastMessage(broadcastCaptor.capture());
        assertThat(broadcastCaptor.getValue()).contains("2").doesNotContain("Event!");
    }

    @Test
    void onCommandShouldAnnounceEventBannerWhenSkillRateIsAnEvent() {
        // Given - broadcasts are on

        // When - a per-skill event rate is set
        when(mcMMO.p.getGeneralConfig().broadcastEventMessages()).thenReturn(true);
        runCommand("mining", "5.3");

        // Then - the event banner and the skill rate are both announced
        final ArgumentCaptor<String> broadcastCaptor = ArgumentCaptor.forClass(String.class);
        verify(mcMMO.p.getServer(), times(2)).broadcastMessage(broadcastCaptor.capture());
        assertThat(broadcastCaptor.getAllValues())
                .anySatisfy(message -> assertThat(message).contains("Event!"))
                .anySatisfy(message -> assertThat(message).contains("Mining").contains("5.3"));
    }

    /**
     * Announcements must use the locale's display-cased skill name from the Overhaul.Name
     * keys (the same ones level-up messages use), so non-English servers see their own
     * language and en_US sees "Mining" rather than the shouted "MINING" SkillName value.
     */
    @Test
    void onCommandShouldAnnounceSkillDisplayNameFromOverhaulLocaleKey() {
        // Given - broadcasts are on and the locale gives mining a non-English display name
        when(mcMMO.p.getGeneralConfig().broadcastEventMessages()).thenReturn(true);
        try (MockedStatic<LocaleLoader> localeLoader = mockStatic(LocaleLoader.class,
                CALLS_REAL_METHODS)) {
            localeLoader.when(() -> LocaleLoader.getString("Overhaul.Name.Mining"))
                    .thenReturn("Bergbau");

            // When - mining is set quietly
            runCommand("mining", "2", "false");
        }

        // Then - the announcement shows the locale's display name for the skill
        final ArgumentCaptor<String> broadcastCaptor = ArgumentCaptor.forClass(String.class);
        verify(mcMMO.p.getServer()).broadcastMessage(broadcastCaptor.capture());
        assertThat(broadcastCaptor.getValue()).contains("Bergbau");
    }

    @Test
    void onCommandShouldDowngradeSkillRateWhenRerunWithLowerRate() {
        // Given - a mining rate of 3 is active

        // When - the sender re-runs the command with a lower rate still above the baseline
        runCommand("mining", "3");
        final boolean handled = runCommand("mining", "2");

        // Then - the new lower rate replaces the old one
        assertThat(handled).isTrue();
        verify(ExperienceConfig.getInstance())
                .setExperienceGainsSkillMultiplier(PrimarySkillType.MINING, 3.0);
        verify(ExperienceConfig.getInstance())
                .setExperienceGainsSkillMultiplier(PrimarySkillType.MINING, 2.0);
    }

    @Test
    void onCommandShouldTreatAllAsGlobalRate() {
        // Given - a sender with permission to start an XP event

        // When - the sender uses 'all' in the skill argument position
        final boolean handled = runCommand("all", "2.5");

        // Then - the global multiplier changes and no per-skill rate is created
        assertThat(handled).isTrue();
        verify(ExperienceConfig.getInstance()).setExperienceGainsGlobalMultiplier(2.5);
        verify(ExperienceConfig.getInstance(), never())
                .setExperienceGainsSkillMultiplier(any(PrimarySkillType.class), anyDouble());
    }

    /**
     * Gotcha coverage: child skills have no XP of their own, so a per-skill rate for them must
     * be rejected before touching any multiplier.
     */
    @ParameterizedTest
    @ValueSource(strings = {"smelting", "salvage"})
    void onCommandShouldRejectChildSkills(String childSkill) {
        // Given - a sender with permission to change rates

        // When - the sender targets a child skill
        final boolean handled = runCommand(childSkill, "2");

        // Then - the sender is told child skills are unsupported and nothing changes
        assertThat(handled).isTrue();
        verify(sender, atLeastOnce()).sendMessage(anyString());
        verify(ExperienceConfig.getInstance(), never())
                .setExperienceGainsSkillMultiplier(any(PrimarySkillType.class), anyDouble());
        verify(mcMMO.p, never()).setXPEventEnabled(anyBoolean());
    }

    @Test
    void onCommandShouldRejectUnknownSkillNames() {
        // Given - a sender with permission to change rates

        // When - the sender targets something that is not a skill
        final boolean handled = runCommand("bogus", "2");

        // Then - the sender gets the invalid-skill message and nothing changes
        assertThat(handled).isTrue();
        verify(sender, atLeastOnce()).sendMessage(anyString());
        verify(ExperienceConfig.getInstance(), never())
                .setExperienceGainsSkillMultiplier(any(PrimarySkillType.class), anyDouble());
    }

    @Test
    void onCommandShouldClearSkillRatesOnReset() {
        // Given - the sender may reset rates

        // When - the sender resets the XP rates
        final boolean handled = runCommand("reset");

        // Then - the global rate is restored and every per-skill rate is cleared
        assertThat(handled).isTrue();
        verify(ExperienceConfig.getInstance())
                .setExperienceGainsGlobalMultiplier(ORIGINAL_RATE);
        verify(ExperienceConfig.getInstance()).clearExperienceGainsSkillMultipliers();
    }

    @Test
    void onCommandShouldListSkillRatesWhenCalledWithNoArgs() {
        // Given - a mining rate is active alongside the global rate
        when(Permissions.xprateShow(sender)).thenReturn(true);
        final Map<PrimarySkillType, Double> overrides = new EnumMap<>(PrimarySkillType.class);
        overrides.put(PrimarySkillType.MINING, 5.3);
        when(ExperienceConfig.getInstance().getExperienceGainsSkillMultiplierOverrides())
                .thenReturn(overrides);

        // When - the sender runs /xprate with no arguments
        final boolean handled = runCommand();

        // Then - both the global rate and the active mining rate are shown
        assertThat(handled).isTrue();
        final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(sender, times(2)).sendMessage(messageCaptor.capture());
        assertThat(messageCaptor.getAllValues())
                .anySatisfy(message -> assertThat(message).contains("Mining").contains("5.3"));
    }

    @Test
    void onCommandShouldShowCurrentRateWhenCalledWithShowArgument() {
        // Given - a fractional rate is active and the sender may view the rate
        when(ExperienceConfig.getInstance().getExperienceGainsGlobalMultiplier())
                .thenReturn(2.5);
        when(Permissions.xprateShow(sender)).thenReturn(true);

        // When - the sender runs /xprate show
        final boolean handled = runCommand("show");

        // Then - it behaves exactly like the no-argument form
        assertThat(handled).isTrue();
        final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(sender).sendMessage(messageCaptor.capture());
        assertThat(messageCaptor.getValue()).contains("2.5");
    }

    @Test
    void onCommandShouldShowHowLongEachRateHasBeenActive() {
        // Given - a global event rate set 90 seconds ago and a mining rate set 2 hours ago
        when(Permissions.xprateShow(sender)).thenReturn(true);
        final long now = System.currentTimeMillis();
        when(ExperienceConfig.getInstance().getExperienceGainsGlobalMultiplier())
                .thenReturn(2.5);
        when(ExperienceConfig.getInstance().getExperienceGainsGlobalMultiplierSetMillis())
                .thenReturn(now - 90_000L);
        final Map<PrimarySkillType, Double> overrides = new EnumMap<>(PrimarySkillType.class);
        overrides.put(PrimarySkillType.MINING, 5.3);
        when(ExperienceConfig.getInstance().getExperienceGainsSkillMultiplierOverrides())
                .thenReturn(overrides);
        when(ExperienceConfig.getInstance()
                .getExperienceGainsSkillMultiplierSetMillis(PrimarySkillType.MINING))
                .thenReturn(now - 7_200_000L);

        // When - the sender views the current rates
        runCommand();

        // Then - each line reports how long its rate has been active
        final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(sender, times(2)).sendMessage(messageCaptor.capture());
        assertThat(messageCaptor.getAllValues())
                .anySatisfy(message -> assertThat(message).contains("2.5").contains("1m 30s"))
                .anySatisfy(message -> assertThat(message).contains("Mining").contains("2h 0m"));
    }

    @Test
    void onCommandShouldSayNoEventIsActiveWhenRatesAreAtBaseline() {
        // Given - the global rate holds the configured baseline, no event, no per-skill rates
        when(Permissions.xprateShow(sender)).thenReturn(true);
        when(ExperienceConfig.getInstance().getExperienceGainsGlobalMultiplier())
                .thenReturn(ORIGINAL_RATE);

        // When - the sender views the current rates
        runCommand();

        // Then - the sender is told no event is active instead of being shown a rate
        final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(sender).sendMessage(messageCaptor.capture());
        assertThat(messageCaptor.getValue()).contains("No XP rate event")
                .doesNotContain("current");
    }

    /**
     * Convenience feature: a new global rate makes any per-skill rate at or below it
     * permanently ineffective (higher wins), so those are cleared and the sender is told.
     */
    @Test
    void onCommandShouldClearCoveredSkillRatesWhenGlobalRateIsSet() {
        // Given - mining 2x and herbalism 5x rates are active
        final Map<PrimarySkillType, Double> overrides = new EnumMap<>(PrimarySkillType.class);
        overrides.put(PrimarySkillType.MINING, 2.0);
        overrides.put(PrimarySkillType.HERBALISM, 5.0);
        when(ExperienceConfig.getInstance().getExperienceGainsSkillMultiplierOverrides())
                .thenReturn(overrides);

        // When - a global rate covering the mining rate is set
        runCommand("3");

        // Then - the covered mining rate is cleared, the higher herbalism rate survives,
        // and the sender is told what was cleared
        verify(ExperienceConfig.getInstance())
                .clearExperienceGainsSkillMultiplier(PrimarySkillType.MINING);
        verify(ExperienceConfig.getInstance(), never())
                .clearExperienceGainsSkillMultiplier(PrimarySkillType.HERBALISM);
        final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(sender, atLeastOnce()).sendMessage(messageCaptor.capture());
        assertThat(messageCaptor.getAllValues())
                .anySatisfy(message -> assertThat(message).contains("Mining")
                        .doesNotContain("Herbalism"));
    }

    @Test
    void onTabCompleteShouldSuggestExampleRatesAfterSkillButNotAfterSingleArgTokens() {
        // Given - the sender already typed a skill, 'all', or a single-argument token

        // When - the second argument is completed
        final List<String> afterSkill = tabComplete("mining", "");
        final List<String> afterAll = tabComplete("all", "");
        final List<String> afterReset = tabComplete("reset", "");
        final List<String> afterShow = tabComplete("show", "");

        // Then - example rates hint that a number comes next, but not after reset or show
        assertThat(afterSkill).contains("1.5", "2", "3");
        assertThat(afterAll).contains("1.5", "2", "3");
        assertThat(afterReset).isEmpty();
        assertThat(afterShow).isEmpty();
    }

    /**
     * Pins the two-most-significant-units duration format shown by /xprate.
     */
    @ParameterizedTest(name = "{0} ms should format as \"{1}\"")
    @CsvSource({
            "47000, 47s",
            "90000, 1m 30s",
            "7500000, 2h 5m",
            "187200000, 2d 4h",
            "0, 0s",
    })
    void formatDurationShouldUseTwoMostSignificantUnits(long millis, String expected) {
        // When - the duration is formatted
        // Then - it uses the two most significant units
        assertThat(XprateCommand.formatDuration(millis)).isEqualTo(expected);
    }

    @Test
    void onTabCompleteShouldSuggestSkillsResetAndAllForFirstArgument() {
        // Given - the sender is completing the first argument

        // When - partial prefixes are completed
        final List<String> miningMatches = tabComplete("mi");
        final List<String> resetMatches = tabComplete("re");
        final List<String> allMatches = tabComplete("al");

        // Then - lowercase skill names, reset, and all are all reachable
        assertThat(miningMatches).contains("mining");
        assertThat(resetMatches).contains("reset");
        assertThat(allMatches).contains("all");
    }

    @Test
    void onTabCompleteShouldSuggestToggleAfterRate() {
        // Given - the sender already typed a rate

        // When - the second argument is completed
        final List<String> afterRate = tabComplete("2", "t");

        // Then - the event toggle keywords are suggested
        assertThat(afterRate).contains("true");
    }

    @Test
    void onTabCompleteShouldSuggestToggleAfterSkillAndRate() {
        // Given - the sender already typed a skill and a rate

        // When - the third argument is completed
        final List<String> matches = tabComplete("mining", "5.3", "f");

        // Then - the event toggle keywords are suggested
        assertThat(matches).contains("false");
    }

    private List<String> tabComplete(String... args) {
        return xprateCommand.onTabComplete(sender, command, "xprate", args);
    }
}
