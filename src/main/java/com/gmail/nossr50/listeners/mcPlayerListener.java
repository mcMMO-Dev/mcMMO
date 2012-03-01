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
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.Combat;
import com.gmail.nossr50.Item;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.commands.general.XprateCommand;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.runnables.RemoveProfileFromMemoryTask;
import com.gmail.nossr50.spout.SpoutStuff;
import com.gmail.nossr50.datatypes.AbilityType;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.skills.Fishing;
import com.gmail.nossr50.skills.Repair;
import com.gmail.nossr50.skills.Skills;


public class mcPlayerListener implements Listener 
{
	protected static final Logger log = Logger.getLogger("Minecraft"); //$NON-NLS-1$
	private mcMMO plugin;

	public mcPlayerListener(mcMMO instance) 
	{
		plugin = instance;
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerWorldChangeEvent(PlayerChangedWorldEvent event)
	{
	    Player player = event.getPlayer();
	    PlayerProfile PP = Users.getProfile(player);
	    
	    if(PP.getGodMode())
	    {
	        if(!mcPermissions.getInstance().mcgod(player))
	        {
	            PP.toggleGodMode();
	            player.sendMessage("[mcMMO] God Mode not permitted on this world (See Permissions)");
	        }
	    }
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerFish(PlayerFishEvent event) 
	{
		Player player = event.getPlayer();
		if(mcPermissions.getInstance().fishing(player))
		{
			State state = event.getState();
			Entity caught = event.getCaught();
			if(state== State.CAUGHT_FISH)
			{
				if(caught instanceof org.bukkit.entity.Item)
					Fishing.processResults(event);
			} 
			else if (state == State.CAUGHT_ENTITY)
			{
				int skillLevel = Users.getProfile(player).getSkillLevel(SkillType.FISHING);
				if(skillLevel >= 150 && caught instanceof LivingEntity)
					Fishing.shakeMob(event);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) 
	{
		if(Users.getProfile(event.getPlayer()).getBerserkMode())
			 event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerLogin(PlayerLoginEvent event) 
	{
		Users.addUser(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event) 
	{
		/*
		 * GARBAGE COLLECTION
		 */
		//Discard the PlayerProfile object
		Player player = event.getPlayer();
		PlayerProfile PP = Users.getProfile(player);
		if(LoadProperties.spoutEnabled && SpoutStuff.playerHUDs.containsKey(player))
			SpoutStuff.playerHUDs.remove(player);
		
		//Bleed it out
		if(PP.getBleedTicks() > 0)
			Combat.dealDamage(player, PP.getBleedTicks()*2);
		
		//Save PlayerData to MySQL/FlatFile on player quit
		PP.save();
		
		//Schedule PlayerProfile removal 2 minutes after quitting
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new RemoveProfileFromMemoryTask(player), 2400);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) 
	{
		Player player = event.getPlayer();
		if(mcPermissions.getInstance().motd(player) && LoadProperties.enableMotd)
		{
			player.sendMessage(mcLocale.getString("mcPlayerListener.MOTD", new Object[] {plugin.getDescription().getVersion(), "mcmmo"}));
			player.sendMessage(mcLocale.getString("mcPlayerListener.WIKI"));
		}
		//THIS IS VERY BAD WAY TO DO THINGS, NEED BETTER WAY
		if(XprateCommand.xpevent)
			player.sendMessage(ChatColor.GOLD+"mcMMO is currently in an XP rate event! XP rate is "+LoadProperties.xpGainMultiplier+"x!");
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event) 
	{
		Player player = event.getPlayer();
		PlayerProfile PP = Users.getProfile(player);
		Action action = event.getAction();
		Block block = event.getClickedBlock();
		ItemStack is = player.getItemInHand();
		
		/*
		 * Ability checks
		 */
		if(action == Action.RIGHT_CLICK_BLOCK)
		{
			Material mat = block.getType();

			if(block != null && mcPermissions.getInstance().repair(player) && block.getTypeId() == LoadProperties.anvilID && (Repair.isTools(is) || Repair.isArmor(is)))
			{
				Repair.repairCheck(player, is, event.getClickedBlock());
				event.setCancelled(true);
				player.updateInventory();
			}

			if(LoadProperties.enableAbilities && m.abilityBlockCheck(block))
			{
				if(block != null && m.isHoe(is) && !mat.equals(Material.DIRT) && !mat.equals(Material.GRASS) && !mat.equals(Material.SOIL))
					Skills.activationCheck(player, SkillType.HERBALISM);
				
				Skills.activationCheck(player, SkillType.AXES);
				Skills.activationCheck(player, SkillType.EXCAVATION);
				Skills.activationCheck(player, SkillType.MINING);
				Skills.activationCheck(player, SkillType.SWORDS);
				Skills.activationCheck(player, SkillType.UNARMED);
				Skills.activationCheck(player, SkillType.WOODCUTTING);
			}

			//GREEN THUMB
			if(block != null && mcPermissions.getInstance().herbalism(player) && (mat.equals(Material.COBBLESTONE) || mat.equals(Material.DIRT) || mat.equals(Material.SMOOTH_BRICK)) && is.getType().equals(Material.SEEDS))
			{
				boolean pass = false;
				int seeds = is.getAmount();
				player.setItemInHand(new ItemStack(Material.SEEDS, seeds - 1));
					
				if(Math.random() * 1500 <= PP.getSkillLevel(SkillType.HERBALISM) && m.blockBreakSimulate(block, player, false))
				{
					switch(mat)
					{
					case COBBLESTONE:
						if(LoadProperties.enableCobbleToMossy)
						{
							block.setType(Material.MOSSY_COBBLESTONE);
							pass = true;
						}
						break;
					case DIRT:
						if(LoadProperties.enableDirtToGrass)
						{
							block.setType(Material.GRASS);
							pass = true;
						}
						break;
					case SMOOTH_BRICK:
						if(LoadProperties.enableSmoothToMossy)
						{
							pass = true;
							block.setData((byte)1);
						}
						break;
					}
					
					if(pass == false)
						player.sendMessage(mcLocale.getString("mcPlayerListener.GreenThumbFail"));
				}
				return;
			}
		}
		
		if(LoadProperties.enableAbilities && action == Action.RIGHT_CLICK_AIR)
		{
			Skills.activationCheck(player, SkillType.AXES);
			Skills.activationCheck(player, SkillType.EXCAVATION);
			Skills.activationCheck(player, SkillType.HERBALISM);
			Skills.activationCheck(player, SkillType.MINING);
			Skills.activationCheck(player, SkillType.SWORDS);
			Skills.activationCheck(player, SkillType.UNARMED);
			Skills.activationCheck(player, SkillType.WOODCUTTING);
		}
		
		/*
		 * ITEM CHECKS
		 */
		if(action == Action.RIGHT_CLICK_AIR)
			Item.itemchecks(player, plugin);
		if(action == Action.RIGHT_CLICK_BLOCK && m.abilityBlockCheck(block))
			Item.itemchecks(player, plugin);
		
		if(player.isSneaking() && mcPermissions.getInstance().taming(player) && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK))
		{
			if(is.getType().equals(Material.BONE) && is.getAmount() >= LoadProperties.bonesConsumedByCOTW)
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
				world.spawnCreature(player.getLocation(), EntityType.WOLF);
				
				int bones = is.getAmount();
				bones = bones - LoadProperties.bonesConsumedByCOTW;
				player.setItemInHand(new ItemStack(Material.BONE, bones));
    	    	player.sendMessage(mcLocale.getString("m.TamingSummon"));
			}
		}
		
		//BLAST MINING
		if((action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) && is.getTypeId() == LoadProperties.detonatorID)
		{
			Block b = player.getTargetBlock(null, 100);
			if(b.getType().equals(Material.TNT) && mcPermissions.getInstance().blastMining(player) && m.blockBreakSimulate(b, player, true) && Users.getProfile(player).getSkillLevel(SkillType.MINING) >= 125)
			{
			    AbilityType ability = AbilityType.BLAST_MINING;
			    //Check cooldown
	            if(!Skills.cooldownOver(player, (PP.getSkillDATS(ability) * 1000), ability.getCooldown()))
	            {
	                player.sendMessage(mcLocale.getString("Skills.TooTired") + ChatColor.YELLOW + " (" + Skills.calculateTimeLeft(player, (PP.getSkillDATS(ability) * 1000), ability.getCooldown()) + "s)");
	                return;
	            }
	            //Send message to nearby players
	            for(Player y : player.getWorld().getPlayers())
                {
                    if(y != player && m.isNear(player.getLocation(), y.getLocation(), 10))
                        y.sendMessage(ability.getAbilityPlayer(player));
                }
	            
	            player.sendMessage(ChatColor.GRAY+"**BOOM**");
	            
				TNTPrimed tnt = player.getWorld().spawn(b.getLocation(), TNTPrimed.class);
				b.setType(Material.AIR);
				tnt.setFuseTicks(0);
				PP.setSkillDATS(ability, System.currentTimeMillis()); //Save DATS for Blast Mining
				PP.setBlastMiningInformed(false);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerChat(PlayerChatEvent event) 
	{
		Player player = event.getPlayer();
		PlayerProfile PP = Users.getProfile(player);
		if(PP.getPartyChatMode())
		{
			event.setCancelled(true);
			String name = (LoadProperties.pDisplayNames) ? player.getDisplayName() : player.getName();
			String format = ChatColor.GREEN + "(" + ChatColor.WHITE + name + ChatColor.GREEN + ") "+event.getMessage();
			for(Player x : Bukkit.getServer().getOnlinePlayers())
			{
				if(Party.getInstance().inSameParty(player, x))
					x.sendMessage(format);
			}
			log.log(Level.INFO, "[P](" + PP.getParty() + ")<" + name + ">" + event.getMessage());
		} else if (PP.getAdminChatMode()) {
			event.setCancelled(true);
			String name = (LoadProperties.aDisplayNames) ? player.getDisplayName() : player.getName();
			String format = ChatColor.AQUA + "{" + ChatColor.WHITE + name + ChatColor.AQUA + "} "+event.getMessage();
			for(Player x : Bukkit.getServer().getOnlinePlayers())
			{
				if(x.isOp() || mcPermissions.getInstance().adminChat(x))
					x.sendMessage(format);
			}
			log.log(Level.INFO, "[A]<" + name + ">" + event.getMessage());
		}
	}
	
	// Dynamically aliasing commands need to be re-done.
	// For now, using a command with an alias will send both the original command, and the mcMMO command
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		String message = event.getMessage();
		if(!message.startsWith("/")) return;
		String command = message.substring(1).split(" ")[0];
		if(plugin.aliasMap.containsKey(command)) {
			if(command.equalsIgnoreCase(plugin.aliasMap.get(command))) return;
			//event.setCancelled(true);
			event.getPlayer().chat(message.replaceFirst(command, plugin.aliasMap.get(command)));
		}
	}
}