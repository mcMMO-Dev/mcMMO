package com.gmail.nossr50.database.commands;

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
import com.gmail.nossr50.database.Database;
import com.gmail.nossr50.locale.LocaleLoader;

public class McremoveCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String playerName;
        String tablePrefix = Config.getInstance().getMySQLTablePrefix();
        //String databaseName = Config.getInstance().getMySQLDatabaseName();
        String usage = LocaleLoader.getString("Commands.Usage.1", "mcremove", "<" + LocaleLoader.getString("Commands.Usage.Player") + ">");
        String success;

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.commands.mcremove")) {
            return true;
        }

        switch (args.length) {
        case 1:
            playerName = args[0];
            success = LocaleLoader.getString("Commands.mcremove.Success", playerName);
            break;

        default:
            sender.sendMessage(usage);
            return true;
        }

        /* MySQL */
        if (Config.getInstance().getUseMySQL()) {
            int affected = 0;
            affected = Database.update("DELETE FROM " + tablePrefix + "users WHERE " + tablePrefix + "users.user = '" + playerName + "'");

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
        String usersFilePath = mcMMO.getUsersFilePath();

        try {
            FileReader file = new FileReader(usersFilePath);
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

            out = new FileWriter(usersFilePath); //Write out the new file
            out.write(writer.toString());
        }
        catch (Exception e) {
            mcMMO.p.getLogger().severe("Exception while reading " + usersFilePath + " (Are you sure you formatted it correctly?)" + e.toString());
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
