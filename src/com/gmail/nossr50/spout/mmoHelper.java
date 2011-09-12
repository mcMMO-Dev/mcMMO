/*
 * This file is from mmoMinecraft (http://code.google.com/p/mmo-minecraft/).
 * 
 * mmoMinecraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.gmail.nossr50.spout;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.*;
import org.getspout.spoutapi.gui.Container;
import org.getspout.spoutapi.gui.GenericContainer;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.spout.util.GenericLivingEntity;

public class mmoHelper 
{
	
	/**
	 * A map of player containers, each container is their party bar
	 */
	public static HashMap<Player, GenericContainer> containers = new HashMap<Player, GenericContainer>();
	
	/**
	 * Get the percentage health of a Player.
	 * @param player The Player we're interested in
	 * @return The percentage of max health
	 */
	public static int getHealth(Entity player) {
		if (player != null && player instanceof LivingEntity) {
			try {
				return Math.min(((LivingEntity) player).getHealth() * 5, 100);
			} catch (Exception e) {
			}
		}
		return 0;
	}
	
	/**
	 * Get the colour of a LivingEntity target from a player's point of view.
	 * @param player The player viewing the target
	 * @param target The target to name
	 * @return The name to use
	 */
	public static String getColor(Player player, LivingEntity target) {
		if (target instanceof Player) {
			if (((Player) target).isOp()) {
				return ChatColor.GOLD.toString();
			}
			return ChatColor.YELLOW.toString();
		} else {
			if (target instanceof Monster) {
				if (player != null && player.equals(((Monster) target).getTarget())) {
					return ChatColor.RED.toString();
				} else {
					return ChatColor.YELLOW.toString();
				}
			} else if (target instanceof WaterMob) {
				return ChatColor.GREEN.toString();
			} else if (target instanceof Flying) {
				return ChatColor.YELLOW.toString();
			} else if (target instanceof Animals) {
				if (player != null && player.equals(((Animals) target).getTarget())) {
					return ChatColor.RED.toString();
				} else if (target instanceof Tameable) {
					Tameable pet = (Tameable) target;
					if (pet.isTamed()) {
						return ChatColor.GREEN.toString();
					} else {
						return ChatColor.YELLOW.toString();
					}
				} else {
					return ChatColor.GRAY.toString();
				}
			} else {
				return ChatColor.GRAY.toString();
			}
		}
	}

	/**
	 * Get the percentage armour of a Player.
	 * @param player The Player we're interested in
	 * @return The percentage of max armour
	 */
	public static int getArmor(Entity player) {
		if (player != null && player instanceof Player) {
			int armor = 0, max, multi[] = {15, 30, 40, 15};
			ItemStack inv[] = ((Player) player).getInventory().getArmorContents();
			for (int i = 0; i < inv.length; i++) {
				max = inv[i].getType().getMaxDurability();
				if (max >= 0) {
					armor += multi[i] * (max - inv[i].getDurability()) / max;
				}
			}
			return armor;
		}
		return 0;
	}
	
	public static String getSimpleName(LivingEntity target, boolean showOwner) {
		String name = "";
		if (target instanceof Player) {
			if (LoadProperties.showDisplayName) {
				name += ((Player) target).getName();
			} else {
				name += ((Player) target).getDisplayName();
			}
		} else if (target instanceof HumanEntity) {
			name += ((HumanEntity) target).getName();
		} else {
			if (target instanceof Tameable) {
				if (((Tameable) target).isTamed()) {
					if (showOwner && ((Tameable) target).getOwner() instanceof Player) {
						if (LoadProperties.showDisplayName) {
							name += ((Player) ((Tameable) target).getOwner()).getName() + "'s ";
						} else {
							name += ((Player) ((Tameable) target).getOwner()).getDisplayName() + "'s ";
						}
					} else {
						name += "Pet ";
					}
				}
			}
			if (target instanceof Chicken) {
				name += "Chicken";
			} else if (target instanceof Cow) {
				name += "Cow";
			} else if (target instanceof Creeper) {
				name += "Creeper";
			} else if (target instanceof Ghast) {
				name += "Ghast";
			} else if (target instanceof Giant) {
				name += "Giant";
			} else if (target instanceof Pig) {
				name += "Pig";
			} else if (target instanceof PigZombie) {
				name += "PigZombie";
			} else if (target instanceof Sheep) {
				name += "Sheep";
			} else if (target instanceof Slime) {
				name += "Slime";
			} else if (target instanceof Skeleton) {
				name += "Skeleton";
			} else if (target instanceof Spider) {
				name += "Spider";
			} else if (target instanceof Squid) {
				name += "Squid";
			} else if (target instanceof Wolf) {
				name += "Wolf";
			} else if (target instanceof Zombie) {
				name += "Zombie";
			} else if (target instanceof Monster) {
				name += "Monster";
			} else if (target instanceof Creature) {
				name += "Creature";
			} else {
				name += "Unknown";
			}
		}
		return name;
	}
	
	public static LivingEntity[] getPets(HumanEntity player) {
		ArrayList<LivingEntity> pets = new ArrayList<LivingEntity>();
		if (player != null && (!(player instanceof Player) || ((Player) player).isOnline())) {
			String name = player.getName();
			for (World world : Bukkit.getServer().getWorlds()) {
				for (LivingEntity entity : world.getLivingEntities()) {
					if (entity instanceof Tameable && ((Tameable) entity).isTamed() && ((Tameable) entity).getOwner() instanceof Player) {
						if (name.equals(((Player) ((Tameable) entity).getOwner()).getName())) {
							pets.add(entity);
						}
					}
				}
			}
		}
		LivingEntity[] list = new LivingEntity[pets.size()];
		pets.toArray(list);
		return list;
	}
	
	public static void update(Player player) 
	{
		//boolean show_pets = true;
		Container container = containers.get(player);
		
		if (container != null) 
		{
			int index = 0;
			Widget[] bars = container.getChildren();
			for (String name : Party.getInstance().getPartyMembersByName(player).meFirst(player.getName())) 
			{
				GenericLivingEntity bar;
				if (index >= bars.length) 
				{
					container.addChild(bar = new GenericLivingEntity());
				} else {
					bar = (GenericLivingEntity)bars[index];
				}
				bar.setEntity(name, Party.getInstance().isPartyLeader(Bukkit.getServer().getPlayer(name), Users.getProfile(Bukkit.getServer().getPlayer(name)).getParty()) ? ChatColor.GREEN + "@" : "");
				//bar.setTargets(show_pets ? getPets(Bukkit.getServer().getPlayer(name)) : null);
				index++;
			}
			while (index < bars.length) {
				container.removeChild(bars[index++]);
			}
			container.updateLayout();
		}
	}
	
	public static void initialize(SpoutPlayer sPlayer, Plugin plugin)
	{
		GenericContainer container = new GenericContainer();
		
		container.setAlign(WidgetAnchor.TOP_LEFT)
			.setAnchor(WidgetAnchor.TOP_LEFT)
			.setX(3)
			.setY(3)
			.setWidth(427)
			.setHeight(240)
			.setFixed(true);
		
		mmoHelper.containers.put(sPlayer, container);
		
		sPlayer.getMainScreen().attachWidget(plugin, container);
	}
	/**
	 * Update all parties.
	 */
	public static void updateAll() {
		for(Player x : Bukkit.getServer().getOnlinePlayers())
		{
			if(Users.getProfile(x).inParty())
			{
				update(x);
			}
		}
	}
	
}
