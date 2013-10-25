package com.gmail.nossr50.util.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillUtils;

import com.google.common.collect.ImmutableList;

public final class CommandUtils {
    public static final List<String> TRUE_FALSE_OPTIONS = ImmutableList.of("on", "off", "true", "false", "enabled", "disabled");
    public static final List<String> RESET_OPTIONS = ImmutableList.of("clear", "reset");

    private CommandUtils() {}

    public static boolean isChildSkill(CommandSender sender, SkillType skill) {
        if (!skill.isChildSkill()) {
            return false;
        }

        sender.sendMessage("Child skills are not supported by this command."); // TODO: Localize this
        return true;
    }

    public static boolean inspectOffline(CommandSender sender, PlayerProfile profile, boolean hasPermission) {
        if (unloadedProfile(sender, profile)) {
            return true;
        }

        if (!hasPermission) {
            sender.sendMessage(LocaleLoader.getString("Inspect.Offline"));
            return true;
        }

        return false;
    }

    public static boolean tooFar(CommandSender sender, Player target, boolean hasPermission) {
        if (sender instanceof Player && !Misc.isNear(((Player) sender).getLocation(), target.getLocation(), Config.getInstance().getInspectDistance()) && !hasPermission) {
            sender.sendMessage(LocaleLoader.getString("Inspect.TooFar"));
            return true;
        }

        return false;
    }

    public static boolean hidden(CommandSender sender, Player target, boolean hasPermission) {
        if (sender instanceof Player && !((Player)sender).canSee(target) && !hasPermission) {
            return true;
        }

        return false;
    }

    public static boolean noConsoleUsage(CommandSender sender) {
        if (sender instanceof Player) {
            return false;
        }

        sender.sendMessage(LocaleLoader.getString("Commands.NoConsole"));
        return true;
    }

    public static boolean isOffline(CommandSender sender, OfflinePlayer player) {
        if (player.isOnline()) {
            return false;
        }

        sender.sendMessage(LocaleLoader.getString("Commands.Offline"));
        return true;
    }

    /**
     * Checks if there is a valid mcMMOPlayer object.
     *
     * @param sender CommandSender who used the command
     * @param playerName name of the target player
     * @param mcMMOPlayer mcMMOPlayer object of the target player
     *
     * @return true if the player is online and a valid mcMMOPlayer object was found
     */
    public static boolean checkPlayerExistence(CommandSender sender, String playerName, McMMOPlayer mcMMOPlayer) {
        if (mcMMOPlayer != null) {
            return true;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        if (!player.hasPlayedBefore()) {
            sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
        }

        sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
        return false;
    }

    public static boolean unloadedProfile(CommandSender sender, PlayerProfile profile) {
        if (profile.isLoaded()) {
            return false;
        }

        sender.sendMessage(LocaleLoader.getString("Commands.Offline"));
        return true;
    }

    public static boolean isInvalidInteger(CommandSender sender, String value) {
        if (StringUtils.isInt(value)) {
            return false;
        }

        sender.sendMessage("That is not a valid integer."); // TODO: Localize
        return true;
    }

    public static boolean isInvalidDouble(CommandSender sender, String value) {
        if (StringUtils.isDouble(value)) {
            return false;
        }

        sender.sendMessage("That is not a valid percentage."); // TODO: Localize
        return true;
    }

    public static boolean isInvalidSkill(CommandSender sender, String skillName) {
        if (SkillUtils.isSkill(skillName)) {
            return false;
        }

        sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
        return true;
    }

    public static boolean shouldEnableToggle(String arg) {
        return arg.equalsIgnoreCase("on") || arg.equalsIgnoreCase("true") || arg.equalsIgnoreCase("enabled");
    }

    public static boolean shouldDisableToggle(String arg) {
        return arg.equalsIgnoreCase("off") || arg.equalsIgnoreCase("false") || arg.equalsIgnoreCase("disabled");
    }

    /**
     * Print out details on Gathering skills. Only for online players.
     *
     * @param inspect The player to retrieve stats for
     * @param display The sender to display stats to
     */
    public static void printGatheringSkills(Player inspect, CommandSender display) {
        printGroupedSkillData(inspect, display, LocaleLoader.getString("Stats.Header.Gathering"), SkillType.GATHERING_SKILLS);
    }

    public static void printGatheringSkills(Player player) {
        printGatheringSkills(player, player);
    }

    /**
     * Print out details on Combat skills. Only for online players.
     *
     * @param inspect The player to retrieve stats for
     * @param display The sender to display stats to
     */
    public static void printCombatSkills(Player inspect, CommandSender display) {
        printGroupedSkillData(inspect, display, LocaleLoader.getString("Stats.Header.Combat"), SkillType.COMBAT_SKILLS);
    }

    public static void printCombatSkills(Player player) {
        printCombatSkills(player, player);
    }

    /**
     * Print out details on Misc skills. Only for online players.
     *
     * @param inspect The player to retrieve stats for
     * @param display The sender to display stats to
     */
    public static void printMiscSkills(Player inspect, CommandSender display) {
        printGroupedSkillData(inspect, display, LocaleLoader.getString("Stats.Header.Misc"), SkillType.MISC_SKILLS);
    }

    public static void printMiscSkills(Player player) {
        printMiscSkills(player, player);
    }

    public static String displaySkill(PlayerProfile profile, SkillType skill) {
        if (skill.isChildSkill()) {
            return LocaleLoader.getString("Skills.ChildStats", LocaleLoader.getString(StringUtils.getCapitalized(skill.toString()) + ".Listener"), profile.getSkillLevel(skill));
        }

        return LocaleLoader.getString("Skills.Stats", LocaleLoader.getString(StringUtils.getCapitalized(skill.toString()) + ".Listener"), profile.getSkillLevel(skill), profile.getSkillXpLevel(skill), profile.getXpToLevel(skill));
    }

    private static void printGroupedSkillData(Player inspect, CommandSender display, String header, List<SkillType> skillGroup) {
        PlayerProfile profile = UserManager.getPlayer(inspect).getProfile();

        List<String> displayData = new ArrayList<String>();
        displayData.add(header);

        for (SkillType skill : skillGroup) {
            if (Permissions.skillEnabled(inspect, skill)) {
                displayData.add(displaySkill(profile, skill));
            }
        }

        int size = displayData.size();

        if (size > 1) {
            display.sendMessage(displayData.toArray(new String[size]));
        }
    }

}
