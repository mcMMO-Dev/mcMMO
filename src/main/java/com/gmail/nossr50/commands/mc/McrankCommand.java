package com.gmail.nossr50.commands.mc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.runnables.mcRankAsync;
import com.gmail.nossr50.util.Leaderboard;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Skills;

public class McrankCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	if(!Config.getInstance().getUseMySQL())
    		Leaderboard.updateLeaderboards(); //Make sure the information is up to date

        if(sender instanceof Player) {
            Player player = (Player) sender;
            String playerName;
            switch(args.length){
                case 0:
                    playerName = player.getName();
                    break;
                case 1:
                    playerName = args[0];
                    break;
                default:
                    return false;
            }
            sender.sendMessage(ChatColor.GOLD + "-=PERSONAL RANKINGS=-");
            sender.sendMessage(ChatColor.RED+"TARGET: "+ChatColor.WHITE+playerName);

            if(Config.getInstance().getUseMySQL()) {
                sqlDisplay(sender, playerName);
            } else {
                flatfileDisplay(sender, playerName);
            }

        } else {
            sender.sendMessage("Command currently not supported for console.");
        }

        return true;
    }

    public void flatfileDisplay(CommandSender sender, String playerName) {
        for (SkillType skillType : SkillType.values()) {
        	
        	int[] rankInts = Leaderboard.getPlayerRank(playerName, skillType);
        	
            if (skillType.equals(SkillType.ALL))
                continue; // We want the overall ranking to be at the bottom
            
            if (rankInts[1] == 0) {
                sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Skill", new Object[] {Skills.localizeSkillName(skillType), LocaleLoader.getString("Commands.mcrank.Unranked")} )); //Don't bother showing ranking for players without skills
            } else {
                sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Skill", new Object[] {Skills.localizeSkillName(skillType), String.valueOf(rankInts[0])} ));
            }
        }
        
        //Show the powerlevel ranking
        int[] rankInts = Leaderboard.getPlayerRank(playerName, SkillType.ALL);
        
        if (rankInts[1] == 0) {
            sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Skill", new Object[] {Skills.localizeSkillName(SkillType.ALL), LocaleLoader.getString("Commands.mcrank.Unranked")} )); //Don't bother showing ranking for players without skills
        } else {
            sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Skill", new Object[] {Skills.localizeSkillName(SkillType.ALL), String.valueOf(rankInts[0])} ));
        }
        
        sender.sendMessage(ChatColor.YELLOW + "Overall" + ChatColor.GREEN + " - " + ChatColor.GOLD + "Rank " + ChatColor.WHITE + "#" + ChatColor.GREEN + Leaderboard.getPlayerRank(playerName, SkillType.ALL));
    }


    private void sqlDisplay(CommandSender sender, String playerName) {
        Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getPluginManager().getPlugin("mcMMO"), new mcRankAsync(playerName, sender));
    }
}
