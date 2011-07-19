package com.gmail.nossr50.listeners;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChatEvent;
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
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.skills.Herbalism;
import com.gmail.nossr50.skills.Repair;
import com.gmail.nossr50.skills.Skills;


public class mcPlayerListener extends PlayerListener 
{
	protected static final Logger log = Logger.getLogger("Minecraft"); //$NON-NLS-1$
	public Location spawn = null;
	private mcMMO plugin;

	public mcPlayerListener(mcMMO instance) 
	{
		plugin = instance;
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
		if(LoadProperties.enableMySpawn && mcPermissions.getInstance().mySpawn(player))
		{
			PlayerProfile PP = Users.getProfile(player);

			if(player != null && PP != null)
			{
				PP.setRespawnATS(System.currentTimeMillis());
				Location mySpawn = PP.getMySpawn(player);
				if(mySpawn != null && plugin.getServer().getWorld(PP.getMySpawnWorld(plugin)) != null)
					mySpawn.setWorld(plugin.getServer().getWorld(PP.getMySpawnWorld(plugin)));
				if(mySpawn != null)
				{
					event.setRespawnLocation(mySpawn);
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
		Users.removeUser(event.getPlayer());
	}

	public void onPlayerJoin(PlayerJoinEvent event) 
	{
		
		Player player = event.getPlayer();

		if(mcPermissions.getInstance().motd(player) && LoadProperties.enableMotd)
		{
			//player.sendMessage(ChatColor.BLUE +"This server is running mcMMO "+plugin.getDescription().getVersion()+" type /"+ChatColor.YELLOW+LoadProperties.mcmmo+ChatColor.BLUE+ " for help.");
			player.sendMessage(mcLocale.getString("mcPlayerListener.MOTD", new Object[] {plugin.getDescription().getVersion(), LoadProperties.mcmmo}));
			//player.sendMessage(ChatColor.GREEN+"http://mcmmo.wikia.com"+ChatColor.BLUE+" - mcMMO Wiki");
			player.sendMessage(mcLocale.getString("mcPlayerListener.WIKI"));
		}
	}

	@SuppressWarnings("deprecation")
	public void onPlayerInteract(PlayerInteractEvent event) {
		

		Player player = event.getPlayer();
		PlayerProfile PP = Users.getProfile(player);
		Action action = event.getAction();
		Block block = event.getClickedBlock();



		//Archery Nerf
		if(player.getItemInHand().getTypeId() == 261 && LoadProperties.archeryFireRateLimit){
			if(System.currentTimeMillis() < PP.getArcheryShotATS() + 1000){
				/*
    			if(m.hasArrows(player))
    				m.addArrows(player);
				 */
				player.updateInventory();
				event.setCancelled(true);
			} else {
				PP.setArcheryShotATS(System.currentTimeMillis());
			}
		}

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
					player.sendMessage(mcLocale.getString("mcPlayerListener.MyspawnSet"));
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
			if(block != null && (block.getType() == Material.COBBLESTONE || block.getType() == Material.DIRT) && player.getItemInHand().getType() == Material.SEEDS)
			{
				boolean pass = false;
				if(Herbalism.hasSeeds(player) && mcPermissions.getInstance().herbalism(player)){
					Herbalism.removeSeeds(player);
					if(LoadProperties.enableCobbleToMossy && m.blockBreakSimulate(block, player, plugin) && block.getType() == Material.COBBLESTONE && Math.random() * 1500 <= PP.getSkillLevel(SkillType.HERBALISM)){
						player.sendMessage(mcLocale.getString("mcPlayerListener.GreenThumb"));
						block.setType(Material.MOSSY_COBBLESTONE);
						pass = true;
					}
					if(block.getType() == Material.DIRT && m.blockBreakSimulate(block, player, plugin) && Math.random() * 1500 <= PP.getSkillLevel(SkillType.HERBALISM)){
						player.sendMessage(mcLocale.getString("mcPlayerListener.GreenThumb"));
						block.setType(Material.GRASS);
						pass = true;
					}
					if(pass == false)
						player.sendMessage(mcLocale.getString("mcPlayerListener.GreenThumbFail"));
				}
				return;
			}
		}
		if(action == Action.RIGHT_CLICK_AIR)
		{
			Skills.hoeReadinessCheck(player);
			Skills.abilityActivationCheck(player);

			/*
			 * HERBALISM MODIFIERS
			 */
			if(mcPermissions.getInstance().herbalism(player))
			{
				Herbalism.breadCheck(player, player.getItemInHand());
				Herbalism.stewCheck(player, player.getItemInHand());
			}
		}
		
		/*
		 * ITEM CHECKS
		 */
		if(action == Action.RIGHT_CLICK_AIR)
			Item.itemhecks(player, plugin);
		if(action == Action.RIGHT_CLICK_BLOCK)
		{
			if(m.abilityBlockCheck(event.getClickedBlock()))
				Item.itemhecks(player, plugin);
		}
	}

	public void onPlayerChat(PlayerChatEvent event) 
	{
		
		Player player = event.getPlayer();
		PlayerProfile PP = Users.getProfile(player);

		String x = ChatColor.GREEN + "(" + ChatColor.WHITE + player.getName() + ChatColor.GREEN + ") "; //$NON-NLS-1$ //$NON-NLS-2$
		String y = ChatColor.AQUA + "{" + ChatColor.WHITE + player.getName() + ChatColor.AQUA + "} "; //$NON-NLS-1$ //$NON-NLS-2$

		if(PP.getPartyChatMode())
		{
			event.setCancelled(true);
			log.log(Level.INFO, "[P]("+PP.getParty()+")"+"<"+player.getName()+"> "+event.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			for(Player herp : plugin.getServer().getOnlinePlayers())
			{
				if(Users.getProfile(herp).inParty())
				{
					if(Party.getInstance().inSameParty(herp, player))
					{
						herp.sendMessage(x+event.getMessage());
					}
				}
			}
			return;
		}

		if((player.isOp() || mcPermissions.getInstance().adminChat(player)) && PP.getAdminChatMode())
		{
			log.log(Level.INFO, "[A]"+"<"+player.getName()+"> "+event.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			event.setCancelled(true);
			for(Player herp : plugin.getServer().getOnlinePlayers()){
				if((herp.isOp() || mcPermissions.getInstance().adminChat(herp))){
					herp.sendMessage(y+event.getMessage());
				}
			}
			return;
		}
	}
}