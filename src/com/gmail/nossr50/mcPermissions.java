/*
	This file is part of mcMMO.

    mcMMO is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    mcMMO is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with mcMMO.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.gmail.nossr50;

import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class mcPermissions 
{
    private static volatile mcPermissions instance;
    
	private enum PermissionType {
		PEX, PERMISSIONS, BUKKIT
	}
	
	private static PermissionType permissionType;
	private static Object PHandle;
	public static boolean permissionsEnabled = false;

    public static void initialize(Server server) 
    {
        Logger log = Logger.getLogger("Minecraft");
        
		if(permissionsEnabled && permissionType != PermissionType.PERMISSIONS) return;
		
		Plugin PEXtest = server.getPluginManager().getPlugin("PermissionsEx");
		Plugin test = server.getPluginManager().getPlugin("Permissions");
		if(PEXtest != null) {
			PHandle = (PermissionManager) PermissionsEx.getPermissionManager();
			permissionType = PermissionType.PEX;
			permissionsEnabled = true;
			log.info("[mcMMO] PermissionsEx found, using PermissionsEx.");
		} else if(test != null) {
			PHandle = (PermissionHandler) ((Permissions) test).getHandler();
			permissionType = PermissionType.PERMISSIONS;
			permissionsEnabled = true;
			log.info("[mcMMO] Permissions version "+test.getDescription().getVersion()+" found, using Permissions.");
		} else {
			permissionType = PermissionType.BUKKIT;
			permissionsEnabled = true;
			log.info("[mcMMO] Using Bukkit Permissions.");
		}
    }
    
    public static boolean getEnabled()
    {
    	return permissionsEnabled;
    }
  
    public static boolean permission(Player player, String permission) 
    {
		if(!permissionsEnabled) return player.isOp();
		switch(permissionType) {
			case PEX:
				return ((PermissionManager) PHandle).has(player, permission);
			case PERMISSIONS:
				return ((PermissionHandler) PHandle).has(player, permission);
			case BUKKIT:
				return player.hasPermission(permission);
			default:
				return true;
		}
	}
    public boolean admin(Player player){
    	if (permissionsEnabled) {
            return permission(player, "mcmmo.admin");
        } else {
            return true;
        }
    }
    public boolean mcrefresh(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mcmmo.tools.mcrefresh");
        } else {
            return true;
        }
    }
    public boolean mmoedit(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mcmmo.tools.mmoedit");
        } else {
            return true;
        }
    }
    public boolean herbalismAbility(Player player){
    	if (permissionsEnabled) {
            return permission(player, "mcmmo.ability.herbalism");
        } else {
            return true;
        }
    }
    public boolean excavationAbility(Player player){
    	if (permissionsEnabled) {
            return permission(player, "mcmmo.ability.excavation");
        } else {
            return true;
        }
    }
    public boolean unarmedAbility(Player player){
    	if (permissionsEnabled) {
            return permission(player, "mcmmo.ability.unarmed");
        } else {
            return true;
        }
    }
    public boolean chimaeraWing(Player player){
    	if (permissionsEnabled) {
            return permission(player, "mcmmo.item.chimaerawing");
        } else {
            return true;
        }
    }
    public boolean miningAbility(Player player){
    	if (permissionsEnabled) {
            return permission(player, "mcmmo.ability.mining");
        } else {
            return true;
        }
    }
    public boolean axesAbility(Player player){
    	if (permissionsEnabled) {
            return permission(player, "mcmmo.ability.axes");
        } else {
            return true;
        }
    }
    public boolean swordsAbility(Player player){
    	if (permissionsEnabled) {
            return permission(player, "mcmmo.ability.swords");
        } else {
            return true;
        }
    }
    public boolean woodCuttingAbility(Player player) {
    	if (permissionsEnabled) {
            return permission(player, "mcmmo.ability.woodcutting");
        } else {
            return true;
        }
    }
    public boolean mcgod(Player player) {
    	if (permissionsEnabled) {
            return permission(player, "mcmmo.tools.mcgod");
        } else {
            return true;
        }
    }
    public boolean regeneration(Player player){
    	if (permissionsEnabled) {
            return permission(player, "mcmmo.regeneration");
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
    public boolean mcAbility(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mcmmo.commands.ability");
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
    public boolean partyLock(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mcmmo.chat.partylock");
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
    public boolean taming(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mcmmo.skills.taming");
        } else {
            return true;
        }
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
