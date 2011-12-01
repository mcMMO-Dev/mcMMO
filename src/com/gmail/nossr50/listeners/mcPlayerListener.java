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
package com.gmail.nossr50.listeners;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.command.ColouredConsoleSender;
import org.bukkit.craftbukkit.entity.CraftItem;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import com.gmail.nossr50.Item;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.command.Commands;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.spout.SpoutStuff;
import com.gmail.nossr50.spout.mmoHelper;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.skills.Fishing;
import com.gmail.nossr50.skills.Herbalism;
import com.gmail.nossr50.skills.Repair;
import com.gmail.nossr50.skills.Skills;
import com.gmail.nossr50.skills.Taming;


public class mcPlayerListener extends PlayerListener 
{
	protected static final Logger log = Logger.getLogger("Minecraft"); //$NON-NLS-1$
	public Location spawn = null;
	private mcMMO plugin;

	public mcPlayerListener(mcMMO instance) 
	{
		plugin = instance;
	}
	
	public void onPlayerFish(PlayerFishEvent event) 
	{
		if(mcPermissions.getInstance().fishing(event.getPlayer()))
		{
			if(event.getState() == State.CAUGHT_FISH)
			{
				if(event.getCaught() instanceof CraftItem)
				{
					Fishing.processResults(event);
				}
			} else if (event.getState() == State.CAUGHT_ENTITY)
			{
				if(Users.getProfile(event.getPlayer()).getSkillLevel(SkillType.FISHING) >= 150 && event.getCaught() instanceof LivingEntity)
				{
					Fishing.shakeMob(event);
				}
			}
		}
	}
	
	 public void onPlayerPickupItem(PlayerPickupItemEvent event) 
	 {
		 if(Users.getProfile(event.getPlayer()).getBerserkMode())
		 {
			 event.setCancelled(true);
		 }
	 }

	public void onPlayerRespawn(PlayerRespawnEvent event) 
	{
		
		Player player = event.getPlayer();
		PlayerProfile PP = Users.getProfile(player);
		if(LoadProperties.enableMySpawn && mcPermissions.getInstance().mySpawn(player))
		{
			if(player != null && PP != null)
			{
				PP.setRespawnATS(System.currentTimeMillis());
				
				Location mySpawn = PP.getMySpawn(player);
				
				if(mySpawn != null)
				{
					{
						event.setRespawnLocation(mySpawn);
					}
				}
			}
		}
	}
	
	public void onPlayerLogin(PlayerLoginEvent event) 
	{
		Users.addUser(event.getPlayer());
	}

	public void onPlayerQuit(PlayerQuitEvent event) 
	{
		
		/*
		 * GARBAGE COLLECTION
		 */
		//Discard the PlayerProfile object
		Player player = event.getPlayer();
		
		if(LoadProperties.spoutEnabled)
		{
			if(SpoutStuff.playerHUDs.containsKey(player))
				SpoutStuff.playerHUDs.remove(player);
			if(mmoHelper.containers.containsKey(player))
				mmoHelper.containers.remove(player);
		}
		
		Users.removeUser(event.getPlayer());
	}

	public void onPlayerJoin(PlayerJoinEvent event) 
	{
		Player player = event.getPlayer();
		if(mcPermissions.getInstance().motd(player) && LoadProperties.enableMotd)
		{
			player.sendMessage(mcLocale.getString("mcPlayerListener.MOTD", new Object[] {plugin.getDescription().getVersion(), LoadProperties.mcmmo}));
			player.sendMessage(mcLocale.getString("mcPlayerListener.WIKI"));
		}
		if(Commands.xpevent)
			player.sendMessage(ChatColor.GOLD+"mcMMO is currently in an XP rate event! XP rate is "+LoadProperties.xpGainMultiplier+"x!");
	}

