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

import com.gmail.nossr50.config.LoadProperties;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class mcPermissions {
	private static volatile mcPermissions instance;

	private enum PermissionType {
		PEX, PERMISSIONS, BUKKIT
	}

	private static PermissionType permissionType;
	private static Object PHandle;

	public static void initialize(Server server) {
		Logger log = Logger.getLogger("Minecraft");

		if (permissionType != null && permissionType != PermissionType.PERMISSIONS)
			return;

		Plugin PEXtest = server.getPluginManager().getPlugin("PermissionsEx");
		Plugin test = server.getPluginManager().getPlugin("Permissions");
		if (PEXtest != null) {
			PHandle = (PermissionManager) PermissionsEx.getPermissionManager();
			permissionType = PermissionType.PEX;
			log.info("[mcMMO] PermissionsEx found, using PermissionsEx.");
		} else if (test != null) {
			PHandle = (PermissionHandler) ((Permissions) test).getHandler();
			permissionType = PermissionType.PERMISSIONS;
			log.info("[mcMMO] Permissions version " + test.getDescription().getVersion() + " found, using Permissions.");
		} else {
			permissionType = PermissionType.BUKKIT;
			log.info("[mcMMO] Using Bukkit Permissions.");
		}
	}

	public static boolean permission(Player player, String permission) {
		if(LoadProperties.disabledWorlds.contains(player.getWorld().getName())) return false;
		switch (permissionType) {
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

	public static mcPermissions getInstance() {
		if (instance == null) {
			instance = new mcPermissions();
		}
		return instance;
	}
	
	public static boolean isEnabled() {
		return permissionType != null;
	}

	public boolean admin(Player player) {
		return permission(player, "mcmmo.admin");
	}

	public boolean mcrefresh(Player player) {
		return permission(player, "mcmmo.tools.mcrefresh");
	}

	public boolean mmoedit(Player player) {
		return permission(player, "mcmmo.tools.mmoedit");
	}

	public boolean herbalismAbility(Player player) {
		return permission(player, "mcmmo.ability.herbalism");
	}

	public boolean excavationAbility(Player player) {
		return permission(player, "mcmmo.ability.excavation");
	}

	public boolean unarmedAbility(Player player) {
		return permission(player, "mcmmo.ability.unarmed");
	}

	public boolean chimaeraWing(Player player) {
		return permission(player, "mcmmo.item.chimaerawing");
	}

	public boolean miningAbility(Player player) {
		return permission(player, "mcmmo.ability.mining");
	}

	public boolean axesAbility(Player player) {
		return permission(player, "mcmmo.ability.axes");
	}

	public boolean swordsAbility(Player player) {
		return permission(player, "mcmmo.ability.swords");
	}

	public boolean woodCuttingAbility(Player player) {
		return permission(player, "mcmmo.ability.woodcutting");
	}

	public boolean mcgod(Player player) {
		return permission(player, "mcmmo.tools.mcgod");
	}

	public boolean regeneration(Player player) {
		return permission(player, "mcmmo.regeneration");
	}

	public boolean motd(Player player) {
		return permission(player, "mcmmo.motd");
	}

	public boolean mcAbility(Player player) {
		return permission(player, "mcmmo.commands.ability");
	}

	public boolean mySpawn(Player player) {
		return permission(player, "mcmmo.commands.myspawn");
	}

	public boolean setMySpawn(Player player) {
		return permission(player, "mcmmo.commands.setmyspawn");
	}

	public boolean partyChat(Player player) {
		return permission(player, "mcmmo.chat.partychat");
	}

	public boolean partyLock(Player player) {
		return permission(player, "mcmmo.chat.partylock");
	}

	public boolean partyTeleport(Player player) {
		return permission(player, "mcmmo.commands.ptp");
	}

	public boolean whois(Player player) {
		return permission(player, "mcmmo.commands.whois");
	}

	public boolean party(Player player) {
		return permission(player, "mcmmo.commands.party");
	}

	public boolean adminChat(Player player) {
		return permission(player, "mcmmo.chat.adminchat");
	}

	public boolean taming(Player player) {
		return permission(player, "mcmmo.skills.taming");
	}

	public boolean mining(Player player) {
		return permission(player, "mcmmo.skills.mining");
	}

	public boolean fishing(Player player) {
		return permission(player, "mcmmo.skills.fishing");
	}

	public boolean alchemy(Player player) {
		return permission(player, "mcmmo.skills.alchemy");
	}

	public boolean enchanting(Player player) {
		return permission(player, "mcmmo.skills.enchanting");
	}

	public boolean woodcutting(Player player) {
		return permission(player, "mcmmo.skills.woodcutting");
	}

	public boolean repair(Player player) {
		return permission(player, "mcmmo.skills.repair");
	}

	public boolean unarmed(Player player) {
		return permission(player, "mcmmo.skills.unarmed");
	}

	public boolean archery(Player player) {
		return permission(player, "mcmmo.skills.archery");
	}

	public boolean herbalism(Player player) {
		return permission(player, "mcmmo.skills.herbalism");
	}

	public boolean excavation(Player player) {
		return permission(player, "mcmmo.skills.excavation");
	}

	public boolean swords(Player player) {
		return permission(player, "mcmmo.skills.swords");
	}

	public boolean axes(Player player) {
		return permission(player, "mcmmo.skills.axes");
	}

	public boolean acrobatics(Player player) {
		return permission(player, "mcmmo.skills.acrobatics");
	}
}
