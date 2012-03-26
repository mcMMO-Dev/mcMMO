package com.gmail.nossr50.commands.mc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;
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
        
        if(args.length == 0)
        {
            sender.sendMessage("Correct usage is /mcremove [Player Name]"); //TODO: Needs more locale.
            return true;
        }
        
        String playerName = args[0]; //Player that we are going to remove
        
        //If the server is using MySQL
        if(LoadProperties.useMySQL)
        {
            int userId = mcMMO.database.getInt("SELECT id FROM "+LoadProperties.MySQLtablePrefix+"users WHERE user = '" + playerName + "'");
            
            //Remove user from tables
            mcMMO.database.write("DELETE FROM "
                    +LoadProperties.MySQLdbName+"."
                    +LoadProperties.MySQLtablePrefix+"users WHERE "
                    +LoadProperties.MySQLtablePrefix+"users.id="+userId);
            
            mcMMO.database.write("DELETE FROM "
                    +LoadProperties.MySQLdbName+"."
                    +LoadProperties.MySQLtablePrefix+"cooldowns WHERE "
                    +LoadProperties.MySQLtablePrefix+"cooldowns.user_id="+userId);
            
            mcMMO.database.write("DELETE FROM "
                    +LoadProperties.MySQLdbName+"."
                    +LoadProperties.MySQLtablePrefix+"huds WHERE "
                    +LoadProperties.MySQLtablePrefix+"huds.user_id="+userId);
            
            mcMMO.database.write("DELETE FROM "
                    +LoadProperties.MySQLdbName+"."
                    +LoadProperties.MySQLtablePrefix+"skills WHERE "
                    +LoadProperties.MySQLtablePrefix+"skills.user_id="+userId);
            
            mcMMO.database.write("DELETE FROM "
            +LoadProperties.MySQLdbName+"."
            +LoadProperties.MySQLtablePrefix+"experience WHERE "
            +LoadProperties.MySQLtablePrefix+"experience.user_id="+userId);

            sender.sendMessage("User "+playerName+" removed from MySQL DB!"); //TODO: Needs more locale.
        } else {
            //FlatFile removal
            //TODO: Properly remove users from FlatFile, it's going to be a huge bitch with how our FlatFile system works. Let's adopt SQLite support.
            if(Bukkit.getServer().getPlayer(playerName) != null)
            {
                Player targetPlayer = Bukkit.getServer().getPlayer(playerName);
                if(targetPlayer.isOnline()) 
                {
                    Users.getProfile(targetPlayer).resetAllData();
                    sender.sendMessage("User "+playerName+" removed from FlatFile DB!"); //TODO: Needs more locale.
                } else {
                    sender.sendMessage("[mcMMO] This command is not fully functional for FlatFile yet, the player needs to be online."); //TODO: Needs more locale.
                    return true;
                }
            } else {
                sender.sendMessage("[mcMMO] This command is not fully functional for FlatFile yet, the player needs to be online."); //TODO: Needs more locale.
                return true;
            }
        }
        
        //Force PlayerProfile stuff to update
        if(Bukkit.getServer().getPlayer(playerName) != null)
        {
            Player targetPlayer = Bukkit.getServer().getPlayer(playerName);
            if(targetPlayer.isOnline())
            {
                targetPlayer.kickPlayer("[mcMMO] Stats have been reset! Rejoin!"); //TODO: Needs more locale.
                Users.removeUserByName(playerName);
            } else {
                Users.removeUser(targetPlayer);
            }
        } else {
            Users.removeUserByName(playerName);
        }
        
        sender.sendMessage("[mcMMO] mcremove operation completed."); //TODO: Needs more locale.
        
        return true;
    }
}