	public void onPlayerInteract(PlayerInteractEvent event) 
	{
		Player player = event.getPlayer();
		PlayerProfile PP = Users.getProfile(player);
		Action action = event.getAction();
		Block block = event.getClickedBlock();

		/*
		 * Ability checks
		 */
		if(action == Action.RIGHT_CLICK_BLOCK)
		{
			ItemStack is = player.getItemInHand();
			if(LoadProperties.enableMySpawn && block != null && player != null)
			{
				if(block.getTypeId() == 26 && mcPermissions.getInstance().setMySpawn(player))
				{
					Location loc = player.getLocation();
					if(mcPermissions.getInstance().setMySpawn(player)){
						PP.setMySpawn(loc.getX(), loc.getY(), loc.getZ(), loc.getWorld().getName());
					}
					//player.sendMessage(mcLocale.getString("mcPlayerListener.MyspawnSet"));
				}
			}

			if(block != null && player != null && mcPermissions.getInstance().repair(player) && event.getClickedBlock().getTypeId() == 42)
			{
				Repair.repairCheck(player, is, event.getClickedBlock());
			}

			if(m.abilityBlockCheck(block))
			{
				if(block != null && m.isHoe(player.getItemInHand()) && block.getTypeId() != 3 && block.getTypeId() != 2 && block.getTypeId() != 60){
					Skills.hoeReadinessCheck(player);
				}
				Skills.abilityActivationCheck(player);
			}

			//GREEN THUMB
			if(block != null && mcPermissions.getInstance().herbalism(player) && (block.getType() == Material.COBBLESTONE || block.getType() == Material.DIRT || block.getType() == Material.SMOOTH_BRICK) && player.getItemInHand().getType() == Material.SEEDS)
			{
				boolean pass = false;
				if(Herbalism.hasSeeds(player))
				{
					Herbalism.removeSeeds(player);
					
					if(block.getType() == Material.DIRT || block.getType() == Material.COBBLESTONE || block.getType() == Material.SMOOTH_BRICK)
					{
						if(Math.random() * 1500 <= PP.getSkillLevel(SkillType.HERBALISM) && m.blockBreakSimulate(block, player))
						{
							switch(block.getType())
							{
							case COBBLESTONE:
								if(LoadProperties.enableCobbleToMossy)
								{
									block.setType(Material.MOSSY_COBBLESTONE);
									pass = true;
								}
								break;
							case DIRT:
								pass = true;
								block.setType(Material.GRASS);
								break;
							case SMOOTH_BRICK:
								pass = true;
								block.setData((byte)1);
								break;
							}
							if(pass == false)
								player.sendMessage(mcLocale.getString("mcPlayerListener.GreenThumbFail"));
						}
					}
				}
				return;
			}
		}
		if(action == Action.RIGHT_CLICK_AIR)
		{
			Skills.hoeReadinessCheck(player);
			Skills.abilityActivationCheck(player);
		}
		
		/*
		 * ITEM CHECKS
		 */
		if(action == Action.RIGHT_CLICK_AIR)
			Item.itemchecks(player, plugin);
		if(action == Action.RIGHT_CLICK_BLOCK)
		{
			if(m.abilityBlockCheck(event.getClickedBlock()))
				Item.itemchecks(player, plugin);
		}
		
		if(player.isSneaking() && mcPermissions.getInstance().taming(player) && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK))
		{
			if(player.getItemInHand().getType() == Material.BONE && player.getItemInHand().getAmount() > 9)
			{
				for(Entity x : player.getNearbyEntities(40, 40, 40))
				{
					if(x instanceof Wolf)
					{
						player.sendMessage(mcLocale.getString("m.TamingSummonFailed"));
						return;
					}
				}
				World world = player.getWorld();
				world.spawnCreature(player.getLocation(), CreatureType.WOLF);
				
				ItemStack[] inventory = player.getInventory().getContents();
    	    	for(ItemStack x : inventory){
    	    		if(x != null && x.getType() == Material.BONE){
    	    			if(x.getAmount() >= 10)
    	    			{
    	    				x.setAmount(x.getAmount() - 10);
    	    				player.getInventory().setContents(inventory);
        	    			player.updateInventory();
        	    			break;
    	    			} else {
    	    				x.setAmount(0);
    	    				x.setTypeId(0);
    	    				player.getInventory().setContents(inventory);
        	    			player.updateInventory();
        	    			break;
    	    			}
    	    		}
    	    	}
    	    	player.sendMessage(mcLocale.getString("m.TamingSummon"));
			}
		}
	}

	public void onPlayerChat(PlayerChatEvent event) 
	{
		Player player = event.getPlayer();
		PlayerProfile PP = Users.getProfile(player);
		if(PP.getPartyChatMode())
		{
			event.setCancelled(true);
			String format = ChatColor.GREEN + "(" + ChatColor.WHITE + player.getDisplayName() + ChatColor.GREEN + ") "+event.getMessage();
			for(Player x : Bukkit.getServer().getOnlinePlayers())
			{
				if(Party.getInstance().inSameParty(player, x))
					x.sendMessage(format);
			}
			if(Bukkit.getServer() instanceof ColouredConsoleSender)
			{
				ColouredConsoleSender ccs = (ColouredConsoleSender) Bukkit.getServer();
				ccs.sendMessage(ChatColor.GREEN+"[P]"+format); //Colors, woot!
			}
		} else if (PP.getAdminChatMode()) {
			event.setCancelled(true);
			String format = ChatColor.AQUA + "{" + ChatColor.WHITE + player.getDisplayName() + ChatColor.AQUA + "} "+event.getMessage();
			for(Player x : Bukkit.getServer().getOnlinePlayers())
			{
				if(x.isOp() || mcPermissions.getInstance().adminChat(x))
					x.sendMessage(format);
			}
			if(Bukkit.getServer() instanceof ColouredConsoleSender)
			{
				ColouredConsoleSender ccs = (ColouredConsoleSender) Bukkit.getServer();
				ccs.sendMessage(ChatColor.AQUA+"[A]"+format); //Colors, woot!
			} else {
				log.log(Level.INFO, "[A]"+format);
			}
		}
	}
}