package com.gmail.nossr50.commands.general;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.runnables.SQLConversionTask;

public class MmoupdateCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command does not support console useage."); //TODO: Needs more locale.
			return true;
		}

		Player player = (Player) sender;

		if (!mcPermissions.getInstance().admin(player)) {
			player.sendMessage(ChatColor.YELLOW + "[mcMMO] " + ChatColor.DARK_RED + mcLocale.getString("mcPlayerListener.NoPermission"));
			return true;
		}
		player.sendMessage(ChatColor.GRAY + "Starting conversion..."); //TODO: Needs more locale.
		Users.clearUsers();
		convertToMySQL();
		for (Player x : Bukkit.getServer().getOnlinePlayers()) {
			Users.addUser(x);
		}
		player.sendMessage(ChatColor.GREEN + "Conversion finished!"); //TODO: Needs more locale.

		return true;
	}
	
	/**
     * Convert FlatFile data to MySQL data.
     */
    private void convertToMySQL() {
        if (!LoadProperties.useMySQL) {
            return;
        }

        Bukkit.getScheduler().scheduleAsyncDelayedTask(Bukkit.getPluginManager().getPlugin("mcMMO"), new SQLConversionTask(), 1);
    }
}