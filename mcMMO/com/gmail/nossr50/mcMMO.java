package com.gmail.nossr50;

import com.gmail.nossr50.datatypes.Mob;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.config.*;
import com.gmail.nossr50.listeners.mcBlockListener;
import com.gmail.nossr50.listeners.mcEntityListener;
import com.gmail.nossr50.listeners.mcPlayerListener;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.skills.*;
import com.nijikokun.bukkit.Permissions.Permissions;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.entity.Player;


public class mcMMO extends JavaPlugin 
{
	/*
	 * I never expected mcMMO to get so popular!
	 * Thanks for all the support for the mod
	 * Thanks to the people who have worked on the code
	 * Thanks to the donators who helped me out financially
	 * Thanks to the server admins who use my mod :)
	 * 
	 * This mod is open source, and its going to stay that way >:3
	 * 
	 * Donate via paypal to nossr50@gmail.com (A million thanks to anyone that does!)
	 */
	
	public static String maindirectory = "plugins" + File.separator + "mcMMO";
	File file = new File(maindirectory + File.separator + "config.yml");
	public static final Logger log = Logger.getLogger("Minecraft"); 
	private final mcPlayerListener playerListener = new mcPlayerListener(this);
	private final mcBlockListener blockListener = new mcBlockListener(this);
	private final mcEntityListener entityListener = new mcEntityListener(this);
	public static mcPermissions permissionHandler = new mcPermissions();
	private Permissions permissions;

	private Timer mcMMO_Timer = new Timer(true); //BLEED AND REGENERATION
	//private Timer mcMMO_SpellTimer = new Timer(true);

	public static Database database = null;
	public Mob mob = new Mob();
	public Misc misc = new Misc(this);
	public Sorcery sorcery = new Sorcery(this);

	//Config file stuff
	LoadProperties config = new LoadProperties();

	public void onEnable() 
	{
		new File(maindirectory).mkdir();
		
		mcPermissions.initialize(getServer());
		config.configCheck();
		
		if(!LoadProperties.useMySQL)
			Users.getInstance().loadUsers(); //Load Users file

		/*
		 * REGISTER EVENTS
		 */

		PluginManager pm = getServer().getPluginManager();

		//Player Stuff
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_LOGIN, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Lowest, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.Normal, this);

		//Block Stuff
		pm.registerEvent(Event.Type.BLOCK_DAMAGE, blockListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.BLOCK_FROMTO, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Normal, this);

		//Entity Stuff
		pm.registerEvent(Event.Type.ENTITY_TARGET, entityListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.CREATURE_SPAWN, entityListener, Priority.Normal, this);

		PluginDescriptionFile pdfFile = this.getDescription();
		mcPermissions.initialize(getServer());

		if(LoadProperties.useMySQL)
		{
			database = new Database(this);
			database.createStructure();
		} else
			Leaderboard.makeLeaderboards(); //Make the leaderboards

