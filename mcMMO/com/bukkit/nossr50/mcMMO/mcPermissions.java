package com.bukkit.nossr50.mcMMO;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import com.nijikokun.bukkit.Permissions.Permissions;

public class mcPermissions {
	//Thanks to myWarp source code for helping me figure this shit out!
	private static Permissions permissionsPlugin;
    private static boolean permissionsEnabled = false;
    private static volatile mcPermissions instance;

    public static void initialize(Server server) {
        Plugin test = server.getPluginManager().getPlugin("Permissions");
        if (test != null) {
            Logger log = Logger.getLogger("Minecraft");
            permissionsPlugin = ((Permissions) test);
            permissionsEnabled = true;
            log.log(Level.INFO, "[mcMMO] Permissions enabled.");
        } else {
            Logger log = Logger.getLogger("Minecraft");
            log.log(Level.SEVERE, "[mcMMO] Permissions isn't loaded, there are no restrictions.");
        }
    }
    private boolean permission(Player player, String string) {
        return permissionsPlugin.Security.permission(player, string);  
    }
    public boolean motd(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mcmmo.motd");
        } else {
            return true;
        }
    }
    public boolean mySpawn(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mcmmo.commands.myspawn");
        } else {
            return true;
        }
    }
    public boolean partyChat(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mcmmo.chat.partychat");
        } else {
            return true;
        }
    }
    public boolean partyTeleport(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mcmmo.commands.ptp");
        } else {
            return true;
        }
    }
    public boolean whois(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mcmmo.commands.whois");
        } else {
            return true;
        }
    }
    public boolean party(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mcmmo.commands.party");
        } else {
            return true;
        }
    }
    public boolean adminChat(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mcmmo.chat.adminchat");
        } else {
            return true;
        }
    }
    public static mcPermissions getInstance() {
    	if (instance == null) {
    	instance = new mcPermissions();
    	}
    	return instance;
    	}
}
