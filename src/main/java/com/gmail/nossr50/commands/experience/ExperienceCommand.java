package com.gmail.nossr50.commands.experience;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;

import com.google.common.collect.ImmutableList;

public abstract class ExperienceCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        SkillType skill;

        switch (args.length) {
            case 2:
                if (CommandUtils.noConsoleUsage(sender)) {
                    return true;
                }

                if (!permissionsCheckSelf(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (!validateArguments(sender, args[0], args[1])) {
                    return true;
                }

                skill = SkillType.getSkill(args[0]);

                if (args[1].equalsIgnoreCase("all")) {
                    skill = null;
                }

                editValues((Player) sender, UserManager.getPlayer(sender.getName()).getProfile(), skill, Integer.parseInt(args[1]));
                return true;

            case 3:
                if (!permissionsCheckOthers(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (!validateArguments(sender, args[1], args[2])) {
                    return true;
                }

                skill = SkillType.getSkill(args[1]);

                if (args[1].equalsIgnoreCase("all")) {
                    skill = null;
                }

                int value = Integer.parseInt(args[2]);

                String playerName = CommandUtils.getMatchedPlayerName(args[0]);
                McMMOPlayer mcMMOPlayer = UserManager.getOfflinePlayer(playerName);

                // If the mcMMOPlayer doesn't exist, create a temporary profile and check if it's present in the database. If it's not, abort the process.
                if (mcMMOPlayer == null) {
                    PlayerProfile profile = mcMMO.getDatabaseManager().loadPlayerProfile(playerName, false);

                    if (CommandUtils.unloadedProfile(sender, profile)) {
                        return true;
                    }

                    editValues(null, profile, skill, value);
                }
                else {
                    editValues(mcMMOPlayer.getPlayer(), mcMMOPlayer.getProfile(), skill, value);
                }

                handleSenderMessage(sender, playerName, skill);
                return true;

            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                List<String> playerNames = CommandUtils.getOnlinePlayerNames(sender);
                return StringUtil.copyPartialMatches(args[0], playerNames, new ArrayList<String>(playerNames.size()));
            case 2:
                return StringUtil.copyPartialMatches(args[1], SkillType.SKILL_NAMES, new ArrayList<String>(SkillType.SKILL_NAMES.size()));
            default:
                return ImmutableList.of();
        }
    }

    protected abstract boolean permissionsCheckSelf(CommandSender sender);
    protected abstract boolean permissionsCheckOthers(CommandSender sender);
    protected abstract void handleCommand(Player player, PlayerProfile profile, SkillType skill, int value);
    protected abstract void handlePlayerMessageAll(Player player, int value);
    protected abstract void handlePlayerMessageSkill(Player player, int value, SkillType skill);

    private boolean validateArguments(CommandSender sender, String skillName, String value) {
        return !(CommandUtils.isInvalidInteger(sender, value) || (!skillName.equalsIgnoreCase("all") && CommandUtils.isInvalidSkill(sender, skillName)));
    }

    protected static void handleSenderMessage(CommandSender sender, String playerName, SkillType skill) {
        if (skill == null) {
            sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardAll.2", playerName));
        }
        else {
            sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardSkill.2", skill.getName(), playerName));
        }
    }

    protected void editValues(Player player, PlayerProfile profile, SkillType skill, int value) {
        if (skill == null) {
            for (SkillType skillType : SkillType.NON_CHILD_SKILLS) {
                handleCommand(player, profile, skillType, value);
            }

            if (player != null) {
                handlePlayerMessageAll(player, value);
            }
        }
        else {
            handleCommand(player, profile, skill, value);

            if (player != null) {
                handlePlayerMessageSkill(player, value, skill);
            }
        }
    }
}
