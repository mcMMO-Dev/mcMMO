package com.gmail.nossr50.commands.player;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.database.Leaderboard;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.runnables.McRankAsync;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Users;

public class McrankCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
        case 0:
            if (!sender.hasPermission("mcmmo.commands.mcrank")) {
                sender.sendMessage(command.getPermissionMessage());
                return true;
            }

            if (!(sender instanceof Player)) {
                return false;
            }

            if (Config.getInstance().getUseMySQL()) {
                sqlDisplay(sender, sender.getName());
            }
            else {
                Leaderboard.updateLeaderboards(); // Make sure the information is up to date
                flatfileDisplay(sender, sender.getName());
            }

            return true;

        case 1:
            if (!sender.hasPermission("mcmmo.commands.mcrank.others")) {
                sender.sendMessage(command.getPermissionMessage());
                return true;
            }

            McMMOPlayer mcMMOPlayer = Users.getPlayer(args[0]);

            if (mcMMOPlayer == null) {
                PlayerProfile profile = new PlayerProfile(args[0], false); //Temporary Profile

                if (!profile.isLoaded()) {
                    sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
                    return true;
                }

                if (sender instanceof Player && !!sender.hasPermission("mcmmo.commands.mcrank.others.offline")) {
                    sender.sendMessage(LocaleLoader.getString("Inspect.Offline"));
                    return true;
                }
            }
            else {
                Player target = mcMMOPlayer.getPlayer();

                if (sender instanceof Player && !Misc.isNear(((Player) sender).getLocation(), target.getLocation(), 5.0) && !sender.hasPermission("mcmmo.commands.mcrank.others.far")) {
                    sender.sendMessage(LocaleLoader.getString("Inspect.TooFar"));
                    return true;
                }
            }

            if (Config.getInstance().getUseMySQL()) {
                sqlDisplay(sender, args[0]);
            }
            else {
                Leaderboard.updateLeaderboards(); // Make sure the information is up to date
                flatfileDisplay(sender, args[0]);
            }

            return true;

        default:
            return false;
        }
    }

    private void flatfileDisplay(CommandSender sender, String playerName) {
        sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Heading"));
        sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Player", playerName));

        for (SkillType skillType : SkillType.values()) {
            int[] rankInts = Leaderboard.getPlayerRank(playerName, skillType);

            if (skillType.isChildSkill()) {
                continue;
            }

            if (rankInts[1] == 0) {
                sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Skill", SkillTools.localizeSkillName(skillType), LocaleLoader.getString("Commands.mcrank.Unranked"))); // Don't bother showing ranking for players without skills
            }
            else {
                sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Skill", SkillTools.localizeSkillName(skillType), String.valueOf(rankInts[0])));
            }
        }

        // Show the powerlevel ranking
        int[] rankInts = Leaderboard.getPlayerRank(playerName);

        if (rankInts[1] == 0) {
            sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Overalll", LocaleLoader.getString("Commands.mcrank.Unranked"))); // Don't bother showing ranking for players without skills
        }
        else {
            sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Overall", String.valueOf(rankInts[0])));
        }
    }

    private void sqlDisplay(CommandSender sender, String playerName) {
        Bukkit.getScheduler().runTaskAsynchronously(mcMMO.p, new McRankAsync(playerName, sender));
    }
}
