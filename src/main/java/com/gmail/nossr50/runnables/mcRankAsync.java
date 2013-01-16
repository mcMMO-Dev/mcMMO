package com.gmail.nossr50.runnables;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.util.Database;
import com.gmail.nossr50.util.Misc;

public class mcRankAsync implements Runnable {
    private Database database = mcMMO.getPlayerDatabase();
	private final String playerName;
	private final CommandSender sender;

    public mcRankAsync(String playerName, CommandSender sender) {
        this.playerName = playerName;
		this.sender = sender;
    }

    @Override
    public void run() {
        final Map<String, Integer> skills = database.readSQLRank(playerName);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("mcMMO"), new Runnable() {

            @Override
            public void run() {
                for (SkillType skillType : SkillType.values()) {
                    if (skillType.equals(SkillType.ALL))
                        continue; // We want the overall ranking to be at the bottom
                    sender.sendMessage(ChatColor.YELLOW + Misc.getCapitalized(skillType.name()) + ChatColor.GREEN + " - " + (skills.get(skillType.name()) == null ? ChatColor.WHITE + "Unranked" : ChatColor.GOLD + "Rank " + ChatColor.WHITE + "#" + ChatColor.GREEN + skills.get(skillType.name())));
                }
                sender.sendMessage(ChatColor.YELLOW + "Overall" + ChatColor.GREEN + " - " + (skills.get("ALL") == null ? ChatColor.WHITE + "Unranked" : ChatColor.GOLD + "Rank " + ChatColor.WHITE + "#" + ChatColor.GREEN + skills.get("ALL")));
            }
            
            
            
        }, 1L);
    }
}