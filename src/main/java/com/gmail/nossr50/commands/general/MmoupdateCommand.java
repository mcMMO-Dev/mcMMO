package com.gmail.nossr50.commands.general;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.runnables.SQLConversionTask;

public class MmoupdateCommand implements CommandExecutor {
<<<<<<< HEAD
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	    Player player = null;
	    
	    if(sender instanceof Player) {
	        player = (Player)sender;
	    }
	    
		if (sender instanceof Player && !mcPermissions.getInstance().admin(player)) {
			sender.sendMessage(ChatColor.YELLOW + "[mcMMO] " + ChatColor.DARK_RED + mcLocale.getString("mcPlayerListener.NoPermission"));
			return true;
		}
		
		sender.sendMessage(ChatColor.GRAY + "Starting conversion..."); //TODO: Needs more locale.
		Users.clearUsers();
		convertToMySQL();
		for (Player x : Bukkit.getServer().getOnlinePlayers()) {
			Users.addUser(x);
		}
		sender.sendMessage(ChatColor.GREEN + "Conversion finished!"); //TODO: Needs more locale.

		return true;
	}
	
	/**
=======

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.admin")) {
            return true;
        }

        sender.sendMessage(ChatColor.GRAY + "Starting conversion..."); //TODO: Needs more locale.
        Users.clearUsers();
        convertToMySQL();

        for (Player x : Bukkit.getServer().getOnlinePlayers()) {
            Users.addUser(x);
        }

        sender.sendMessage(ChatColor.GREEN + "Conversion finished!"); //TODO: Needs more locale.

        return true;
    }

    /**
>>>>>>> d9b4647cf5b277ae33d20e4e78ca67e1712f1ec7
     * Convert FlatFile data to MySQL data.
     */
    private void convertToMySQL() {
        if (!LoadProperties.useMySQL) {
            return;
        }

        Bukkit.getScheduler().scheduleAsyncDelayedTask(Bukkit.getPluginManager().getPlugin("mcMMO"), new SQLConversionTask(), 1);
    }
}
