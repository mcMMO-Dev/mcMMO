package com.gmail.nossr50.commands;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.notifications.SensitiveCommandType;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.skills.SkillTools;
import com.gmail.nossr50.util.text.StringUtils;
import com.google.common.collect.ImmutableList;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class XprateCommand implements TabExecutor {
    // The token that targets the global rate in the skill argument position
    private static final String ALL_SKILLS_TOKEN = "all";

    // Example rates suggested after a skill argument, hinting that a number comes next
    private static final List<String> RATE_SUGGESTIONS = ImmutableList.of("1.5", "2", "3");

    // Matches how MessageFormat renders doubles in locale strings: no trailing ".0" on
    // whole numbers, grouping separators, up to three decimal places
    private static final String XP_RATE_FORMAT_PATTERN = "#,##0.###";

    private final double ORIGINAL_XP_RATE = ExperienceConfig.getInstance()
            .getExperienceGainsGlobalMultiplier();

    // Completions for the first argument: reset options, 'all', and non-child skill names
    private final List<String> firstArgumentCompletions;

    public XprateCommand() {
        final TreeSet<String> completions = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        completions.addAll(CommandUtils.RESET_OPTIONS);
        completions.add("show");
        completions.add(ALL_SKILLS_TOKEN);
        // Lowercased to read like the other keywords; matching stays case-insensitive
        for (PrimarySkillType skill : SkillTools.NON_CHILD_SKILLS) {
            completions.add(mcMMO.p.getSkillTools().getLocalizedSkillName(skill)
                    .toLowerCase(Locale.ENGLISH));
        }
        firstArgumentCompletions = ImmutableList.copyOf(completions);
    }

    /**
     * The locale's display-cased skill name from the Overhaul.Name keys, matching what
     * level-up messages show, rather than the all-caps SkillName value used for matching.
     */
    private static String displaySkillName(PrimarySkillType skill) {
        return LocaleLoader.getString(
                "Overhaul.Name." + StringUtils.getCapitalized(skill.toString()));
    }

    /**
     * Formats an XP rate for display. Creates a new format per call because DecimalFormat is
     * not thread safe and regionized servers run player commands on multiple threads.
     */
    static String formatXpRate(double xpRate) {
        return new DecimalFormat(XP_RATE_FORMAT_PATTERN,
                DecimalFormatSymbols.getInstance(Locale.US)).format(xpRate);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, String[] args) {
        switch (args.length) {
            case 0:
                return showCurrentRates(sender, command);

            case 1:
                if (args[0].equalsIgnoreCase("show")) {
                    return showCurrentRates(sender, command);
                }

                if (args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("clear")) {
                    return resetXpRates(sender, command);
                }

                if (StringUtils.isDouble(args[0])) {
                    // Bare rate: an XP event unless the sender says otherwise
                    return setGlobalXpRate(sender, command, args[0], true);
                }

                return false;

            case 2:
                if (StringUtils.isDouble(args[0])) {
                    final Boolean xpEvent = parseXpEventToggle(args[1]);
                    return xpEvent != null && setGlobalXpRate(sender, command, args[0], xpEvent);
                }

                return setSkillXpRate(sender, command, args[0], args[1], true);

            case 3:
                final Boolean xpEvent = parseXpEventToggle(args[2]);
                return xpEvent != null
                        && setSkillXpRate(sender, command, args[0], args[1], xpEvent);

            default:
                return false;
        }
    }

    private boolean showCurrentRates(CommandSender sender, Command command) {
        if (!Permissions.xprateShow(sender)) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        final ExperienceConfig experienceConfig = ExperienceConfig.getInstance();
        final long now = System.currentTimeMillis();

        final double globalRate = experienceConfig.getExperienceGainsGlobalMultiplier();
        if (globalRate == ORIGINAL_XP_RATE && !mcMMO.p.isXPEventEnabled()
                && experienceConfig.getExperienceGainsSkillMultiplierOverrides().isEmpty()) {
            sender.sendMessage(LocaleLoader.getString("Commands.xprate.none"));
            return true;
        }

        String globalLine = LocaleLoader.getString("Commands.xprate.current",
                formatXpRate(globalRate));
        final long globalSetMillis = experienceConfig
                .getExperienceGainsGlobalMultiplierSetMillis();
        // The baseline rate from experience.yml is not an event, so it gets no duration
        if (globalRate != ORIGINAL_XP_RATE && globalSetMillis > 0) {
            globalLine += LocaleLoader.getString("Commands.xprate.duration",
                    formatDuration(now - globalSetMillis));
        }
        sender.sendMessage(globalLine);

        experienceConfig.getExperienceGainsSkillMultiplierOverrides().forEach((skill, rate) -> {
            String skillLine = LocaleLoader.getString("Commands.xprate.current.skill",
                    displaySkillName(skill), formatXpRate(rate));
            final long setMillis = experienceConfig
                    .getExperienceGainsSkillMultiplierSetMillis(skill);
            if (setMillis > 0) {
                skillLine += LocaleLoader.getString("Commands.xprate.duration",
                        formatDuration(now - setMillis));
            }
            sender.sendMessage(skillLine);
        });
        return true;
    }

    /**
     * Formats a duration as its two most significant units, like "2d 4h" or "5m 32s".
     */
    static String formatDuration(long durationMillis) {
        final long totalSeconds = Math.max(0, durationMillis / 1000);
        final long days = totalSeconds / 86_400;
        final long hours = (totalSeconds % 86_400) / 3_600;
        final long minutes = (totalSeconds % 3_600) / 60;
        final long seconds = totalSeconds % 60;

        if (days > 0) {
            return days + "d " + hours + "h";
        }
        if (hours > 0) {
            return hours + "h " + minutes + "m";
        }
        if (minutes > 0) {
            return minutes + "m " + seconds + "s";
        }
        return seconds + "s";
    }

    private boolean resetXpRates(CommandSender sender, Command command) {
        if (!Permissions.xprateReset(sender)) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        if (mcMMO.p.isXPEventEnabled()) {
            if (mcMMO.p.getAdvancedConfig().useTitlesForXPEvent()) {
                NotificationManager.broadcastTitle(mcMMO.p.getServer(),
                        LocaleLoader.getString("Commands.Event.Stop"),
                        LocaleLoader.getString("Commands.Event.Stop.Subtitle"),
                        10, 10 * 20, 20);
            }

            if (mcMMO.p.getGeneralConfig().broadcastEventMessages()) {
                mcMMO.p.getServer()
                        .broadcastMessage(LocaleLoader.getString("Commands.Event.Stop"));
                mcMMO.p.getServer().broadcastMessage(
                        LocaleLoader.getString("Commands.Event.Stop.Subtitle"));
            }

            //Admin notification
            NotificationManager.processSensitiveCommandNotification(sender,
                    SensitiveCommandType.XPRATE_END);

            mcMMO.p.toggleXpEventEnabled();
        }

        ExperienceConfig.getInstance().setExperienceGainsGlobalMultiplier(ORIGINAL_XP_RATE);
        ExperienceConfig.getInstance().clearExperienceGainsSkillMultipliers();
        return true;
    }

    private boolean setGlobalXpRate(CommandSender sender, Command command, String rateArgument,
            boolean xpEvent) {
        if (!Permissions.xprateSet(sender)) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        final Double newXpRate = parseXpRate(sender, rateArgument);
        if (newXpRate == null) {
            return true;
        }

        mcMMO.p.setXPEventEnabled(xpEvent);
        ExperienceConfig.getInstance().setExperienceGainsGlobalMultiplier(newXpRate);
        clearSkillRatesCoveredByGlobal(sender, newXpRate);

        final String displayRate = formatXpRate(newXpRate);
        broadcastRateChange(xpEvent,
                LocaleLoader.getString("Commands.Event.XP", displayRate), null);

        //Admin notification
        NotificationManager.processSensitiveCommandNotification(sender,
                SensitiveCommandType.XPRATE_MODIFY, displayRate);

        return true;
    }

    private boolean setSkillXpRate(CommandSender sender, Command command, String skillArgument,
            String rateArgument, boolean xpEvent) {
        if (skillArgument.equalsIgnoreCase(ALL_SKILLS_TOKEN)) {
            return setGlobalXpRate(sender, command, rateArgument, xpEvent);
        }

        if (CommandUtils.isInvalidSkill(sender, skillArgument)) {
            return true;
        }

        final PrimarySkillType skill = mcMMO.p.getSkillTools().matchSkill(skillArgument);
        if (CommandUtils.isChildSkill(sender, skill)) {
            return true;
        }

        if (!Permissions.xprateSet(sender)) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        final Double newXpRate = parseXpRate(sender, rateArgument);
        if (newXpRate == null) {
            return true;
        }

        // A quiet per-skill change must not end an event other rates are part of, so only
        // 'true' touches the event flag here
        if (xpEvent) {
            mcMMO.p.setXPEventEnabled(true);
        }
        ExperienceConfig.getInstance().setExperienceGainsSkillMultiplier(skill, newXpRate);

        final String skillName = displaySkillName(skill);
        final String displayRate = formatXpRate(newXpRate);

        // A quiet change while a higher global event rate applies to the skill needs saying,
        // or the announced rate looks like the one players will actually get
        String followUpLine = null;
        if (!xpEvent && mcMMO.p.isXPEventEnabled()) {
            final double globalRate = ExperienceConfig.getInstance()
                    .getExperienceGainsGlobalMultiplier();
            if (globalRate > newXpRate) {
                followUpLine = LocaleLoader.getString("Commands.xprate.skill.globalApplies",
                        skillName, formatXpRate(globalRate));
            }
        }

        broadcastRateChange(xpEvent,
                LocaleLoader.getString("Commands.Event.XP.Skill", skillName, displayRate),
                followUpLine);

        //Admin notification
        NotificationManager.processSensitiveCommandNotification(sender,
                SensitiveCommandType.XPRATE_MODIFY_SKILL, skillName, displayRate);

        return true;
    }

    /**
     * Clears per-skill rates the new global rate covers. The higher rate wins, so a rate at or
     * below the new global one can never apply again and keeping it around would only clutter
     * /xprate output and survive into a lower future global rate by surprise.
     */
    private static void clearSkillRatesCoveredByGlobal(CommandSender sender, double globalRate) {
        final ExperienceConfig experienceConfig = ExperienceConfig.getInstance();
        final List<String> clearedSkills = new ArrayList<>();

        experienceConfig.getExperienceGainsSkillMultiplierOverrides()
                .forEach((skill, rate) -> {
                    if (rate <= globalRate) {
                        experienceConfig.clearExperienceGainsSkillMultiplier(skill);
                        clearedSkills.add(displaySkillName(skill));
                    }
                });

        if (!clearedSkills.isEmpty()) {
            sender.sendMessage(LocaleLoader.getString("Commands.xprate.skill.cleared",
                    String.join(", ", clearedSkills)));
        }
    }

    /**
     * Validates and parses a rate argument, messaging the sender when it is unusable. Rates
     * below the configured baseline are rejected because /xprate rates never reduce XP below
     * what the config files grant; such a rate would be a silent no-op at best.
     *
     * @return the parsed rate, or null if the argument was rejected
     */
    private @Nullable Double parseXpRate(CommandSender sender, String rateArgument) {
        if (CommandUtils.isInvalidDouble(sender, rateArgument)) {
            return null;
        }

        final double xpRate = Double.parseDouble(rateArgument);

        if (xpRate < 0) {
            sender.sendMessage(
                    ChatColor.RED + LocaleLoader.getString("Commands.NegativeNumberWarn"));
            return null;
        }

        if (xpRate == 0 || !Double.isFinite(xpRate)) {
            sender.sendMessage(
                    ChatColor.RED + LocaleLoader.getString("Commands.xprate.invalid"));
            return null;
        }

        if (xpRate < ORIGINAL_XP_RATE) {
            sender.sendMessage(LocaleLoader.getString("Commands.xprate.tooLow",
                    formatXpRate(xpRate), formatXpRate(ORIGINAL_XP_RATE)));
            return null;
        }

        return xpRate;
    }

    private static @Nullable Boolean parseXpEventToggle(String argument) {
        if (CommandUtils.shouldEnableToggle(argument)) {
            return Boolean.TRUE;
        }

        if (CommandUtils.shouldDisableToggle(argument)) {
            return Boolean.FALSE;
        }

        return null;
    }

    /**
     * Announces a rate change. Event changes get the event banner and title fanfare; quiet
     * changes ('false') are announced as a plain rate line so nothing reads like an event
     * starting or ending.
     */
    private static void broadcastRateChange(boolean xpEvent, String xpRateLine,
            @Nullable String followUpLine) {
        if (xpEvent && mcMMO.p.getAdvancedConfig().useTitlesForXPEvent()) {
            NotificationManager.broadcastTitle(mcMMO.p.getServer(),
                    LocaleLoader.getString("Commands.Event.Start"), xpRateLine, 10, 10 * 20, 20);
        }

        if (mcMMO.p.getGeneralConfig().broadcastEventMessages()) {
            if (xpEvent) {
                mcMMO.p.getServer()
                        .broadcastMessage(LocaleLoader.getString("Commands.Event.Start"));
            }

            mcMMO.p.getServer().broadcastMessage(xpRateLine);

            if (followUpLine != null) {
                mcMMO.p.getServer().broadcastMessage(followUpLine);
            }
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String alias, String[] args) {
        switch (args.length) {
            case 1:
                if (StringUtils.isDouble(args[0])) {
                    return ImmutableList.of();
                }

                return StringUtil.copyPartialMatches(args[0], firstArgumentCompletions,
                        new ArrayList<>(firstArgumentCompletions.size()));
            case 2:
                // After a rate the next argument is the event toggle; after a skill it is a rate
                if (StringUtils.isDouble(args[0])) {
                    return StringUtil.copyPartialMatches(args[1], CommandUtils.TRUE_FALSE_OPTIONS,
                            new ArrayList<>(CommandUtils.TRUE_FALSE_OPTIONS.size()));
                }

                if (args[0].equalsIgnoreCase("show") || args[0].equalsIgnoreCase("reset")
                        || args[0].equalsIgnoreCase("clear")) {
                    return ImmutableList.of();
                }

                return StringUtil.copyPartialMatches(args[1], RATE_SUGGESTIONS,
                        new ArrayList<>(RATE_SUGGESTIONS.size()));
            case 3:
                if (!StringUtils.isDouble(args[0]) && StringUtils.isDouble(args[1])) {
                    return StringUtil.copyPartialMatches(args[2], CommandUtils.TRUE_FALSE_OPTIONS,
                            new ArrayList<>(CommandUtils.TRUE_FALSE_OPTIONS.size()));
                }

                return ImmutableList.of();
            default:
                return ImmutableList.of();
        }
    }
}
