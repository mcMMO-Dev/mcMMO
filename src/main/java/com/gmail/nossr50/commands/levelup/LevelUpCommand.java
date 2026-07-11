package com.gmail.nossr50.commands.levelup;

import static java.util.Objects.requireNonNull;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.placeholders.PapiPlaceholders;
import com.gmail.nossr50.util.LogUtils;
import com.gmail.nossr50.util.skills.SkillTools;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

/**
 * Runs configured commands when a {@link LevelUpCondition} matches a level up. Commands run
 * once per matched milestone level with placeholders injected, dispatched through FoliaLib so
 * they execute on the right thread on Folia servers.
 * <p>
 * Supported placeholders: {@code {@player}}, {@code {@skill}} and {@code {@level}} (skill
 * milestones only), {@code {@power_level}} (the matched milestone for power level triggers,
 * otherwise the player's current power level), and {@code {@<skill>_level}} (current level of
 * any non-child skill, e.g. {@code {@mining_level}}).
 * <p>
 * When PlaceholderAPI is installed, its placeholders are also resolved against the player who
 * leveled up before the command runs.
 */
public final class LevelUpCommand implements LevelUpAction {

    /** Who executes the configured commands. */
    public enum RunAs {
        CONSOLE,
        PLAYER
    }

    private static final String POWER_LEVEL_TOKEN = "{@power_level}";
    private static final Map<PrimarySkillType, String> SKILL_LEVEL_TOKENS;

    static {
        final Map<PrimarySkillType, String> tokens = new EnumMap<>(PrimarySkillType.class);
        for (PrimarySkillType nonChildSkill : SkillTools.NON_CHILD_SKILLS) {
            tokens.put(nonChildSkill,
                    "{@" + nonChildSkill.name().toLowerCase(Locale.ENGLISH) + "_level}");
        }
        SKILL_LEVEL_TOKENS = Map.copyOf(tokens);
    }

    private final @NotNull LevelUpCondition condition;
    private final @NotNull List<String> commands;
    private final @NotNull RunAs runAs;

    public LevelUpCommand(@NotNull LevelUpCondition condition, @NotNull List<String> commands,
            @NotNull RunAs runAs) {
        this.condition = requireNonNull(condition, "condition must not be null");
        this.commands = List.copyOf(requireNonNull(commands, "commands must not be null"));
        this.runAs = requireNonNull(runAs, "runAs must not be null");
        if (this.commands.isEmpty()) {
            throw new IllegalArgumentException("commands must not be empty");
        }
    }

    public static @NotNull LevelUpCommandBuilder builder() {
        return new LevelUpCommandBuilder();
    }

    @Override
    public void onLevelUp(@NotNull McMMOPlayer mmoPlayer,
            @NotNull PrimarySkillType primarySkillType, @NotNull Set<Integer> levelsGained,
            @NotNull Set<Integer> powerLevelsGained) {
        for (int matchedLevel : condition.matchedSkillLevels(primarySkillType, levelsGained)) {
            executeCommands(mmoPlayer, primarySkillType, matchedLevel, null);
        }
        for (int matchedPowerLevel : condition.matchedPowerLevels(powerLevelsGained)) {
            executeCommands(mmoPlayer, null, null, matchedPowerLevel);
        }
    }

    @VisibleForTesting
    void executeCommands(@NotNull McMMOPlayer mmoPlayer, @Nullable PrimarySkillType skill,
            @Nullable Integer matchedLevel, @Nullable Integer matchedPowerLevel) {
        for (String command : commands) {
            final String injected = PapiPlaceholders.replace(mmoPlayer.getPlayer(),
                    injectPlaceholders(command, mmoPlayer, skill, matchedLevel,
                            matchedPowerLevel));
            LogUtils.debug(mcMMO.p.getLogger(), "Executing level up command: " + injected);
            dispatch(mmoPlayer.getPlayer(), injected);
        }
    }

    private void dispatch(@NotNull Player player, @NotNull String commandLine) {
        if (runAs == RunAs.PLAYER) {
            mcMMO.p.getFoliaLib().getScheduler().runAtEntity(player,
                    task -> Bukkit.dispatchCommand(player, commandLine));
        } else {
            mcMMO.p.getFoliaLib().getScheduler().runNextTick(
                    task -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandLine));
        }
    }

    @VisibleForTesting
    static @NotNull String injectPlaceholders(@NotNull String command,
            @NotNull McMMOPlayer mmoPlayer, @Nullable PrimarySkillType skill,
            @Nullable Integer matchedLevel, @Nullable Integer matchedPowerLevel) {
        final StringBuilder builder = new StringBuilder(command);
        replaceAll(builder, "{@player}", mmoPlayer.getPlayer().getName());

        if (skill != null) {
            replaceAll(builder, "{@skill}", mcMMO.p.getSkillTools().getLocalizedSkillName(skill));
        }
        if (matchedLevel != null) {
            replaceAll(builder, "{@level}", String.valueOf(matchedLevel));
        }

        if (builder.indexOf(POWER_LEVEL_TOKEN) != -1) {
            final int powerLevel = matchedPowerLevel != null
                    ? matchedPowerLevel : mmoPlayer.getPowerLevel();
            replaceAll(builder, POWER_LEVEL_TOKEN, String.valueOf(powerLevel));
        }

        for (PrimarySkillType primarySkillType : SkillTools.NON_CHILD_SKILLS) {
            final String token = SKILL_LEVEL_TOKENS.get(primarySkillType);
            if (builder.indexOf(token) != -1) {
                replaceAll(builder, token,
                        String.valueOf(mmoPlayer.getSkillLevel(primarySkillType)));
            }
        }

        return builder.toString();
    }

    private static void replaceAll(StringBuilder builder, String from, String to) {
        int index = builder.indexOf(from);
        while (index != -1) {
            builder.replace(index, index + from.length(), to);
            index = builder.indexOf(from, index + to.length());
        }
    }

    public @NotNull LevelUpCondition getCondition() {
        return condition;
    }

    public @NotNull List<String> getCommands() {
        return commands;
    }

    public @NotNull RunAs getRunAs() {
        return runAs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LevelUpCommand that = (LevelUpCommand) o;
        return condition.equals(that.condition) && commands.equals(that.commands)
                && runAs == that.runAs;
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, commands, runAs);
    }

    @Override
    public String toString() {
        return "LevelUpCommand{condition=" + condition + ", commands=" + commands
                + ", runAs=" + runAs + '}';
    }
}
