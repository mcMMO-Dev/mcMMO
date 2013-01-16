package com.gmail.nossr50.commands.mc;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.runnables.McRankAsync;
import com.gmail.nossr50.util.Leaderboard;
import com.gmail.nossr50.util.Skills;

public class McrankCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //TODO: Better input handling, add usage string

        if (!Config.getInstance().getUseMySQL()) {
            Leaderboard.updateLeaderboards(); //Make sure the information is up to date
        }

        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        Player player = (Player) sender;
        String playerName;
        switch (args.length) {
            case 0:
                playerName = player.getName();
                break;

            case 1:
                playerName = args[0];
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
        sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Player", new Object[] {playerName}));
        for (SkillType skillType : SkillType.values()) {
            int[] rankInts = Leaderboard.getPlayerRank(playerName, skillType);

            if (skillType.equals(SkillType.ALL)) {
                continue; // We want the overall ranking to be at the bottom
            }

            if (rankInts[1] == 0) {
                sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Skill", new Object[] {Skills.localizeSkillName(skillType), LocaleLoader.getString("Commands.mcrank.Unranked")} )); //Don't bother showing ranking for players without skills
            }
            else {
                sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Skill", new Object[] {Skills.localizeSkillName(skillType), String.valueOf(rankInts[0])} ));
            }
        }

        //Show the powerlevel ranking
        int[] rankInts = Leaderboard.getPlayerRank(playerName, SkillType.ALL);
        
        if (rankInts[1] == 0) {
            sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Overalll", new Object[] {LocaleLoader.getString("Commands.mcrank.Unranked")} )); //Don't bother showing ranking for players without skills
        }
        else {
            sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Overall", new Object[] {String.valueOf(rankInts[0])} ));
        }
    }

    private void sqlDisplay(CommandSender sender, String playerName) {
        Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getPluginManager().getPlugin("mcMMO"), new McRankAsync(playerName, sender));
    }
}
