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

public class mcPermissions 
{
    private static volatile mcPermissions instance;
    
    public boolean permission(Player player, String perm) {
        return player.hasPermission(perm);
    }

    public boolean admin(Player player){
        return player.hasPermission("mcmmo.admin");
    }
    public boolean mcrefresh(Player player) {
        return player.hasPermission("mcmmo.tools.mcrefresh");
    }
    public boolean mmoedit(Player player) {
        return player.hasPermission("mcmmo.tools.mmoedit");
    }
    public boolean herbalismAbility(Player player){
        return player.hasPermission("mcmmo.ability.herbalism");
    }
    public boolean excavationAbility(Player player){
        return player.hasPermission("mcmmo.ability.excavation");
    }
    public boolean unarmedAbility(Player player){
    	return player.hasPermission("mcmmo.ability.unarmed");
    }
    public boolean chimaeraWing(Player player){
    	return player.hasPermission("mcmmo.item.chimaerawing");
    }
    public boolean miningAbility(Player player){
    	return player.hasPermission("mcmmo.ability.mining");
    }
    public boolean axesAbility(Player player){
    	return player.hasPermission("mcmmo.ability.axes");
    }

    public boolean swordsAbility(Player player){
    	return player.hasPermission("mcmmo.ability.swords");
    }
    public boolean woodCuttingAbility(Player player) {
    	return player.hasPermission("mcmmo.ability.woodcutting");
    }
    public boolean mcgod(Player player) {
    	return player.hasPermission("mcmmo.tools.mcgod");
    }
    public boolean regeneration(Player player){
    	return player.hasPermission("mcmmo.regeneration");
    }
    public boolean motd(Player player) {
        return player.hasPermission("mcmmo.motd");
    }
    public boolean mcAbility(Player player) {
        return player.hasPermission("mcmmo.commands.ability");
    }
    public boolean mySpawn(Player player) {
        return player.hasPermission("mcmmo.commands.myspawn");
    }
    public boolean setMySpawn(Player player) {
        return player.hasPermission("mcmmo.commands.setmyspawn");
    }
    public boolean partyChat(Player player) {
        return player.hasPermission("mcmmo.chat.partychat");
    }
    public boolean partyLock(Player player) {
        return player.hasPermission("mcmmo.chat.partylock");
    }
    public boolean partyTeleport(Player player) {
        return player.hasPermission("mcmmo.commands.ptp");
    }
    public boolean whois(Player player) {
        return player.hasPermission("mcmmo.commands.whois");
    }
    public boolean party(Player player) {
        return player.hasPermission("mcmmo.commands.party");
    }
    public boolean adminChat(Player player) {
        return player.hasPermission("mcmmo.chat.adminchat");
    }
    public static mcPermissions getInstance() {
    	if (instance == null) {
    	    instance = new mcPermissions();
    	}
    	return instance;
    }

    public boolean taming(Player player) {
        return player.hasPermission("mcmmo.skills.taming");
    }
    public boolean mining(Player player) {
        return player.hasPermission("mcmmo.skills.mining");
    }
    public boolean fishing(Player player) {
        return player.hasPermission("mcmmo.skills.fishing");
    }
    public boolean alchemy(Player player) {
        return player.hasPermission("mcmmo.skills.alchemy");
    }
    public boolean enchanting(Player player) {
        return player.hasPermission("mcmmo.skills.enchanting");
    }
    public boolean woodcutting(Player player) {
        return player.hasPermission("mcmmo.skills.woodcutting");
    }
    public boolean repair(Player player) {
        return player.hasPermission("mcmmo.skills.repair");
    }
    public boolean unarmed(Player player) {
        return player.hasPermission("mcmmo.skills.unarmed");
    }
    public boolean archery(Player player) {
        return player.hasPermission("mcmmo.skills.archery");
    }
    public boolean herbalism(Player player) {
        return player.hasPermission("mcmmo.skills.herbalism");
    }
    public boolean excavation(Player player) {
        return player.hasPermission("mcmmo.skills.excavation");
    }
    public boolean swords(Player player) {
        return player.hasPermission("mcmmo.skills.swords");
    }
    public boolean axes(Player player) {
        return player.hasPermission("mcmmo.skills.axes");
    }
    public boolean acrobatics(Player player) {
        return player.hasPermission("mcmmo.skills.acrobatics");
    }
}
