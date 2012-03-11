package com.gmail.nossr50.runnables;

import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;

public class mcSaveTimer implements Runnable {
    private final mcMMO plugin;
    
    public mcSaveTimer(final mcMMO plugin) 
    {
        this.plugin = plugin;
    }
    
    @Override
    public void run() 
    {
        //All player data will be saved periodically through this
        for(Player player : plugin.getServer().getOnlinePlayers())
        {
            Users.getProfile(player).save();
        }
    }
}