		for(Player player : getServer().getOnlinePlayers()){Users.addUser(player);} //In case of reload add all users back into PlayerProfile
		System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );  
		mcMMO_Timer.schedule(new mcTimer(this), (long)0, (long)(1000));
		//mcMMO_SpellTimer.schedule(new mcTimerSpells(this), (long)0, (long)(100));
	}

	public boolean inSameParty(Player playera, Player playerb){
		if(Users.getProfile(playera).inParty() && Users.getProfile(playerb).inParty()){
			if(Users.getProfile(playera).getParty().equals(Users.getProfile(playerb).getParty())){
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	public void getXpToLevel(Player player, String skillname){
		Users.getProfile(player).getXpToLevel(skillname.toLowerCase());
	}
	public void removeXp(Player player, String skillname, Integer newvalue){
		PlayerProfile PP = Users.getProfile(player);
		PP.removeXP(skillname, newvalue);
		Skills.XpCheck(player);
	}
	public void addXp(Player player, String skillname, Integer newvalue){
		PlayerProfile PP = Users.getProfile(player);
		PP.addXP(skillname, newvalue);
		Skills.XpCheck(player);
	}
	public void modifySkill(Player player, String skillname, Integer newvalue){
		PlayerProfile PP = Users.getProfile(player);
		PP.modifyskill(newvalue, skillname);
	}
	public ArrayList<String> getParties(){
		String location = "plugins/mcMMO/mcmmo.users"; 
		ArrayList<String> parties = new ArrayList<String>();
		try {
			//Open the users file
			FileReader file = new FileReader(location);
			BufferedReader in = new BufferedReader(file);
			String line = ""; 
			while((line = in.readLine()) != null)
			{
				String[] character = line.split(":"); 
				String theparty = null;
				//Party
				if(character.length > 3)
					theparty = character[3];
				if(!parties.contains(theparty))
					parties.add(theparty);
			}
			in.close();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Exception while reading " 
					+ location + " (Are you sure you formatted it correctly?)", e); 
		}
		return parties;
	}
	public static String getPartyName(Player player){
		PlayerProfile PP = Users.getProfile(player);
		return PP.getParty();
	}
	public static boolean inParty(Player player){
		PlayerProfile PP = Users.getProfile(player);
		return PP.inParty();
	}
	public Permissions getPermissions() {
		return permissions;
	}
	public void onDisable() {
		System.out.println("mcMMO was disabled."); 
	}

	public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
		Player player = (Player) sender;
		PlayerProfile PP = Users.getProfile(player);
		
		String[] split = new String[args.length + 1];
		split[0] = label;
		for(int a = 0; a < args.length; a++){
			split[a + 1] = args[a];
		}
		
		//Check if the command is an MMO related help command
		if(split[0].equalsIgnoreCase("taming") || split[0].toLowerCase().equalsIgnoreCase(mcLocale.getString("m.SkillTaming").toLowerCase())){ 
			float skillvalue = (float)PP.getSkill("taming");

			String percentage = String.valueOf((skillvalue / 1000) * 100);
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.SkillTaming")})); 
			player.sendMessage(mcLocale.getString("m.XPGain", new Object[] {mcLocale.getString("m.XPGainTaming")})); 
			if(mcPermissions.getInstance().taming(player))
				player.sendMessage(mcLocale.getString("m.LVL", new Object[] {PP.getSkillToString("taming"), PP.getSkillToString("tamingXP"), PP.getXpToLevel("taming")}));
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.Effects")})); 
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsTaming1_0"), mcLocale.getString("m.EffectsTaming1_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsTaming2_0"), mcLocale.getString("m.EffectsTaming2_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsTaming3_0"), mcLocale.getString("m.EffectsTaming3_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsTaming4_0"), mcLocale.getString("m.EffectsTaming4_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsTaming5_0"), mcLocale.getString("m.EffectsTaming5_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsTaming6_0"), mcLocale.getString("m.EffectsTaming6_1")}));  
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.YourStats")})); 
			if(PP.getSkill("taming") < 100)
				player.sendMessage(mcLocale.getString("m.AbilityLockTemplate", new Object[] {mcLocale.getString("m.AbilLockTaming1")})); 
			else
				player.sendMessage(mcLocale.getString("m.AbilityBonusTemplate", new Object[] {mcLocale.getString("m.AbilBonusTaming1_0"), mcLocale.getString("m.AbilBonusTaming1_1")}));  
			if(PP.getSkill("taming") < 250)
				player.sendMessage(mcLocale.getString("m.AbilityLockTemplate", new Object[] {mcLocale.getString("m.AbilLockTaming2")})); 
			else
				player.sendMessage(mcLocale.getString("m.AbilityBonusTemplate", new Object[] {mcLocale.getString("m.AbilBonusTaming2_0"), mcLocale.getString("m.AbilBonusTaming2_1")}));  
			if(PP.getSkill("taming") < 500)
				player.sendMessage(mcLocale.getString("m.AbilityLockTemplate", new Object[] {mcLocale.getString("m.AbilLockTaming3")})); 
			else
				player.sendMessage(mcLocale.getString("m.AbilityBonusTemplate", new Object[] {mcLocale.getString("m.AbilBonusTaming3_0"), mcLocale.getString("m.AbilBonusTaming3_1")}));  
			if(PP.getSkill("taming") < 750)
				player.sendMessage(mcLocale.getString("m.AbilityLockTemplate", new Object[] {mcLocale.getString("m.AbilLockTaming4")})); 
			else
				player.sendMessage(mcLocale.getString("m.AbilityBonusTemplate", new Object[] {mcLocale.getString("m.AbilBonusTaming4_0"), mcLocale.getString("m.AbilBonusTaming4_1")}));  
			player.sendMessage(mcLocale.getString("m.TamingGoreChance", new Object[] {percentage})); 
		}
		if(split[0].equalsIgnoreCase("woodcutting") || split[0].toLowerCase().equalsIgnoreCase(mcLocale.getString("m.SkillWoodCutting").toLowerCase())){ 
			float skillvalue = (float)PP.getSkill("woodcutting");
			int ticks = 2;
			int x = PP.getSkill("woodcutting");
			while(x >= 50){
				x-=50;
				ticks++;
			}
			String percentage = String.valueOf((skillvalue / 1000) * 100);
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.SkillWoodCutting")})); 
			player.sendMessage(mcLocale.getString("m.XPGain", new Object[] {mcLocale.getString("m.XPGainWoodCutting")})); 
			if(mcPermissions.getInstance().woodcutting(player))
				player.sendMessage(mcLocale.getString("m.LVL", new Object[] {PP.getSkillToString("woodcutting"), PP.getSkillToString("woodcuttingXP"), PP.getXpToLevel("woodcutting")}));
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.Effects")})); 
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsWoodCutting1_0"), mcLocale.getString("m.EffectsWoodCutting1_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsWoodCutting2_0"), mcLocale.getString("m.EffectsWoodCutting2_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsWoodCutting3_0"), mcLocale.getString("m.EffectsWoodCutting3_1")}));  
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.YourStats")})); 
			if(PP.getSkill("woodcutting") < 100)
				player.sendMessage(mcLocale.getString("m.AbilityLockTemplate", new Object[] {mcLocale.getString("m.AbilLockWoodCutting1")})); 
			else
				player.sendMessage(mcLocale.getString("m.AbilityBonusTemplate", new Object[] {mcLocale.getString("m.AbilBonusWoodCutting1_0"), mcLocale.getString("m.AbilBonusWoodCutting1_1")}));  
			player.sendMessage(mcLocale.getString("m.WoodCuttingDoubleDropChance", new Object[] {percentage})); 
			player.sendMessage(mcLocale.getString("m.WoodCuttingTreeFellerLength", new Object[] {ticks})); 
		}
		if(split[0].equalsIgnoreCase("archery") || split[0].toLowerCase().equalsIgnoreCase(mcLocale.getString("m.SkillArchery").toLowerCase())){ 
			Integer rank = 0;
			if(PP.getSkill("archery") >= 50)
				rank++;
			if(PP.getSkill("archery") >= 250)
				rank++;
			if(PP.getSkill("archery") >= 575)
				rank++;
			if(PP.getSkill("archery") >= 725)
				rank++;
			if(PP.getSkill("archery") >= 1000)
				rank++;
			float skillvalue = (float)PP.getSkill("archery");
			String percentage = String.valueOf((skillvalue / 1000) * 100);

			int ignition = 20;
			if(PP.getSkill("archery") >= 200)
				ignition+=20;
			if(PP.getSkill("archery") >= 400)
				ignition+=20;
			if(PP.getSkill("archery") >= 600)
				ignition+=20;
			if(PP.getSkill("archery") >= 800)
				ignition+=20;
			if(PP.getSkill("archery") >= 1000)
				ignition+=20;

			String percentagedaze;
			if(PP.getSkill("archery") < 1000){
				percentagedaze = String.valueOf((skillvalue / 2000) * 100);
			} else {
				percentagedaze = "50"; 
			}
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.SkillArchery")})); 
			player.sendMessage(mcLocale.getString("m.XPGain", new Object[] {mcLocale.getString("m.XPGainArchery")})); 
			if(mcPermissions.getInstance().archery(player))
				player.sendMessage(mcLocale.getString("m.LVL", new Object[] {PP.getSkillToString("archery"), PP.getSkillToString("archeryXP"), PP.getXpToLevel("archery")}));
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
		if(split[0].equalsIgnoreCase("axes") || split[0].toLowerCase().equalsIgnoreCase(mcLocale.getString("m.SkillAxes"))){ 
			String percentage;
			float skillvalue = (float)PP.getSkill("axes");
			if(PP.getSkill("axes") < 750){
				percentage = String.valueOf((skillvalue / 1000) * 100);
			} else {
				percentage = "75"; 
			}
			int ticks = 2;
			int x = PP.getSkill("axes");
			while(x >= 50){
				x-=50;
				ticks++;
			}

			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.SkillAxes")})); 
			player.sendMessage(mcLocale.getString("m.XPGain", new Object[] {mcLocale.getString("m.XPGainAxes")})); 
			if(mcPermissions.getInstance().axes(player))
				player.sendMessage(mcLocale.getString("m.LVL", new Object[] {PP.getSkillToString("axes"), PP.getSkillToString("axesXP"), PP.getXpToLevel("axes")}));
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.Effects")})); 
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsAxes1_0"), mcLocale.getString("m.EffectsAxes1_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsAxes2_0"), mcLocale.getString("m.EffectsAxes2_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsAxes3_0"), mcLocale.getString("m.EffectsAxes3_1")}));  
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.YourStats")})); 
			player.sendMessage(mcLocale.getString("m.AxesCritChance", new Object[] {percentage})); 
			if(PP.getSkill("axes") < 500){
				player.sendMessage(mcLocale.getString("m.AbilityLockTemplate", new Object[] {mcLocale.getString("m.AbilLockAxes1")})); 
			} else {
				player.sendMessage(mcLocale.getString("m.AbilityBonusTemplate", new Object[] {mcLocale.getString("m.AbilBonusAxes1_0"), mcLocale.getString("m.AbilBonusAxes1_1")}));  
			}
			player.sendMessage(mcLocale.getString("m.AxesSkullLength", new Object[] {ticks})); 
		}
		if(split[0].equalsIgnoreCase("swords") || split[0].toLowerCase().equalsIgnoreCase(mcLocale.getString("m.SkillSwords").toLowerCase())){ 
			int bleedrank = 2;
			String percentage, parrypercentage = null, counterattackpercentage;
			float skillvalue = (float)PP.getSkill("swords");
			if(PP.getSkill("swords") < 750){
				percentage = String.valueOf((skillvalue / 1000) * 100);
			} else {
				percentage = "75"; 
			}
			if(skillvalue >= 750)
				bleedrank+=1;

			if(PP.getSkill("swords") <= 900){
				parrypercentage = String.valueOf((skillvalue / 3000) * 100);
			} else {
				parrypercentage = "30"; 
			}

			if(PP.getSkill("swords") <= 600){
				counterattackpercentage = String.valueOf((skillvalue / 2000) * 100);
			} else {
				counterattackpercentage = "30"; 
			}

			int ticks = 2;
			int x = PP.getSkill("swords");
			while(x >= 50){
				x-=50;
				ticks++;
			}

			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.SkillSwords")})); 
			player.sendMessage(mcLocale.getString("m.XPGain", new Object[] {mcLocale.getString("m.XPGainSwords")})); 
			if(mcPermissions.getInstance().swords(player))
				player.sendMessage(mcLocale.getString("m.LVL", new Object[] {PP.getSkillToString("swords"), PP.getSkillToString("swordsXP"), PP.getXpToLevel("swords")}));
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
		if(split[0].equalsIgnoreCase("acrobatics") || split[0].toLowerCase().equalsIgnoreCase(mcLocale.getString("m.SkillAcrobatics").toLowerCase())){ 
			String dodgepercentage;
			float skillvalue = (float)PP.getSkill("acrobatics");
			String percentage = String.valueOf((skillvalue / 1000) * 100);
			String gracepercentage = String.valueOf(((skillvalue / 1000) * 100) * 2);
			if(PP.getSkill("acrobatics") <= 800){
				dodgepercentage = String.valueOf((skillvalue / 4000 * 100));
			} else {
				dodgepercentage = "20"; 
			}
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.SkillAcrobatics")})); 
			player.sendMessage(mcLocale.getString("m.XPGain", new Object[] {mcLocale.getString("m.XPGainAcrobatics")})); 
			if(mcPermissions.getInstance().acrobatics(player))
				player.sendMessage(mcLocale.getString("m.LVL", new Object[] {PP.getSkillToString("acrobatics"), PP.getSkillToString("acrobaticsXP"), PP.getXpToLevel("acrobatics")}));
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.Effects")})); 
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsAcrobatics1_0"), mcLocale.getString("m.EffectsAcrobatics1_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsAcrobatics2_0"), mcLocale.getString("m.EffectsAcrobatics2_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsAcrobatics3_0"), mcLocale.getString("m.EffectsAcrobatics3_1")}));  
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.YourStats")})); 
			player.sendMessage(mcLocale.getString("m.AcrobaticsRollChance", new Object[] {percentage})); 
			player.sendMessage(mcLocale.getString("m.AcrobaticsGracefulRollChance", new Object[] {gracepercentage})); 
			player.sendMessage(mcLocale.getString("m.AcrobaticsDodgeChance", new Object[] {dodgepercentage})); 
		}
		if(split[0].equalsIgnoreCase("mining") || split[0].toLowerCase().equalsIgnoreCase(mcLocale.getString("m.SkillMining"))){ 
			float skillvalue = (float)PP.getSkill("mining");
			String percentage = String.valueOf((skillvalue / 1000) * 100);
			int ticks = 2;
			int x = PP.getSkill("mining");
			while(x >= 50){
				x-=50;
				ticks++;
			}
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.SkillMining")})); 
			player.sendMessage(mcLocale.getString("m.XPGain", new Object[] {mcLocale.getString("m.XPGainMining")})); 
			if(mcPermissions.getInstance().mining(player))
				player.sendMessage(mcLocale.getString("m.LVL", new Object[] {PP.getSkillToString("mining"), PP.getSkillToString("miningXP"), PP.getXpToLevel("mining")}));
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.Effects")})); 
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsMining1_0"), mcLocale.getString("m.EffectsMining1_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsMining2_0"), mcLocale.getString("m.EffectsMining2_1")}));  
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.YourStats")})); 
			player.sendMessage(mcLocale.getString("m.MiningDoubleDropChance", new Object[] {percentage})); 
			player.sendMessage(mcLocale.getString("m.MiningSuperBreakerLength", new Object[] {ticks})); 
		}
		if(split[0].equalsIgnoreCase("repair") || split[0].toLowerCase().equalsIgnoreCase(mcLocale.getString("m.SkillRepair").toLowerCase())){ 
			float skillvalue = (float)PP.getSkill("repair");
			String percentage = String.valueOf((skillvalue / 1000) * 100);
			String repairmastery = String.valueOf((skillvalue / 500) * 100);
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.SkillRepair")})); 
			player.sendMessage(mcLocale.getString("m.XPGain", new Object[] {mcLocale.getString("m.XPGainRepair")})); 
			if(mcPermissions.getInstance().repair(player))
				player.sendMessage(mcLocale.getString("m.LVL", new Object[] {PP.getSkillToString("repair"), PP.getSkillToString("repairXP"), PP.getXpToLevel("repair")}));
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.Effects")})); 
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsRepair1_0"), mcLocale.getString("m.EffectsRepair1_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsRepair2_0"), mcLocale.getString("m.EffectsRepair2_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsRepair3_0"), mcLocale.getString("m.EffectsRepair3_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsRepair4_0", new Object[]{LoadProperties.repairdiamondlevel}), mcLocale.getString("m.EffectsRepair4_1")}));  
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.YourStats")})); 
			player.sendMessage(mcLocale.getString("m.RepairRepairMastery", new Object[] {repairmastery})); 
			player.sendMessage(mcLocale.getString("m.RepairSuperRepairChance", new Object[] {percentage})); 
		}
		if(split[0].equalsIgnoreCase("unarmed")){ 
			String percentage, arrowpercentage;
			float skillvalue = (float)PP.getSkill("unarmed");

			if(PP.getSkill("unarmed") < 1000){
				percentage = String.valueOf((skillvalue / 4000) * 100);
			} else {
				percentage = "25"; 
			}

			if(PP.getSkill("unarmed") < 1000){
				arrowpercentage = String.valueOf(((skillvalue / 1000) * 100) / 2);
			} else {
				arrowpercentage = "50"; 
			}


			int ticks = 2;
			int x = PP.getSkill("unarmed");
			while(x >= 50){
				x-=50;
				ticks++;
			}

			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.SkillUnarmed")})); 
			player.sendMessage(mcLocale.getString("m.XPGain", new Object[] {mcLocale.getString("m.XPGainUnarmed")})); 
			if(mcPermissions.getInstance().unarmed(player))
				player.sendMessage(mcLocale.getString("m.LVL", new Object[] {PP.getSkillToString("unarmed"), PP.getSkillToString("unarmedXP"), PP.getXpToLevel("unarmed")}));
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.Effects")})); 
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsUnarmed1_0"), mcLocale.getString("m.EffectsUnarmed1_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsUnarmed2_0"), mcLocale.getString("m.EffectsUnarmed2_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsUnarmed3_0"), mcLocale.getString("m.EffectsUnarmed3_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsUnarmed4_0"), mcLocale.getString("m.EffectsUnarmed4_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsUnarmed5_0"), mcLocale.getString("m.EffectsUnarmed5_1")}));  
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.YourStats")})); 
			player.sendMessage(mcLocale.getString("m.UnarmedArrowDeflectChance", new Object[] {arrowpercentage})); 
			player.sendMessage(mcLocale.getString("m.UnarmedDisarmChance", new Object[] {percentage})); 
			if(PP.getSkill("unarmed") < 250){
				player.sendMessage(mcLocale.getString("m.AbilityLockTemplate", new Object[] {mcLocale.getString("m.AbilLockUnarmed1")})); 
			} else if(PP.getSkill("unarmed") >= 250 && PP.getSkill("unarmed") < 500){
				player.sendMessage(mcLocale.getString("m.AbilityBonusTemplate", new Object[] {mcLocale.getString("m.AbilBonusUnarmed1_0"), mcLocale.getString("m.AbilBonusUnarmed1_1")}));  
				player.sendMessage(mcLocale.getString("m.AbilityLockTemplate", new Object[] {mcLocale.getString("m.AbilLockUnarmed2")})); 
			} else {
				player.sendMessage(mcLocale.getString("m.AbilityBonusTemplate", new Object[] {mcLocale.getString("m.AbilBonusUnarmed2_0"), mcLocale.getString("m.AbilBonusUnarmed2_1")}));  
			}
			player.sendMessage(mcLocale.getString("m.UnarmedBerserkLength", new Object[] {ticks})); 
		}
		if(split[0].equalsIgnoreCase("herbalism") || split[0].toLowerCase().equalsIgnoreCase(mcLocale.getString("m.SkillHerbalism").toLowerCase())){ 
			int rank = 0;
			if(PP.getSkill("herbalism") >= 50)
				rank++;
			if (PP.getSkill("herbalism") >= 150)
				rank++;
			if (PP.getSkill("herbalism") >= 250)
				rank++;
			if (PP.getSkill("herbalism") >= 350)
				rank++;
			if (PP.getSkill("herbalism") >= 450)
				rank++;
			if (PP.getSkill("herbalism") >= 550)
				rank++;
			if (PP.getSkill("herbalism") >= 650)
				rank++;
			if (PP.getSkill("herbalism") >= 750)
				rank++;
			int bonus = 0;
			if(PP.getSkill("herbalism") >= 200)
				bonus++;
			if(PP.getSkill("herbalism") >= 400)
				bonus++;
			if(PP.getSkill("herbalism") >= 600)
				bonus++;

			int ticks = 2;
			int x = PP.getSkill("herbalism");
			while(x >= 50){
				x-=50;
				ticks++;
			}

			float skillvalue = (float)PP.getSkill("herbalism");
			String percentage = String.valueOf((skillvalue / 1000) * 100);
			String gpercentage = String.valueOf((skillvalue / 1500) * 100);
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.SkillHerbalism")})); 
			player.sendMessage(mcLocale.getString("m.XPGain", new Object[] {mcLocale.getString("m.XPGainHerbalism")})); 
			if(mcPermissions.getInstance().herbalism(player))
				player.sendMessage(mcLocale.getString("m.LVL", new Object[] {PP.getSkillToString("herbalism"), PP.getSkillToString("herbalismXP"), PP.getXpToLevel("herbalism")}));
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

		if(split[0].equalsIgnoreCase("excavation") || split[0].toLowerCase().equalsIgnoreCase(mcLocale.getString("m.SkillExcavation").toLowerCase())) 
		{
			int ticks = 2;
			int x = PP.getSkill("excavation");
			while(x >= 50){
				x-=50;
				ticks++;
			}
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.SkillExcavation")})); 
			player.sendMessage(mcLocale.getString("m.XPGain", new Object[] {mcLocale.getString("m.XPGainExcavation")})); 
			if(mcPermissions.getInstance().excavation(player))
				player.sendMessage(mcLocale.getString("m.LVL", new Object[] {PP.getSkillToString("excavation"), PP.getSkillToString("excavationXP"), PP.getXpToLevel("excavation")}));
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.Effects")})); 
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsExcavation1_0"), mcLocale.getString("m.EffectsExcavation1_1")}));  
			player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] {mcLocale.getString("m.EffectsExcavation2_0"), mcLocale.getString("m.EffectsExcavation2_1")}));  
			player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] {mcLocale.getString("m.YourStats")})); 
			player.sendMessage(mcLocale.getString("m.ExcavationGreenTerraLength", new Object[] {ticks})); 
		}

		if(split[0].equalsIgnoreCase("sorcery") || split[0].toLowerCase().equalsIgnoreCase(mcLocale.getString("m.SkillSorcery").toLowerCase())) 
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

		if(LoadProperties.mcmmoEnable && split[0].equalsIgnoreCase(LoadProperties.mcmmo)){ 
			player.sendMessage(ChatColor.RED+"-----[]"+ChatColor.GREEN+"mMO"+ChatColor.RED+"[]-----");   
			player.sendMessage(ChatColor.YELLOW+"mcMMO is an RPG server mod for minecraft."); 
			player.sendMessage(ChatColor.YELLOW+"There are many skills added by mcMMO to minecraft."); 
			player.sendMessage(ChatColor.YELLOW+"They can do anything from giving a chance"); 
			player.sendMessage(ChatColor.YELLOW+"for double drops to letting you break materials instantly."); 
			player.sendMessage(ChatColor.YELLOW+"For example, by harvesting logs from trees you will gain"); 
			player.sendMessage(ChatColor.YELLOW+"Woodcutting xp and once you have enough xp you will gain"); 
			player.sendMessage(ChatColor.YELLOW+"a skill level in Woodcutting. By raising this skill you will"); 
			player.sendMessage(ChatColor.YELLOW+"be able to receive benefits like "+ChatColor.RED+"double drops");  
			player.sendMessage(ChatColor.YELLOW+"and increase the effects of the "+ChatColor.RED+"\"Tree Felling\""+ChatColor.YELLOW+" ability.");   
			player.sendMessage(ChatColor.YELLOW+"mMO has abilities related to the skill, skills normally"); 
			player.sendMessage(ChatColor.YELLOW+"provide passive bonuses but they also have activated"); 
			player.sendMessage(ChatColor.YELLOW+"abilities too. Each ability is activated by holding"); 
			player.sendMessage(ChatColor.YELLOW+"the appropriate tool and "+ChatColor.RED+"right clicking.");  
			player.sendMessage(ChatColor.YELLOW+"For example, if you hold a Mining Pick and right click"); 
			player.sendMessage(ChatColor.YELLOW+"you will ready your Pickaxe, attack mining materials"); 
			player.sendMessage(ChatColor.YELLOW+"and then "+ChatColor.RED+"Super Breaker "+ChatColor.YELLOW+"will activate.");   
			player.sendMessage(ChatColor.GREEN+"Find out mcMMO commands with "+ChatColor.DARK_AQUA+LoadProperties.mcc);  
			player.sendMessage(ChatColor.GREEN+"You can donate via paypal to"+ChatColor.DARK_RED+" nossr50@gmail.com");  
		}
		if(LoadProperties.mccEnable && split[0].equalsIgnoreCase(LoadProperties.mcc)){ 
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
		if(LoadProperties.mcabilityEnable && mcPermissions.permissionsEnabled && split[0].equalsIgnoreCase(LoadProperties.mcability)){ 
			if(PP.getAbilityUse()){
				player.sendMessage(mcLocale.getString("mcPlayerListener.AbilitiesOff")); 
				PP.toggleAbilityUse();
			} else {
				player.sendMessage(mcLocale.getString("mcPlayerListener.AbilitiesOn")); 
				PP.toggleAbilityUse();
			}
		}

		/*
		 * FFS -> MySQL
		 */
		if(player.isOp() && split[0].equalsIgnoreCase("mmoupdate"))
		{
			if(!mcPermissions.getInstance().admin(player))
			{
				player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +mcLocale.getString("mcPlayerListener.NoPermission"));  
				return true;
			}
			player.sendMessage(ChatColor.GRAY+"Starting conversion..."); 
			Users.clearUsers();
			m.convertToMySQL(this);
			for(Player x : this.getServer().getOnlinePlayers())
			{
				Users.addUser(x);
			}
			player.sendMessage(ChatColor.GREEN+"Conversion finished!"); 
		}

		/*
		 * LEADER BOARD COMMAND
		 */
		if(LoadProperties.mctopEnable && split[0].equalsIgnoreCase(LoadProperties.mctop)){ 

			if(LoadProperties.useMySQL == false){
				/*
				 * POWER LEVEL INFO RETRIEVAL
				 */
				if(split.length == 1){
					int p = 1;
					String[] info = Leaderboard.retrieveInfo("powerlevel", p); 
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
				if(split.length >= 2 && Leaderboard.isInt(split[1])){
					int p = 1;
					//Grab page value if specified
					if(split.length >= 2){
						if(Leaderboard.isInt(split[1])){
							p = Integer.valueOf(split[1]);
						}
					}
					int pt = p;
					if(p > 1){
						pt -= 1;
						pt += (pt * 10);
						pt = 10;
					}
					String[] info = Leaderboard.retrieveInfo("powerlevel", p); 
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
				if(split.length >= 2 && Skills.isSkill(split[1])){
					int p = 1;
					//Grab page value if specified
					if(split.length >= 3){
						if(Leaderboard.isInt(split[2])){
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

					String[] info = Leaderboard.retrieveInfo(split[1].toLowerCase(), p);
					player.sendMessage(mcLocale.getString("mcPlayerListener.SkillLeaderboard", new Object[] {capitalized}));  
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

		if(LoadProperties.mcrefreshEnable && split[0].equalsIgnoreCase(LoadProperties.mcrefresh)){ 

			if(!mcPermissions.getInstance().mcrefresh(player)){
				player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +mcLocale.getString("mcPlayerListener.NoPermission"));  
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
		if(LoadProperties.mcgodEnable && split[0].equalsIgnoreCase(LoadProperties.mcgod))
		{ 
			if(mcPermissions.permissionsEnabled)
			{
				if(!mcPermissions.getInstance().mcgod(player))
				{
					player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +mcLocale.getString("mcPlayerListener.NoPermission"));  
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
			} else if (player.isOp())
			{
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
		if(LoadProperties.clearmyspawnEnable && LoadProperties.enableMySpawn && mcPermissions.getInstance().mySpawn(player) && split[0].equalsIgnoreCase(LoadProperties.clearmyspawn))
		{ 
			double x = this.getServer().getWorlds().get(0).getSpawnLocation().getX();
			double y = this.getServer().getWorlds().get(0).getSpawnLocation().getY();
			double z = this.getServer().getWorlds().get(0).getSpawnLocation().getZ();
			String worldname = this.getServer().getWorlds().get(0).getName();
			PP.setMySpawn(x, y, z, worldname);
			player.sendMessage(mcLocale.getString("mcPlayerListener.MyspawnCleared")); 
		}
		if(LoadProperties.mmoeditEnable && mcPermissions.permissionsEnabled && split[0].equalsIgnoreCase(""+LoadProperties.mmoedit))
		{ 

			if(!mcPermissions.getInstance().mmoedit(player))
			{
				player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +mcLocale.getString("mcPlayerListener.NoPermission"));  
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
					Users.getProfile(getPlayer(split[1])).modifyskill(newvalue, split[2]);
					player.sendMessage(ChatColor.RED+split[2]+" has been modified."); 
				}
			}
			else if(split.length == 3)
			{
				if(m.isInt(split[2]) && Skills.isSkill(split[1]))
				{
					int newvalue = Integer.valueOf(split[2]);
					PP.modifyskill(newvalue, split[1]);
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
		if(LoadProperties.addxpEnable && mcPermissions.permissionsEnabled && split[0].equalsIgnoreCase(LoadProperties.addxp)){ 

			if(!mcPermissions.getInstance().mmoedit(player)){
				player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +mcLocale.getString("mcPlayerListener.NoPermission"));  
				return true;
			}
			if(split.length < 3){
				player.sendMessage(ChatColor.RED+"Usage is /"+LoadProperties.addxp+" playername skillname xp");  
				return true;
			}
			if(split.length == 4){
				if(isPlayer(split[1]) && m.isInt(split[3]) && Skills.isSkill(split[2])){
					int newvalue = Integer.valueOf(split[3]);
					Users.getProfile(getPlayer(split[1])).addXP(split[2], newvalue);
					getPlayer(split[1]).sendMessage(ChatColor.GREEN+"Experience granted!"); 
					player.sendMessage(ChatColor.RED+split[2]+" has been modified."); 
					Skills.XpCheck(getPlayer(split[1]));
				}
			}
			else if(split.length == 3 && m.isInt(split[2]) && Skills.isSkill(split[1])){
				int newvalue = Integer.valueOf(split[2]);
				Users.getProfile(player).addXP(split[1], newvalue);
				player.sendMessage(ChatColor.RED+split[1]+" has been modified."); 
			} else {
				player.sendMessage(ChatColor.RED+"Usage is /"+LoadProperties.addxp+" playername skillname xp");  
			}
		}
		if(LoadProperties.ptpEnable && PP != null && PP.inParty() && split[0].equalsIgnoreCase(LoadProperties.ptp)){ 

			if(!mcPermissions.getInstance().partyTeleport(player)){
				player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +mcLocale.getString("mcPlayerListener.NoPermission"));  
				return true;
			}
			if(split.length < 2){
				player.sendMessage(ChatColor.RED+"Usage is /"+LoadProperties.ptp+" <playername>");  
				return true;
			}
			if(!isPlayer(split[1])){
				player.sendMessage("That is not a valid player"); 
			}
			if(isPlayer(split[1])){
				Player target = getPlayer(split[1]);
				PlayerProfile PPt = Users.getProfile(target);
				if(PP.getParty().equals(PPt.getParty())){
					player.teleport(target);
					player.sendMessage(ChatColor.GREEN+"You have teleported to "+target.getName()); 
					target.sendMessage(ChatColor.GREEN+player.getName() + " has teleported to you."); 
				}
			}
		}
		/*
		 * WHOIS COMMAND
		 */
		if(LoadProperties.whoisEnable && (player.isOp() || mcPermissions.getInstance().whois(player)) && split[0].equalsIgnoreCase(LoadProperties.whois)){ 
			if(split.length < 2){
				player.sendMessage(ChatColor.RED + "Proper usage is /"+LoadProperties.whois+" <playername>");  
				return true;
			}
			//if split[1] is a player
			if(isPlayer(split[1]))
			{
				Player target = getPlayer(split[1]);
				PlayerProfile PPt = Users.getProfile(target);
				double x,y,z;
				x = target.getLocation().getX();
				y = target.getLocation().getY();
				z = target.getLocation().getZ();
				player.sendMessage(ChatColor.GREEN + "~~WHOIS RESULTS~~"); 
				player.sendMessage(target.getName());
				if(PPt.inParty())
					player.sendMessage("Party: "+PPt.getParty()); 
				player.sendMessage("Health: "+target.getHealth()+ChatColor.GRAY+" (20 is full health)");  
				player.sendMessage("OP: " + target.isOp()); 
				player.sendMessage(ChatColor.GREEN+"mcMMO Stats for "+ChatColor.YELLOW+target.getName()); 

				player.sendMessage(ChatColor.GOLD+"-=GATHERING SKILLS=-");
				if(mcPermissions.getInstance().excavation(target))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.ExcavationSkill"), PPt.getSkillToString("excavation"), PPt.getSkillToString("excavationXP"), PPt.getXpToLevel("excavation")));
				if(mcPermissions.getInstance().herbalism(target))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.HerbalismSkill"), PPt.getSkillToString("herbalism"), PPt.getSkillToString("herbalismXP"), PPt.getXpToLevel("herbalism")));
				if(mcPermissions.getInstance().mining(target))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.MiningSkill"), PPt.getSkillToString("mining"), PPt.getSkillToString("miningXP"), PPt.getXpToLevel("mining")));
				if(mcPermissions.getInstance().woodCuttingAbility(target))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.WoodcuttingSkill"), PPt.getSkillToString("woodcutting"), PPt.getSkillToString("woodcuttingXP"), PPt.getXpToLevel("woodcutting")));

				player.sendMessage(ChatColor.GOLD+"-=COMBAT SKILLS=-");
				if(mcPermissions.getInstance().axes(target))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.AxesSkill"), PPt.getSkillToString("axes"), PPt.getSkillToString("axesXP"), PPt.getXpToLevel("axes")));
				if(mcPermissions.getInstance().archery(player))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.ArcherySkill"), PPt.getSkillToString("archery"), PPt.getSkillToString("archeryXP"), PPt.getXpToLevel("archery")));
				//if(mcPermissions.getInstance().sorcery(target))
				//player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.SorcerySkill"), PPt.getSkillToString("sorcery"), PPt.getSkillToString("sorceryXP"), PPt.getXpToLevel("excavation")));
				if(mcPermissions.getInstance().swords(target))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.SwordsSkill"), PPt.getSkillToString("swords"), PPt.getSkillToString("swordsXP"), PPt.getXpToLevel("swords")));
				if(mcPermissions.getInstance().taming(target))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.TamingSkill"), PPt.getSkillToString("taming"), PPt.getSkillToString("tamingXP"), PPt.getXpToLevel("taming")));
				if(mcPermissions.getInstance().unarmed(target))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.UnarmedSkill"), PPt.getSkillToString("unarmed"), PPt.getSkillToString("unarmedXP"), PPt.getXpToLevel("unarmed")));

				player.sendMessage(ChatColor.GOLD+"-=MISC SKILLS=-");
				if(mcPermissions.getInstance().acrobatics(target))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.AcrobaticsSkill"), PPt.getSkillToString("acrobatics"), PPt.getSkillToString("acrobaticsXP"), PPt.getXpToLevel("acrobatics")));
				if(mcPermissions.getInstance().repair(target))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.RepairSkill"), PPt.getSkillToString("repair"), PPt.getSkillToString("repairXP"), PPt.getXpToLevel("repair")));	

				player.sendMessage(mcLocale.getString("mcPlayerListener.PowerLevel") +ChatColor.GREEN+(m.getPowerLevel(target))); 

				player.sendMessage(ChatColor.GREEN+"~~COORDINATES~~"); 
				player.sendMessage("X: "+x); 
				player.sendMessage("Y: "+y); 
				player.sendMessage("Z: "+z); 
			}
		}
		/*
		 * STATS COMMAND
		 */
		if(LoadProperties.statsEnable && split[0].equalsIgnoreCase(LoadProperties.stats)){ 

			player.sendMessage(mcLocale.getString("mcPlayerListener.YourStats"));
			
			if(mcPermissions.getEnabled())
				player.sendMessage(mcLocale.getString("mcPlayerListener.NoSkillNote")); 

			ChatColor header = ChatColor.GOLD;

			if(Skills.hasGatheringSkills(player)){
				player.sendMessage(header+"-=GATHERING SKILLS=-");
				if(mcPermissions.getInstance().excavation(player))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.ExcavationSkill"), PP.getSkillToString("excavation"), PP.getSkillToString("excavationXP"), PP.getXpToLevel("excavation")));
				if(mcPermissions.getInstance().herbalism(player))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.HerbalismSkill"), PP.getSkillToString("herbalism"), PP.getSkillToString("herbalismXP"), PP.getXpToLevel("herbalism")));
				if(mcPermissions.getInstance().mining(player))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.MiningSkill"), PP.getSkillToString("mining"), PP.getSkillToString("miningXP"), PP.getXpToLevel("mining")));
				if(mcPermissions.getInstance().woodCuttingAbility(player))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.WoodcuttingSkill"), PP.getSkillToString("woodcutting"), PP.getSkillToString("woodcuttingXP"), PP.getXpToLevel("woodcutting")));
			}
			if(Skills.hasCombatSkills(player)){
				player.sendMessage(header+"-=COMBAT SKILLS=-");
				if(mcPermissions.getInstance().axes(player))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.AxesSkill"), PP.getSkillToString("axes"), PP.getSkillToString("axesXP"), PP.getXpToLevel("axes")));
				if(mcPermissions.getInstance().archery(player))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.ArcherySkill"), PP.getSkillToString("archery"), PP.getSkillToString("archeryXP"), PP.getXpToLevel("archery")));
				//if(mcPermissions.getInstance().sorcery(player))
				//player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.SorcerySkill"), PP.getSkillToString("sorcery"), PP.getSkillToString("sorceryXP"), PP.getXpToLevel("excavation")));
				if(mcPermissions.getInstance().swords(player))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.SwordsSkill"), PP.getSkillToString("swords"), PP.getSkillToString("swordsXP"), PP.getXpToLevel("swords")));
				if(mcPermissions.getInstance().taming(player))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.TamingSkill"), PP.getSkillToString("taming"), PP.getSkillToString("tamingXP"), PP.getXpToLevel("taming")));
				if(mcPermissions.getInstance().unarmed(player))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.UnarmedSkill"), PP.getSkillToString("unarmed"), PP.getSkillToString("unarmedXP"), PP.getXpToLevel("unarmed")));
			}

			if(Skills.hasMiscSkills(player)){
				player.sendMessage(header+"-=MISC SKILLS=-");
				if(mcPermissions.getInstance().acrobatics(player))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.AcrobaticsSkill"), PP.getSkillToString("acrobatics"), PP.getSkillToString("acrobaticsXP"), PP.getXpToLevel("acrobatics")));
				if(mcPermissions.getInstance().repair(player))
					player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.RepairSkill"), PP.getSkillToString("repair"), PP.getSkillToString("repairXP"), PP.getXpToLevel("repair")));	
			}
			player.sendMessage(mcLocale.getString("mcPlayerListener.PowerLevel")+ChatColor.GREEN+(m.getPowerLevel(player))); 
		}
		//Invite Command
		if(LoadProperties.inviteEnable && mcPermissions.getInstance().party(player) && split[0].equalsIgnoreCase(LoadProperties.invite)){ 

			if(!PP.inParty()){
				player.sendMessage(mcLocale.getString("mcPlayerListener.NotInParty")); 
				return true;
			}
			if(split.length < 2){
				player.sendMessage(ChatColor.RED+"Usage is /"+LoadProperties.invite+" <playername>");  
				return true;
			}
			if(PP.inParty() && split.length >= 2 && isPlayer(split[1])){
				Player target = getPlayer(split[1]);
				PlayerProfile PPt = Users.getProfile(target);
				PPt.modifyInvite(PP.getParty());
				player.sendMessage(mcLocale.getString("mcPlayerListener.InviteSuccess")); 
				//target.sendMessage(ChatColor.RED+"ALERT: "+ChatColor.GREEN+"You have received a party invite for "+PPt.getInvite()+" from "+player.getName());   
				target.sendMessage(mcLocale.getString("mcPlayerListener.ReceivedInvite1", new Object[] {PPt.getInvite(), player.getName()}));
				//target.sendMessage(ChatColor.YELLOW+"Type "+ChatColor.GREEN+LoadProperties.accept+ChatColor.YELLOW+" to accept the invite");   
				target.sendMessage(mcLocale.getString("mcPlayerListener.ReceivedInvite2", new Object[] {LoadProperties.accept}));
			}
		}
		//Accept invite
		if(LoadProperties.acceptEnable && mcPermissions.getInstance().party(player) && split[0].equalsIgnoreCase(LoadProperties.accept)){ 
			if(PP.hasPartyInvite()){
				if(PP.inParty()){
					Party.getInstance().informPartyMembersQuit(player, getPlayersOnline());
				}
				PP.acceptInvite();
				Party.getInstance().informPartyMembers(player, getPlayersOnline());
				player.sendMessage(mcLocale.getString("mcPlayerListener.InviteAccepted", new Object[]{PP.getParty()}));  
			} else {
				player.sendMessage(mcLocale.getString("mcPlayerListener.NoInvites")); 
			}
		}
		//Party command
		if(LoadProperties.partyEnable && split[0].equalsIgnoreCase(LoadProperties.party)){ 
			if(!mcPermissions.getInstance().party(player)){
				player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +mcLocale.getString("mcPlayerListener.NoPermission"));  
				return true;
			}
			if(split.length == 1 && !PP.inParty()){
				player.sendMessage("Proper usage is "+LoadProperties.party+" <name> or 'q' to quit");   
				return true;
			}
			if(split.length == 1 && PP.inParty()){
				String tempList = ""; 
				int x = 0;
				for(Player p : this.getServer().getOnlinePlayers()){
					if(PP.getParty().equals(Users.getProfile(p).getParty())){
						if(p != null && x+1 >= Party.getInstance().partyCount(player, getPlayersOnline())){
							tempList+= p.getName();
							x++;
						}
						if(p != null && x < Party.getInstance().partyCount(player, getPlayersOnline())){
							tempList+= p.getName() +", "; 
							x++;
						}
					}
				}
				player.sendMessage(mcLocale.getString("mcPlayerListener.YouAreInParty", new Object[] {PP.getParty()}));
				player.sendMessage(mcLocale.getString("mcPlayerListener.PartyMembers")+" ("+ChatColor.WHITE+tempList+ChatColor.GREEN+")");  
			}
			if(split.length > 1 && split[1].equals("q") && PP.inParty()){ 
				Party.getInstance().informPartyMembersQuit(player, getPlayersOnline());
				PP.removeParty();
				player.sendMessage(mcLocale.getString("mcPlayerListener.LeftParty")); 
				return true;
			}
			if(split.length >= 2){
				if(PP.inParty())
					Party.getInstance().informPartyMembersQuit(player, getPlayersOnline());
				PP.setParty(split[1]);
				player.sendMessage(mcLocale.getString("mcPlayerListener.JoinedParty", new Object[] {split[1]}));
				Party.getInstance().informPartyMembers(player, getPlayersOnline());
			}
		}
		if(LoadProperties.partyEnable && split[0].equalsIgnoreCase("p")){

			if(!mcPermissions.getInstance().party(player)){
				player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +mcLocale.getString("mcPlayerListener.NoPermission"));  
				return true;
			}
			
			// Not a toggle, a message

			if (args.length >= 1) {
				String pMessage = args[0];
				for (int i = 1; i <= args.length - 1; i++) {
					pMessage = pMessage + " " + args[i];
				}
				String pPrefix = ChatColor.GREEN + "(" + ChatColor.WHITE
				+ player.getName() + ChatColor.GREEN + ") ";

				log.log(Level.INFO,
						"[P](" + PP.getParty() + ")" + "<" + player.getName()
						+ "> " + pMessage);

				for (Player herp : getServer().getOnlinePlayers()) {
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

		if(split[0].equalsIgnoreCase("a") && (player.isOp() || mcPermissions.getInstance().adminChat(player))){
			if(!mcPermissions.getInstance().adminChat(player) && !player.isOp()){
				player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +mcLocale.getString("mcPlayerListener.NoPermission"));  
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
				log.log(Level.INFO, "[A]" + "<" + player.getName() + "> "
						+ aMessage);
				for (Player herp : getServer().getOnlinePlayers()) {
					if ((herp.isOp() || mcPermissions.getInstance().adminChat(
							herp)))
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
		if(LoadProperties.myspawnEnable && LoadProperties.enableMySpawn && split[0].equalsIgnoreCase(LoadProperties.myspawn)){ 
			if(!mcPermissions.getInstance().mySpawn(player)){
				player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +mcLocale.getString("mcPlayerListener.NoPermission"));  
				return true;
			}
			if(System.currentTimeMillis() < PP.getMySpawnATS() + 3600000){
				long x = ((PP.getMySpawnATS() + 3600000) - System.currentTimeMillis());
				int y = (int) (x/60000);
				int z = (int) ((x/1000) - (y*60));
				player.sendMessage(mcLocale.getString("mcPlayerListener.MyspawnTimeNotice", new Object[] {y, z}));    
				return true;
			}
			PP.setMySpawnATS(System.currentTimeMillis());
			if(PP.getMySpawn(player) != null){
				Location mySpawn = PP.getMySpawn(player);
				if(mySpawn != null && this.getServer().getWorld(PP.getMySpawnWorld(this)) != null)
					mySpawn.setWorld(this.getServer().getWorld(PP.getMySpawnWorld(this)));
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

	public Player[] getPlayersOnline() {
		return this.getServer().getOnlinePlayers();
	}

	public boolean isPlayer(String playerName){
		for(Player herp :  getPlayersOnline()){
			if(herp.getName().toLowerCase().equals(playerName.toLowerCase())){
				return true;
			}
		}
		return false;
	}

	public Player getPlayer(String playerName){
		for(Player herp : getPlayersOnline()){
			if(herp.getName().toLowerCase().equals(playerName.toLowerCase())){
				return herp;
			}
		}
		return null;
	}
}