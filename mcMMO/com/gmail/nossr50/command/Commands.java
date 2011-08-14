package com.gmail.nossr50.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.Leaderboard;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.skills.Skills;
import com.gmail.nossr50.spout.SpoutStuff;

public class Commands 
{
	//Setup some vars
	public static final Logger log = Logger.getLogger("Minecraft"); 
	public static boolean xpevent = false;
	static int oldrate = LoadProperties.xpGainMultiplier;

	
	public static boolean isPlayer(String playerName){
		for(Player herp :  Bukkit.getServer().getOnlinePlayers()){
			if(herp.getName().toLowerCase().equals(playerName.toLowerCase())){
				return true;
			}
		}
		return false;
	}
	
	public static Player getPlayer(String playerName){
		for(Player herp : Bukkit.getServer().getOnlinePlayers()){
			if(herp.getName().toLowerCase().equals(playerName.toLowerCase())){
				return herp;
			}
		}
		return null;
	}
	
	public static boolean processCommands(CommandSender sender, Command command, String label, String[] args)
	{
		Player player = null;
		PlayerProfile PP = null;
		if(sender instanceof Player) {
			player = (Player) sender;
			PP = Users.getProfile(player);
		}

		String[] split = new String[args.length + 1];
		split[0] = label;
		for(int a = 0; a < args.length; a++){
			split[a + 1] = args[a];
		}

		//Check if the command is an MMO related help command
		if(label.equalsIgnoreCase("taming") || split[0].toLowerCase().equalsIgnoreCase(mcLocale.getString("m.SkillTaming").toLowerCase())){ 
			float skillvalue = (float)PP.getSkillLevel(SkillType.TAMING);

			String percentage = String.valueOf((skillvalue / 1000) * 100);
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.SkillTaming")})); 
			player.sendMessage(mcLocale.getString("m.XPGain", new Object[] {mcLocale.getString("m.XPGainTaming")})); 
			if(mcPermissions.getInstance().taming(player))
				player.sendMessage(mcLocale.getString("m.LVL", new Object[] {PP.getSkillLevel(SkillType.TAMING), PP.getSkillXpLevel(SkillType.TAMING), PP.getXpToLevel(SkillType.TAMING)}));
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.Effects")})); 
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsTaming1_0"), mcLocale.getString("m.EffectsTaming1_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsTaming2_0"), mcLocale.getString("m.EffectsTaming2_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsTaming3_0"), mcLocale.getString("m.EffectsTaming3_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsTaming4_0"), mcLocale.getString("m.EffectsTaming4_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsTaming5_0"), mcLocale.getString("m.EffectsTaming5_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsTaming6_0"), mcLocale.getString("m.EffectsTaming6_1")}));  
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.YourStats")})); 
			if(PP.getSkillLevel(SkillType.TAMING) < 100)
				player.sendMessage(mcLocale.getString("m.AbilityLockTemplate", new Object[] {mcLocale.getString("m.AbilLockTaming1")})); 
			else
				player.sendMessage(mcLocale.getString("m.AbilityBonusTemplate", new Object[] {mcLocale.getString("m.AbilBonusTaming1_0"), mcLocale.getString("m.AbilBonusTaming1_1")}));  
			if(PP.getSkillLevel(SkillType.TAMING) < 250)
				player.sendMessage(mcLocale.getString("m.AbilityLockTemplate", new Object[] {mcLocale.getString("m.AbilLockTaming2")})); 
			else
				player.sendMessage(mcLocale.getString("m.AbilityBonusTemplate", new Object[] {mcLocale.getString("m.AbilBonusTaming2_0"), mcLocale.getString("m.AbilBonusTaming2_1")}));  
			if(PP.getSkillLevel(SkillType.TAMING) < 500)
				player.sendMessage(mcLocale.getString("m.AbilityLockTemplate", new Object[] {mcLocale.getString("m.AbilLockTaming3")})); 
			else
				player.sendMessage(mcLocale.getString("m.AbilityBonusTemplate", new Object[] {mcLocale.getString("m.AbilBonusTaming3_0"), mcLocale.getString("m.AbilBonusTaming3_1")}));  
			if(PP.getSkillLevel(SkillType.TAMING) < 750)
				player.sendMessage(mcLocale.getString("m.AbilityLockTemplate", new Object[] {mcLocale.getString("m.AbilLockTaming4")})); 
			else
				player.sendMessage(mcLocale.getString("m.AbilityBonusTemplate", new Object[] {mcLocale.getString("m.AbilBonusTaming4_0"), mcLocale.getString("m.AbilBonusTaming4_1")}));  
			player.sendMessage(mcLocale.getString("m.TamingGoreChance", new Object[] {percentage})); 
		}
		else if(label.equalsIgnoreCase("woodcutting") || split[0].toLowerCase().equalsIgnoreCase(mcLocale.getString("m.SkillWoodCutting").toLowerCase())){ 
			float skillvalue = (float)PP.getSkillLevel(SkillType.WOODCUTTING);
			int ticks = 2;
			int x = PP.getSkillLevel(SkillType.WOODCUTTING);
			while(x >= 50){
				x-=50;
				ticks++;
			}
			String percentage = String.valueOf((skillvalue / 1000) * 100);
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.SkillWoodCutting")})); 
			player.sendMessage(mcLocale.getString("m.XPGain", new Object[] {mcLocale.getString("m.XPGainWoodCutting")})); 
			if(mcPermissions.getInstance().woodcutting(player))
				player.sendMessage(mcLocale.getString("m.LVL", new Object[] {PP.getSkillLevel(SkillType.WOODCUTTING), PP.getSkillXpLevel(SkillType.WOODCUTTING), PP.getXpToLevel(SkillType.WOODCUTTING)}));
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.Effects")})); 
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsWoodCutting1_0"), mcLocale.getString("m.EffectsWoodCutting1_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsWoodCutting2_0"), mcLocale.getString("m.EffectsWoodCutting2_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsWoodCutting3_0"), mcLocale.getString("m.EffectsWoodCutting3_1")}));  
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.YourStats")})); 
			if(PP.getSkillLevel(SkillType.WOODCUTTING) < 100)
				player.sendMessage(mcLocale.getString("m.AbilityLockTemplate", new Object[] {mcLocale.getString("m.AbilLockWoodCutting1")})); 
			else
				player.sendMessage(mcLocale.getString("m.AbilityBonusTemplate", new Object[] {mcLocale.getString("m.AbilBonusWoodCutting1_0"), mcLocale.getString("m.AbilBonusWoodCutting1_1")}));  
			player.sendMessage(mcLocale.getString("m.WoodCuttingDoubleDropChance", new Object[] {percentage})); 
			player.sendMessage(mcLocale.getString("m.WoodCuttingTreeFellerLength", new Object[] {ticks})); 
		}
		else if(label.equalsIgnoreCase("archery") || split[0].toLowerCase().equalsIgnoreCase(mcLocale.getString("m.SkillArchery").toLowerCase())){ 
			Integer rank = 0;
			if(PP.getSkillLevel(SkillType.ARCHERY) >= 50)
				rank++;
			if(PP.getSkillLevel(SkillType.ARCHERY) >= 250)
				rank++;
			if(PP.getSkillLevel(SkillType.ARCHERY) >= 575)
				rank++;
			if(PP.getSkillLevel(SkillType.ARCHERY) >= 725)
				rank++;
			if(PP.getSkillLevel(SkillType.ARCHERY) >= 1000)
				rank++;
			float skillvalue = (float)PP.getSkillLevel(SkillType.ARCHERY);
			String percentage = String.valueOf((skillvalue / 1000) * 100);

			int ignition = 20;
			if(PP.getSkillLevel(SkillType.ARCHERY) >= 200)
				ignition+=20;
			if(PP.getSkillLevel(SkillType.ARCHERY) >= 400)
				ignition+=20;
			if(PP.getSkillLevel(SkillType.ARCHERY) >= 600)
				ignition+=20;
			if(PP.getSkillLevel(SkillType.ARCHERY) >= 800)
				ignition+=20;
			if(PP.getSkillLevel(SkillType.ARCHERY) >= 1000)
				ignition+=20;

			String percentagedaze;
			if(PP.getSkillLevel(SkillType.ARCHERY) < 1000){
				percentagedaze = String.valueOf((skillvalue / 2000) * 100);
			} else {
				percentagedaze = "50"; 
			}
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.SkillArchery")})); 
			player.sendMessage(mcLocale.getString("m.XPGain", new Object[] {mcLocale.getString("m.XPGainArchery")})); 
			if(mcPermissions.getInstance().archery(player))
				player.sendMessage(mcLocale.getString("m.LVL", new Object[] {PP.getSkillLevel(SkillType.ARCHERY), PP.getSkillXpLevel(SkillType.ARCHERY), PP.getXpToLevel(SkillType.ARCHERY)}));
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.Effects")})); 
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsArchery1_0"), mcLocale.getString("m.EffectsArchery1_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsArchery2_0"), mcLocale.getString("m.EffectsArchery2_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsArchery3_0"), mcLocale.getString("m.EffectsArchery3_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsArchery4_0"), mcLocale.getString("m.EffectsArchery4_1")}));  
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.YourStats")})); 
			player.sendMessage(mcLocale.getString("m.ArcheryDazeChance", new Object[] {percentagedaze})); 
			player.sendMessage(mcLocale.getString("m.ArcheryRetrieveChance", new Object[] {percentage})); 
			player.sendMessage(mcLocale.getString("m.ArcheryIgnitionLength", new Object[] {(ignition / 20)})); 
			player.sendMessage(mcLocale.getString("m.ArcheryDamagePlus", new Object[] {rank})); 
		}
		else if(label.equalsIgnoreCase("axes") || split[0].toLowerCase().equalsIgnoreCase(mcLocale.getString("m.SkillAxes"))){ 
			String percentage;
			float skillvalue = (float)PP.getSkillLevel(SkillType.AXES);
			if(PP.getSkillLevel(SkillType.AXES) < 750){
				percentage = String.valueOf((skillvalue / 1000) * 100);
			} else {
				percentage = "75"; 
			}
			int ticks = 2;
			int x = PP.getSkillLevel(SkillType.AXES);
			while(x >= 50){
				x-=50;
				ticks++;
			}

			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.SkillAxes")})); 
			player.sendMessage(mcLocale.getString("m.XPGain", new Object[] {mcLocale.getString("m.XPGainAxes")})); 
			if(mcPermissions.getInstance().axes(player))
				player.sendMessage(mcLocale.getString("m.LVL", new Object[] {PP.getSkillLevel(SkillType.AXES), PP.getSkillXpLevel(SkillType.AXES), PP.getXpToLevel(SkillType.AXES)}));
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.Effects")})); 
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsAxes1_0"), mcLocale.getString("m.EffectsAxes1_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsAxes2_0"), mcLocale.getString("m.EffectsAxes2_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsAxes3_0"), mcLocale.getString("m.EffectsAxes3_1")}));  
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.YourStats")})); 
			player.sendMessage(mcLocale.getString("m.AxesCritChance", new Object[] {percentage})); 
			if(PP.getSkillLevel(SkillType.AXES) < 500){
				player.sendMessage(mcLocale.getString("m.AbilityLockTemplate", new Object[] {mcLocale.getString("m.AbilLockAxes1")})); 
			} else {
				player.sendMessage(mcLocale.getString("m.AbilityBonusTemplate", new Object[] {mcLocale.getString("m.AbilBonusAxes1_0"), mcLocale.getString("m.AbilBonusAxes1_1")}));  
			}
			player.sendMessage(mcLocale.getString("m.AxesSkullLength", new Object[] {ticks})); 
		}
		else if(label.equalsIgnoreCase("swords") || split[0].toLowerCase().equalsIgnoreCase(mcLocale.getString("m.SkillSwords").toLowerCase())){ 
			int bleedrank = 2;
			String percentage, parrypercentage = null, counterattackpercentage;
			float skillvalue = (float)PP.getSkillLevel(SkillType.SWORDS);
			if(PP.getSkillLevel(SkillType.SWORDS) < 750){
				percentage = String.valueOf((skillvalue / 1000) * 100);
			} else {
				percentage = "75"; 
			}
			if(skillvalue >= 750)
				bleedrank+=1;

			if(PP.getSkillLevel(SkillType.SWORDS) <= 900){
				parrypercentage = String.valueOf((skillvalue / 3000) * 100);
			} else {
				parrypercentage = "30"; 
			}

			if(PP.getSkillLevel(SkillType.SWORDS) <= 600){
				counterattackpercentage = String.valueOf((skillvalue / 2000) * 100);
			} else {
				counterattackpercentage = "30"; 
			}

			int ticks = 2;
			int x = PP.getSkillLevel(SkillType.SWORDS);
			while(x >= 50){
				x-=50;
				ticks++;
			}

			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.SkillSwords")})); 
			player.sendMessage(mcLocale.getString("m.XPGain", new Object[] {mcLocale.getString("m.XPGainSwords")})); 
			if(mcPermissions.getInstance().swords(player))
				player.sendMessage(mcLocale.getString("m.LVL", new Object[] {PP.getSkillLevel(SkillType.SWORDS), PP.getSkillXpLevel(SkillType.SWORDS), PP.getXpToLevel(SkillType.SWORDS)}));
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.Effects")})); 
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsSwords1_0"), mcLocale.getString("m.EffectsSwords1_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsSwords2_0"), mcLocale.getString("m.EffectsSwords2_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsSwords3_0"), mcLocale.getString("m.EffectsSwords3_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsSwords4_0"), mcLocale.getString("m.EffectsSwords4_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsSwords5_0"), mcLocale.getString("m.EffectsSwords5_1")}));  
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.YourStats")})); 
			player.sendMessage(mcLocale.getString("m.SwordsCounterAttChance", new Object[] {counterattackpercentage})); 
			player.sendMessage(mcLocale.getString("m.SwordsBleedLength", new Object[] {bleedrank})); 
			player.sendMessage(mcLocale.getString("m.SwordsTickNote")); 
			player.sendMessage(mcLocale.getString("m.SwordsBleedLength", new Object[] {percentage})); 
			player.sendMessage(mcLocale.getString("m.SwordsParryChance", new Object[] {parrypercentage})); 
			player.sendMessage(mcLocale.getString("m.SwordsSSLength", new Object[] {ticks})); 

		}
		else if(label.equalsIgnoreCase("acrobatics") || split[0].toLowerCase().equalsIgnoreCase(mcLocale.getString("m.SkillAcrobatics").toLowerCase())){ 
			String dodgepercentage;
			float skillvalue = (float)PP.getSkillLevel(SkillType.ACROBATICS);
			String percentage = String.valueOf((skillvalue / 1000) * 100);
			String gracepercentage = String.valueOf(((skillvalue / 1000) * 100) * 2);
			if(PP.getSkillLevel(SkillType.ACROBATICS) <= 800){
				dodgepercentage = String.valueOf((skillvalue / 4000 * 100));
			} else {
				dodgepercentage = "20"; 
			}
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.SkillAcrobatics")})); 
			player.sendMessage(mcLocale.getString("m.XPGain", new Object[] {mcLocale.getString("m.XPGainAcrobatics")})); 
			if(mcPermissions.getInstance().acrobatics(player))
				player.sendMessage(mcLocale.getString("m.LVL", new Object[] {PP.getSkillLevel(SkillType.ACROBATICS), PP.getSkillXpLevel(SkillType.ACROBATICS), PP.getXpToLevel(SkillType.ACROBATICS)}));
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.Effects")})); 
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsAcrobatics1_0"), mcLocale.getString("m.EffectsAcrobatics1_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsAcrobatics2_0"), mcLocale.getString("m.EffectsAcrobatics2_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsAcrobatics3_0"), mcLocale.getString("m.EffectsAcrobatics3_1")}));  
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.YourStats")})); 
			player.sendMessage(mcLocale.getString("m.AcrobaticsRollChance", new Object[] {percentage})); 
			player.sendMessage(mcLocale.getString("m.AcrobaticsGracefulRollChance", new Object[] {gracepercentage})); 
			player.sendMessage(mcLocale.getString("m.AcrobaticsDodgeChance", new Object[] {dodgepercentage})); 
		}
		else if(label.equalsIgnoreCase("mining") || split[0].toLowerCase().equalsIgnoreCase(mcLocale.getString("m.SkillMining"))){ 
			float skillvalue = (float)PP.getSkillLevel(SkillType.MINING);
			String percentage = String.valueOf((skillvalue / 1000) * 100);
			int ticks = 2;
			int x = PP.getSkillLevel(SkillType.MINING);
			while(x >= 50){
				x-=50;
				ticks++;
			}
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.SkillMining")})); 
			player.sendMessage(mcLocale.getString("m.XPGain", new Object[] {mcLocale.getString("m.XPGainMining")})); 
			if(mcPermissions.getInstance().mining(player))
				player.sendMessage(mcLocale.getString("m.LVL", new Object[] {PP.getSkillLevel(SkillType.MINING), PP.getSkillXpLevel(SkillType.MINING), PP.getXpToLevel(SkillType.MINING)}));
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.Effects")})); 
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsMining1_0"), mcLocale.getString("m.EffectsMining1_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsMining2_0"), mcLocale.getString("m.EffectsMining2_1")}));  
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.YourStats")})); 
			player.sendMessage(mcLocale.getString("m.MiningDoubleDropChance", new Object[] {percentage})); 
			player.sendMessage(mcLocale.getString("m.MiningSuperBreakerLength", new Object[] {ticks})); 
		}
		else if(label.equalsIgnoreCase("repair") || split[0].toLowerCase().equalsIgnoreCase(mcLocale.getString("m.SkillRepair").toLowerCase())){ 
			float skillvalue = (float)PP.getSkillLevel(SkillType.REPAIR);
			String percentage = String.valueOf((skillvalue / 1000) * 100);
			String repairmastery = String.valueOf((skillvalue / 500) * 100);
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.SkillRepair")})); 
			player.sendMessage(mcLocale.getString("m.XPGain", new Object[] {mcLocale.getString("m.XPGainRepair")})); 
			if(mcPermissions.getInstance().repair(player))
				player.sendMessage(mcLocale.getString("m.LVL", new Object[] {PP.getSkillLevel(SkillType.REPAIR), PP.getSkillXpLevel(SkillType.REPAIR), PP.getXpToLevel(SkillType.REPAIR)}));
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.Effects")})); 
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsRepair1_0"), mcLocale.getString("m.EffectsRepair1_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsRepair2_0"), mcLocale.getString("m.EffectsRepair2_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsRepair3_0"), mcLocale.getString("m.EffectsRepair3_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsRepair4_0", new Object[]{LoadProperties.repairdiamondlevel}), mcLocale.getString("m.EffectsRepair4_1")}));  
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.YourStats")})); 
			player.sendMessage(mcLocale.getString("m.RepairRepairMastery", new Object[] {repairmastery})); 
			player.sendMessage(mcLocale.getString("m.RepairSuperRepairChance", new Object[] {percentage})); 
		}
		else if(label.equalsIgnoreCase("unarmed")){ 
			String percentage, arrowpercentage;
			float skillvalue = (float)PP.getSkillLevel(SkillType.UNARMED);

			if(PP.getSkillLevel(SkillType.UNARMED) < 1000){
				percentage = String.valueOf((skillvalue / 4000) * 100);
			} else {
				percentage = "25"; 
			}

			if(PP.getSkillLevel(SkillType.UNARMED) < 1000){
				arrowpercentage = String.valueOf(((skillvalue / 1000) * 100) / 2);
			} else {
				arrowpercentage = "50"; 
			}


			int ticks = 2;
			int x = PP.getSkillLevel(SkillType.UNARMED);
			while(x >= 50){
				x-=50;
				ticks++;
			}

			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.SkillUnarmed")})); 
			player.sendMessage(mcLocale.getString("m.XPGain", new Object[] {mcLocale.getString("m.XPGainUnarmed")})); 
			if(mcPermissions.getInstance().unarmed(player))
				player.sendMessage(mcLocale.getString("m.LVL", new Object[] {PP.getSkillLevel(SkillType.UNARMED), PP.getSkillXpLevel(SkillType.UNARMED), PP.getXpToLevel(SkillType.UNARMED)}));
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.Effects")})); 
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsUnarmed1_0"), mcLocale.getString("m.EffectsUnarmed1_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsUnarmed2_0"), mcLocale.getString("m.EffectsUnarmed2_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsUnarmed3_0"), mcLocale.getString("m.EffectsUnarmed3_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsUnarmed4_0"), mcLocale.getString("m.EffectsUnarmed4_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsUnarmed5_0"), mcLocale.getString("m.EffectsUnarmed5_1")}));  
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.YourStats")})); 
			player.sendMessage(mcLocale.getString("m.UnarmedArrowDeflectChance", new Object[] {arrowpercentage})); 
			player.sendMessage(mcLocale.getString("m.UnarmedDisarmChance", new Object[] {percentage})); 
			if(PP.getSkillLevel(SkillType.UNARMED) < 250){
				player.sendMessage(mcLocale.getString("m.AbilityLockTemplate", new Object[] {mcLocale.getString("m.AbilLockUnarmed1")})); 
			} else if(PP.getSkillLevel(SkillType.UNARMED) >= 250 && PP.getSkillLevel(SkillType.UNARMED) < 500){
				player.sendMessage(mcLocale.getString("m.AbilityBonusTemplate", new Object[] {mcLocale.getString("m.AbilBonusUnarmed1_0"), mcLocale.getString("m.AbilBonusUnarmed1_1")}));  
				player.sendMessage(mcLocale.getString("m.AbilityLockTemplate", new Object[] {mcLocale.getString("m.AbilLockUnarmed2")})); 
			} else {
				player.sendMessage(mcLocale.getString("m.AbilityBonusTemplate", new Object[] {mcLocale.getString("m.AbilBonusUnarmed2_0"), mcLocale.getString("m.AbilBonusUnarmed2_1")}));  
			}
			player.sendMessage(mcLocale.getString("m.UnarmedBerserkLength", new Object[] {ticks})); 
		}
		else if(label.equalsIgnoreCase("herbalism") || split[0].toLowerCase().equalsIgnoreCase(mcLocale.getString("m.SkillHerbalism").toLowerCase())){ 
			int rank = 0;
			if(PP.getSkillLevel(SkillType.HERBALISM) >= 50)
				rank++;
			if (PP.getSkillLevel(SkillType.HERBALISM) >= 150)
				rank++;
			if (PP.getSkillLevel(SkillType.HERBALISM) >= 250)
				rank++;
			if (PP.getSkillLevel(SkillType.HERBALISM) >= 350)
				rank++;
			if (PP.getSkillLevel(SkillType.HERBALISM) >= 450)
				rank++;
			if (PP.getSkillLevel(SkillType.HERBALISM) >= 550)
				rank++;
			if (PP.getSkillLevel(SkillType.HERBALISM) >= 650)
				rank++;
			if (PP.getSkillLevel(SkillType.HERBALISM) >= 750)
				rank++;
			int bonus = 0;
			if(PP.getSkillLevel(SkillType.HERBALISM) >= 200)
				bonus++;
			if(PP.getSkillLevel(SkillType.HERBALISM) >= 400)
				bonus++;
			if(PP.getSkillLevel(SkillType.HERBALISM) >= 600)
				bonus++;

			int ticks = 2;
			int x = PP.getSkillLevel(SkillType.HERBALISM);
			while(x >= 50){
				x-=50;
				ticks++;
			}

			float skillvalue = (float)PP.getSkillLevel(SkillType.HERBALISM);
			String percentage = String.valueOf((skillvalue / 1000) * 100);
			String gpercentage = String.valueOf((skillvalue / 1500) * 100);
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.SkillHerbalism")})); 
			player.sendMessage(mcLocale.getString("m.XPGain", new Object[] {mcLocale.getString("m.XPGainHerbalism")})); 
			if(mcPermissions.getInstance().herbalism(player))
				player.sendMessage(mcLocale.getString("m.LVL", new Object[] {PP.getSkillLevel(SkillType.HERBALISM), PP.getSkillXpLevel(SkillType.HERBALISM), PP.getXpToLevel(SkillType.HERBALISM)}));
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.Effects")})); 
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsHerbalism1_0"), mcLocale.getString("m.EffectsHerbalism1_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsHerbalism2_0"), mcLocale.getString("m.EffectsHerbalism2_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsHerbalism3_0"), mcLocale.getString("m.EffectsHerbalism3_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsHerbalism4_0"), mcLocale.getString("m.EffectsHerbalism4_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsHerbalism5_0"), mcLocale.getString("m.EffectsHerbalism5_1")}));  
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.YourStats")})); 
			player.sendMessage(mcLocale.getString("m.HerbalismGreenTerraLength", new Object[] {ticks})); 
			player.sendMessage(mcLocale.getString("m.HerbalismGreenThumbChance", new Object[] {gpercentage})); 
			player.sendMessage(mcLocale.getString("m.HerbalismGreenThumbStage", new Object[] {bonus})); 
			player.sendMessage(mcLocale.getString("m.HerbalismDoubleDropChance", new Object[] {percentage})); 
			player.sendMessage(mcLocale.getString("m.HerbalismFoodPlus", new Object[] {rank})); 
		}

		else if(label.equalsIgnoreCase("excavation") || split[0].toLowerCase().equalsIgnoreCase(mcLocale.getString("m.SkillExcavation").toLowerCase())) 
		{
			int ticks = 2;
			int x = PP.getSkillLevel(SkillType.EXCAVATION);
			while(x >= 50){
				x-=50;
				ticks++;
			}
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.SkillExcavation")})); 
			player.sendMessage(mcLocale.getString("m.XPGain", new Object[] {mcLocale.getString("m.XPGainExcavation")})); 
			if(mcPermissions.getInstance().excavation(player))
				player.sendMessage(mcLocale.getString("m.LVL", new Object[] {PP.getSkillLevel(SkillType.EXCAVATION), PP.getSkillXpLevel(SkillType.EXCAVATION), PP.getXpToLevel(SkillType.EXCAVATION)}));
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.Effects")})); 
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsExcavation1_0"), mcLocale.getString("m.EffectsExcavation1_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsExcavation2_0"), mcLocale.getString("m.EffectsExcavation2_1")}));  
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.YourStats")})); 
			player.sendMessage(mcLocale.getString("m.ExcavationGreenTerraLength", new Object[] {ticks})); 
		}

		else if(label.equalsIgnoreCase("sorcery") || split[0].toLowerCase().equalsIgnoreCase(mcLocale.getString("m.SkillSorcery").toLowerCase())) 
		{
			/*
	        player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.SkillExcavation")})); 
			player.sendMessage(mcLocale.getString("m.XPGain", new Object[] {mcLocale.getString("m.XPGainExcavation")})); 
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.Effects")})); 
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsExcavation1_0"), mcLocale.getString("m.EffectsExcavation1_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsExcavation2_0"), mcLocale.getString("m.EffectsExcavation2_1")}));  
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.YourStats")})); 
			player.sendMessage(mcLocale.getString("m.ExcavationGreenTerraLength", new Object[] {ticks})); 
			 */
		}

		else if(LoadProperties.mcmmoEnable && label.equalsIgnoreCase(LoadProperties.mcmmo))
		{ 
			player.sendMessage(ChatColor.RED+"-----[]"+ChatColor.GREEN+"mcMMO"+ChatColor.RED+"[]-----");
			String description = mcLocale.getString("mcMMO.Description", new Object[] {LoadProperties.mcc});
			String[] mcSplit = description.split(",");
			
			for(String x : mcSplit)
			{
				player.sendMessage(x);
			}
			if(LoadProperties.spoutEnabled && player instanceof SpoutPlayer)
			{
				SpoutPlayer sPlayer = (SpoutPlayer)player;
				if(LoadProperties.donateMessage)
					sPlayer.sendNotification("[mcMMO] Donate!", "Paypal nossr50@gmail.com", Material.CAKE);
			}
			else
			{
				if(LoadProperties.donateMessage)
					player.sendMessage(ChatColor.GREEN+"If you like my work you can donate via Paypal: nossr50@gmail.com");
			}
		}
		else if(LoadProperties.mccEnable && label.equalsIgnoreCase(LoadProperties.mcc))
		{ 
			player.sendMessage(ChatColor.RED+"---[]"+ChatColor.YELLOW+"mcMMO Commands"+ChatColor.RED+"[]---");   
			if(mcPermissions.getInstance().party(player)){
				player.sendMessage(mcLocale.getString("m.mccPartyCommands")); 
				player.sendMessage(LoadProperties.party+" "+mcLocale.getString("m.mccParty"));   
				player.sendMessage(LoadProperties.party+" q "+mcLocale.getString("m.mccPartyQ"));
				if(mcPermissions.getInstance().partyChat(player))
					player.sendMessage("/p "+mcLocale.getString("m.mccPartyToggle"));  
				player.sendMessage(LoadProperties.invite+" "+mcLocale.getString("m.mccPartyInvite"));   
				player.sendMessage(LoadProperties.accept+" "+mcLocale.getString("m.mccPartyAccept"));   
				if(mcPermissions.getInstance().partyTeleport(player))
					player.sendMessage(LoadProperties.ptp+" "+mcLocale.getString("m.mccPartyTeleport"));   
			}
			player.sendMessage(mcLocale.getString("m.mccOtherCommands")); 
			player.sendMessage(LoadProperties.stats+ChatColor.RED+" "+mcLocale.getString("m.mccStats"));  
			player.sendMessage("/mctop <skillname> <page> "+ChatColor.RED+mcLocale.getString("m.mccLeaderboards"));  
			if(mcPermissions.getInstance().mySpawn(player)){
				player.sendMessage(LoadProperties.myspawn+" "+ChatColor.RED+mcLocale.getString("m.mccMySpawn"));   
				player.sendMessage(LoadProperties.clearmyspawn+" "+ChatColor.RED+mcLocale.getString("m.mccClearMySpawn"));   
			}
			if(mcPermissions.getInstance().mcAbility(player))
				player.sendMessage(LoadProperties.mcability+ChatColor.RED+" "+mcLocale.getString("m.mccToggleAbility"));  
			if(mcPermissions.getInstance().adminChat(player)){
				player.sendMessage("/a "+ChatColor.RED+mcLocale.getString("m.mccAdminToggle"));  
			}
			if(mcPermissions.getInstance().whois(player))
				player.sendMessage(LoadProperties.whois+" "+mcLocale.getString("m.mccWhois"));   
			if(mcPermissions.getInstance().mmoedit(player)){
				//player.sendMessage(LoadProperties.mmoedit+" [skill] [newvalue] "+ChatColor.RED+"Modify the designated skill value");
				player.sendMessage(LoadProperties.mmoedit+mcLocale.getString("m.mccMmoedit"));   
			}
			if(mcPermissions.getInstance().mcgod(player))
				player.sendMessage(LoadProperties.mcgod+ChatColor.RED+" "+mcLocale.getString("m.mccMcGod"));  
			player.sendMessage(mcLocale.getString("m.mccSkillInfo"));  
			player.sendMessage(LoadProperties.mcmmo+" "+mcLocale.getString("m.mccModDescription"));   
		}
		else if(LoadProperties.mcabilityEnable && mcPermissions.permissionsEnabled && label.equalsIgnoreCase(LoadProperties.mcability)){ 
			if(PP.getAbilityUse()){
				player.sendMessage(mcLocale.getString("mcPlayerListener.AbilitiesOff")); 
				PP.toggleAbilityUse();
			} else {
				player.sendMessage(mcLocale.getString("mcPlayerListener.AbilitiesOn")); 
				PP.toggleAbilityUse();
			}
		}
		else if (label.equalsIgnoreCase("xprate"))
		{
			//TODO: Localization.. I know me so lazy today, I'll do it tomorrow
			
			if(sender instanceof Player)
			{
				if(!mcPermissions.getInstance().admin(player))
				{
					player.sendMessage(ChatColor.YELLOW+"[mcMMO] "+ChatColor.DARK_RED +mcLocale.getString("mcPlayerListener.NoPermission"));  
					return true;
				}
				if(split.length <= 1)
				{
					player.sendMessage(ChatColor.DARK_AQUA+"Proper usage is /xprate [integer] [true:false]");
					player.sendMessage(ChatColor.DARK_AQUA+"Also you can type /xprate reset to turn everything back to normal");
				}
				if(split.length == 2 && split[1].equalsIgnoreCase("reset"))
				{
					if(xpevent)
					{
						for(Player x : Bukkit.getServer().getOnlinePlayers())
							x.sendMessage(ChatColor.RED+"mcMMO XP Event is OVER!!");
						xpevent = !xpevent;
						LoadProperties.xpGainMultiplier = oldrate;
					} else
					{
						LoadProperties.xpGainMultiplier = oldrate;
					}
				}
				if(split.length >= 2 && m.isInt(split[1]))
				{
					oldrate = LoadProperties.xpGainMultiplier;
					
					if(split.length >= 3 && (split[2].equalsIgnoreCase("true") || split[2].equalsIgnoreCase("false")))
					{
						if(split[2].equalsIgnoreCase("true"))
							xpevent = true;
						else
							xpevent = false;
					} else
					{
						player.sendMessage("Enter true or false for the second value");
						return true;
					}
					LoadProperties.xpGainMultiplier = m.getInt(split[1]);
					if(xpevent = true)
						for(Player x : Bukkit.getServer().getOnlinePlayers())
						{
							x.sendMessage(ChatColor.GOLD+"XP EVENT FOR mcMMO HAS STARTED!");
							x.sendMessage(ChatColor.GOLD+"mcMMO XP RATE IS NOW "+LoadProperties.xpGainMultiplier+"x!!");
						}
				}
			} else
			{
				if(split.length <= 1)
				{
					System.out.println(ChatColor.DARK_AQUA+"Proper usage is /xprate [integer] [true:false]");
					System.out.println(ChatColor.DARK_AQUA+"Also you can type /xprate reset to turn everything back to normal");
				}
				
				if(split.length == 2 && split[1].equalsIgnoreCase("reset"))
				{
					if(xpevent)
					{
						for(Player x : Bukkit.getServer().getOnlinePlayers())
							x.sendMessage(ChatColor.RED+"mcMMO XP Event is OVER!!");
						xpevent = !xpevent;
						LoadProperties.xpGainMultiplier = oldrate;
					} else
					{
						LoadProperties.xpGainMultiplier = oldrate;
					}
				}
				
				if(split.length >= 2 && m.isInt(split[1]))
				{
					oldrate = LoadProperties.xpGainMultiplier;
					
					if(split.length >= 3 && (split[2].equalsIgnoreCase("true") || split[2].equalsIgnoreCase("false")))
					{
						if(split[2].equalsIgnoreCase("true"))
							xpevent = true;
						else
							xpevent = false;
					} else
					{
						System.out.println("Enter true or false for the second value");
						return true;
					}
					LoadProperties.xpGainMultiplier = m.getInt(split[1]);
					if(xpevent = true)
						for(Player x : Bukkit.getServer().getOnlinePlayers())
						{
							x.sendMessage(ChatColor.GOLD+"XP EVENT FOR mcMMO HAS STARTED!");
							x.sendMessage(ChatColor.GOLD+"mcMMO XP RATE IS NOW "+LoadProperties.xpGainMultiplier+"x!!");
						}
				}
			}
		}
		/*
		 * FFS -> MySQL
		 */
		else if(label.equalsIgnoreCase("mmoupdate"))
		{
			if(!mcPermissions.getInstance().admin(player))
			{
				player.sendMessage(ChatColor.YELLOW+"[mcMMO] "+ChatColor.DARK_RED +mcLocale.getString("mcPlayerListener.NoPermission"));  
				return true;
			}
			player.sendMessage(ChatColor.GRAY+"Starting conversion..."); 
			Users.clearUsers();
			m.convertToMySQL();
			for(Player x : Bukkit.getServer().getOnlinePlayers())
			{
				Users.addUser(x);
			}
			player.sendMessage(ChatColor.GREEN+"Conversion finished!"); 
		}

		/*
		 * LEADER BOARD COMMAND
		 */
		else if(LoadProperties.mctopEnable && label.equalsIgnoreCase(LoadProperties.mctop)){ 

			if(LoadProperties.useMySQL == false){
				/*
				 * POWER LEVEL INFO RETRIEVAL
				 */
				if(split.length == 1){
					int p = 1;
					String[] info = Leaderboard.retrieveInfo(SkillType.ALL.toString(), p); 
					player.sendMessage(mcLocale.getString("mcPlayerListener.PowerLevelLeaderboard"));
					int n = 1 * p; //Position
					for(String x : info){
						if(x != null){
							String digit = String.valueOf(n);
							if(n < 10)
								digit ="0"+String.valueOf(n); 
							String[] splitx = x.split(":"); 
							//Format: 1. Playername - skill value
							player.sendMessage(digit+". "+ChatColor.GREEN+splitx[1]+" - "+ChatColor.WHITE+splitx[0]);  
							n++;
						}
					}
				}
				if(split.length >= 2 && m.isInt(split[1])){
					int p = 1;
					//Grab page value if specified
					if(split.length >= 2){
						if(m.isInt(split[1])){
							p = Integer.valueOf(split[1]);
						}
					}
					int pt = p;
					if(p > 1){
						pt -= 1;
						pt += (pt * 10);
						pt = 10;
					}
					String[] info = Leaderboard.retrieveInfo(SkillType.ALL.toString(), p); 
					player.sendMessage(mcLocale.getString("mcPlayerListener.PowerLevelLeaderboard")); 
					int n = 1 * pt; //Position
					for(String x : info){
						if(x != null){
							String digit = String.valueOf(n);
							if(n < 10)
								digit ="0"+String.valueOf(n); 
							String[] splitx = x.split(":"); 
							//Format: 1. Playername - skill value
							player.sendMessage(digit+". "+ChatColor.GREEN+splitx[1]+" - "+ChatColor.WHITE+splitx[0]);  
							n++;
						}
					}
				}
				/*
				 * SKILL SPECIFIED INFO RETRIEVAL
				 */
				if(split.length >= 2 && Skills.isSkill(split[1]))
				{
					int p = 1;
					//Grab page value if specified
					if(split.length >= 3)
					{
						if(m.isInt(split[2])){
							p = Integer.valueOf(split[2]);
						}
					}
					int pt = p;
					if(p > 1){
						pt -= 1;
						pt += (pt * 10);
						pt = 10;
					}
					String firstLetter = split[1].substring(0,1);  // Get first letter
					String remainder   = split[1].substring(1);    // Get remainder of word.
					String capitalized = firstLetter.toUpperCase() + remainder.toLowerCase();

					String[] info = Leaderboard.retrieveInfo(split[1].toUpperCase(), p);
					player.sendMessage(mcLocale.getString("mcPlayerListener.SkillLeaderboard", new Object[] {capitalized}));  
					int n = 1 * pt; //Position
					for(String x : info)
					{
						if(x != null)
						{
							String digit = String.valueOf(n);
							if(n < 10)
								digit ="0"+String.valueOf(n); 
							String[] splitx = x.split(":"); 
							//Format: 1. Playername - skill value
							player.sendMessage(digit+". "+ChatColor.GREEN+splitx[1]+" - "+ChatColor.WHITE+splitx[0]);  
							n++;
						}
					}
				}
			} else {
				/*
				 * MYSQL LEADERBOARDS
				 */
				String powerlevel = "taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics"; 
				if(split.length >= 2 && Skills.isSkill(split[1])){
					/*
					 * Create a nice consistent capitalized leaderboard name
					 */
					String lowercase = split[1].toLowerCase(); //For the query
					String firstLetter = split[1].substring(0,1); //Get first letter
					String remainder   = split[1].substring(1); //Get remainder of word.
					String capitalized = firstLetter.toUpperCase() + remainder.toLowerCase();

					player.sendMessage(mcLocale.getString("mcPlayerListener.SkillLeaderboard", new Object[] {capitalized}));  
					if(split.length >= 3 && m.isInt(split[2])){
						int n = 1; //For the page number
						int n2 = Integer.valueOf(split[2]);
						if(n2 > 1){
							//Figure out the 'page' here
							n = 10;
							n = n * (n2-1);
						}
						//If a page number is specified
						HashMap<Integer, ArrayList<String>> userslist = mcMMO.database.Read("SELECT "+lowercase+", user_id FROM "  
								+LoadProperties.MySQLtablePrefix+"skills WHERE "+lowercase+" > 0 ORDER BY `"+LoadProperties.MySQLtablePrefix+"skills`.`"+lowercase+"` DESC ");    

						for(int i=n;i<=n+10;i++){
							if (i > userslist.size() || mcMMO.database.Read("SELECT user FROM "+LoadProperties.MySQLtablePrefix+"users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'") == null)   
								break;
							HashMap<Integer, ArrayList<String>> username =  mcMMO.database.Read("SELECT user FROM "+LoadProperties.MySQLtablePrefix+"users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'");   
							player.sendMessage(String.valueOf(i)+". "+ChatColor.GREEN+userslist.get(i).get(0)+" - "+ChatColor.WHITE+username.get(1).get(0));  
						}
						return true;
					}
					//If no page number is specified
					HashMap<Integer, ArrayList<String>> userslist = mcMMO.database.Read("SELECT "+lowercase+", user_id FROM "  
							+LoadProperties.MySQLtablePrefix+"skills WHERE "+lowercase+" > 0 ORDER BY `"+LoadProperties.MySQLtablePrefix+"skills`.`"+lowercase+"` DESC ");    
					for(int i=1;i<=10;i++){ //i<=userslist.size()
						if (i > userslist.size() || mcMMO.database.Read("SELECT user FROM "+LoadProperties.MySQLtablePrefix+"users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'") == null)   
							break;
						HashMap<Integer, ArrayList<String>> username =  mcMMO.database.Read("SELECT user FROM "+LoadProperties.MySQLtablePrefix+"users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'");   
						player.sendMessage(String.valueOf(i)+". "+ChatColor.GREEN+userslist.get(i).get(0)+" - "+ChatColor.WHITE+username.get(1).get(0));  
					}
					return true;
				}
				if(split.length >= 1){
					player.sendMessage(mcLocale.getString("mcPlayerListener.PowerLevelLeaderboard")); 
					if(split.length >= 2 && m.isInt(split[1])){
						int n = 1; //For the page number
						int n2 = Integer.valueOf(split[1]);
						if(n2 > 1){
							//Figure out the 'page' here
							n = 10;
							n = n * (n2-1);
						}
						//If a page number is specified
						HashMap<Integer, ArrayList<String>> userslist = mcMMO.database.Read("SELECT "+powerlevel+", user_id FROM "  
								+LoadProperties.MySQLtablePrefix+"skills WHERE "+powerlevel+" > 0 ORDER BY taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics DESC ");  
						for(int i=n;i<=n+10;i++){
							if (i > userslist.size() || mcMMO.database.Read("SELECT user FROM "+LoadProperties.MySQLtablePrefix+"users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'") == null)   
								break;
							HashMap<Integer, ArrayList<String>> username =  mcMMO.database.Read("SELECT user FROM "+LoadProperties.MySQLtablePrefix+"users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'");   
							player.sendMessage(String.valueOf(i)+". "+ChatColor.GREEN+userslist.get(i).get(0)+" - "+ChatColor.WHITE+username.get(1).get(0));  
						}
						return true;
					}
					HashMap<Integer, ArrayList<String>> userslist = mcMMO.database.Read("SELECT taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics, user_id FROM " 
							+LoadProperties.MySQLtablePrefix+"skills WHERE "+powerlevel+" > 0 ORDER BY taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics DESC ");  
					for(int i=1;i<=10;i++){
						if (i > userslist.size() || mcMMO.database.Read("SELECT user FROM "+LoadProperties.MySQLtablePrefix+"users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'") == null)   
							break;
						HashMap<Integer, ArrayList<String>> username =  mcMMO.database.Read("SELECT user FROM "+LoadProperties.MySQLtablePrefix+"users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'");   
						player.sendMessage(String.valueOf(i)+". "+ChatColor.GREEN+userslist.get(i).get(0)+" - "+ChatColor.WHITE+username.get(1).get(0));  
						//System.out.println(username.get(1).get(0));
						//System.out.println("Mining : " + userslist.get(i).get(0) + ", User id : " + userslist.get(i).get(1));
					}
				}
			}
		}

		else if(LoadProperties.mcrefreshEnable && label.equalsIgnoreCase(LoadProperties.mcrefresh)){ 

			if(!mcPermissions.getInstance().mcrefresh(player)){
				player.sendMessage(ChatColor.YELLOW+"[mcMMO] "+ChatColor.DARK_RED +mcLocale.getString("mcPlayerListener.NoPermission"));  
				return true;
			}
			if(split.length >= 2 && isPlayer(split[1])){
				player.sendMessage("You have refreshed "+split[1]+"'s cooldowns!");  
				player = getPlayer(split[1]);
			}
			/*
			 * PREP MODES
			 */
			PP = Users.getProfile(player);
			PP.setRecentlyHurt((long) 0);
			PP.setHoePreparationMode(false);
			PP.setAxePreparationMode(false);
			PP.setFistsPreparationMode(false);
			PP.setSwordsPreparationMode(false);
			PP.setPickaxePreparationMode(false);
			/*
			 * GREEN TERRA
			 */
			PP.setGreenTerraMode(false);
			PP.setGreenTerraDeactivatedTimeStamp((long) 0);

			/*
			 * GIGA DRILL BREAKER
			 */
			PP.setGigaDrillBreakerMode(false);
			PP.setGigaDrillBreakerDeactivatedTimeStamp((long) 0);
			/*
			 * SERRATED STRIKE
			 */
			PP.setSerratedStrikesMode(false);
			PP.setSerratedStrikesDeactivatedTimeStamp((long) 0);
			/*
			 * SUPER BREAKER
			 */
			PP.setSuperBreakerMode(false);
			PP.setSuperBreakerDeactivatedTimeStamp((long) 0);
			/*
			 * TREE FELLER
			 */
			PP.setTreeFellerMode(false);
			PP.setTreeFellerDeactivatedTimeStamp((long) 0);
			/*
			 * BERSERK
			 */
			PP.setBerserkMode(false);
			PP.setBerserkDeactivatedTimeStamp((long)0);

			player.sendMessage(mcLocale.getString("mcPlayerListener.AbilitiesRefreshed")); 
		}
		/*
		 * GODMODE COMMAND
		 */
		else if(LoadProperties.mcgodEnable && label.equalsIgnoreCase(LoadProperties.mcgod))
		{ 
			if(mcPermissions.permissionsEnabled)
			{
				if(!mcPermissions.getInstance().mcgod(player))
				{
					player.sendMessage(ChatColor.YELLOW+"[mcMMO] "+ChatColor.DARK_RED +mcLocale.getString("mcPlayerListener.NoPermission"));  
					return true;
				}
				if(PP.getGodMode())
				{
					player.sendMessage(mcLocale.getString("mcPlayerListener.GodModeDisabled")); 
					PP.toggleGodMode();
				} else 
				{
					player.sendMessage(mcLocale.getString("mcPlayerListener.GodModeEnabled")); 
					PP.toggleGodMode();
				}
			}
		}
		else if(LoadProperties.clearmyspawnEnable && LoadProperties.enableMySpawn && label.equalsIgnoreCase(LoadProperties.clearmyspawn) && mcPermissions.getInstance().mySpawn(player))
		{ 
			double x = Bukkit.getServer().getWorlds().get(0).getSpawnLocation().getX();
			double y = Bukkit.getServer().getWorlds().get(0).getSpawnLocation().getY();
			double z = Bukkit.getServer().getWorlds().get(0).getSpawnLocation().getZ();
			String worldname = Bukkit.getServer().getWorlds().get(0).getName();
			PP.setMySpawn(x, y, z, worldname);
			player.sendMessage(mcLocale.getString("mcPlayerListener.MyspawnCleared")); 
		}
		else if(LoadProperties.mmoeditEnable && mcPermissions.permissionsEnabled && label.equalsIgnoreCase(""+LoadProperties.mmoedit))
		{ 

			if(!mcPermissions.getInstance().mmoedit(player))
			{
				player.sendMessage(ChatColor.YELLOW+"[mcMMO] "+ChatColor.DARK_RED +mcLocale.getString("mcPlayerListener.NoPermission"));  
				return true;
			}
			if(split.length < 3)
			{
				player.sendMessage(ChatColor.RED+"Usage is /"+LoadProperties.mmoedit+" playername skillname newvalue");  
				return true;
			}
			if(split.length == 4)
			{
				if(isPlayer(split[1]) && m.isInt(split[3]) && Skills.isSkill(split[2]))
				{
					int newvalue = Integer.valueOf(split[3]);
					Users.getProfile(getPlayer(split[1])).modifyskill(Skills.getSkillType(split[2]), newvalue);
					player.sendMessage(ChatColor.RED+split[2]+" has been modified."); 
				}
			}
			else if(split.length == 3)
			{
				if(m.isInt(split[2]) && Skills.isSkill(split[1]))
				{
					int newvalue = Integer.valueOf(split[2]);
					PP.modifyskill(Skills.getSkillType(split[1]), newvalue);
					player.sendMessage(ChatColor.RED+split[1]+" has been modified."); 
				}
			} else 
			{
				player.sendMessage(ChatColor.RED+"Usage is /"+LoadProperties.mmoedit+" playername skillname newvalue");  
			}
		}
		/*
		 * ADD EXPERIENCE COMMAND
		 */
		else if(LoadProperties.addxpEnable && mcPermissions.permissionsEnabled && label.equalsIgnoreCase(LoadProperties.addxp)){ 

			if(!mcPermissions.getInstance().mmoedit(player)){
				player.sendMessage(ChatColor.YELLOW+"[mcMMO] "+ChatColor.DARK_RED +mcLocale.getString("mcPlayerListener.NoPermission"));  
				return true;
			}
			if(split.length < 3){
				player.sendMessage(ChatColor.RED+"Usage is /"+LoadProperties.addxp+" playername skillname xp");  
				return true;
			}
			if(split.length == 4){
				if(isPlayer(split[1]) && m.isInt(split[3]) && Skills.isSkill(split[2]))
				{
					int newvalue = Integer.valueOf(split[3]);
					Users.getProfile(getPlayer(split[1])).addXP(Skills.getSkillType(split[2]), newvalue);
					getPlayer(split[1]).sendMessage(ChatColor.GREEN+"Experience granted!"); 
					player.sendMessage(ChatColor.RED+split[2]+" has been modified."); 
					Skills.XpCheckAll(getPlayer(split[1]));
				}
			}
			else if(split.length == 3 && m.isInt(split[2]) && Skills.isSkill(split[1]))
			{
				int newvalue = Integer.valueOf(split[2]);
				Users.getProfile(player).addXP(Skills.getSkillType(split[1]), newvalue);
				player.sendMessage(ChatColor.RED+split[1]+" has been modified."); 
			} else {
				player.sendMessage(ChatColor.RED+"Usage is /"+LoadProperties.addxp+" playername skillname xp");  
			}
		}
		else if(LoadProperties.ptpEnable && label.equalsIgnoreCase(LoadProperties.ptp) && PP.inParty())
		{
			

			if(!mcPermissions.getInstance().partyTeleport(player))
			{
				player.sendMessage(ChatColor.YELLOW+"[mcMMO] "+ChatColor.DARK_RED +mcLocale.getString("mcPlayerListener.NoPermission"));  
				return true;
			}
			if(split.length < 2)
			{
				player.sendMessage(ChatColor.RED+"Usage is /"+LoadProperties.ptp+" <playername>");  
				return true;
			}
			if(!isPlayer(split[1]))
			{
				player.sendMessage("That is not a valid player"); 
			}
			if(isPlayer(split[1]))
			{
				Player target = getPlayer(split[1]);
				PlayerProfile PPt = Users.getProfile(target);
				if(PP.getParty().equals(PPt.getParty()))
				{
					player.teleport(target);
					player.sendMessage(ChatColor.GREEN+"You have teleported to "+target.getName()); 
					target.sendMessage(ChatColor.GREEN+player.getName() + " has teleported to you."); 
				}
			}
		}
		/*
		 * WHOIS COMMAND
		 */
		else if(LoadProperties.whoisEnable && label.equalsIgnoreCase(LoadProperties.whois) && mcPermissions.getInstance().whois(player)){ 
			if(split.length < 2){
				player.sendMessage(ChatColor.RED + "Proper usage is /"+LoadProperties.whois+" <playername>");  
				return true;
			}
			//if split[1] is a player
			if(isPlayer(split[1]))
			{
				Player target = getPlayer(split[1]);
				PlayerProfile PPt = Users.getProfile(target);

				player.sendMessage(ChatColor.GREEN + "~~WHOIS RESULTS~~"); 
				player.sendMessage(target.getName());
				if(PPt.inParty())
					player.sendMessage("Party: "+PPt.getParty()); 
				player.sendMessage("Health: "+target.getHealth()+ChatColor.GRAY+" (20 is full health)");  
				player.sendMessage("OP: " + target.isOp()); 
				player.sendMessage(ChatColor.GREEN+"mcMMO Stats for "+ChatColor.YELLOW+target.getName()); 

				player.sendMessage(ChatColor.GOLD+"-=GATHERING SKILLS=-");
				if(mcPermissions.getInstance().excavation(target))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.ExcavationSkill"), PPt.getSkillLevel(SkillType.EXCAVATION), PPt.getSkillXpLevel(SkillType.EXCAVATION), PPt.getXpToLevel(SkillType.EXCAVATION)));
				if(mcPermissions.getInstance().herbalism(target))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.HerbalismSkill"), PPt.getSkillLevel(SkillType.HERBALISM), PPt.getSkillXpLevel(SkillType.HERBALISM), PPt.getXpToLevel(SkillType.HERBALISM)));
				if(mcPermissions.getInstance().mining(target))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.MiningSkill"), PPt.getSkillLevel(SkillType.MINING), PPt.getSkillXpLevel(SkillType.MINING), PPt.getXpToLevel(SkillType.MINING)));
				if(mcPermissions.getInstance().woodCuttingAbility(target))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.WoodcuttingSkill"), PPt.getSkillLevel(SkillType.WOODCUTTING), PPt.getSkillXpLevel(SkillType.WOODCUTTING), PPt.getXpToLevel(SkillType.WOODCUTTING)));

				player.sendMessage(ChatColor.GOLD+"-=COMBAT SKILLS=-");
				if(mcPermissions.getInstance().axes(target))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.AxesSkill"), PPt.getSkillLevel(SkillType.AXES), PPt.getSkillXpLevel(SkillType.AXES), PPt.getXpToLevel(SkillType.AXES)));
				if(mcPermissions.getInstance().archery(player))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.ArcherySkill"), PPt.getSkillLevel(SkillType.ARCHERY), PPt.getSkillXpLevel(SkillType.ARCHERY), PPt.getXpToLevel(SkillType.ARCHERY)));
				//if(mcPermissions.getInstance().sorcery(target))
				//player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.SorcerySkill"), PPt.getSkill("sorcery"), PPt.getSkill("sorceryXP"), PPt.getXpToLevel("excavation")));
				if(mcPermissions.getInstance().swords(target))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.SwordsSkill"), PPt.getSkillLevel(SkillType.SWORDS), PPt.getSkillXpLevel(SkillType.SWORDS), PPt.getXpToLevel(SkillType.SWORDS)));
				if(mcPermissions.getInstance().taming(target))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.TamingSkill"), PPt.getSkillLevel(SkillType.TAMING), PPt.getSkillXpLevel(SkillType.TAMING), PPt.getXpToLevel(SkillType.TAMING)));
				if(mcPermissions.getInstance().unarmed(target))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.UnarmedSkill"), PPt.getSkillLevel(SkillType.UNARMED), PPt.getSkillXpLevel(SkillType.UNARMED), PPt.getXpToLevel(SkillType.UNARMED)));

				player.sendMessage(ChatColor.GOLD+"-=MISC SKILLS=-");
				if(mcPermissions.getInstance().acrobatics(target))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.AcrobaticsSkill"), PPt.getSkillLevel(SkillType.ACROBATICS), PPt.getSkillXpLevel(SkillType.ACROBATICS), PPt.getXpToLevel(SkillType.ACROBATICS)));
				if(mcPermissions.getInstance().repair(target))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.RepairSkill"), PPt.getSkillLevel(SkillType.REPAIR), PPt.getSkillXpLevel(SkillType.REPAIR), PPt.getXpToLevel(SkillType.REPAIR)));	

				player.sendMessage(mcLocale.getString("mcPlayerListener.PowerLevel") +ChatColor.GREEN+(m.getPowerLevel(target))); 
			}
		}
		/*
		 * STATS COMMAND
		 */
		else if(LoadProperties.statsEnable && label.equalsIgnoreCase(LoadProperties.stats))
		{ 

			player.sendMessage(mcLocale.getString("mcPlayerListener.YourStats"));

			if(mcPermissions.getEnabled())
				player.sendMessage(mcLocale.getString("mcPlayerListener.NoSkillNote")); 

			ChatColor header = ChatColor.GOLD;

			if(Skills.hasGatheringSkills(player)){
				player.sendMessage(header+"-=GATHERING SKILLS=-");
				if(mcPermissions.getInstance().excavation(player))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.ExcavationSkill"), PP.getSkillLevel(SkillType.EXCAVATION), PP.getSkillXpLevel(SkillType.EXCAVATION), PP.getXpToLevel(SkillType.EXCAVATION)));
				if(mcPermissions.getInstance().herbalism(player))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.HerbalismSkill"), PP.getSkillLevel(SkillType.HERBALISM), PP.getSkillXpLevel(SkillType.HERBALISM), PP.getXpToLevel(SkillType.HERBALISM)));
				if(mcPermissions.getInstance().mining(player))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.MiningSkill"), PP.getSkillLevel(SkillType.MINING), PP.getSkillXpLevel(SkillType.MINING), PP.getXpToLevel(SkillType.MINING)));
				if(mcPermissions.getInstance().woodcutting(player))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.WoodcuttingSkill"), PP.getSkillLevel(SkillType.WOODCUTTING), PP.getSkillXpLevel(SkillType.WOODCUTTING), PP.getXpToLevel(SkillType.WOODCUTTING)));
			}
			if(Skills.hasCombatSkills(player)){
				player.sendMessage(header+"-=COMBAT SKILLS=-");
				if(mcPermissions.getInstance().axes(player))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.AxesSkill"), PP.getSkillLevel(SkillType.AXES), PP.getSkillXpLevel(SkillType.AXES), PP.getXpToLevel(SkillType.AXES)));
				if(mcPermissions.getInstance().archery(player))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.ArcherySkill"), PP.getSkillLevel(SkillType.ARCHERY), PP.getSkillXpLevel(SkillType.ARCHERY), PP.getXpToLevel(SkillType.ARCHERY)));
				//if(mcPermissions.getInstance().sorcery(player))
				//player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.SorcerySkill"), PP.getSkill("sorcery"), PP.getSkill("sorceryXP"), PP.getXpToLevel("excavation")));
				if(mcPermissions.getInstance().swords(player))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.SwordsSkill"), PP.getSkillLevel(SkillType.SWORDS), PP.getSkillXpLevel(SkillType.SWORDS), PP.getXpToLevel(SkillType.SWORDS)));
				if(mcPermissions.getInstance().taming(player))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.TamingSkill"), PP.getSkillLevel(SkillType.TAMING), PP.getSkillXpLevel(SkillType.TAMING), PP.getXpToLevel(SkillType.TAMING)));
				if(mcPermissions.getInstance().unarmed(player))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.UnarmedSkill"), PP.getSkillLevel(SkillType.UNARMED), PP.getSkillXpLevel(SkillType.UNARMED), PP.getXpToLevel(SkillType.UNARMED)));
			}

			if(Skills.hasMiscSkills(player)){
				player.sendMessage(header+"-=MISC SKILLS=-");
				if(mcPermissions.getInstance().acrobatics(player))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.AcrobaticsSkill"), PP.getSkillLevel(SkillType.ACROBATICS), PP.getSkillXpLevel(SkillType.ACROBATICS), PP.getXpToLevel(SkillType.ACROBATICS)));
				if(mcPermissions.getInstance().repair(player))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.RepairSkill"), PP.getSkillLevel(SkillType.REPAIR), PP.getSkillXpLevel(SkillType.REPAIR), PP.getXpToLevel(SkillType.REPAIR)));	
			}
			player.sendMessage(mcLocale.getString("mcPlayerListener.PowerLevel")+ChatColor.GREEN+(m.getPowerLevel(player))); 
		}
		//Invite Command
		else if(LoadProperties.inviteEnable && label.equalsIgnoreCase(LoadProperties.invite) && mcPermissions.getInstance().party(player)){ 

			Party Pinstance = Party.getInstance();
			
			if(!PP.inParty())
			{
				player.sendMessage(mcLocale.getString("mcPlayerListener.NotInParty")); 
				return true;
			}
			if(split.length < 2)
			{
				player.sendMessage(ChatColor.RED+"Usage is /"+LoadProperties.invite+" <playername>");  
				return true;
			}
			if(PP.inParty() && split.length >= 2 && isPlayer(split[1]))
			{
				if(Pinstance.canInvite(player, PP)) {
					Player target = getPlayer(split[1]);
					PlayerProfile PPt = Users.getProfile(target);
					PPt.modifyInvite(PP.getParty());
	
	
					player.sendMessage(mcLocale.getString("mcPlayerListener.InviteSuccess")); 
					//target.sendMessage(ChatColor.RED+"ALERT: "+ChatColor.GREEN+"You have received a party invite for "+PPt.getInvite()+" from "+player.getName());   
					target.sendMessage(mcLocale.getString("mcPlayerListener.ReceivedInvite1", new Object[] {PPt.getInvite(), player.getName()}));
					//target.sendMessage(ChatColor.YELLOW+"Type "+ChatColor.GREEN+LoadProperties.accept+ChatColor.YELLOW+" to accept the invite");   
					target.sendMessage(mcLocale.getString("mcPlayerListener.ReceivedInvite2", new Object[] {LoadProperties.accept}));
				} else {
					player.sendMessage(mcLocale.getString("Party.Locked"));
					return true;
				}
			}
		}
		//Accept invite
		else if(LoadProperties.acceptEnable && label.equalsIgnoreCase(LoadProperties.accept) && mcPermissions.getInstance().party(player))
		{ 
			if(PP.hasPartyInvite())
			{
				Party Pinstance = Party.getInstance();
				
				if(PP.inParty()) 
				{
					Pinstance.removeFromParty(player, PP);
				}
				PP.acceptInvite();
				Pinstance.addToParty(player, PP, PP.getParty(), true);
			} else {
				player.sendMessage(mcLocale.getString("mcPlayerListener.NoInvites")); 
			}
		}
		//Party command
		else if(LoadProperties.partyEnable && label.equalsIgnoreCase(LoadProperties.party))
		{ 
			if(!mcPermissions.getInstance().party(player))
			{
				player.sendMessage(ChatColor.YELLOW+"[mcMMO] "+ChatColor.DARK_RED +mcLocale.getString("mcPlayerListener.NoPermission"));  
				return true;
			}
			
			Party Pinstance = Party.getInstance();
			
			if(PP.inParty() && (!Pinstance.isParty(PP.getParty()) || !Pinstance.isInParty(player, PP))) 
			{
				Pinstance.addToParty(player, PP, PP.getParty(), false);
			}
			
			if(args.length == 0 && !PP.inParty())
			{
				player.sendMessage(mcLocale.getString("Party.Help1", new Object[] {LoadProperties.party}));
				player.sendMessage(mcLocale.getString("Party.Help2", new Object[] {LoadProperties.party}));
				player.sendMessage(mcLocale.getString("Party.Help3", new Object[] {LoadProperties.party}));
				return true;
			} else if(args.length == 0 && PP.inParty())
			{
				String tempList = ""; 
				int x = 0;
				for(Player p : Bukkit.getServer().getOnlinePlayers())
				{
					if(PP.getParty().equals(Users.getProfile(p).getParty()))
					{
						if(p != null && x+1 >= Pinstance.partyCount(player, Bukkit.getServer().getOnlinePlayers()))
						{
							if(Pinstance.isPartyLeader(p, PP.getParty()))
							{
								tempList+=ChatColor.GOLD+p.getName();
								x++;
							}
							else
							{
								tempList+= ChatColor.WHITE+p.getName();
								x++;
							}
						}
						if(p != null && x < Pinstance.partyCount(player, Bukkit.getServer().getOnlinePlayers()))
						{
							if(Pinstance.isPartyLeader(p, PP.getParty()))
							{
								tempList+=ChatColor.GOLD+p.getName()+", ";
								x++;
							}
							else
							{
								tempList+= ChatColor.WHITE+p.getName()+", ";
								x++;
							}
						}
					}
				}
				player.sendMessage(mcLocale.getString("mcPlayerListener.YouAreInParty", new Object[] {PP.getParty()}));
				player.sendMessage(mcLocale.getString("mcPlayerListener.PartyMembers")+" ("+tempList+ChatColor.GREEN+")");
				return true;
			} else if(args.length == 1){
				if(args[0].equals("q") && PP.inParty()) 
				{
					ArrayList<Player> partymembers = Party.getInstance().getPartyMembers(player);
					
					Pinstance.removeFromParty(player, PP);
					
					if(LoadProperties.spoutEnabled)
						SpoutStuff.resetPartyHealthBarDisplays(partymembers);
					
					player.sendMessage(mcLocale.getString("mcPlayerListener.LeftParty")); 
					return true;
				} else if (args[0].equalsIgnoreCase("?")) {
					player.sendMessage(mcLocale.getString("Party.Help4", new Object[] {LoadProperties.party}));
					player.sendMessage(mcLocale.getString("Party.Help2", new Object[] {LoadProperties.party}));
					player.sendMessage(mcLocale.getString("Party.Help5", new Object[] {LoadProperties.party}));
					player.sendMessage(mcLocale.getString("Party.Help6", new Object[] {LoadProperties.party}));
					player.sendMessage(mcLocale.getString("Party.Help7", new Object[] {LoadProperties.party}));
					player.sendMessage(mcLocale.getString("Party.Help8", new Object[] {LoadProperties.party}));
					player.sendMessage(mcLocale.getString("Party.Help9", new Object[] {LoadProperties.party}));
				} else if (args[0].equalsIgnoreCase("lock")) 
				{
					if(PP.inParty()) 
					{
						if(Pinstance.isPartyLeader(player, PP.getParty())) 
						{
							Pinstance.lockParty(PP.getParty());
							player.sendMessage(mcLocale.getString("Party.Locked"));
						} else 
						{
							player.sendMessage(mcLocale.getString("Party.NotOwner"));
						}
					} else 
					{
						player.sendMessage(mcLocale.getString("Party.InvalidName"));
					}
				} else if (args[0].equalsIgnoreCase("unlock")) 
				{
					if(PP.inParty()) 
					{
						if(Pinstance.isPartyLeader(player, PP.getParty())) 
						{
							Pinstance.unlockParty(PP.getParty());
							player.sendMessage(mcLocale.getString("Party.Unlocked"));
						} else 
						{
							player.sendMessage(mcLocale.getString("Party.NotOwner"));
						}
					} else 
					{
						player.sendMessage(mcLocale.getString("Party.InvalidName"));
					}
				//Party debugging command.
				//} else if (args[0].equalsIgnoreCase("dump")) {
				//	Pinstance.dump(player);
				} else 
				{
					if(PP.inParty()) 
					{
						ArrayList<Player> partymembers = Party.getInstance().getPartyMembers(player);
						
						Pinstance.removeFromParty(player, PP);
						
						if(LoadProperties.spoutEnabled)
							SpoutStuff.resetPartyHealthBarDisplays(partymembers);
					}
					Pinstance.addToParty(player, PP, args[0], false);
					return true;
				}
			} else if(args.length == 2 && PP.inParty()) 
			{
				if(args[0].equalsIgnoreCase("password")) 
				{
					if(Pinstance.isPartyLeader(player, PP.getParty())) 
					{
						if(Pinstance.isPartyLocked(PP.getParty())) 
						{
							Pinstance.setPartyPassword(PP.getParty(), args[1]);
							player.sendMessage(mcLocale.getString("Party.PasswordSet", new Object[] {args[1]}));
						} else 
						{
							player.sendMessage(mcLocale.getString("Party.IsntLocked"));
						}
					} else 
					{
						player.sendMessage(mcLocale.getString("Party.NotOwner"));
					}
				} else if(args[0].equalsIgnoreCase("kick")) 
				{
					if(Pinstance.isPartyLeader(player, PP.getParty())) 
					{
						if(Pinstance.isPartyLocked(PP.getParty())) 
						{
							Player tPlayer = null;
							if(Bukkit.getServer().getPlayer(args[1]) != null) tPlayer = Bukkit.getServer().getPlayer(args[1]);
							if(tPlayer == null) 
							{
								player.sendMessage(mcLocale.getString("Party.CouldNotKick", new Object[] {args[1]}));
							}
							if(!Pinstance.inSameParty(player, tPlayer)) 
							{
								player.sendMessage(mcLocale.getString("Party.NotInYourParty", new Object[] {tPlayer.getName()}));
							} else 
							{
								//Not an admin
								if(!mcPermissions.getInstance().admin(player)) 
								{
									//Can't kick an admin
									if(mcPermissions.getInstance().admin(tPlayer)) 
									{
										player.sendMessage(mcLocale.getString("Party.CouldNotKick", new Object[] {tPlayer.getName()}));
									}
								}
								PlayerProfile tPP = Users.getProfile(tPlayer);
								
								ArrayList<Player> partymembers = Party.getInstance().getPartyMembers(player);
								
								Pinstance.removeFromParty(tPlayer, tPP);
								
								if(LoadProperties.spoutEnabled)
									SpoutStuff.resetPartyHealthBarDisplays(partymembers);
								
								tPlayer.sendMessage(mcLocale.getString("mcPlayerListener.LeftParty"));
							}
						} else {
							player.sendMessage(mcLocale.getString("Party.IsntLocked"));
						}
					} else {
						player.sendMessage(mcLocale.getString("Party.NotOwner"));
					}
				} else if(args[0].equalsIgnoreCase("owner")) 
				{
					if(Pinstance.isPartyLeader(player, PP.getParty())) 
					{
						Player tPlayer = null;
						if(Bukkit.getServer().getPlayer(args[1]) != null) tPlayer = Bukkit.getServer().getPlayer(args[1]);
						if(tPlayer == null) 
						{
							player.sendMessage(mcLocale.getString("Party.CouldNotSetOwner", new Object[] {args[1]}));
						}
						if(!Pinstance.inSameParty(player, tPlayer)) 
						{
							player.sendMessage(mcLocale.getString("Party.CouldNotSetOwner", new Object[] {tPlayer.getName()}));
						} else 
						{
							Pinstance.setPartyLeader(PP.getParty(), tPlayer.getName());
						}
					} else 
					{
						player.sendMessage(mcLocale.getString("Party.NotOwner"));
					}
				} else 
				{
					Pinstance.removeFromParty(player, PP);
					Pinstance.addToParty(player, PP, args[0], false, args[1]);
				}
			} else if(args.length == 2 && !PP.inParty()) 
			{
				Pinstance.addToParty(player, PP, args[0], false, args[1]);
			}
		}
		else if(LoadProperties.partyEnable && label.equalsIgnoreCase("p")){

			// Console message?
			if(!(sender instanceof Player)) 
			{
				if(args.length < 2) return true;
				String pMessage = args[1];
				for (int i = 2; i <= args.length - 1; i++) {
					pMessage = pMessage + " " + args[i];
				}

				String pPrefix = ChatColor.GREEN + "(" + ChatColor.WHITE
				+ "*Console*" + ChatColor.GREEN + ") ";

				log.log(Level.INFO,
						"[P](" + args[0] + ")" + "<*Console*> "
						+ pMessage);

				for(Player herp : Bukkit.getServer().getOnlinePlayers()) {
					if(Users.getProfile(herp).inParty()) {
						if(Users.getProfile(herp).getParty().equalsIgnoreCase(args[0])) {
							herp.sendMessage(pPrefix + pMessage);
						}
					}
				}
				return true;
			}

			if(!mcPermissions.getInstance().party(player)){
				player.sendMessage(ChatColor.YELLOW+"[mcMMO] "+ChatColor.DARK_RED +mcLocale.getString("mcPlayerListener.NoPermission"));  
				return true;
			}

			// Not a toggle, a message

			if (args.length >= 1) {
				String pMessage = args[0];
				for (int i = 1; i <= args.length - 1; i++) {
					pMessage = pMessage + " " + args[i];
				}
				String pPrefix = ChatColor.GREEN + "(" + ChatColor.WHITE
				+ player.getDisplayName() + ChatColor.GREEN + ") ";

				log.log(Level.INFO,
						"[P](" + PP.getParty() + ")" + "<" + player.getDisplayName()
						+ "> " + pMessage);

				for (Player herp : Bukkit.getServer().getOnlinePlayers()) {
					if (Users.getProfile(herp).inParty()) {
						if (Party.getInstance().inSameParty(herp, player))
							herp.sendMessage(pPrefix + pMessage);
					}
				}

				return true;
			}

			if(PP.getAdminChatMode())
				PP.toggleAdminChat();

			PP.togglePartyChat();

			if(PP.getPartyChatMode()){
				//player.sendMessage(ChatColor.GREEN + "Party Chat Toggled On"); 
				player.sendMessage(mcLocale.getString("mcPlayerListener.PartyChatOn"));
			} else {
				//player.sendMessage(ChatColor.GREEN + "Party Chat Toggled " + ChatColor.RED + "Off");  
				player.sendMessage(mcLocale.getString("mcPlayerListener.PartyChatOff"));
			}
		}

		else if(label.equalsIgnoreCase("a")){
			// Console message?
			if(!(sender instanceof Player) && args.length >= 1) 
			{
				String aMessage = args[0];
				for (int i = 1; i <= args.length - 1; i++) {
					aMessage = aMessage + " " + args[i];
				}

				String aPrefix = ChatColor.AQUA + "{" + ChatColor.WHITE
				+ "*Console*" + ChatColor.AQUA + "} ";

				log.log(Level.INFO, "[A]<*Console*> "
						+ aMessage);

				for (Player herp : Bukkit.getServer().getOnlinePlayers()) {
					if (mcPermissions.getInstance().adminChat(herp) || herp.isOp())
						herp.sendMessage(aPrefix + aMessage);
				}
				return true;
			}

			if(!mcPermissions.getInstance().adminChat(player) && !player.isOp())
			{
				player.sendMessage(ChatColor.YELLOW+"[mcMMO] "+ChatColor.DARK_RED +mcLocale.getString("mcPlayerListener.NoPermission"));  
				return true;
			}

			// Not a toggle, a message

			if (args.length >= 1) {
				String aMessage = args[0];
				for (int i = 1; i <= args.length - 1; i++) {
					aMessage = aMessage + " " + args[i];
				}

				String aPrefix = ChatColor.AQUA + "{" + ChatColor.WHITE
				+ player.getName() + ChatColor.AQUA + "} ";
				log.log(Level.INFO, "[A]<" + player.getDisplayName() + "> "
						+ aMessage);
				for (Player herp : Bukkit.getServer().getOnlinePlayers()) {
					if (mcPermissions.getInstance().adminChat(herp) || herp.isOp())
						herp.sendMessage(aPrefix + aMessage);
				}
				return true;
			}

			if(PP.getPartyChatMode())
				PP.togglePartyChat();

			PP.toggleAdminChat();

			if(PP.getAdminChatMode())
			{
				player.sendMessage(mcLocale.getString("mcPlayerListener.AdminChatOn"));
				//player.sendMessage(ChatColor.AQUA + "Admin chat toggled " + ChatColor.GREEN + "On");  
			} else {
				player.sendMessage(mcLocale.getString("mcPlayerListener.AdminChatOff"));
				//player.sendMessage(ChatColor.AQUA + "Admin chat toggled " + ChatColor.RED + "Off");  
			}
		}

		/*
		 * MYSPAWN
		 */
		else if(LoadProperties.myspawnEnable && LoadProperties.enableMySpawn && label.equalsIgnoreCase(LoadProperties.myspawn)){ 
			if(!mcPermissions.getInstance().mySpawn(player)){
				player.sendMessage(ChatColor.YELLOW+"[mcMMO] "+ChatColor.DARK_RED +mcLocale.getString("mcPlayerListener.NoPermission"));  
				return true;
			}
			if(System.currentTimeMillis() < (PP.getMySpawnATS()*1000) + 3600000){
				long x = (((PP.getMySpawnATS()*1000) + 3600000) - System.currentTimeMillis());
				int y = (int) (x/60000);
				int z = (int) ((x/1000) - (y*60));
				player.sendMessage(mcLocale.getString("mcPlayerListener.MyspawnTimeNotice", new Object[] {y, z}));    
				return true;
			}
			PP.setMySpawnATS(System.currentTimeMillis());
			if(PP.getMySpawn(player) != null)
			{
				Location mySpawn = PP.getMySpawn(player);
				
				if(mySpawn != null){
					//It's done twice because it acts oddly when you are in another world
					player.teleport(mySpawn);
					player.teleport(mySpawn);
				}
			} else {
				player.sendMessage(mcLocale.getString("mcPlayerListener.MyspawnNotExist")); 
			}
		}
		return true;
	}
}
