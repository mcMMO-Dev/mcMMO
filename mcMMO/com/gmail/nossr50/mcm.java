package com.gmail.nossr50;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.FakeBlockBreakEvent;

public class mcm {
	/*
	 * I'm storing my functions/methods in here in an unorganized manner. Spheal with it.
	 */
	private static mcMMO plugin;
	private static mcMMO pluginx;
	public mcm(mcMMO instance) {
    	plugin = instance;
    }
	
	public static int getPowerLevel(Player player){
		PlayerProfile PP = mcUsers.getProfile(player);
		int x = 0;
		if(mcPermissions.getInstance().mining(player))
			x+=PP.getMiningInt();
		if(mcPermissions.getInstance().woodcutting(player))
			x+=PP.getWoodCuttingInt();
		if(mcPermissions.getInstance().unarmed(player))
			x+=PP.getUnarmedInt();
		if(mcPermissions.getInstance().herbalism(player))
			x+=PP.getHerbalismInt();
		if(mcPermissions.getInstance().excavation(player))
			x+=PP.getExcavationInt();
		if(mcPermissions.getInstance().archery(player))
			x+=PP.getArcheryInt();
		if(mcPermissions.getInstance().swords(player))
			x+=PP.getSwordsInt();
		if(mcPermissions.getInstance().axes(player))
			x+=PP.getAxesInt();
		if(mcPermissions.getInstance().acrobatics(player))
			x+=PP.getAcrobaticsInt();
		if(mcPermissions.getInstance().repair(player))
			x+=PP.getRepairInt();
		return x;
	}
	public static boolean blockBreakSimulate(Block block, Player player, Plugin plugin){

    	FakeBlockBreakEvent event = new FakeBlockBreakEvent(block, player);
    	if(block != null && plugin != null && player != null){
    		plugin.getServer().getPluginManager().callEvent(event);
	    	if(!event.isCancelled())
	    	{
	    		return true; //Return true if not cancelled
	    	} else {
	    		return false; //Return false if cancelled
	    	}
    	} else {
    		return false; //Return false if something went wrong
    	}
    }
	
