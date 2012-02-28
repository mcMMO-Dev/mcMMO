package com.gmail.nossr50.commands.mc;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.locale.mcLocale;

public class McremoveCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = null;
        
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        if (player != null && !mcPermissions.getInstance().mcremove(player)) {
            player.sendMessage(ChatColor.YELLOW + "[mcMMO] " + ChatColor.DARK_RED + mcLocale.getString("mcPlayerListener.NoPermission"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command does not support console useage.");
            return true;
        }
        
        if(args.length == 0)
        {
            sender.sendMessage("Correct usage is /mcremove [Player Name]");
        }
        
        String playerName = args[0]; //Player that we are going to remove
        
        //If the server is using MySQL
        if(LoadProperties.useMySQL)
        {
            
            
        } else {
            
        }
        
        return true;
    }
}