package com.gmail.nossr50.skills;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;


public class Excavation 
{
	public static void gigaDrillBreakerActivationCheck(Player player, Block block)
	{
		PlayerProfile PP = Users.getProfile(player);
		if(m.isShovel(player.getItemInHand()))
		{
	    	if(block != null)
	    	{
		    	if(!m.abilityBlockCheck(block))
		    		return;
	    	}
	    	if(PP.getShovelPreparationMode())
	    	{
    			PP.setShovelPreparationMode(false);
    		}
	    	int ticks = 2;
	    	int x = PP.getSkillLevel(SkillType.EXCAVATION);
    		while(x >= 50)
    		{
    			x-=50;
    			ticks++;
    		}
    		
	    	if(!PP.getGigaDrillBreakerMode() && PP.getGigaDrillBreakerDeactivatedTimeStamp() < System.currentTimeMillis())
	    	{
	    		player.sendMessage(mcLocale.getString("Skills.GigaDrillBreakerOn"));
	    		for(Player y : player.getWorld().getPlayers())
	    		{
	    			if(y != null && y != player && m.getDistance(player.getLocation(), y.getLocation()) < 10)
	    				y.sendMessage(mcLocale.getString("Skills.GigaDrillBreakerPlayer", new Object[] {player.getName()}));
	    		}
	    		PP.setGigaDrillBreakerActivatedTimeStamp(System.currentTimeMillis());
	    		PP.setGigaDrillBreakerDeactivatedTimeStamp(System.currentTimeMillis() + (ticks * 1000));
	    		PP.setGigaDrillBreakerMode(true);
	    	}
	    	
	    }
	}
	public static boolean canBeGigaDrillBroken(Block block)
	{
		return block.getType() == Material.DIRT || block.getType() == Material.GRASS || block.getType() == Material.SAND || block.getType() == Material.GRAVEL || block.getType() == Material.CLAY;
	}
	public static void excavationProcCheck(byte data, Material type, Location loc, Player player)
	{
		PlayerProfile PP = Users.getProfile(player);
    	ArrayList<ItemStack> is = new ArrayList<ItemStack>();
    	
    	int xp = 0;
    	
    	switch(type)
    	{
    	case GRASS:
    		if(PP.getSkillLevel(SkillType.EXCAVATION) >= 250)
    		{
	    		//CHANCE TO GET EGGS
	    		if(LoadProperties.eggs == true && Math.random() * 100 > 99)
	    		{
	    			xp+= LoadProperties.meggs;
					is.add(new ItemStack(Material.EGG, 1, (byte)0, (byte)0));
	    		}
	    		//CHANCE TO GET APPLES
	    		if(LoadProperties.apples == true && Math.random() * 100 > 99)
	    		{
	    			xp+= LoadProperties.mapple;
					is.add(new ItemStack(Material.APPLE, 1, (byte)0, (byte)0));
	    		}
    		}
    		break;
    	case GRAVEL:
    		//CHANCE TO GET NETHERRACK
    		if(LoadProperties.netherrack == true && PP.getSkillLevel(SkillType.EXCAVATION) >= 850 && Math.random() * 200 > 199)
    		{
    			xp+= LoadProperties.mnetherrack;
				is.add(new ItemStack(Material.NETHERRACK, 1, (byte)0, (byte)0));
				
    		}
    		//CHANCE TO GET SULPHUR
    		if(LoadProperties.sulphur == true && PP.getSkillLevel(SkillType.EXCAVATION) >= 75)
    		{
	    		if(Math.random() * 10 > 9)
	    		{
	    			xp+= LoadProperties.msulphur;
					is.add(new ItemStack(Material.SULPHUR, 1, (byte)0, (byte)0));
	    		}
    		}
    		//CHANCE TO GET BONES
    		if(LoadProperties.bones == true && PP.getSkillLevel(SkillType.EXCAVATION) >= 175)
    		{
        		if(Math.random() * 10 > 9)
        		{
        			xp+= LoadProperties.mbones;
    				is.add(new ItemStack(Material.BONE, 1, (byte)0, (byte)0));
        		}
        	}
    		break;
    	case SAND:
    		//CHANCE TO GET GLOWSTONE
    		if(LoadProperties.glowstone == true && PP.getSkillLevel(SkillType.EXCAVATION) >= 50 && Math.random() * 100 > 95)
    		{
    			xp+= LoadProperties.mglowstone2;
				is.add(new ItemStack(Material.GLOWSTONE_DUST, 1, (byte)0, (byte)0));
				
    		}
    		//CHANCE TO GET SOUL SAND
    		if(LoadProperties.slowsand == true && PP.getSkillLevel(SkillType.EXCAVATION) >= 650 && Math.random() * 200 > 199)
    		{
    			xp+= LoadProperties.mslowsand;
				is.add(new ItemStack(Material.SOUL_SAND, 1, (byte)0, (byte)0));
    		}
    		break;
    	case CLAY:
    		if(LoadProperties.slimeballs && PP.getSkillLevel(SkillType.EXCAVATION) >= 50)
    		{
    			if(Math.random() * 20 > 19)
    			{
    				xp+= LoadProperties.mslimeballs;
    				is.add(new ItemStack(Material.SLIME_BALL, 1, (byte)0, (byte)0));
    			}
    		}
    		if(LoadProperties.string && PP.getSkillLevel(SkillType.EXCAVATION) >= 250)
    		{
    			if(Math.random() * 20 > 19)
    			{
    				xp+= LoadProperties.mstring;
    				is.add(new ItemStack(Material.STRING, 1, (byte)0, (byte)0));
    			}
    		}
    		if(LoadProperties.watch && PP.getSkillLevel(SkillType.EXCAVATION) >= 500)
    		{
    			if(Math.random() * 100 > 99)
    			{
    				MapView mv = Bukkit.getServer().createMap(loc.getWorld());	
    				xp+= LoadProperties.mwatch;
    				is.add(new ItemStack(Material.WATCH, 1, mv.getId()));
    			}
    		}
    		if(LoadProperties.bucket && PP.getSkillLevel(SkillType.EXCAVATION) >= 500)
    		{
    			if(Math.random() * 100 > 99)
    			{
    				xp+= LoadProperties.mbucket;
    				is.add(new ItemStack(Material.BUCKET, 1, (byte)0, (byte)0));
    			}
    		}
    		if(LoadProperties.web && PP.getSkillLevel(SkillType.EXCAVATION) >= 750)
    		{
    			if(Math.random() * 20 > 19)
    			{
    				xp+= LoadProperties.mweb;
    				is.add(new ItemStack(Material.WEB, 1, (byte)0, (byte)0));
    			}
    		}
    		break;
    	}
    	
    	//DIRT SAND OR GRAVEL
    	if(type == Material.GRASS || type == Material.DIRT || type == Material.GRAVEL || type == Material.SAND || type == Material.CLAY)
    	{
    		xp+= LoadProperties.mbase;
    		if(PP.getSkillLevel(SkillType.EXCAVATION) >= 750)
    		{
    			//CHANCE TO GET CAKE
    			if(LoadProperties.cake == true && Math.random() * 2000 > 1999)
    			{
    				xp+= LoadProperties.mcake;
    				is.add(new ItemStack(Material.CAKE, 1, (byte)0, (byte)0));
    			}
    		}
    		if(PP.getSkillLevel(SkillType.EXCAVATION) >= 350)
    		{
    			//CHANCE TO GET DIAMOND
    			if(LoadProperties.diamond == true && Math.random() * 750 > 749)
    			{
    					xp+= LoadProperties.mdiamond2;
        				is.add(new ItemStack(Material.DIAMOND, 1, (byte)0, (byte)0));
    			}
    		}
    		if(PP.getSkillLevel(SkillType.EXCAVATION) >= 250)
    		{
    			//CHANCE TO GET YELLOW MUSIC
    			if(LoadProperties.music == true && Math.random() * 2000 > 1999)
    			{
    				xp+= LoadProperties.mmusic;
    				is.add(new ItemStack(Material.GOLD_RECORD, 1, (byte)0, (byte)0));
    			}
    		}
    		if(PP.getSkillLevel(SkillType.EXCAVATION) >= 350)
    		{
    			//CHANCE TO GET GREEN MUSIC
    			if(LoadProperties.music == true && Math.random() * 2000 > 1999)
    			{
    				xp+= LoadProperties.mmusic;
    				is.add(new ItemStack(Material.GREEN_RECORD, 1, (byte)0, (byte)0));
    			}
    		}
    	}
    	
    	//GRASS OR DIRT
    	if(type == Material.DIRT || type == Material.GRASS)
    	{
    		if(PP.getSkillLevel(SkillType.EXCAVATION) >= 50)
    		{
    			//CHANCE FOR COCOA BEANS
    			if(LoadProperties.cocoabeans == true && Math.random() * 75 > 74)
    			{
    				xp+= LoadProperties.mcocoa;
					is.add(new ItemStack(Material.getMaterial(351), 1, (byte)0, (byte)3));
    			}
    		}
    		//CHANCE FOR SHROOMS
    		if(LoadProperties.mushrooms == true && PP.getSkillLevel(SkillType.EXCAVATION) >= 500 && Math.random() * 200 > 199)
    		{
    			xp+= LoadProperties.mmushroom2;
    			switch((int) Math.random() * 1)
    			{
    			case 0:
    				is.add(new ItemStack(Material.BROWN_MUSHROOM, 1, (byte)0, (byte)0));
    				break;
    			case 1:
    				is.add(new ItemStack(Material.RED_MUSHROOM, 1, (byte)0, (byte)0));
    				break;
    			}
				
    		}
    		//CHANCE TO GET GLOWSTONE
    		if(LoadProperties.glowstone == true && PP.getSkillLevel(SkillType.EXCAVATION) >= 25 && Math.random() * 100 > 95)
    		{
    			xp+= LoadProperties.mglowstone2;
				is.add(new ItemStack(Material.GLOWSTONE_DUST, 1, (byte)0, (byte)0));
    		}
    	}
    	
    	//Drop items
    	for(ItemStack x : is)
    	{
    		if(x != null)
    			loc.getWorld().dropItemNaturally(loc, x);
    	}
    	
    	//Handle XP related tasks
    	PP.addXP(SkillType.EXCAVATION, xp);
    	Skills.XpCheckSkill(SkillType.EXCAVATION, player);
    }
}
