package com.gmail.nossr50.commands.experience;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.google.common.collect.ImmutableList;

public class SkillresetCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1:
                if (CommandUtils.noConsoleUsage(sender)) {
                    return true;
                }

                if (!Permissions.skillreset(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (CommandUtils.isInvalidSkill(sender, args[0])) {
                    return true;
                }

                editValues((Player) sender, UserManager.getPlayer(sender.getName()).getProfile(), SkillType.getSkill(args[0]), args.length, sender, command);
                return true;

            case 2:
                if (!Permissions.skillresetOthers(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (CommandUtils.isInvalidSkill(sender, args[1])) {
                    return true;
                }

                SkillType skill = SkillType.getSkill(args[1]);

                String playerName = Misc.getMatchedPlayerName(args[0]);
                McMMOPlayer mcMMOPlayer = UserManager.getPlayer(playerName, true);

                // If the mcMMOPlayer doesn't exist, create a temporary profile and check if it's present in the database. If it's not, abort the process.
                if (mcMMOPlayer == null) {
                    PlayerProfile profile = mcMMO.getDatabaseManager().loadPlayerProfile(playerName, false);

                    if (CommandUtils.unloadedProfile(sender, profile)) {
                        return true;
                    }

                    editValues(null, profile, skill, args.length, sender, command);
                }
                else {
                    editValues(mcMMOPlayer.getPlayer(), mcMMOPlayer.getProfile(), skill, args.length, sender, command);
                }

                ExperienceCommand.handleSenderMessage(sender, playerName, skill);
                return true;

            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                Set<String> playerNames = UserManager.getPlayerNames();
                return StringUtil.copyPartialMatches(args[0], playerNames, new ArrayList<String>(playerNames.size()));
            case 2:
                return StringUtil.copyPartialMatches(args[1], SkillType.SKILL_NAMES, new ArrayList<String>(SkillType.SKILL_NAMES.size()));
            default:
                return ImmutableList.of();
        }
    }

    private void editValues(Player player, PlayerProfile profile, SkillType skill, int argsLength, CommandSender sender, Command command) {
        if (skill == null) {
            for (SkillType skillType : SkillType.values()) {
                handleCommand(player, profile, skillType, argsLength, sender, command);
            }

            if (player != null) {
                player.sendMessage(LocaleLoader.getString("Commands.Reset.All"));
            }
        }
        else {
            handleCommand(player, profile, skill, argsLength, sender, command);

            if (player != null) {
                player.sendMessage(LocaleLoader.getString("Commands.Reset.Single", skill.getName()));
            }
        }
    }

    private void handleCommand(Player player, PlayerProfile profile, SkillType skill, int argsLength, CommandSender sender, Command command) {
        if (argsLength == 1 && !Permissions.skillreset(sender, skill) || (argsLength == 2 && !Permissions.skillresetOthers(sender, skill))) {
            sender.sendMessage(command.getPermissionMessage());
            return;
        }

        int levelsRemoved = profile.getSkillLevel(skill);
        float xpRemoved = profile.getSkillXpLevelRaw(skill);

        profile.modifySkill(skill, 0);

        if (player == null) {
            profile.save();
            return;
        }

        EventUtils.handleLevelChangeEvent(player, skill, levelsRemoved, xpRemoved, false);
    }
}
