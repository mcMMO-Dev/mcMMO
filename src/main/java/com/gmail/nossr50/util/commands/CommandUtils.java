package com.gmail.nossr50.util.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillUtils;

public final class CommandUtils {
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
        if (sender instanceof Player && !Misc.isNear(((Player) sender).getLocation(), target.getLocation(), 5.0) && !hasPermission) {
            sender.sendMessage(LocaleLoader.getString("Inspect.TooFar"));
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

    public static boolean isOffline(CommandSender sender, Player player) {
        if (player.isOnline()) {
            return false;
        }

        sender.sendMessage(LocaleLoader.getString("Commands.Offline"));
        return true;
    }

    public static boolean checkPlayerExistence(CommandSender sender, String playerName, McMMOPlayer mcMMOPlayer) {
        if (mcMMOPlayer != null) {
            return false;
        }

        PlayerProfile playerProfile = new PlayerProfile(playerName, false);

        if (unloadedProfile(sender, playerProfile)) {
            return true;
        }

        sender.sendMessage(LocaleLoader.getString("Commands.Offline"));
        return true;
    }

    public static boolean unloadedProfile(CommandSender sender, PlayerProfile profile) {
        if (profile.isLoaded()) {
            return false;
        }

        sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
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
        if (SkillUtils.hasGatheringSkills(inspect)) {
            PlayerProfile profile = UserManager.getPlayer(inspect).getProfile();

            display.sendMessage(LocaleLoader.getString("Stats.Header.Gathering"));
            displaySkill(inspect, profile, SkillType.EXCAVATION, display);
            displaySkill(inspect, profile, SkillType.FISHING, display);
            displaySkill(inspect, profile, SkillType.HERBALISM, display);
            displaySkill(inspect, profile, SkillType.MINING, display);
            displaySkill(inspect, profile, SkillType.WOODCUTTING, display);
        }
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
        if (SkillUtils.hasCombatSkills(inspect)) {
            PlayerProfile profile = UserManager.getPlayer(inspect).getProfile();

            display.sendMessage(LocaleLoader.getString("Stats.Header.Combat"));
            displaySkill(inspect, profile, SkillType.AXES, display);
            displaySkill(inspect, profile, SkillType.ARCHERY, display);
            displaySkill(inspect, profile, SkillType.SWORDS, display);
            displaySkill(inspect, profile, SkillType.TAMING, display);
            displaySkill(inspect, profile, SkillType.UNARMED, display);
        }
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
        if (SkillUtils.hasMiscSkills(inspect)) {
            PlayerProfile profile = UserManager.getPlayer(inspect).getProfile();

            display.sendMessage(LocaleLoader.getString("Stats.Header.Misc"));
            displaySkill(inspect, profile, SkillType.ACROBATICS, display);
            displaySkill(inspect, profile, SkillType.REPAIR, display);
        }
    }

    public static void printMiscSkills(Player player) {
        printMiscSkills(player, player);
    }

    private static void displaySkill(Player player, PlayerProfile profile, SkillType skill, CommandSender display) {
        if (Permissions.skillEnabled(player, skill)) {
            displaySkill(display, profile, skill);
        }
    }

    public static void displaySkill(CommandSender sender, PlayerProfile profile, SkillType skill) {
        sender.sendMessage(LocaleLoader.getString("Skills.Stats", LocaleLoader.getString(StringUtils.getCapitalized(skill.toString()) + ".Listener"), profile.getSkillLevel(skill), profile.getSkillXpLevel(skill), profile.getXpToLevel(skill)));
    }
}
