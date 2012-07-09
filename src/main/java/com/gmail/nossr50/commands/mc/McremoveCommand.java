package com.gmail.nossr50.commands.mc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.datatypes.SpoutHud;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.spout.SpoutStuff;
import com.gmail.nossr50.util.Database;
import com.gmail.nossr50.util.Users;

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
        String databaseName = Config.getInstance().getMySQLDatabaseName();
        String usage = ChatColor.RED + "Proper usage is /mcremove <player>"; //TODO: Needs more locale.
        String success;

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.tools.mcremove")) {
            return true;
        }

        switch (args.length) {
        case 1:
            playerName = args[0];
            success = ChatColor.GREEN + playerName + " was successfully removed from the database!"; //TODO: Locale
            break;

        default:
            sender.sendMessage(usage);
            return true;
        }

        /* MySQL */
        if (Config.getInstance().getUseMySQL()) {
            Database database = mcMMO.getPlayerDatabase();
            int userId = 0;
            userId = database.getInt("SELECT id FROM " + tablePrefix + "users WHERE user = '" + playerName + "'");

            if (userId > 0) {
                database.write("DELETE FROM "
                        + databaseName + "."
                        + tablePrefix + "users WHERE "
                        + tablePrefix + "users.id=" + userId);

                database.write("DELETE FROM "
                        + databaseName + "."
                        + tablePrefix + "cooldowns WHERE "
                        + tablePrefix + "cooldowns.user_id=" + userId);

                database.write("DELETE FROM "
                        + databaseName + "."
                        + tablePrefix + "huds WHERE "
                        + tablePrefix + "huds.user_id=" + userId);

                database.write("DELETE FROM "
                        + databaseName + "."
                        + tablePrefix + "skills WHERE "
                        + tablePrefix + "skills.user_id=" + userId);

                database.write("DELETE FROM "
                        + databaseName + "."
                        + tablePrefix + "experience WHERE "
                        + tablePrefix + "experience.user_id=" + userId);

                sender.sendMessage(success);

            }
            else {
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

        //Force PlayerProfile stuff to update
        McMMOPlayer mcmmoPlayer = Users.getPlayer(playerName);

        if (mcmmoPlayer != null) {
            Player player = mcmmoPlayer.getPlayer();
            SpoutHud spoutHud = mcmmoPlayer.getProfile().getSpoutHud();

            if (spoutHud != null) {
                spoutHud.removeWidgets();
            }

            Users.remove(playerName);

            if (player.isOnline()) {
                Users.addUser(player);

                if (mcMMO.spoutEnabled) {
                    SpoutStuff.reloadSpoutPlayer(player);
                }
            }
        }

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
