package com.gmail.nossr50.commands.player;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.runnables.McRankAsync;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Leaderboard;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class McrankCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // TODO: Better input handling, add usage string
        if (!Config.getInstance().getUseMySQL()) {
            Leaderboard.updateLeaderboards(); // Make sure the information is up to date
        }

        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.commands.mcrank")) {
            return true;
        }

        Player player = (Player) sender;
        String playerName;

        switch (args.length) {
        case 0:
            playerName = player.getName();
            break;

        case 1:
            if (CommandHelper.noCommandPermissions(sender, "mcmmo.commands.mcrank.others")) {
                return true;
            }

            playerName = args[0];
            McMMOPlayer mcmmoPlayer = Users.getPlayer(playerName);

            if (mcmmoPlayer != null) {
                Player target = mcmmoPlayer.getPlayer();

                if (sender instanceof Player && !Misc.isNear(((Player) sender).getLocation(), target.getLocation(), 5.0) && !Permissions.hasPermission(sender, "mcmmo.commands.mcrank.others.far")) {
                    sender.sendMessage(LocaleLoader.getString("Inspect.TooFar"));
                    return true;
                }
            }
            else if (sender instanceof Player && !Permissions.hasPermission(sender, "mcmmo.commands.mcrank.others.offline")) {
                sender.sendMessage(LocaleLoader.getString("Inspect.Offline"));
                return true;
            }

            break;

        default:
            return false;
        }

        if (Config.getInstance().getUseMySQL()) {
            sqlDisplay(sender, playerName);
        }
        else {
            flatfileDisplay(sender, playerName);
        }

        return true;
    }

    public void flatfileDisplay(CommandSender sender, String playerName) {
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
