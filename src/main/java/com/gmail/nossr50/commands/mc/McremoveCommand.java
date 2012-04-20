package com.gmail.nossr50.commands.mc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

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
    String location = "plugins/mcMMO/FlatFileStuff/mcmmo.users";
    
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
            int userId = 0;
            userId = mcMMO.database.getInt("SELECT id FROM "+LoadProperties.MySQLtablePrefix+"users WHERE user = '" + playerName + "'");
            
            if(userId > 0) {
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
                sender.sendMessage("Unabled to find player named "+playerName+" in the database!");
            }
        } else {
            if(removeFlatFileUser(playerName)) {
                sender.sendMessage(ChatColor.GREEN+"[mcMMO] It worked! User was removed.");
            } else {
                sender.sendMessage(ChatColor.RED+"[mcMMO] Couldn't find the user, remember its case sensitive!");
            }
        }
        
        //Force PlayerProfile stuff to update
        if(Bukkit.getServer().getPlayer(playerName) != null && Users.players.containsKey(playerName.toLowerCase()))
        {
            Users.players.remove(playerName.toLowerCase());
            Users.addUser(Bukkit.getServer().getPlayer(playerName));
        }
        
        sender.sendMessage("[mcMMO] mcremove operation completed."); //TODO: Needs more locale.
        
        return true;
    }
    
    private boolean removeFlatFileUser(String playerName) {
        boolean worked = false;
        try {
            FileReader file = new FileReader(location);
            BufferedReader in = new BufferedReader(file);
            StringBuilder writer = new StringBuilder();
            String line = "";
            while ((line = in.readLine()) != null) {
                /* Write out the same file but when we get to the player we want to remove we skip his line */
                if(!line.split(":")[0].equalsIgnoreCase(playerName))
                {
                    writer.append(line).append("\r\n");
                } else {
                    System.out.println("User found, removing...");
                    worked = true;
                    continue; //Skip the player
                }
            }
            
            in.close();
            FileWriter out = new FileWriter(location); //Write out the new file
            out.write(writer.toString());
            out.close();
            return worked;
        } catch (Exception e) {
            Bukkit.getLogger().severe("Exception while reading " + location + " (Are you sure you formatted it correctly?)" + e.toString());
            return worked;
        }
    }
}