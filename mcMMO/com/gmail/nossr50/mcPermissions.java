package com.gmail.nossr50;

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
    public boolean mmoedit(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mcmmo.tools.mmoedit");
        } else {
            return true;
        }
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
    public boolean setMySpawn(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mcmmo.commands.setmyspawn");
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
    public boolean mining(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mcmmo.skills.mining");
        } else {
            return true;
        }
    }
    public boolean woodcutting(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mcmmo.skills.woodcutting");
        } else {
            return true;
        }
    }
    public boolean repair(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mcmmo.skills.repair");
        } else {
            return true;
        }
    }
    public boolean unarmed(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mcmmo.skills.unarmed");
        } else {
            return true;
        }
    }
    public boolean archery(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mcmmo.skills.archery");
        } else {
            return true;
        }
    }
    public boolean herbalism(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mcmmo.skills.herbalism");
        } else {
            return true;
        }
    }
    public boolean excavation(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mcmmo.skills.excavation");
        } else {
            return true;
        }
    }
    public boolean swords(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mcmmo.skills.swords");
        } else {
            return true;
        }
    }
    public boolean axes(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mcmmo.skills.axes");
        } else {
            return true;
        }
    }
    public boolean acrobatics(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mcmmo.skills.acrobatics");
        } else {
            return true;
        }
    }
}
