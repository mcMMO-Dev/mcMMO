package com.gmail.nossr50.commands.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.database.LeaderboardManager;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.runnables.commands.McrankCommandAsyncTask;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillUtils;

import com.google.common.collect.ImmutableList;

public class McrankCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 0:
                if (CommandUtils.noConsoleUsage(sender)) {
                    return true;
                }

                if (!Permissions.mcrank(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (Config.getInstance().getUseMySQL()) {
                    sqlDisplay(sender, sender.getName());
                }
                else {
                    flatfileDisplay(sender, sender.getName());
                }

                return true;

            case 1:
                if (!Permissions.mcrankOthers(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                String playerName = args[0];
                McMMOPlayer mcMMOPlayer = UserManager.getPlayer(playerName);

                if (mcMMOPlayer != null) {
                    playerName = mcMMOPlayer.getPlayer().getName();

                    if (CommandUtils.tooFar(sender, mcMMOPlayer.getPlayer(), Permissions.mcrankFar(sender))) {
                        return true;
                    }

                } else if (CommandUtils.inspectOffline(sender, new PlayerProfile(playerName, false), Permissions.mcrankOffline(sender))) {
                    return true;
                }

                if (Config.getInstance().getUseMySQL()) {
                    sqlDisplay(sender, playerName);
                }
                else {
                    flatfileDisplay(sender, playerName);
                }

                return true;

            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                Set<String> playerNames = UserManager.getPlayers().keySet();
                return StringUtil.copyPartialMatches(args[0], playerNames, new ArrayList<String>(playerNames.size()));
            default:
                return ImmutableList.of();
        }
    }

    private void flatfileDisplay(CommandSender sender, String playerName) {
        LeaderboardManager.updateLeaderboards(); // Make sure the information is up to date

        sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Heading"));
        sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Player", playerName));

        for (SkillType skillType : SkillType.values()) {
            int[] rankInts = LeaderboardManager.getPlayerRank(playerName, skillType);

            if (!Permissions.skillEnabled(sender, skillType) || skillType.isChildSkill()) {
                continue;
            }

            if (rankInts[1] == 0) {
                sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Skill", SkillUtils.getSkillName(skillType), LocaleLoader.getString("Commands.mcrank.Unranked"))); // Don't bother showing ranking for players without skills
            }
            else {
                sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Skill", SkillUtils.getSkillName(skillType), rankInts[0]));
            }
        }

        // Show the powerlevel ranking
        int[] rankInts = LeaderboardManager.getPlayerRank(playerName);

        if (rankInts[1] == 0) {
            sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Overall", LocaleLoader.getString("Commands.mcrank.Unranked"))); // Don't bother showing ranking for players without skills
        }
        else {
            sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Overall", rankInts[0]));
        }
    }

    private void sqlDisplay(CommandSender sender, String playerName) {
        new McrankCommandAsyncTask(playerName, sender).runTaskAsynchronously(mcMMO.p);
    }
}
