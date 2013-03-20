package com.gmail.nossr50.commands.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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

public class McrankCommand implements CommandExecutor {
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

                McMMOPlayer mcMMOPlayer = UserManager.getPlayer(args[0]);

                if (mcMMOPlayer == null) {
                    if (CommandUtils.inspectOffline(sender, new PlayerProfile(args[0], false), Permissions.mcrankOffline(sender))) {
                        return true;
                    }
                }
                else if (CommandUtils.tooFar(sender, mcMMOPlayer.getPlayer(), Permissions.mcrankFar(sender))) {
                    return true;
                }

                if (Config.getInstance().getUseMySQL()) {
                    sqlDisplay(sender, args[0]);
                }
                else {
                    flatfileDisplay(sender, args[0]);
                }

                return true;

            default:
                return false;
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
