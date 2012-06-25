package com.gmail.nossr50.commands.general;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.runnables.SQLConversionTask;
import com.gmail.nossr50.util.Users;

public class MmoupdateCommand implements CommandExecutor {
    private final mcMMO plugin;

    public MmoupdateCommand (mcMMO plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noCommandPermissions(sender, "mcmmo.admin")) {
            return true;
        }

        sender.sendMessage(ChatColor.GRAY + "Starting conversion..."); //TODO: Needs more locale.
        Users.clearAll();
        convertToMySQL();

        for (Player x : plugin.getServer().getOnlinePlayers()) {
            Users.addUser(x);
        }

        sender.sendMessage(ChatColor.GREEN + "Conversion finished!"); //TODO: Needs more locale.

        return true;
    }

    /**
     * Convert FlatFile data to MySQL data.
     */
    private void convertToMySQL() {
        if (!Config.getInstance().getUseMySQL()) {
            return;
        }

        plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new SQLConversionTask(plugin), 1);
    }
}
