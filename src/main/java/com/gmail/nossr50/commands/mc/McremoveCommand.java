package com.gmail.nossr50.commands.mc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Database;

public class McremoveCommand implements CommandExecutor {
    private final String location;
    private final mcMMO plugin;

    public McremoveCommand (mcMMO plugin) {
        this.plugin = plugin;
        this.location = mcMMO.getUsersFile();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String playerName;
        String tablePrefix = Config.getInstance().getMySQLTablePrefix();
        //String databaseName = Config.getInstance().getMySQLDatabaseName();
        String usage = LocaleLoader.getString("Commands.Usage.1", new Object[] {"mcremove", "<" + LocaleLoader.getString("Commands.Usage.Player") + ">"});
        String success;

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.tools.mcremove")) {
            return true;
        }

        switch (args.length) {
        case 1:
            playerName = args[0];
            success = LocaleLoader.getString("Commands.mcremove.Success", new Object[] {playerName});
            break;

        default:
            sender.sendMessage(usage);
            return true;
        }

        /* MySQL */
        if (Config.getInstance().getUseMySQL()) {
            Database database = mcMMO.getPlayerDatabase();
            int affected = 0;
            affected = database.update("DELETE FROM " + tablePrefix + "users WHERE " + tablePrefix + "users.user = '" + playerName + "'");

            if (affected > 0) {
                sender.sendMessage(success);
            } else {
                sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
            }
        }
        else {
            if (removeFlatFileUser(playerName)) {
                sender.sendMessage(success);
            }
            else {
                sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
            }
        }

        Database.profileCleanup(playerName);

        return true;
    }

    private boolean removeFlatFileUser(String playerName) {
        boolean worked = false;

        BufferedReader in = null;
        FileWriter out = null;

        try {
            FileReader file = new FileReader(location);
            in = new BufferedReader(file);
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

            out = new FileWriter(location); //Write out the new file
            out.write(writer.toString());
        }
        catch (Exception e) {
            plugin.getLogger().severe("Exception while reading " + location + " (Are you sure you formatted it correctly?)" + e.toString());
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return worked;
    }
}
