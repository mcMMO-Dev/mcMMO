package com.gmail.nossr50.commands.experience;

import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
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

/**
 * This class mirrors the structure of ExperienceCommand, except the
 * value/quantity argument is removed.
 */
public class SkillResetCommand implements TabExecutor {

    private final mcMMO pluginRef;

    public SkillResetCommand(mcMMO pluginRef) {
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
            case 1:
                if (pluginRef.getCommandTools().noConsoleUsage(sender)) {
                    return true;
                }

                if (!permissionsCheckSelf(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (!validateArguments(sender, args[0])) {
                    return true;
                }

                if (args[0].equalsIgnoreCase("all")) {
                    skill = null;
                } else {
                    skill = pluginRef.getSkillTools().matchSkill(args[1]);
                }

                editValues((Player) sender, pluginRef.getUserManager().getPlayer(sender.getName()).getProfile(), skill);
                return true;

            case 2:
                if (!permissionsCheckOthers(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (!validateArguments(sender, args[1])) {
                    return true;
                }

                if (args[1].equalsIgnoreCase("all")) {
                    skill = null;
                } else {
                    skill = pluginRef.getSkillTools().matchSkill(args[1]);
                }

                String playerName = pluginRef.getCommandTools().getMatchedPlayerName(args[0]);
                McMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getOfflinePlayer(playerName);

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

                    editValues(null, profile, skill);
                } else {
                    editValues(mcMMOPlayer.getPlayer(), mcMMOPlayer.getProfile(), skill);
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

    protected void handleCommand(Player player, PlayerProfile profile, PrimarySkillType skill) {
        int levelsRemoved = profile.getSkillLevel(skill);
        double xpRemoved = profile.getSkillXpLevelRaw(skill);

        profile.modifySkill(skill, 0);

        if (player == null) {
            profile.scheduleAsyncSave();
            return;
        }

        pluginRef.getEventManager().tryLevelChangeEvent(player, skill, levelsRemoved, xpRemoved, false, XPGainReason.COMMAND);
    }

    protected boolean permissionsCheckSelf(CommandSender sender) {
        return Permissions.skillreset(sender);
    }

    protected boolean permissionsCheckOthers(CommandSender sender) {
        return Permissions.skillresetOthers(sender);
    }

    protected void handlePlayerMessageAll(Player player) {
        player.sendMessage(pluginRef.getLocaleManager().getString("Commands.Reset.All"));
    }

    protected void handlePlayerMessageSkill(Player player, PrimarySkillType skill) {
        player.sendMessage(pluginRef.getLocaleManager().getString("Commands.Reset.Single", pluginRef.getSkillTools().getLocalizedSkillName(skill)));
    }

    private boolean validateArguments(CommandSender sender, String skillName) {
        return skillName.equalsIgnoreCase("all") || !pluginRef.getCommandTools().isInvalidSkill(sender, skillName);
    }

    protected void editValues(Player player, PlayerProfile profile, PrimarySkillType skill) {
        if (skill == null) {
            for (PrimarySkillType primarySkillType : pluginRef.getSkillTools().NON_CHILD_SKILLS) {
                handleCommand(player, profile, primarySkillType);
            }

            if (player != null) {
                handlePlayerMessageAll(player);
            }
        } else {
            handleCommand(player, profile, skill);

            if (player != null) {
                handlePlayerMessageSkill(player, skill);
            }
        }
    }
}
