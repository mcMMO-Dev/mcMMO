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
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.locale.mcLocale;

public class McremoveCommand implements CommandExecutor {
    private final String LOCATION = "plugins/mcMMO/FlatFileStuff/mcmmo.users";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String playerName;
        String usage = ChatColor.RED + "Proper usage is /mcremove <playername>"; //TODO: Needs more locale.
        String success;

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.tools.mcremove")) {
            return true;
        }

        switch (args.length) {
        case 1:
            playerName = args[0];
            success = ChatColor.GREEN + playerName + "was successfully removed from the database!"; //TODO: Locale
            break;

        default:
            sender.sendMessage(usage);
            return true;
        }

        /* MySQL */
        if (LoadProperties.useMySQL) {
            int userId = 0;
            userId = mcMMO.database.getInt("SELECT id FROM " + LoadProperties.MySQLtablePrefix + "users WHERE user = '" + playerName + "'");

            if (userId > 0) {
                mcMMO.database.write("DELETE FROM "
                        + LoadProperties.MySQLdbName + "."
                        + LoadProperties.MySQLtablePrefix + "users WHERE "
                        + LoadProperties.MySQLtablePrefix + "users.id=" + userId);

                mcMMO.database.write("DELETE FROM "
                        + LoadProperties.MySQLdbName + "."
                        + LoadProperties.MySQLtablePrefix + "cooldowns WHERE "
                        + LoadProperties.MySQLtablePrefix + "cooldowns.user_id=" + userId);

                mcMMO.database.write("DELETE FROM "
                        + LoadProperties.MySQLdbName + "."
                        + LoadProperties.MySQLtablePrefix + "huds WHERE "
                        + LoadProperties.MySQLtablePrefix + "huds.user_id=" + userId);

                mcMMO.database.write("DELETE FROM "
                        + LoadProperties.MySQLdbName + "."
                        + LoadProperties.MySQLtablePrefix + "skills WHERE "
                        + LoadProperties.MySQLtablePrefix + "skills.user_id=" + userId);

                mcMMO.database.write("DELETE FROM "
                        + LoadProperties.MySQLdbName + "."
                        + LoadProperties.MySQLtablePrefix + "experience WHERE "
                        + LoadProperties.MySQLtablePrefix + "experience.user_id=" + userId);

                sender.sendMessage(success);

            }
            else {
                sender.sendMessage(mcLocale.getString("Commands.DoesNotExist"));
            }
        }
        else {
            if (removeFlatFileUser(playerName)) {
                sender.sendMessage(success);
            }
            else {
                sender.sendMessage(mcLocale.getString("Commands.DoesNotExist"));
            }
        }

        //Force PlayerProfile stuff to update
        Player player = Bukkit.getServer().getPlayer(playerName);

        if (player != null && Users.players.containsKey(playerName.toLowerCase())) {
            Users.removeUser(player);
            Users.addUser(player);
        }

        return true;
    }

    private boolean removeFlatFileUser(String playerName) {
        boolean worked = false;

        try {
            FileReader file = new FileReader(LOCATION);
            BufferedReader in = new BufferedReader(file);
            StringBuilder writer = new StringBuilder();
            String line = "";

            while ((line = in.readLine()) != null) {

                /* Write out the same file but when we get to the player we want to remove, we skip his line. */
                if (!line.split(":")[0].equalsIgnoreCase(playerName)) {
                    writer.append(line).append("\r\n");
                }
                else {
                    System.out.println("User found, removing...");
                    worked = true;
                    continue; //Skip the player
                }
            }

            in.close();
            FileWriter out = new FileWriter(LOCATION); //Write out the new file
            out.write(writer.toString());
            out.close();

            return worked;
        }
        catch (Exception e) {
            Bukkit.getLogger().severe("Exception while reading " + LOCATION + " (Are you sure you formatted it correctly?)" + e.toString());
            return worked;
        }
    }
}
