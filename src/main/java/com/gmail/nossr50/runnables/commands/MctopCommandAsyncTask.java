package com.gmail.nossr50.runnables.commands;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.database.DatabaseManager;

public class MctopCommandAsyncTask extends BukkitRunnable {

    private CommandSender sender;
    private String query;
    private int page;

    public MctopCommandAsyncTask(int page, String query, CommandSender sender) {
        this.page = page;
        this.query = query;
        this.sender = sender;
    }

    @Override
    public void run() {
        String tablePrefix = Config.getInstance().getMySQLTablePrefix();
        final HashMap<Integer, ArrayList<String>> userslist = DatabaseManager.read("SELECT " + query + ", user, NOW() FROM " + tablePrefix + "users JOIN " + tablePrefix + "skills ON (user_id = id) WHERE " + query + " > 0 ORDER BY " + query + " DESC, user LIMIT " + ((page * 10) - 10) + ",10");

        new MctopCommandDisplayTask(userslist, page, tablePrefix, sender).runTaskLater(mcMMO.p, 1);
    }

}