	public static void damageTool(Player player, short damage){
		if(player.getItemInHand().getTypeId() == 0)
			return;
		player.getItemInHand().setDurability((short) (player.getItemInHand().getDurability() + damage));
		if(player.getItemInHand().getDurability() >= getMaxDurability(getTier(player), player.getItemInHand())){
			ItemStack[] inventory = player.getInventory().getContents();
	    	for(ItemStack x : inventory){
	    		if(x != null && x.getTypeId() == player.getItemInHand().getTypeId() && x.getDurability() == player.getItemInHand().getDurability()){
	    			x.setTypeId(0);
	    			x.setAmount(0);
	    			player.getInventory().setContents(inventory);
	    			return;
	    		}
	    	}
		}
	}
	public boolean hasArrows(Player player){
		for(ItemStack x : player.getInventory().getContents()){
			if(x.getTypeId() == 262)
				return true;
		}
		return false;
	}
	public void addArrows(Player player){
		ItemStack[] inventory = player.getInventory().getContents();
    	for(ItemStack x : inventory){
    		if(x != null && x.getTypeId() == 262){
    			if(x.getAmount() >= 1 && x.getAmount() < 64){
    				x.setAmount(x.getAmount() + 1);
    				player.getInventory().setContents(inventory);
    			}
    			return;
    		}
    	}
	}
	public static Integer getTier(Player player){
		int i = player.getItemInHand().getTypeId();
		if(i == 268 || i == 269 || i == 270 || i == 271 || i == 290){
			return 1; //WOOD
		} else if (i == 272 || i == 273 || i == 274 || i == 275 || i == 291){
			return 2; //STONE
		} else if (i == 256 || i == 257 || i == 258 || i == 267 || i == 292){
			return 3; //IRON
		} else if (i == 283 || i == 284 || i == 285 || i == 286 || i == 294){
			return 1; //GOLD
		} else if (i == 276 || i == 277 || i == 278 || i == 279 || i == 293){
			return 4; //DIAMOND
		} else {
			return 1; //UNRECOGNIZED
		}
	}
	public static Integer getMaxDurability(Integer tier, ItemStack item){
		int id = item.getTypeId();
		if(tier == 1){
			if((id == 276 || id == 277 || id == 278 || id == 279 || id == 293)){
				return 33;
			} else {
				return 60;
			}
		} else if (tier == 2){
			return 132;
		} else if (tier == 3){
			return 251;
		} else if (tier == 4){
			return 1562;
		} else {
			return 0;
		}
	}
	public static double getDistance(Location loca, Location locb)
    {
	return Math.sqrt(Math.pow(loca.getX() - locb.getX(), 2) + Math.pow(loca.getY() - locb.getY(), 2)
    + Math.pow(loca.getZ() - locb.getZ(), 2));
    }
	public static boolean abilityBlockCheck(Block block){
		int i = block.getTypeId();
		if(i == 68 || i == 355 || i == 26 || i == 323 || i == 25 || i == 54 || i == 69 || i == 92 || i == 77 || i == 58 || i == 61 || i == 62 || i == 42 || i == 71 || i == 64 || i == 84 || i == 324 || i == 330){
			return false;
		} else {
			return true;
		}
	}
	public static boolean isBlockAround(Location loc, Integer radius, Integer typeid){
		Block blockx = loc.getBlock();
    	int ox = blockx.getX();
        int oy = blockx.getY();
        int oz = blockx.getZ();
    	for (int cx = -radius; cx <= radius; cx++) {
            for (int cy = -radius; cy <= radius; cy++) {
                for (int cz = -radius; cz <= radius; cz++) {
                    Block block = loc.getWorld().getBlockAt(ox + cx, oy + cy, oz + cz);
                    //If block is block
                    if (block.getTypeId() == typeid) {
                        return true;
                    }
                }
            }
        }
    	return false;
	}
	public static boolean isPvpEnabled(){
		String propertyName = "pvp";
		FileReader fr = null;
		try {
			fr = new FileReader("server.properties");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(fr);
		String property;
		String s = null;
		try {
			while((s=br.readLine()) .indexOf(propertyName)==-1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		property = s.split("=")[1];
		try {
			fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(property.toLowerCase().equals("true")){
			return true;
		} else {
			return false;
		}
	}
	public static boolean shouldBeWatched(Block block){
		int id = block.getTypeId();
		if(id == 49 || id == 81 || id == 83 || id == 86 || id == 91 || id == 1 || id == 17 || id == 42 || id == 87 || id == 89 || id == 2 || id == 3 || id == 12 || id == 13 || id == 21 || id == 15 || id == 14 || id == 56 || id == 38 || id == 37 || id == 39 || id == 40 || id == 24){
			return true;
		} else {
			return false;
		}
	}
    public static Integer calculateHealth(Integer health, Integer newvalue){
    	if((health + newvalue) > 20){
    		return 20;
    	} else {
    		return health+newvalue;
    	}
    }
    public Integer calculateMinusHealth(Integer health, Integer newvalue){
    	if((health - newvalue) < 1){
    		return 0;
    	} else {
    		return health-newvalue;
    	}
    }
    public static Integer getHealth(Entity entity){
    	if(entity instanceof Monster){
    		Monster monster = (Monster)entity;
    		return monster.getHealth();
    	} else if (entity instanceof Animals){
    		Animals animals = (Animals)entity;
    		return animals.getHealth();
    	} else if (entity instanceof Player){
    		Player player = (Player)entity;
    		return player.getHealth();
    	} else {
    		return 0;
    	}
    }
    public static boolean isInt(String string){
		try {
		    int x = Integer.parseInt(string);
		}
		catch(NumberFormatException nFE) {
		    return false;
		}
		return true;
	}
    public static void mcDropItem(Location loc, int id){
    	if(loc != null){
    	Material mat = Material.getMaterial(id);
		byte damage = 0;
		ItemStack item = new ItemStack(mat, 1, (byte)0, damage);
		loc.getWorld().dropItemNaturally(loc, item);
    	}
    }
	
    public static boolean isSwords(ItemStack is){
    	if(is.getTypeId() == 268 || is.getTypeId() == 267 || is.getTypeId() == 272 || is.getTypeId() == 283 || is.getTypeId() == 276){
    		return true;
    	} else {
    		return false;
    	}
    }
    public static boolean isHoe(ItemStack is){
    	int id = is.getTypeId();
    	if(id == 290 || id == 291 || id == 292 || id == 293 || id == 294){
    		return true;
    	} else {
    		return false;
    	}
    }
    public static boolean isShovel(ItemStack is){
    	if(is.getTypeId() == 269 || is.getTypeId() == 273 || is.getTypeId() == 277 || is.getTypeId() == 284 || is.getTypeId() == 256){
    		return true;
    	} else {
    		return false;
    	}
    }
    public static boolean isAxes(ItemStack is){
    	if(is.getTypeId() == 271 || is.getTypeId() == 258 || is.getTypeId() == 286 || is.getTypeId() == 279 || is.getTypeId() == 275){
    		return true;
    	} else {
    		return false;
    	}
    }
    public static boolean isMiningPick(ItemStack is){
    	if(is.getTypeId() == 270 || is.getTypeId() == 274 || is.getTypeId() == 285 || is.getTypeId() == 257 || is.getTypeId() == 278){
    		return true;
    	} else {
    		return false;
    	}
    }
    public boolean isGold(ItemStack is){
    	int i = is.getTypeId();
    	if(i == 283 || i == 284 || i == 285 || i == 286 || i == 294 || i == 314 || i == 315 || i == 316 || i == 317){
    		return true;
    	} else {
    		return false;
    	}
    }
    public static void mcmmoHelpCheck(String[] split, Player player, PlayerChatEvent event){
    	PlayerProfile PP = mcUsers.getProfile(player);
    	if(split[0].equalsIgnoreCase("/woodcutting")){
			event.setCancelled(true);
			float skillvalue = (float)PP.getWoodCuttingInt();
			int ticks = 2;
			int x = PP.getWoodCuttingInt();
    		while(x >= 50){
    			x-=50;
    			ticks++;
    		}
    		String percentage = String.valueOf((skillvalue / 1000) * 100);
			player.sendMessage(ChatColor.RED+"-----[]"+ChatColor.GREEN+"WOODCUTTING"+ChatColor.RED+"[]-----");
			player.sendMessage(ChatColor.DARK_GRAY+"XP GAIN: "+ChatColor.WHITE+"Chopping down trees");
			player.sendMessage(ChatColor.RED+"---[]"+ChatColor.GREEN+"EFFECTS"+ChatColor.RED+"[]---");
			player.sendMessage(ChatColor.DARK_AQUA+"Tree Feller (ABILITY): "+ChatColor.GREEN+"Make trees explode");
			player.sendMessage(ChatColor.DARK_AQUA+"Double Drops: "+ChatColor.YELLOW+ChatColor.GREEN+"Double the normal loot");
			player.sendMessage(ChatColor.RED+"---[]"+ChatColor.GREEN+"YOUR STATS"+ChatColor.RED+"[]---");
			player.sendMessage(ChatColor.RED+"Double Drop Chance: "+ChatColor.YELLOW+percentage+"%");
			player.sendMessage(ChatColor.RED+"Tree Feller Length: "+ChatColor.YELLOW+ticks+"s");
    	}
    	if(split[0].equalsIgnoreCase("/archery")){
			event.setCancelled(true);
			Integer rank = 0;
			if(PP.getArcheryInt() >= 50)
    			rank++;
    		if(PP.getArcheryInt() >= 250)
    			rank++;
    		if(PP.getArcheryInt() >= 575)
    			rank++;
    		if(PP.getArcheryInt() >= 725)
    			rank++;
    		if(PP.getArcheryInt() >= 1000)
    			rank++;
			float skillvalue = (float)PP.getArcheryInt();
    		String percentage = String.valueOf((skillvalue / 1000) * 100);
    		
    		int ignition = 20;
			if(PP.getArcheryInt() >= 200)
				ignition+=20;
			if(PP.getArcheryInt() >= 400)
				ignition+=20;
			if(PP.getArcheryInt() >= 600)
				ignition+=20;
			if(PP.getArcheryInt() >= 800)
				ignition+=20;
			if(PP.getArcheryInt() >= 1000)
				ignition+=20;
			
    		String percentagedaze;
			if(PP.getArcheryInt() < 1000){
				percentagedaze = String.valueOf((skillvalue / 2000) * 100);
			} else {
				percentagedaze = "50";
			}
			player.sendMessage(ChatColor.RED+"-----[]"+ChatColor.GREEN+"ARCHERY"+ChatColor.RED+"[]-----");
			player.sendMessage(ChatColor.DARK_GRAY+"XP GAIN: "+ChatColor.WHITE+"Attacking Monsters");
			player.sendMessage(ChatColor.RED+"---[]"+ChatColor.GREEN+"EFFECTS"+ChatColor.RED+"[]---");
			player.sendMessage(ChatColor.DARK_AQUA+"Ignition: "+ChatColor.GREEN+"25% Chance Enemies will ignite");
			player.sendMessage(ChatColor.DARK_AQUA+"Daze (Players): "+ChatColor.GREEN+"Disorients foes");
			player.sendMessage(ChatColor.DARK_AQUA+"Damage+: "+ChatColor.GREEN+"Modifies Damage");
			player.sendMessage(ChatColor.DARK_AQUA+"Arrow Retrieval: "+ChatColor.GREEN+"Chance to retrieve arrows from corpses");
			player.sendMessage(ChatColor.RED+"---[]"+ChatColor.GREEN+"YOUR STATS"+ChatColor.RED+"[]---");
			player.sendMessage(ChatColor.RED+"Chance to Daze: "+ChatColor.YELLOW+percentagedaze+"%");
			player.sendMessage(ChatColor.RED+"Chance to Retrieve Arrows: "+ChatColor.YELLOW+percentage+"%");
			player.sendMessage(ChatColor.RED+"Length of Ignition: "+ChatColor.YELLOW+(ignition / 20)+" seconds");
			player.sendMessage(ChatColor.RED+"Damage+ (Rank"+rank+"): Bonus "+rank+" damage");
    	}
    	if(split[0].equalsIgnoreCase("/axes")){
			event.setCancelled(true);
			String percentage;
			float skillvalue = (float)PP.getAxesInt();
			if(PP.getAxesInt() < 750){
				percentage = String.valueOf((skillvalue / 1000) * 100);
			} else {
				percentage = "75";
			}
			int ticks = 2;
			int x = PP.getAxesInt();
    		while(x >= 50){
    			x-=50;
    			ticks++;
    		}
    		
			player.sendMessage(ChatColor.RED+"-----[]"+ChatColor.GREEN+"AXES"+ChatColor.RED+"[]-----");
			player.sendMessage(ChatColor.DARK_GRAY+"XP GAIN: "+ChatColor.WHITE+"Attacking Monsters");
			player.sendMessage(ChatColor.RED+"---[]"+ChatColor.GREEN+"EFFECTS"+ChatColor.RED+"[]---");
			player.sendMessage(ChatColor.DARK_AQUA+"Skull Splitter (ABILITY): "+ChatColor.GREEN+"Deal AoE Damage");
			player.sendMessage(ChatColor.DARK_AQUA+"Critical Strikes: "+ChatColor.GREEN+"Double Damage");
			player.sendMessage(ChatColor.DARK_AQUA+"Axe Mastery (500 SKILL): "+ChatColor.GREEN+"Modifies Damage");
			player.sendMessage(ChatColor.RED+"---[]"+ChatColor.GREEN+"YOUR STATS"+ChatColor.RED+"[]---");
			player.sendMessage(ChatColor.RED+"Chance to crtically strike: "+ChatColor.YELLOW+percentage+"%");
			if(PP.getAxesInt() < 500){
				player.sendMessage(ChatColor.GRAY+"LOCKED UNTIL 500+ SKILL (AXEMASTERY)");
			} else {
				player.sendMessage(ChatColor.RED+"Axe Mastery:"+ChatColor.YELLOW+" Bonus 4 damage");
			}
			player.sendMessage(ChatColor.RED+"Skull Splitter Length: "+ChatColor.YELLOW+ticks+"s");
    	}
    	if(split[0].equalsIgnoreCase("/swords")){
			event.setCancelled(true);
			int bleedrank = 2;
			String percentage, parrypercentage = null, counterattackpercentage;
			float skillvalue = (float)PP.getSwordsInt();
			if(PP.getSwordsInt() < 750){
				percentage = String.valueOf((skillvalue / 1000) * 100);
			} else {
				percentage = "75";
			}
			if(skillvalue >= 750)
				bleedrank+=1;
			
			if(PP.getSwordsInt() <= 900){
				parrypercentage = String.valueOf((skillvalue / 3000) * 100);
			} else {
				parrypercentage = "30";
			}
			
			if(PP.getSwordsInt() <= 600){
				counterattackpercentage = String.valueOf((skillvalue / 2000) * 100);
			} else {
				counterattackpercentage = "30";
			}
			
			int ticks = 2;
			int x = PP.getSwordsInt();
    		while(x >= 50){
    			x-=50;
    			ticks++;
    		}
    		
			player.sendMessage(ChatColor.RED+"-----[]"+ChatColor.GREEN+"SWORDS"+ChatColor.RED+"[]-----");
			player.sendMessage(ChatColor.DARK_GRAY+"XP GAIN: "+ChatColor.WHITE+"Attacking Monsters");
			player.sendMessage(ChatColor.RED+"---[]"+ChatColor.GREEN+"EFFECTS"+ChatColor.RED+"[]---");
			player.sendMessage(ChatColor.DARK_AQUA+"Counter Attack: "+ChatColor.GREEN+"Reflect 50% of damage taken");
			player.sendMessage(ChatColor.DARK_AQUA+"Serrated Strikes (ABILITY): "+ChatColor.GREEN+"25% DMG AoE, Bleed+ AoE");
			player.sendMessage(ChatColor.DARK_GRAY+"Serrated Strikes Bleed+: "+ChatColor.GREEN+"5 Tick Bleed");
			player.sendMessage(ChatColor.DARK_AQUA+"Parrying: "+ChatColor.GREEN+"Negates Damage");
			player.sendMessage(ChatColor.DARK_AQUA+"Bleed: "+ChatColor.GREEN+"Apply a bleed DoT");
			player.sendMessage(ChatColor.RED+"---[]"+ChatColor.GREEN+"YOUR STATS"+ChatColor.RED+"[]---");
			player.sendMessage(ChatColor.RED+"Counter Attack Chance: "+ChatColor.YELLOW+counterattackpercentage+"%");
			player.sendMessage(ChatColor.RED+"Bleed Length: "+ChatColor.YELLOW+bleedrank+" ticks");
			player.sendMessage(ChatColor.GRAY+"NOTE: "+ChatColor.YELLOW+"1 Tick happens every 2 seconds");
			player.sendMessage(ChatColor.RED+"Bleed Chance: "+ChatColor.YELLOW+percentage+"%");
			player.sendMessage(ChatColor.RED+"Parry Chance: "+ChatColor.YELLOW+parrypercentage+"%");
			player.sendMessage(ChatColor.RED+"Serrated Strikes Length: "+ChatColor.YELLOW+ticks+"s");
			
    	}
    	if(split[0].equalsIgnoreCase("/acrobatics")){
			event.setCancelled(true);
			String dodgepercentage;
			float skillvalue = (float)PP.getAcrobaticsInt();
    		String percentage = String.valueOf((skillvalue / 1000) * 100);
    		String gracepercentage = String.valueOf(((skillvalue / 1000) * 100) * 2);
    		if(PP.getAcrobaticsInt() <= 800){
    			dodgepercentage = String.valueOf((skillvalue / 4000 * 100));
    		} else {
    			dodgepercentage = "20";
    		}
			player.sendMessage(ChatColor.RED+"-----[]"+ChatColor.GREEN+"ACROBATICS"+ChatColor.RED+"[]-----");
			player.sendMessage(ChatColor.DARK_GRAY+"XP GAIN: "+ChatColor.WHITE+"Falling");
			player.sendMessage(ChatColor.RED+"---[]"+ChatColor.GREEN+"EFFECTS"+ChatColor.RED+"[]---");
			player.sendMessage(ChatColor.DARK_AQUA+"Roll: "+ChatColor.GREEN+"Reduces or Negates damage");
			player.sendMessage(ChatColor.DARK_AQUA+"Graceful Roll: "+ChatColor.GREEN+"Twice as effective as Roll");
			player.sendMessage(ChatColor.DARK_AQUA+"Dodge: "+ChatColor.GREEN+"Reduce damage by half");
			player.sendMessage(ChatColor.RED+"---[]"+ChatColor.GREEN+"YOUR STATS"+ChatColor.RED+"[]---");
			player.sendMessage(ChatColor.RED+"Roll Chance: "+ChatColor.YELLOW+percentage+"%");
			player.sendMessage(ChatColor.RED+"Graceful Roll Chance: "+ChatColor.YELLOW+gracepercentage+"%");
			player.sendMessage(ChatColor.RED+"Dodge Chance: "+ChatColor.YELLOW+dodgepercentage+"%");
    	}
    	if(split[0].equalsIgnoreCase("/mining")){
    		float skillvalue = (float)PP.getMiningInt();
    		String percentage = String.valueOf((skillvalue / 1000) * 100);
    		int ticks = 2;
    		int x = PP.getMiningInt();
    		while(x >= 50){
    			x-=50;
    			ticks++;
    		}
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED+"-----[]"+ChatColor.GREEN+"MINING"+ChatColor.RED+"[]-----");
			player.sendMessage(ChatColor.DARK_GRAY+"XP GAIN: "+ChatColor.WHITE+"Mining Stone & Ore");
			player.sendMessage(ChatColor.RED+"---[]"+ChatColor.GREEN+"EFFECTS"+ChatColor.RED+"[]---");
			player.sendMessage(ChatColor.DARK_AQUA+"Super Breaker (ABILITY): "+ChatColor.GREEN+"Speed+, Triple Drop Chance");
			player.sendMessage(ChatColor.DARK_AQUA+"Double Drops: "+ChatColor.GREEN+"Double the normal loot");
			player.sendMessage(ChatColor.RED+"---[]"+ChatColor.GREEN+"YOUR STATS"+ChatColor.RED+"[]---");
			player.sendMessage(ChatColor.RED+"Double Drop Chance: "+ChatColor.YELLOW+percentage+"%");
			player.sendMessage(ChatColor.RED+"Super Breaker Length: "+ChatColor.YELLOW+ticks+"s");
    	}
    	if(split[0].equalsIgnoreCase("/repair")){
    		float skillvalue = (float)PP.getRepairInt();
    		String percentage = String.valueOf((skillvalue / 1000) * 100);
    		String repairmastery = String.valueOf((skillvalue / 500) * 100);
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED+"-----[]"+ChatColor.GREEN+"REPAIR"+ChatColor.RED+"[]-----");
			player.sendMessage(ChatColor.DARK_GRAY+"XP GAIN: "+ChatColor.WHITE+"Repairing");
			player.sendMessage(ChatColor.RED+"---[]"+ChatColor.GREEN+"EFFECTS"+ChatColor.RED+"[]---");
			player.sendMessage(ChatColor.DARK_AQUA+"Repair: "+ChatColor.GREEN+"Repair Iron Tools & Armor");
			player.sendMessage(ChatColor.DARK_AQUA+"Repair Mastery: "+ChatColor.GREEN+"Increased repair amount");
			player.sendMessage(ChatColor.DARK_AQUA+"Super Repair: "+ChatColor.GREEN+"Double effectiveness");
			player.sendMessage(ChatColor.DARK_AQUA+"Diamond Repair ("+mcLoadProperties.repairdiamondlevel+"+ SKILL): "+ChatColor.GREEN+"Repair Diamond Tools & Armor");
			player.sendMessage(ChatColor.RED+"---[]"+ChatColor.GREEN+"YOUR STATS"+ChatColor.RED+"[]---");
			player.sendMessage(ChatColor.RED+"Repair Mastery: "+ChatColor.YELLOW+"Extra "+repairmastery+"% durability restored");
			player.sendMessage(ChatColor.RED+"Super Repair Chance: "+ChatColor.YELLOW+percentage+"%");
    	}
    	if(split[0].equalsIgnoreCase("/unarmed")){
			event.setCancelled(true);
			String percentage, arrowpercentage;
			float skillvalue = (float)PP.getUnarmedInt();
			
			if(PP.getUnarmedInt() < 1000){
				percentage = String.valueOf((skillvalue / 4000) * 100);
			} else {
				percentage = "25";
			}
			
			if(PP.getUnarmedInt() < 1000){
				arrowpercentage = String.valueOf(((skillvalue / 1000) * 100) / 2);
			} else {
				arrowpercentage = "50";
			}
			
			
			int ticks = 2;
			int x = PP.getUnarmedInt();
    		while(x >= 50){
    			x-=50;
    			ticks++;
    		}
    		
			player.sendMessage(ChatColor.RED+"-----[]"+ChatColor.GREEN+"UNARMED"+ChatColor.RED+"[]-----");
			player.sendMessage(ChatColor.DARK_GRAY+"XP GAIN: "+ChatColor.WHITE+"Attacking Monsters");
			player.sendMessage(ChatColor.RED+"---[]"+ChatColor.GREEN+"EFFECTS"+ChatColor.RED+"[]---");
			player.sendMessage(ChatColor.DARK_AQUA+"Berserk (ABILITY): "+ChatColor.GREEN+"+50% DMG, Breaks weak materials");
			player.sendMessage(ChatColor.DARK_AQUA+"Disarm (Players): "+ChatColor.GREEN+"Drops the foes item held in hand");
			player.sendMessage(ChatColor.DARK_AQUA+"Unarmed Mastery: "+ChatColor.GREEN+"Large Damage Upgrade");
			player.sendMessage(ChatColor.DARK_AQUA+"Unarmed Apprentice: "+ChatColor.GREEN+"Damage Upgrade");
			player.sendMessage(ChatColor.DARK_AQUA+"Arrow Deflect: "+ChatColor.GREEN+"Deflect arrows");
			player.sendMessage(ChatColor.RED+"---[]"+ChatColor.GREEN+"YOUR STATS"+ChatColor.RED+"[]---");
			player.sendMessage(ChatColor.RED+"Arrow Deflect Chance: "+ChatColor.YELLOW+arrowpercentage+"%");
			player.sendMessage(ChatColor.RED+"Disarm Chance: "+ChatColor.YELLOW+percentage+"%");
			if(PP.getUnarmedInt() < 250){
				player.sendMessage(ChatColor.GRAY+"LOCKED UNTIL 250+ SKILL (UNARMED APPRENTICE)");
			} else if(PP.getUnarmedInt() >= 250 && PP.getUnarmedInt() < 500){
				player.sendMessage(ChatColor.RED+"Unarmed Apprentice: "+ChatColor.YELLOW+"Damage Upgrade");
				player.sendMessage(ChatColor.GRAY+"LOCKED UNTIL 500+ SKILL (UNARMED MASTERY)");
			} else {
				player.sendMessage(ChatColor.RED+"Unarmed Mastery: "+ChatColor.YELLOW+"Large Damage Upgrade");
			}
			player.sendMessage(ChatColor.RED+"Berserk Length: "+ChatColor.YELLOW+ticks+"s");
    	}
    	if(split[0].equalsIgnoreCase("/herbalism")){
			event.setCancelled(true);
			int rank = 0;
			if(PP.getHerbalismInt() >= 50)
    			rank++;
    		if (PP.getHerbalismInt() >= 150)
    			rank++;
    		if (PP.getHerbalismInt() >= 250)
    			rank++;
    		if (PP.getHerbalismInt() >= 350)
    			rank++;
    		if (PP.getHerbalismInt() >= 450)
    			rank++;
    		if (PP.getHerbalismInt() >= 550)
    			rank++;
    		if (PP.getHerbalismInt() >= 650)
    			rank++;
    		if (PP.getHerbalismInt() >= 750)
    			rank++;
    		int bonus = 0;
    		if(PP.getHerbalismInt() >= 200)
    			bonus++;
    		if(PP.getHerbalismInt() >= 400)
    			bonus++;
    		if(PP.getHerbalismInt() >= 600)
    			bonus++;
    		
    		int ticks = 2;
			int x = PP.getHerbalismInt();
    		while(x >= 50){
    			x-=50;
    			ticks++;
    		}
    		
			float skillvalue = (float)PP.getHerbalismInt();
    		String percentage = String.valueOf((skillvalue / 1000) * 100);
    		String gpercentage = String.valueOf((skillvalue / 1500) * 100);
			player.sendMessage(ChatColor.RED+"-----[]"+ChatColor.GREEN+"HERBALISM"+ChatColor.RED+"[]-----");
			player.sendMessage(ChatColor.DARK_GRAY+"XP GAIN: "+ChatColor.WHITE+"Harvesting Herbs");
			player.sendMessage(ChatColor.RED+"---[]"+ChatColor.GREEN+"EFFECTS"+ChatColor.RED+"[]---");
			player.sendMessage(ChatColor.DARK_AQUA+"Green Terra (ABILITY): "+ChatColor.GREEN+"Spread the Terra, 3x Drops");
			player.sendMessage(ChatColor.DARK_AQUA+"Green Thumb (Wheat): "+ChatColor.GREEN+"Auto-Plants wheat when harvesting");
			player.sendMessage(ChatColor.DARK_AQUA+"Green Thumb (Cobble): "+ChatColor.GREEN+"Cobblestone -> Mossy w/ Seeds");
			player.sendMessage(ChatColor.DARK_AQUA+"Food+: "+ChatColor.GREEN+"Modifies health received from bread/stew");
			player.sendMessage(ChatColor.DARK_AQUA+"Double Drops (All Herbs): "+ChatColor.GREEN+"Double the normal loot");
			player.sendMessage(ChatColor.RED+"---[]"+ChatColor.GREEN+"YOUR STATS"+ChatColor.RED+"[]---");
			player.sendMessage(ChatColor.RED+"Green Terra Length: "+ChatColor.YELLOW+ticks+"s");
			player.sendMessage(ChatColor.RED+"Green Thumb Chance: "+gpercentage+"%");
			player.sendMessage(ChatColor.RED+"Green Thumb Stage: Wheat grows in stage "+bonus);
			player.sendMessage(ChatColor.RED+"Double Drop Chance: "+percentage+"%");
			player.sendMessage(ChatColor.RED+"Food+ (Rank"+rank+"): Bonus "+rank+" healing");
    	}
    	if(split[0].equalsIgnoreCase("/excavation")){
			event.setCancelled(true);
			int ticks = 2;
			int x = PP.getExcavationInt();
    		while(x >= 50){
    			x-=50;
    			ticks++;
    		}
			player.sendMessage(ChatColor.RED+"-----[]"+ChatColor.GREEN+"EXCAVATION"+ChatColor.RED+"[]-----");
			player.sendMessage(ChatColor.DARK_GRAY+"XP GAIN: "+ChatColor.WHITE+"Digging and finding treasures");
			player.sendMessage(ChatColor.RED+"---[]"+ChatColor.GREEN+"EFFECTS"+ChatColor.RED+"[]---");
			player.sendMessage(ChatColor.DARK_AQUA+"Giga Drill Breaker (ABILITY): "+ChatColor.GREEN+"3x Drop Rate, 3x EXP, +Speed");
			player.sendMessage(ChatColor.DARK_AQUA+"Treasure Hunter: "+ChatColor.GREEN+"Ability to dig for treasure");
			player.sendMessage(ChatColor.RED+"---[]"+ChatColor.GREEN+"YOUR STATS"+ChatColor.RED+"[]---");
			player.sendMessage(ChatColor.RED+"Giga Drill Breaker Length: "+ChatColor.YELLOW+ticks+"s");
    	}
		if(split[0].equalsIgnoreCase("/"+mcLoadProperties.mcmmo)){
			event.setCancelled(true);
    		player.sendMessage(ChatColor.RED+"-----[]"+ChatColor.GREEN+"mcMMO"+ChatColor.RED+"[]-----");
    		player.sendMessage(ChatColor.YELLOW+"mcMMO is an RPG server mod for minecraft.");
    		player.sendMessage(ChatColor.YELLOW+"There are many skills added by mcMMO to minecraft.");
    		player.sendMessage(ChatColor.YELLOW+"They can do anything from giving a chance");
    		player.sendMessage(ChatColor.YELLOW+"for double drops to letting you break materials instantly.");
    		player.sendMessage(ChatColor.YELLOW+"For example, by harvesting logs from trees you will gain");
    		player.sendMessage(ChatColor.YELLOW+"Woodcutting xp and once you have enough xp you will gain");
    		player.sendMessage(ChatColor.YELLOW+"a skill level in Woodcutting. By raising this skill you will");
    		player.sendMessage(ChatColor.YELLOW+"be able to receive benefits like "+ChatColor.RED+"double drops");
    		player.sendMessage(ChatColor.YELLOW+"and increase the effects of the "+ChatColor.RED+"\"Tree Felling\""+ChatColor.YELLOW+" ability.");
    		player.sendMessage(ChatColor.YELLOW+"mcMMO has abilities related to the skill, skills normally");
    		player.sendMessage(ChatColor.YELLOW+"provide passive bonuses but they also have activated");
    		player.sendMessage(ChatColor.YELLOW+"abilities too. Each ability is activated by holding");
    		player.sendMessage(ChatColor.YELLOW+"the appropriate tool and "+ChatColor.RED+"right clicking.");
    		player.sendMessage(ChatColor.YELLOW+"For example, if you hold a Mining Pick and right click");
    		player.sendMessage(ChatColor.YELLOW+"you will ready your Pickaxe, attack mining materials");
    		player.sendMessage(ChatColor.YELLOW+"and then "+ChatColor.RED+"Super Breaker "+ChatColor.YELLOW+"will activate.");
    		player.sendMessage(ChatColor.GREEN+"Find out mcMMO commands with "+ChatColor.DARK_AQUA+"/"+mcLoadProperties.mcc);
    		player.sendMessage(ChatColor.GREEN+"You can donate via paypal to"+ChatColor.DARK_RED+" nossr50@gmail.com");
    	}
    	if(split[0].equalsIgnoreCase("/"+mcLoadProperties.mcc)){
    		event.setCancelled(true);
    		player.sendMessage(ChatColor.RED+"---[]"+ChatColor.YELLOW+"mcMMO Commands"+ChatColor.RED+"[]---");
    		player.sendMessage("/"+mcLoadProperties.stats+ChatColor.RED+" - View your mcMMO stats");
    		if(mcPermissions.getInstance().party(player)){
    			player.sendMessage(ChatColor.GREEN+"--PARTY COMMANDS--");
    			player.sendMessage("/"+mcLoadProperties.party+" [party name] "+ChatColor.RED+"- Create/Join designated party");
    			player.sendMessage("/"+mcLoadProperties.party+" q "+ChatColor.RED+"- Leave your current party");
    			if(mcPermissions.getInstance().partyChat(player))
    				player.sendMessage("/p "+ChatColor.RED+" - Toggle Party Chat");
    			player.sendMessage("/"+mcLoadProperties.invite+" [player name] "+ChatColor.RED+"- Send party invite");
    			player.sendMessage("/"+mcLoadProperties.accept+" "+ChatColor.RED+"- Accept party invite");
    			if(mcPermissions.getInstance().partyTeleport(player))
    				player.sendMessage("/"+mcLoadProperties.ptp+" [party member name] "+ChatColor.RED+"- Teleport to party member");
    		}
    		if(mcPermissions.getInstance().mySpawn(player)){
	    		player.sendMessage(ChatColor.GREEN+"--MYSPAWN COMMANDS--");
	    		player.sendMessage("/"+mcLoadProperties.myspawn+" "+ChatColor.RED+"- Clears inventory & teleports to myspawn");
	    		player.sendMessage("/"+mcLoadProperties.clearmyspawn+" "+ChatColor.RED+"- Clears your MySpawn");
    		}
    		player.sendMessage(ChatColor.GREEN+"--OTHER COMMANDS--");
    		player.sendMessage("/mctop <skillname> <page> "+ChatColor.RED+"- Leaderboards");
    		if(mcPermissions.getInstance().mcAbility(player))
    			player.sendMessage("/"+mcLoadProperties.mcability+ChatColor.RED+" - Toggle ability activation with right click");
    		if(mcPermissions.getInstance().adminChat(player)){
    			player.sendMessage("/a "+ChatColor.RED+"- Toggle admin chat");
    		}
    		if(mcPermissions.getInstance().whois(player))
    			player.sendMessage("/"+mcLoadProperties.whois+" [playername] "+ChatColor.RED+"- View detailed player info");
    		if(mcPermissions.getInstance().mmoedit(player)){
    			//player.sendMessage("/"+mcLoadProperties.mmoedit+" [skill] [newvalue] "+ChatColor.RED+"Modify the designated skill value");
    			player.sendMessage("/"+mcLoadProperties.mmoedit+" [playername] [skill] [newvalue] "+ChatColor.RED+"- Modify target");
    		}
    		if(mcPermissions.getInstance().mcgod(player))
    			player.sendMessage("/"+mcLoadProperties.mcgod+ChatColor.RED+" - God Mode");
    		player.sendMessage("/[skillname] "+ChatColor.RED+" View detailed information about a skill");
    		player.sendMessage("/"+mcLoadProperties.mcmmo+" "+ChatColor.RED+"- Read brief mod description");
    	}
    }
}
