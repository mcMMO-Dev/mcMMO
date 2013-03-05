package com.gmail.nossr50.util.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.SkillUtils;

public final class CommandUtils {
    private CommandUtils() {}

    public static boolean noConsoleUsage(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(LocaleLoader.getString("Commands.NoConsole"));
            return true;
        }

        return false;
    }

    /**
     * Print out details on Gathering skills. Only for online players.
     *
     * @param inspect The player to retrieve stats for
     * @param profile The player's profile
     * @param display The sender to display stats to
     */
    public static void printGatheringSkills(Player inspect, PlayerProfile profile, CommandSender display) {
        if (SkillUtils.hasGatheringSkills(inspect)) {
            display.sendMessage(LocaleLoader.getString("Stats.Header.Gathering"));

            if (Permissions.skillEnabled(inspect, SkillType.EXCAVATION)) {
                display.sendMessage(LocaleLoader.getString("Skills.Stats", LocaleLoader.getString("Excavation.Listener"), profile.getSkillLevel(SkillType.EXCAVATION), profile.getSkillXpLevel(SkillType.EXCAVATION), profile.getXpToLevel(SkillType.EXCAVATION)));
            }

            if (Permissions.skillEnabled(inspect, SkillType.FISHING)) {
                display.sendMessage(LocaleLoader.getString("Skills.Stats", LocaleLoader.getString("Fishing.Listener"), profile.getSkillLevel(SkillType.FISHING), profile.getSkillXpLevel(SkillType.FISHING), profile.getXpToLevel(SkillType.FISHING)));
            }

            if (Permissions.skillEnabled(inspect, SkillType.HERBALISM)) {
                display.sendMessage(LocaleLoader.getString("Skills.Stats", LocaleLoader.getString("Herbalism.Listener"), profile.getSkillLevel(SkillType.HERBALISM), profile.getSkillXpLevel(SkillType.HERBALISM), profile.getXpToLevel(SkillType.HERBALISM)));
            }

            if (Permissions.skillEnabled(inspect, SkillType.MINING)) {
                display.sendMessage(LocaleLoader.getString("Skills.Stats", LocaleLoader.getString("Mining.Listener"), profile.getSkillLevel(SkillType.MINING), profile.getSkillXpLevel(SkillType.MINING), profile.getXpToLevel(SkillType.MINING)));
            }

            if (Permissions.skillEnabled(inspect, SkillType.WOODCUTTING)) {
                display.sendMessage(LocaleLoader.getString("Skills.Stats", LocaleLoader.getString("Woodcutting.Listener"), profile.getSkillLevel(SkillType.WOODCUTTING), profile.getSkillXpLevel(SkillType.WOODCUTTING), profile.getXpToLevel(SkillType.WOODCUTTING)));
            }
        }
    }

    public static void printGatheringSkills(Player player, PlayerProfile profile) {
        printGatheringSkills(player, profile, player);
    }

    /**
     * Print out details on Combat skills. Only for online players.
     *
     * @param inspect The player to retrieve stats for
     * @param profile The player's profile
     * @param display The sender to display stats to
     */
    public static void printCombatSkills(Player inspect, PlayerProfile profile, CommandSender display) {
        if (SkillUtils.hasCombatSkills(inspect)) {
            display.sendMessage(LocaleLoader.getString("Stats.Header.Combat"));

            if (Permissions.skillEnabled(inspect, SkillType.AXES)) {
                display.sendMessage(LocaleLoader.getString("Skills.Stats", LocaleLoader.getString("Axes.Listener"), profile.getSkillLevel(SkillType.AXES), profile.getSkillXpLevel(SkillType.AXES), profile.getXpToLevel(SkillType.AXES)));
            }

            if (Permissions.skillEnabled(inspect, SkillType.ARCHERY)) {
                display.sendMessage(LocaleLoader.getString("Skills.Stats", LocaleLoader.getString("Archery.Listener"), profile.getSkillLevel(SkillType.ARCHERY), profile.getSkillXpLevel(SkillType.ARCHERY), profile.getXpToLevel(SkillType.ARCHERY)));
            }

            if (Permissions.skillEnabled(inspect, SkillType.SWORDS)) {
                display.sendMessage(LocaleLoader.getString("Skills.Stats", LocaleLoader.getString("Swords.Listener"), profile.getSkillLevel(SkillType.SWORDS), profile.getSkillXpLevel(SkillType.SWORDS), profile.getXpToLevel(SkillType.SWORDS)));
            }

            if (Permissions.skillEnabled(inspect, SkillType.TAMING)) {
                display.sendMessage(LocaleLoader.getString("Skills.Stats", LocaleLoader.getString("Taming.Listener"), profile.getSkillLevel(SkillType.TAMING), profile.getSkillXpLevel(SkillType.TAMING), profile.getXpToLevel(SkillType.TAMING)));
            }

            if (Permissions.skillEnabled(inspect, SkillType.UNARMED)) {
                display.sendMessage(LocaleLoader.getString("Skills.Stats", LocaleLoader.getString("Unarmed.Listener"), profile.getSkillLevel(SkillType.UNARMED), profile.getSkillXpLevel(SkillType.UNARMED), profile.getXpToLevel(SkillType.UNARMED)));
            }
        }
    }

    public static void printCombatSkills(Player player, PlayerProfile profile) {
        printCombatSkills(player, profile, player);
    }

    /**
     * Print out details on Misc skills. Only for online players.
     *
     * @param inspect The player to retrieve stats for
     * @param profile The player's profile
     * @param display The sender to display stats to
     */
    public static void printMiscSkills(Player inspect, PlayerProfile profile, CommandSender display) {
        if (SkillUtils.hasMiscSkills(inspect)) {
            display.sendMessage(LocaleLoader.getString("Stats.Header.Misc"));

            if (Permissions.skillEnabled(inspect, SkillType.ACROBATICS)) {
                display.sendMessage(LocaleLoader.getString("Skills.Stats", LocaleLoader.getString("Acrobatics.Listener"), profile.getSkillLevel(SkillType.ACROBATICS), profile.getSkillXpLevel(SkillType.ACROBATICS), profile.getXpToLevel(SkillType.ACROBATICS)));
            }

            if (Permissions.skillEnabled(inspect, SkillType.REPAIR)) {
                display.sendMessage(LocaleLoader.getString("Skills.Stats", LocaleLoader.getString("Repair.Listener"), profile.getSkillLevel(SkillType.REPAIR), profile.getSkillXpLevel(SkillType.REPAIR), profile.getXpToLevel(SkillType.REPAIR)));
            }
        }
    }

    public static void printMiscSkills(Player player, PlayerProfile profile) {
        printMiscSkills(player, profile, player);
    }
}
