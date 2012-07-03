package com.gmail.nossr50.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Skills;
import com.gmail.nossr50.util.Users;

public class CommandHelper {

    /**
     * Checks for command permissions.
     *
     * @param sender The command sender
     * @param permission The permission to check
     * @return true if the sender is a player without permissions, false otherwise
     */
    public static boolean noCommandPermissions(CommandSender sender, String permission) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!Permissions.getInstance().permission(player, permission)) {
                player.sendMessage(LocaleLoader.getString("mcMMO.NoPermission"));
                return true;
            }
        }

        return false;
    }

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
     * @param display The sender to display stats to
     */
    public static void printGatheringSkills(Player inspect, CommandSender display) {
        if (Skills.hasGatheringSkills(inspect)) {
            PlayerProfile PP = Users.getProfile(inspect);

            display.sendMessage(LocaleLoader.getString("Stats.Header.Gathering"));

            if (Permissions.getInstance().excavation(inspect)) {
                display.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Excavation.Listener"), PP.getSkillLevel(SkillType.EXCAVATION), PP.getSkillXpLevel(SkillType.EXCAVATION), PP.getXpToLevel(SkillType.EXCAVATION) }));
            }

            if (Permissions.getInstance().fishing(inspect)) {
                display.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Fishing.Listener"), PP.getSkillLevel(SkillType.FISHING), PP.getSkillXpLevel(SkillType.FISHING), PP.getXpToLevel(SkillType.FISHING) }));
            }

            if (Permissions.getInstance().herbalism(inspect)) {
                display.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Herbalism.Listener"), PP.getSkillLevel(SkillType.HERBALISM), PP.getSkillXpLevel(SkillType.HERBALISM), PP.getXpToLevel(SkillType.HERBALISM) }));
            }

            if (Permissions.getInstance().mining(inspect)) {
                display.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Mining.Listener"), PP.getSkillLevel(SkillType.MINING), PP.getSkillXpLevel(SkillType.MINING), PP.getXpToLevel(SkillType.MINING) }));
            }

            if (Permissions.getInstance().woodcutting(inspect)) {
                display.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Woodcutting.Listener"), PP.getSkillLevel(SkillType.WOODCUTTING), PP.getSkillXpLevel(SkillType.WOODCUTTING), PP.getXpToLevel(SkillType.WOODCUTTING) }));
            }
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
        if (Skills.hasCombatSkills(inspect)) {
            PlayerProfile PP = Users.getProfile(inspect);

            display.sendMessage(LocaleLoader.getString("Stats.Header.Combat"));

            if (Permissions.getInstance().axes(inspect)) {
                display.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Axes.Listener"), PP.getSkillLevel(SkillType.AXES), PP.getSkillXpLevel(SkillType.AXES), PP.getXpToLevel(SkillType.AXES) }));
            }

            if (Permissions.getInstance().archery(inspect)) {
                display.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Archery.Listener"), PP.getSkillLevel(SkillType.ARCHERY), PP.getSkillXpLevel(SkillType.ARCHERY), PP.getXpToLevel(SkillType.ARCHERY) }));
            }

            if (Permissions.getInstance().swords(inspect)) {
                display.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Swords.Listener"), PP.getSkillLevel(SkillType.SWORDS), PP.getSkillXpLevel(SkillType.SWORDS), PP.getXpToLevel(SkillType.SWORDS) }));
            }

            if (Permissions.getInstance().taming(inspect)) {
                display.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Taming.Listener"), PP.getSkillLevel(SkillType.TAMING), PP.getSkillXpLevel(SkillType.TAMING), PP.getXpToLevel(SkillType.TAMING) }));
            }

            if (Permissions.getInstance().unarmed(inspect)) {
                display.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Unarmed.Listener"), PP.getSkillLevel(SkillType.UNARMED), PP.getSkillXpLevel(SkillType.UNARMED), PP.getXpToLevel(SkillType.UNARMED) }));
            }
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
        if (Skills.hasMiscSkills(inspect)) {
            PlayerProfile PP = Users.getProfile(inspect);
            display.sendMessage(LocaleLoader.getString("Stats.Header.Misc"));

            if (Permissions.getInstance().acrobatics(inspect)) {
                display.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Acrobatics.Listener"), PP.getSkillLevel(SkillType.ACROBATICS), PP.getSkillXpLevel(SkillType.ACROBATICS), PP.getXpToLevel(SkillType.ACROBATICS) }));
            }

            if (Permissions.getInstance().repair(inspect)) {
                display.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Repair.Listener"), PP.getSkillLevel(SkillType.REPAIR), PP.getSkillXpLevel(SkillType.REPAIR), PP.getXpToLevel(SkillType.REPAIR) }));
            }
        }
    }

    public static void printMiscSkills(Player player) {
        printMiscSkills(player, player);
    }
}
