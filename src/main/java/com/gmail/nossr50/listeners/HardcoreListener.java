package com.gmail.nossr50.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.util.Hardcore;
import com.gmail.nossr50.util.Permissions;

public class HardcoreListener implements Listener {
    
    @EventHandler()
    public void PlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity(); //Note this returns a Player object for this subevent
        
        if(!Permissions.getInstance().hardcoremodeBypass(player)) {
            if(player.getKiller() instanceof Player) {
                if(Config.getInstance().getHardcoreVampirismEnabled()) {
                    Hardcore.invokeVampirism(((Player)player.getKiller()), player);
                }
            }
            
            Hardcore.invokeStatPenalty(player);
        }
    }
}
