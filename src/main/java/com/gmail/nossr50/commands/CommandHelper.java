package com.gmail.nossr50.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.skills.Skills;

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

            if (player != null && !mcPermissions.getInstance().permission(player, permission)) {
                player.sendMessage(mcLocale.getString("mcPlayerListener.NoPermission"));
                return true;
            }
        }

        return false;
    }

    /**
     * Print out details on Gathering skills. Only for online players.
     *
     * @param inspect The player to retrieve stats for
     * @param display The sender to display stats to
     * @param online true if the player to retrieve stats for is online, false otherwise
     */
    public static void printGatheringSkills(Player inspect, CommandSender display) {
        if (Skills.hasGatheringSkills(inspect)) {
            PlayerProfile PP = Users.getProfile(inspect);

            display.sendMessage(mcLocale.getString("Stats.GatheringHeader"));

            if (mcPermissions.getInstance().excavation(inspect)) {
                display.sendMessage(mcLocale.getString("m.SkillStats", new Object[] { mcLocale.getString("mcPlayerListener.ExcavationSkill"), PP.getSkillLevel(SkillType.EXCAVATION), PP.getSkillXpLevel(SkillType.EXCAVATION), PP.getXpToLevel(SkillType.EXCAVATION) }));
            }

            if (mcPermissions.getInstance().fishing(inspect)) {
                display.sendMessage(mcLocale.getString("m.SkillStats", new Object[] { mcLocale.getString("mcPlayerListener.FishingSkill"), PP.getSkillLevel(SkillType.FISHING), PP.getSkillXpLevel(SkillType.FISHING), PP.getXpToLevel(SkillType.FISHING) }));
            }

            if (mcPermissions.getInstance().herbalism(inspect)) {
                display.sendMessage(mcLocale.getString("m.SkillStats", new Object[] { mcLocale.getString("mcPlayerListener.HerbalismSkill"), PP.getSkillLevel(SkillType.HERBALISM), PP.getSkillXpLevel(SkillType.HERBALISM), PP.getXpToLevel(SkillType.HERBALISM) }));
            }

            if (mcPermissions.getInstance().mining(inspect)) {
                display.sendMessage(mcLocale.getString("m.SkillStats", new Object[] { mcLocale.getString("mcPlayerListener.MiningSkill"), PP.getSkillLevel(SkillType.MINING), PP.getSkillXpLevel(SkillType.MINING), PP.getXpToLevel(SkillType.MINING) }));
            }

            if (mcPermissions.getInstance().woodcutting(inspect)) {
                display.sendMessage(mcLocale.getString("m.SkillStats", new Object[] { mcLocale.getString("mcPlayerListener.WoodcuttingSkill"), PP.getSkillLevel(SkillType.WOODCUTTING), PP.getSkillXpLevel(SkillType.WOODCUTTING), PP.getXpToLevel(SkillType.WOODCUTTING) }));
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
     * @param online true if the player to retrieve stats for is online, false otherwise
     */
    public static void printCombatSkills(Player inspect, CommandSender display) {
        if (Skills.hasCombatSkills(inspect)) {
            PlayerProfile PP = Users.getProfile(inspect);

            display.sendMessage(mcLocale.getString("Stats.CombatHeader"));

            if (mcPermissions.getInstance().axes(inspect)) {
                display.sendMessage(mcLocale.getString("m.SkillStats", new Object[] { mcLocale.getString("mcPlayerListener.AxesSkill"), PP.getSkillLevel(SkillType.AXES), PP.getSkillXpLevel(SkillType.AXES), PP.getXpToLevel(SkillType.AXES) }));
            }

            if (mcPermissions.getInstance().archery(inspect)) {
                display.sendMessage(mcLocale.getString("m.SkillStats", new Object[] { mcLocale.getString("mcPlayerListener.ArcherySkill"), PP.getSkillLevel(SkillType.ARCHERY), PP.getSkillXpLevel(SkillType.ARCHERY), PP.getXpToLevel(SkillType.ARCHERY) }));
            }

            if (mcPermissions.getInstance().swords(inspect)) {
                display.sendMessage(mcLocale.getString("m.SkillStats", new Object[] { mcLocale.getString("mcPlayerListener.SwordsSkill"), PP.getSkillLevel(SkillType.SWORDS), PP.getSkillXpLevel(SkillType.SWORDS), PP.getXpToLevel(SkillType.SWORDS) }));
            }

            if (mcPermissions.getInstance().taming(inspect)) {
                display.sendMessage(mcLocale.getString("m.SkillStats", new Object[] { mcLocale.getString("mcPlayerListener.TamingSkill"), PP.getSkillLevel(SkillType.TAMING), PP.getSkillXpLevel(SkillType.TAMING), PP.getXpToLevel(SkillType.TAMING) }));
            }

            if (mcPermissions.getInstance().unarmed(inspect)) {
                display.sendMessage(mcLocale.getString("m.SkillStats", new Object[] { mcLocale.getString("mcPlayerListener.UnarmedSkill"), PP.getSkillLevel(SkillType.UNARMED), PP.getSkillXpLevel(SkillType.UNARMED), PP.getXpToLevel(SkillType.UNARMED) }));
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
     * @param online true if the player to retrieve stats for is online, false otherwise
     */
    public static void printMiscSkills(Player inspect, CommandSender display) {
        if (Skills.hasMiscSkills(inspect)) {
            PlayerProfile PP = Users.getProfile(inspect);
            display.sendMessage(mcLocale.getString("Stats.MiscHeader"));

            if (mcPermissions.getInstance().acrobatics(inspect)) {
                display.sendMessage(mcLocale.getString("m.SkillStats", new Object[] { mcLocale.getString("mcPlayerListener.AcrobaticsSkill"), PP.getSkillLevel(SkillType.ACROBATICS), PP.getSkillXpLevel(SkillType.ACROBATICS), PP.getXpToLevel(SkillType.ACROBATICS) }));
            }

            if (mcPermissions.getInstance().repair(inspect)) {
                display.sendMessage(mcLocale.getString("m.SkillStats", new Object[] { mcLocale.getString("mcPlayerListener.RepairSkill"), PP.getSkillLevel(SkillType.REPAIR), PP.getSkillXpLevel(SkillType.REPAIR), PP.getXpToLevel(SkillType.REPAIR) }));
            }
        }
    }

    public static void printMiscSkills(Player player) {
        printMiscSkills(player, player);
    }
}
