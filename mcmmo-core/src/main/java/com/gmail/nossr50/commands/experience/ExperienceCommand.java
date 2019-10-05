package com.gmail.nossr50.commands.experience;

import com.gmail.nossr50.datatypes.player.BukkitMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.google.common.collect.ImmutableList;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class ExperienceCommand implements TabExecutor {

    protected mcMMO pluginRef;

    public ExperienceCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    protected void handleSenderMessage(CommandSender sender, String playerName, PrimarySkillType skill) {
        if (skill == null) {
            sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.addlevels.AwardAll.2", playerName));
        } else {
            sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.addlevels.AwardSkill.2", pluginRef.getSkillTools().getLocalizedSkillName(skill), playerName));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PrimarySkillType skill;

        switch (args.length) {
            case 2:
                if (pluginRef.getCommandTools().noConsoleUsage(sender)) {
                    return true;
                }

                if (!permissionsCheckSelf(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (!validateArguments(sender, args[0], args[1])) {
                    return true;
                }

                skill = pluginRef.getSkillTools().matchSkill(args[0]);

                if (args[1].equalsIgnoreCase("all")) {
                    skill = null;
                }

                if (skill != null && pluginRef.getSkillTools().isChildSkill(skill)) {
                    sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Skill.ChildSkill"));
                    return true;
                }

                //Profile not loaded
                if (pluginRef.getUserManager().getPlayer(sender.getName()) == null) {
                    sender.sendMessage(pluginRef.getLocaleManager().getString("Profile.PendingLoad"));
                    return true;
                }


                editValues((Player) sender, pluginRef.getUserManager().getPlayer(sender.getName()).getProfile(), skill, Integer.parseInt(args[1]));
                return true;

            case 3:
                if (!permissionsCheckOthers(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (!validateArguments(sender, args[1], args[2])) {
                    return true;
                }

                skill = pluginRef.getSkillTools().matchSkill(args[1]);

                if (args[1].equalsIgnoreCase("all")) {
                    skill = null;
                }

                if (skill != null && pluginRef.getSkillTools().isChildSkill(skill)) {
                    sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Skill.ChildSkill"));
                    return true;
                }

                int value = Integer.parseInt(args[2]);

                String playerName = pluginRef.getCommandTools().getMatchedPlayerName(args[0]);
                BukkitMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getOfflinePlayer(playerName);

                // If the mcMMOPlayer doesn't exist, create a temporary profile and check if it's present in the database. If it's not, abort the process.
                if (mcMMOPlayer == null) {
                    UUID uuid = null;
                    OfflinePlayer player = pluginRef.getServer().getOfflinePlayer(playerName);
                    if (player != null) {
                        uuid = player.getUniqueId();
                    }
                    PlayerProfile profile = pluginRef.getDatabaseManager().loadPlayerProfile(playerName, uuid, false);

                    if (pluginRef.getCommandTools().unloadedProfile(sender, profile)) {
                        return true;
                    }

                    editValues(null, profile, skill, value);
                } else {
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
                List<String> playerNames = pluginRef.getCommandTools().getOnlinePlayerNames(sender);
                return StringUtil.copyPartialMatches(args[0], playerNames, new ArrayList<>(playerNames.size()));
            case 2:
                return StringUtil.copyPartialMatches(args[1], pluginRef.getSkillTools().LOCALIZED_SKILL_NAMES, new ArrayList<>(pluginRef.getSkillTools().LOCALIZED_SKILL_NAMES.size()));
            default:
                return ImmutableList.of();
        }
    }

    protected abstract boolean permissionsCheckSelf(CommandSender sender);

    protected abstract boolean permissionsCheckOthers(CommandSender sender);

    protected abstract void handleCommand(Player player, PlayerProfile profile, PrimarySkillType skill, int value);

    protected abstract void handlePlayerMessageAll(Player player, int value);

    protected abstract void handlePlayerMessageSkill(Player player, int value, PrimarySkillType skill);

    private boolean validateArguments(CommandSender sender, String skillName, String value) {
        return !(pluginRef.getCommandTools().isInvalidInteger(sender, value) || (!skillName.equalsIgnoreCase("all") && pluginRef.getCommandTools().isInvalidSkill(sender, skillName)));
    }

    protected void editValues(Player player, PlayerProfile profile, PrimarySkillType skill, int value) {
        if (skill == null) {
            for (PrimarySkillType primarySkillType : pluginRef.getSkillTools().NON_CHILD_SKILLS) {
                handleCommand(player, profile, primarySkillType, value);
            }

            if (player != null) {
                handlePlayerMessageAll(player, value);
            }
        } else {
            handleCommand(player, profile, skill, value);

            if (player != null) {
                handlePlayerMessageSkill(player, value, skill);
            }
        }
    }
}
