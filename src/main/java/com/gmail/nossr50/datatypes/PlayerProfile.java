package com.gmail.nossr50.datatypes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.events.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;

public class PlayerProfile
{
    //HUD
    private HUDType hud;
    
    //MISC
	private String party, invite;
	
	//TOGGLES
	private boolean loaded = false, partyhud = true, spoutcraft = false, xpbarlocked = false, placedAnvil = false, partyChatMode = false, adminChatMode = false, godMode = false, greenTerraMode, partyChatOnly = false, greenTerraInformed = true, berserkInformed = true, skullSplitterInformed = true, gigaDrillBreakerInformed = true, 
	superBreakerInformed = true, blastMiningInformed = true, serratedStrikesInformed = true, treeFellerInformed = true, abilityuse = true, treeFellerMode, superBreakerMode, gigaDrillBreakerMode, 
	serratedStrikesMode, hoePreparationMode = false, shovelPreparationMode = false, swordsPreparationMode = false, fistsPreparationMode = false, pickaxePreparationMode = false, axePreparationMode = false, skullSplitterMode, berserkMode;
	
	//TIMESTAMPS
	//ATS = (Time of) Activation Time Stamp
	//DATS = (Time of) Deactivation Time Stamp
	private int xpGainATS = 0, recentlyHurt = 0, respawnATS, hoePreparationATS, shovelPreparationATS, swordsPreparationATS, fistsPreparationATS, axePreparationATS, pickaxePreparationATS;
	
	private SkillType lastgained = null, skillLock = null;
	
	//MySQL STUFF
	private int xpbarinc=0, lastlogin=0, userid = 0, bleedticks = 0;
	
	private String playerName;
	
	//Time to HashMap this shiz
	HashMap<SkillType, Integer> skills = new HashMap<SkillType, Integer>(); //Skills and XP
	HashMap<SkillType, Integer> skillsXp = new HashMap<SkillType, Integer>(); //Skills and XP
	HashMap<AbilityType, Integer> skillsDATS = new HashMap<AbilityType, Integer>(); //Skill ATS
	
    String location = "plugins/mcMMO/FlatFileStuff/mcmmo.users";
        
	public PlayerProfile(String name)
	{
		hud = LoadProperties.defaulthud;
		//Setup the HashMap for ability DATS
		for(AbilityType abilityType : AbilityType.values())
		{
		    skillsDATS.put(abilityType, 0);
		}
		
		//Setup the HashMap for the skills
		for(SkillType skillType : SkillType.values())
		{
			if(skillType != SkillType.ALL)
			{
				skills.put(skillType, 0);
				skillsXp.put(skillType, 0);
			}
		}
		
		playerName = name;
		if (LoadProperties.useMySQL) 
		{
			if(!loadMySQL()) {
				addMySQLPlayer();
				loadMySQL();//This is probably not needed anymore, could just delete
			}
		} else {
			if(!load()) { addPlayer();}
		}
		lastlogin = ((Long) (System.currentTimeMillis()/1000)).intValue();
	}
	
	public PlayerProfile(String name, boolean addNew)
    {
        hud = LoadProperties.defaulthud;
        //Setup the HashMap for ability DATS
        for(AbilityType abilityType : AbilityType.values())
        {
            skillsDATS.put(abilityType, 0);
        }
        
        //Setup the HashMap for the skills
        for(SkillType skillType : SkillType.values())
        {
            if(skillType != SkillType.ALL)
            {
                skills.put(skillType, 0);
                skillsXp.put(skillType, 0);
            }
        }
        
        playerName = name;
        if (LoadProperties.useMySQL) 
        {
            if(!loadMySQL() && addNew) {
                addMySQLPlayer();
                loadMySQL();//This is probably not needed anymore, could just delete
            }
        } else {
            if(!load() && addNew) { addPlayer(); loaded = true; }
        }
        lastlogin = ((Long) (System.currentTimeMillis()/1000)).intValue();
    }
	
	public int getLastLogin()
	{
		return lastlogin;
	}
	public int getMySQLuserId()
	{
		return userid;
	}
	
	public boolean loadMySQL() 
	{
		Integer id = 0;
		id = mcMMO.database.getInt("SELECT id FROM "+LoadProperties.MySQLtablePrefix+"users WHERE user = '" + playerName + "'");
		if(id == 0)
			return false;
		this.userid = id;
		if (id > 0) {
			HashMap<Integer, ArrayList<String>> huds = mcMMO.database.read("SELECT hudtype FROM "+LoadProperties.MySQLtablePrefix+"huds WHERE user_id = " + id);
			if(huds.get(1) == null)
			{
				mcMMO.database.write("INSERT INTO "+LoadProperties.MySQLtablePrefix+"huds (user_id) VALUES ("+id+")");
			} else {
				if(huds.get(1).get(0) != null)
				{
				for(HUDType x : HUDType.values())
				{
					if(x.toString().equals(huds.get(1).get(0)))
					{
						hud = x;
					}
				}
				} else {
					hud = LoadProperties.defaulthud;
				}
			}
			HashMap<Integer, ArrayList<String>> users = mcMMO.database.read("SELECT lastlogin, party FROM "+LoadProperties.MySQLtablePrefix+"users WHERE id = " + id);
				//lastlogin = Integer.parseInt(users.get(1).get(0));
				party = users.get(1).get(1);				
			HashMap<Integer, ArrayList<String>> cooldowns = mcMMO.database.read("SELECT mining, woodcutting, unarmed, herbalism, excavation, swords, axes, blast_mining FROM "+LoadProperties.MySQLtablePrefix+"cooldowns WHERE user_id = " + id);
			/*
			 * I'm still learning MySQL, this is a fix for adding a new table
			 * its not pretty but it works
			 */
			if(cooldowns.get(1) == null)
			{
				mcMMO.database.write("INSERT INTO "+LoadProperties.MySQLtablePrefix+"cooldowns (user_id) VALUES ("+id+")");
			}
			else
			{
				skillsDATS.put(AbilityType.SUPER_BREAKER, Integer.valueOf(cooldowns.get(1).get(0)));
				skillsDATS.put(AbilityType.TREE_FELLER, Integer.valueOf(cooldowns.get(1).get(1)));
				skillsDATS.put(AbilityType.BERSERK, Integer.valueOf(cooldowns.get(1).get(2)));
				skillsDATS.put(AbilityType.GREEN_TERRA, Integer.valueOf(cooldowns.get(1).get(3)));
				skillsDATS.put(AbilityType.GIGA_DRILL_BREAKER, Integer.valueOf(cooldowns.get(1).get(4)));
				skillsDATS.put(AbilityType.SERRATED_STRIKES, Integer.valueOf(cooldowns.get(1).get(5)));
				skillsDATS.put(AbilityType.SKULL_SPLIITER, Integer.valueOf(cooldowns.get(1).get(6)));
				skillsDATS.put(AbilityType.BLAST_MINING, Integer.valueOf(cooldowns.get(1).get(7)));
			}
			HashMap<Integer, ArrayList<String>> stats = mcMMO.database.read("SELECT taming, mining, repair, woodcutting, unarmed, herbalism, excavation, archery, swords, axes, acrobatics, fishing FROM "+LoadProperties.MySQLtablePrefix+"skills WHERE user_id = " + id);
				skills.put(SkillType.TAMING, Integer.valueOf(stats.get(1).get(0)));
				skills.put(SkillType.MINING, Integer.valueOf(stats.get(1).get(1)));
				skills.put(SkillType.REPAIR, Integer.valueOf(stats.get(1).get(2)));
				skills.put(SkillType.WOODCUTTING, Integer.valueOf(stats.get(1).get(3)));
				skills.put(SkillType.UNARMED, Integer.valueOf(stats.get(1).get(4)));
				skills.put(SkillType.HERBALISM, Integer.valueOf(stats.get(1).get(5)));
				skills.put(SkillType.EXCAVATION, Integer.valueOf(stats.get(1).get(6)));
				skills.put(SkillType.ARCHERY, Integer.valueOf(stats.get(1).get(7)));
				skills.put(SkillType.SWORDS, Integer.valueOf(stats.get(1).get(8)));
				skills.put(SkillType.AXES, Integer.valueOf(stats.get(1).get(9)));
				skills.put(SkillType.ACROBATICS, Integer.valueOf(stats.get(1).get(10)));
				skills.put(SkillType.FISHING, Integer.valueOf(stats.get(1).get(11)));
			HashMap<Integer, ArrayList<String>> experience = mcMMO.database.read("SELECT taming, mining, repair, woodcutting, unarmed, herbalism, excavation, archery, swords, axes, acrobatics, fishing FROM "+LoadProperties.MySQLtablePrefix+"experience WHERE user_id = " + id);
				skillsXp.put(SkillType.TAMING, Integer.valueOf(experience.get(1).get(0)));
				skillsXp.put(SkillType.MINING, Integer.valueOf(experience.get(1).get(1)));
				skillsXp.put(SkillType.REPAIR, Integer.valueOf(experience.get(1).get(2)));
				skillsXp.put(SkillType.WOODCUTTING, Integer.valueOf(experience.get(1).get(3)));
				skillsXp.put(SkillType.UNARMED, Integer.valueOf(experience.get(1).get(4)));
				skillsXp.put(SkillType.HERBALISM, Integer.valueOf(experience.get(1).get(5)));
				skillsXp.put(SkillType.EXCAVATION, Integer.valueOf(experience.get(1).get(6)));
				skillsXp.put(SkillType.ARCHERY, Integer.valueOf(experience.get(1).get(7)));
				skillsXp.put(SkillType.SWORDS, Integer.valueOf(experience.get(1).get(8)));
				skillsXp.put(SkillType.AXES, Integer.valueOf(experience.get(1).get(9)));
				skillsXp.put(SkillType.ACROBATICS, Integer.valueOf(experience.get(1).get(10)));
				skillsXp.put(SkillType.FISHING, Integer.valueOf(experience.get(1).get(11)));
			loaded = true;
			return true;
		}
		else {
			return false;
		}		
	}
	public void addMySQLPlayer() {
		Integer id = 0;
		mcMMO.database.write("INSERT INTO "+LoadProperties.MySQLtablePrefix+"users (user, lastlogin) VALUES ('" + playerName + "'," + System.currentTimeMillis() / 1000 +")");
		id = mcMMO.database.getInt("SELECT id FROM "+LoadProperties.MySQLtablePrefix+"users WHERE user = '" + playerName + "'");
		mcMMO.database.write("INSERT INTO "+LoadProperties.MySQLtablePrefix+"cooldowns (user_id) VALUES ("+id+")");
		mcMMO.database.write("INSERT INTO "+LoadProperties.MySQLtablePrefix+"skills (user_id) VALUES ("+id+")");
		mcMMO.database.write("INSERT INTO "+LoadProperties.MySQLtablePrefix+"experience (user_id) VALUES ("+id+")");
		this.userid = id;
	}
	
	public boolean load()
	{
        try {
        	//Open the user file
        	FileReader file = new FileReader(location);
        	BufferedReader in = new BufferedReader(file);
        	String line = "";
        	while((line = in.readLine()) != null)
        	{
        		//Find if the line contains the player we want.
        		String[] character = line.split(":");

        		if(!character[0].equals(playerName)){continue;}
        		
    			//Get Mining
    			if(character.length > 1 && m.isInt(character[1]))
    				skills.put(SkillType.MINING, Integer.valueOf(character[1]));
    			//Party
    			if(character.length > 3)
    				party = character[3];
    			//Mining XP
    			if(character.length > 4 && m.isInt(character[4]))
    				skillsXp.put(SkillType.MINING, Integer.valueOf(character[4]));
    			if(character.length > 5 && m.isInt(character[5]))
    				skills.put(SkillType.WOODCUTTING, Integer.valueOf(character[5]));
    			if(character.length > 6 && m.isInt(character[6]))
    				skillsXp.put(SkillType.WOODCUTTING, Integer.valueOf(character[6]));
    			if(character.length > 7 && m.isInt(character[7]))
    				skills.put(SkillType.REPAIR, Integer.valueOf(character[7]));
    			if(character.length > 8 && m.isInt(character[8]))
    				skills.put(SkillType.UNARMED,  Integer.valueOf(character[8]));
    			if(character.length > 9 && m.isInt(character[9]))
    				skills.put(SkillType.HERBALISM, Integer.valueOf(character[9]));
    			if(character.length > 10 && m.isInt(character[10]))
    				skills.put(SkillType.EXCAVATION, Integer.valueOf(character[10]));
    			if(character.length > 11 && m.isInt(character[11]))
    				skills.put(SkillType.ARCHERY, Integer.valueOf(character[11]));
    			if(character.length > 12 && m.isInt(character[12]))
    				skills.put(SkillType.SWORDS, Integer.valueOf(character[12]));
    			if(character.length > 13 && m.isInt(character[13]))
    				skills.put(SkillType.AXES, Integer.valueOf(character[13]));
    			if(character.length > 14 && m.isInt(character[14]))
    				skills.put(SkillType.ACROBATICS, Integer.valueOf(character[14]));
    			if(character.length > 15 && m.isInt(character[15]))
    				skillsXp.put(SkillType.REPAIR, Integer.valueOf(character[15]));
    			if(character.length > 16 && m.isInt(character[16]))
    				skillsXp.put(SkillType.UNARMED, Integer.valueOf(character[16]));
    			if(character.length > 17 && m.isInt(character[17]))
    				skillsXp.put(SkillType.HERBALISM, Integer.valueOf(character[17]));
    			if(character.length > 18 && m.isInt(character[18]))
    				skillsXp.put(SkillType.EXCAVATION, Integer.valueOf(character[18]));
    			if(character.length > 19 && m.isInt(character[19]))
    				skillsXp.put(SkillType.ARCHERY, Integer.valueOf(character[19]));
    			if(character.length > 20 && m.isInt(character[20]))
    				skillsXp.put(SkillType.SWORDS, Integer.valueOf(character[20]));
    			if(character.length > 21 && m.isInt(character[21]))
    				skillsXp.put(SkillType.AXES, Integer.valueOf(character[21]));
    			if(character.length > 22 && m.isInt(character[22]))
    				skillsXp.put(SkillType.ACROBATICS, Integer.valueOf(character[22]));
    			if(character.length > 24 && m.isInt(character[24]))
    				skills.put(SkillType.TAMING, Integer.valueOf(character[24]));
    			if(character.length > 25 && m.isInt(character[25]))
    				skillsXp.put(SkillType.TAMING, Integer.valueOf(character[25]));
    			if(character.length > 26)
    				skillsDATS.put(AbilityType.BERSERK, Integer.valueOf(character[26]));
    			if(character.length > 27)
    			    skillsDATS.put(AbilityType.GIGA_DRILL_BREAKER, Integer.valueOf(character[27]));
    			if(character.length > 28)
    			    skillsDATS.put(AbilityType.TREE_FELLER, Integer.valueOf(character[28]));
    			if(character.length > 29)
    			    skillsDATS.put(AbilityType.GREEN_TERRA, Integer.valueOf(character[29]));
    			if(character.length > 30)
    			    skillsDATS.put(AbilityType.SERRATED_STRIKES, Integer.valueOf(character[30]));
    			if(character.length > 31)
    			    skillsDATS.put(AbilityType.SKULL_SPLIITER, Integer.valueOf(character[31]));
    			if(character.length > 32)
    			    skillsDATS.put(AbilityType.SUPER_BREAKER, Integer.valueOf(character[32]));
    			if(character.length > 33)
    			{
    				for(HUDType x : HUDType.values())
    				{
    					if(x.toString().equalsIgnoreCase(character[33]))
    					{
    						hud = x;
    					}
    				}
    			}
    			if(character.length > 34)
    				skills.put(SkillType.FISHING, Integer.valueOf(character[34]));
    			if(character.length > 35)
    				skillsXp.put(SkillType.FISHING, Integer.valueOf(character[35]));
    			if(character.length > 36)
    			    skillsDATS.put(AbilityType.BLAST_MINING, Integer.valueOf(character[36]));
            	in.close();
            	loaded = true;
    			return true;
        	}
        	in.close();
        } catch (Exception e) {
            Bukkit.getLogger().severe("Exception while reading " + location + " (Are you sure you formatted it correctly?)" + e.toString());
        }
        return false;
	}
	
    public void save()
    {
    	Long timestamp = System.currentTimeMillis()/1000; //Convert to seconds
    	// if we are using mysql save to database
    	if (LoadProperties.useMySQL) 
    	{
    		mcMMO.database.write("UPDATE "+LoadProperties.MySQLtablePrefix+"huds SET "
    				+" hudtype = '"+hud.toString()+"' WHERE user_id = "+this.userid);
    		mcMMO.database.write("UPDATE "+LoadProperties.MySQLtablePrefix+"users SET lastlogin = " + timestamp.intValue() + " WHERE id = " + this.userid);
    		mcMMO.database.write("UPDATE "+LoadProperties.MySQLtablePrefix+"users SET party = '"+this.party+"' WHERE id = " +this.userid);
    		mcMMO.database.write("UPDATE "+LoadProperties.MySQLtablePrefix+"cooldowns SET "
    				+" mining = " + skillsDATS.get(AbilityType.SUPER_BREAKER)
    				+", woodcutting = " + skillsDATS.get(AbilityType.TREE_FELLER)
    				+", unarmed = " + skillsDATS.get(AbilityType.BERSERK)
    				+", herbalism = " + skillsDATS.get(AbilityType.GREEN_TERRA)
    				+", excavation = " + skillsDATS.get(AbilityType.GIGA_DRILL_BREAKER)
    				+", swords = " + skillsDATS.get(AbilityType.SERRATED_STRIKES)
    				+", axes = " + skillsDATS.get(AbilityType.SKULL_SPLIITER)
    				+", blast_mining = " + skillsDATS.get(AbilityType.BLAST_MINING)
    				+" WHERE user_id = "+this.userid);
    		mcMMO.database.write("UPDATE "+LoadProperties.MySQLtablePrefix+"skills SET "
    				+"  taming = "+skills.get(SkillType.TAMING)
    				+", mining = "+skills.get(SkillType.MINING)
    				+", repair = "+skills.get(SkillType.REPAIR)
    				+", woodcutting = "+skills.get(SkillType.WOODCUTTING)
    				+", unarmed = "+skills.get(SkillType.UNARMED)
    				+", herbalism = "+skills.get(SkillType.HERBALISM)
    				+", excavation = "+skills.get(SkillType.EXCAVATION)
    				+", archery = " +skills.get(SkillType.ARCHERY)
    				+", swords = " +skills.get(SkillType.SWORDS)
    				+", axes = "+skills.get(SkillType.AXES)
    				+", acrobatics = "+skills.get(SkillType.ACROBATICS)
    				+", fishing = "+skills.get(SkillType.FISHING)
    				+" WHERE user_id = "+this.userid);
    		mcMMO.database.write("UPDATE "+LoadProperties.MySQLtablePrefix+"experience SET "
    				+"  taming = "+skillsXp.get(SkillType.TAMING)
    				+", mining = "+skillsXp.get(SkillType.MINING)
    				+", repair = "+skillsXp.get(SkillType.REPAIR)
    				+", woodcutting = "+skillsXp.get(SkillType.WOODCUTTING)
    				+", unarmed = "+skillsXp.get(SkillType.UNARMED)
    				+", herbalism = "+skillsXp.get(SkillType.HERBALISM)
    				+", excavation = "+skillsXp.get(SkillType.EXCAVATION)
    				+", archery = " +skillsXp.get(SkillType.ARCHERY)
    				+", swords = " +skillsXp.get(SkillType.SWORDS)
    				+", axes = "+skillsXp.get(SkillType.AXES)
    				+", acrobatics = "+skillsXp.get(SkillType.ACROBATICS)
    				+", fishing = "+skillsXp.get(SkillType.FISHING)
    				+" WHERE user_id = "+this.userid);
    	} else 
    	{
    		// otherwise save to flatfile
	        try {
	        	//Open the file
	        	FileReader file = new FileReader(location);
	            BufferedReader in = new BufferedReader(file);
	            StringBuilder writer = new StringBuilder();
	        	String line = "";
	        	
	        	//While not at the end of the file
	        	while((line = in.readLine()) != null)
	        	{
	        		//Read the line in and copy it to the output it's not the player
	        		//we want to edit
	        		if(!line.split(":")[0].equalsIgnoreCase(playerName))
	        		{
	                    writer.append(line).append("\r\n");
	                    
	                //Otherwise write the new player information
	        		} else {
	        			writer.append(playerName + ":");
	        			writer.append(skills.get(SkillType.MINING) + ":");
	        			writer.append("" + ":");
	        			writer.append(party+":");
	        			writer.append(skillsXp.get(SkillType.MINING) + ":");
	        			writer.append(skills.get(SkillType.WOODCUTTING) + ":");
	        			writer.append(skillsXp.get(SkillType.WOODCUTTING) + ":");
	        			writer.append(skills.get(SkillType.REPAIR) + ":");
	        			writer.append(skills.get(SkillType.UNARMED) + ":");
	        			writer.append(skills.get(SkillType.HERBALISM) + ":");
	        			writer.append(skills.get(SkillType.EXCAVATION) + ":");
	        			writer.append(skills.get(SkillType.ARCHERY) + ":");
	        			writer.append(skills.get(SkillType.SWORDS) + ":");
	        			writer.append(skills.get(SkillType.AXES) + ":");
	        			writer.append(skills.get(SkillType.ACROBATICS) + ":");
	        			writer.append(skillsXp.get(SkillType.REPAIR) + ":");
	        			writer.append(skillsXp.get(SkillType.UNARMED) + ":");
	        			writer.append(skillsXp.get(SkillType.HERBALISM) + ":");
	        			writer.append(skillsXp.get(SkillType.EXCAVATION) + ":");
	        			writer.append(skillsXp.get(SkillType.ARCHERY) + ":");
	        			writer.append(skillsXp.get(SkillType.SWORDS) + ":");
	        			writer.append(skillsXp.get(SkillType.AXES) + ":");
	        			writer.append(skillsXp.get(SkillType.ACROBATICS) + ":");
	        			writer.append(""+":");
	        			writer.append(skills.get(SkillType.TAMING) + ":");
	        			writer.append(skillsXp.get(SkillType.TAMING) + ":");
	        			//Need to store the DATS of abilities nao
	        			//Berserk, Gigadrillbreaker, Tree Feller, Green Terra, Serrated Strikes, Skull Splitter, Super Breaker
	        			writer.append(String.valueOf(skillsDATS.get(AbilityType.BERSERK))+":");
	        			writer.append(String.valueOf(skillsDATS.get(AbilityType.GIGA_DRILL_BREAKER))+":");
	        			writer.append(String.valueOf(skillsDATS.get(AbilityType.TREE_FELLER))+":");
	        			writer.append(String.valueOf(skillsDATS.get(AbilityType.GREEN_TERRA))+":");
	        			writer.append(String.valueOf(skillsDATS.get(AbilityType.SERRATED_STRIKES))+":");
	        			writer.append(String.valueOf(skillsDATS.get(AbilityType.SKULL_SPLIITER))+":");
	        			writer.append(String.valueOf(skillsDATS.get(AbilityType.SUPER_BREAKER))+":");
	        			writer.append(hud.toString()+":");
	        			writer.append(skills.get(SkillType.FISHING) + ":");
	        			writer.append(skillsXp.get(SkillType.FISHING) + ":");
	        			writer.append(String.valueOf(skillsDATS.get(AbilityType.BLAST_MINING)) + ":");
	        			writer.append("\r\n");                   			
	        		}
	        	}
	        	in.close();
	        	//Write the new file
	            FileWriter out = new FileWriter(location);
	            out.write(writer.toString());
	            out.close();
	        } catch (Exception e) {
	            Bukkit.getLogger().severe("Exception while writing to " + location + " (Are you sure you formatted it correctly?)" + e.toString());
	        }
    	}
	}

    public void resetAllData()
    {
        //This will reset everything to default values and then save the information to FlatFile/MySQL
        for(SkillType skillType : SkillType.values())
        {
            if(skillType == SkillType.ALL)
                continue;
            skills.put(skillType, 0);
            skillsXp.put(skillType, 0);
        }
        
        for(AbilityType abilityType : AbilityType.values())
        {
            skillsDATS.put(abilityType, 0);
        }
        
        //Misc stuff
        party = "";
        
        save();
    }
    
    public void addPlayer()
    {
        try {
        	//Open the file to write the player
        	FileWriter file = new FileWriter(location, true);
            BufferedWriter out = new BufferedWriter(file);
            
            //Add the player to the end
            out.append(playerName + ":");
            out.append(0 + ":"); //mining
            out.append(""+":");
            out.append(party+":");
            out.append(0+":"); //XP
            out.append(0+":"); //woodcutting
            out.append(0+":"); //woodCuttingXP
            out.append(0+":"); //repair
            out.append(0+":"); //unarmed
            out.append(0+":"); //herbalism
            out.append(0+":"); //excavation
            out.append(0+":"); //archery
            out.append(0+":"); //swords
            out.append(0+":"); //axes
            out.append(0+":"); //acrobatics
            out.append(0+":"); //repairXP
            out.append(0+":"); //unarmedXP
            out.append(0+":"); //herbalismXP
            out.append(0+":"); //excavationXP
            out.append(0+":"); //archeryXP
            out.append(0+":"); //swordsXP
            out.append(0+":"); //axesXP
            out.append(0+":"); //acrobaticsXP
            out.append(""+":");
            out.append(0+":"); //taming
            out.append(0+":"); //tamingXP
            out.append(0+":"); //DATS
            out.append(0+":"); //DATS
            out.append(0+":"); //DATS
            out.append(0+":"); //DATS
            out.append(0+":"); //DATS
            out.append(0+":"); //DATS
            out.append(0+":"); //DATS
            out.append(LoadProperties.defaulthud.toString()+":");//HUD
            out.append(0+":"); //Fishing
            out.append(0+":"); //FishingXP
            out.append(0+":"); //Blast Mining

            //Add more in the same format as the line above
            
			out.newLine();
			out.close();
        } catch (Exception e) {
            Bukkit.getLogger().severe("Exception while writing to " + location + " (Are you sure you formatted it correctly?)" + e.toString());
        }
    }
    public void togglePartyHUD()
    {
    	partyhud = !partyhud;
    }
    public boolean isLoaded()
    {
        return loaded;
    }
    public boolean getPartyHUD()
    {
    	return partyhud;
    }
    public void toggleSpoutEnabled()
    {
    	spoutcraft = !spoutcraft;
    }
    public HUDType getHUDType()
    {
    	return hud;
    }
    public void setHUDType(HUDType type)
    {
    	hud = type;
    	save();
    }
    public boolean getXpBarLocked()
    {
    	return xpbarlocked;
    }
    public void toggleXpBarLocked()
    {
    	xpbarlocked = !xpbarlocked;
    }
    public int getXpBarInc()
    {
    	return xpbarinc;
    }
    public void setXpBarInc(int newvalue)
    {
    	xpbarinc = newvalue;
    }
    public void setSkillLock(SkillType newvalue)
    {
    	skillLock = newvalue;
    }
    public SkillType getSkillLock()
    {
    	return skillLock;
    }
    public void setLastGained(SkillType newvalue)
    {
    	lastgained = newvalue;
    }
    public SkillType getLastGained()
    {
    	return lastgained;
    }
    
    public boolean getAdminChatMode() {return adminChatMode;}
    public boolean getPartyChatMode() {return partyChatMode;}
    
    public boolean getGodMode() {return godMode;}
    
    public void togglePlacedAnvil()
    {
    	placedAnvil = !placedAnvil;
    }
    public Boolean getPlacedAnvil()
    {
    	return placedAnvil;
    }
    public void toggleAdminChat()
    {
    	adminChatMode = !adminChatMode;
    }
	
    public void toggleGodMode()
    {
    	godMode = !godMode;
    }
    
    public void togglePartyChat()
    {
    	partyChatMode = !partyChatMode;
    }

	public boolean isPlayer(String player)
	{
		return player.equals(Bukkit.getPlayer(playerName));
	}
	public boolean getPartyChatOnlyToggle(){return partyChatOnly;}
	public void togglePartyChatOnly(){partyChatOnly = !partyChatOnly;}
	public boolean getAbilityUse(){
		return abilityuse;
	}
	public void toggleAbilityUse()
	{
		abilityuse = !abilityuse;
	}
	public void decreaseBleedTicks()
	{
		bleedticks--;
	}
	public Integer getBleedTicks(){
		return bleedticks;
	}
	public void setBleedTicks(Integer newvalue){
		bleedticks = newvalue;
		
		//Cap maximum ticks at 10
        if(bleedticks > 10)
            bleedticks = 10;
	}
	public void addBleedTicks(Integer newvalue){
		bleedticks+=newvalue;
		
		//Cap maximum ticks at 10
		if(bleedticks > 10)
		    bleedticks = 10;
	}
	/*
	 * EXPLOIT PREVENTION
	 */
	public long getRespawnATS() {return respawnATS;}
	public void setRespawnATS(long newvalue) {respawnATS = (int) (newvalue/1000);}
	
	/*
	 * HOE PREPARATION
	 */
	public boolean getHoePreparationMode(){
		return hoePreparationMode;
	}
	public void setHoePreparationMode(boolean bool){
		hoePreparationMode = bool;
	}
	public long getHoePreparationATS(){
		return hoePreparationATS;
	}
	public void setHoePreparationATS(long newvalue){
		hoePreparationATS = (int) (newvalue/1000);
	}
	
	/*
	 * SWORDS PREPARATION
	 */
	public boolean getSwordsPreparationMode(){
		return swordsPreparationMode;
	}
	public void setSwordsPreparationMode(boolean bool){
		swordsPreparationMode = bool;
	}
	public long getSwordsPreparationATS(){
		return swordsPreparationATS;
	}
	public void setSwordsPreparationATS(long newvalue){
		swordsPreparationATS = (int) (newvalue/1000);
	}
	/*
	 * SHOVEL PREPARATION
	 */
	public boolean getShovelPreparationMode(){
		return shovelPreparationMode;
	}
	public void setShovelPreparationMode(boolean bool){
		shovelPreparationMode = bool;
	}
	public long getShovelPreparationATS(){
		return shovelPreparationATS;
	}
	public void setShovelPreparationATS(long newvalue){
		shovelPreparationATS = (int) (newvalue/1000);
	}
	/*
	 * FISTS PREPARATION
	 */
	public boolean getFistsPreparationMode(){
		return fistsPreparationMode;
	}
	public void setFistsPreparationMode(boolean bool){
		fistsPreparationMode = bool;
	}
	public long getFistsPreparationATS(){
		return fistsPreparationATS;
	}
	public void setFistsPreparationATS(long newvalue){
		fistsPreparationATS = (int) (newvalue/1000);
	}
	/*
	 * AXE PREPARATION
	 */
	public boolean getAxePreparationMode(){
		return axePreparationMode;
	}
	public void setAxePreparationMode(boolean bool){
		axePreparationMode = bool;
	}
	public long getAxePreparationATS(){
		return axePreparationATS;
	}
	public void setAxePreparationATS(long newvalue){
		axePreparationATS = (int) (newvalue/1000);
	}
	/*
	 * PICKAXE PREPARATION
	 */
	public boolean getPickaxePreparationMode(){
		return pickaxePreparationMode;
	}
	public void setPickaxePreparationMode(boolean bool){
		pickaxePreparationMode = bool;
	}
	public long getPickaxePreparationATS(){
	    return pickaxePreparationATS;
	}
	public void setPickaxePreparationATS(long newvalue){
	   pickaxePreparationATS = (int) (newvalue/1000);
	}
	/*
	 * GREEN TERRA MODE
	 */
	public boolean getGreenTerraInformed() {return greenTerraInformed;}
	public void setGreenTerraInformed(boolean bool){
		greenTerraInformed = bool;
	}
	public boolean getGreenTerraMode(){
		return greenTerraMode;
	}
	public void setGreenTerraMode(boolean bool){
		greenTerraMode = bool;
	}
	
	/*
	 * BERSERK MODE
	 */
	public boolean getBerserkInformed() {return berserkInformed;}
	public void setBerserkInformed(boolean bool){
		berserkInformed = bool;
	}
	public boolean getBerserkMode(){
		return berserkMode;
	}
	public void setBerserkMode(boolean bool){
		berserkMode = bool;
	}
	/*
	 * SKULL SPLITTER
	 */
	public boolean getSkullSplitterInformed() {return skullSplitterInformed;}
	public void setSkullSplitterInformed(boolean bool){
		skullSplitterInformed = bool;
	}
	public boolean getSkullSplitterMode(){
		return skullSplitterMode;
	}
	public void setSkullSplitterMode(boolean bool){
		skullSplitterMode = bool;
	}
	/*
	 * SERRATED STRIKES
	 */
	public boolean getSerratedStrikesInformed() {return serratedStrikesInformed;}
	public void setSerratedStrikesInformed(boolean bool){
		serratedStrikesInformed = bool;
	}
	public boolean getSerratedStrikesMode(){
		return serratedStrikesMode;
	}
	public void setSerratedStrikesMode(boolean bool){
		serratedStrikesMode = bool;
	}
	/*
	 * GIGA DRILL BREAKER
	 */
	public boolean getGigaDrillBreakerInformed() {return gigaDrillBreakerInformed;}
	public void setGigaDrillBreakerInformed(boolean bool){
		gigaDrillBreakerInformed = bool;
	}
	public boolean getGigaDrillBreakerMode(){
		return gigaDrillBreakerMode;
	}
	public void setGigaDrillBreakerMode(boolean bool){
		gigaDrillBreakerMode = bool;
	}
	/*
	 * TREE FELLER STUFF
	 */
	public boolean getTreeFellerInformed() {return treeFellerInformed;}
	public void setTreeFellerInformed(boolean bool){
		treeFellerInformed = bool;
	}
	public boolean getTreeFellerMode(){
		return treeFellerMode;
	}
	public void setTreeFellerMode(boolean bool){
		treeFellerMode = bool;
	}
	/*
	 * MINING
	 */
	public boolean getSuperBreakerInformed() {return superBreakerInformed;}
	public void setSuperBreakerInformed(boolean bool){
		superBreakerInformed = bool;
	}
	public boolean getBlastMiningInformed() {return blastMiningInformed;}
	public void setBlastMiningInformed(boolean bool){
	    blastMiningInformed = bool;
	}
	public boolean getSuperBreakerMode(){
		return superBreakerMode;
	}
	public void setSuperBreakerMode(boolean bool){
		superBreakerMode = bool;
	}
	public long getRecentlyHurt(){
		return recentlyHurt;
	}
	public void setRecentlyHurt(long newvalue){
		recentlyHurt = (int) (newvalue/1000);
	}
	public void skillUp(SkillType skillType, int newvalue)
	{
		skills.put(skillType, skills.get(skillType)+newvalue);
	}
	public Integer getSkillLevel(SkillType skillType)
	{
		return skills.get(skillType);
	}
	public Integer getSkillXpLevel(SkillType skillType)
	{
		return skillsXp.get(skillType);
	}
	public void resetSkillXp(SkillType skillType)
	{
		skills.put(skillType, 0);
	}
	public long getSkillDATS(AbilityType abilityType)
    {
	    //Is this actually unused, or should it actually be returning the convertedBack variable?
	    //It *is* unused, I don't think I put this here so I'm going to comment it out - nossr50
		//long convertedBack = skillsDATS.get(abilityType) * 1000;
        return skillsDATS.get(abilityType);
    }
    public void setSkillDATS(AbilityType abilityType, long value)
    {
        int wearsOff = (int) (value * .001D);
        skillsDATS.put(abilityType, wearsOff);
    }
    public void resetCooldowns()
    {
        for(AbilityType x : skillsDATS.keySet())
        {
            skillsDATS.put(x, 0);
        }
    }
    
    /**
     * Adds XP to the player, doesn't calculate for XP Rate
     * @param skillType The skill to add XP to
     * @param newvalue The amount of XP to add
     */
    public void addXPOverrideNoBonus(SkillType skillType, int newvalue)
    {
        if(skillType == SkillType.ALL)
        {
            for(SkillType x : SkillType.values())
            {
                if(x == SkillType.ALL)
                    continue;
                Bukkit.getPluginManager().callEvent(new McMMOPlayerXpGainEvent(Bukkit.getPlayer(playerName), x, newvalue));
                skillsXp.put(x, skillsXp.get(x)+newvalue);
            }
        } else {
            int xp = newvalue;

            Bukkit.getPluginManager().callEvent(new McMMOPlayerXpGainEvent(Bukkit.getPlayer(playerName), skillType, xp));
            skillsXp.put(skillType, skillsXp.get(skillType)+xp);
            lastgained = skillType;
        }
    }
    
	/**
	 * Adds XP to the player, this ignores skill modifiers
	 * @param skillType The skill to add XP to
	 * @param newvalue The amount of XP to add
	 */
	public void addXPOverride(SkillType skillType, int newvalue)
	{
		if(skillType == SkillType.ALL)
		{
			for(SkillType x : SkillType.values())
			{
				if(x == SkillType.ALL)
					continue;
				Bukkit.getPluginManager().callEvent(new McMMOPlayerXpGainEvent(Bukkit.getPlayer(playerName), x, newvalue));
				skillsXp.put(x, skillsXp.get(x)+newvalue);
			}
		} else {
			int xp = newvalue;
			
			xp=xp*LoadProperties.xpGainMultiplier;
			Bukkit.getPluginManager().callEvent(new McMMOPlayerXpGainEvent(Bukkit.getPlayer(playerName), skillType, xp));
			skillsXp.put(skillType, skillsXp.get(skillType)+xp);
			lastgained = skillType;
		}
	}
	
	/**
	 * Adds XP to the player, this is affected by skill modifiers and xp rate
	 * @param skillType The skill to add XP to
	 * @param newvalue The amount of XP to add
	 */
	public void addXP(SkillType skillType, int newvalue, Player thisplayer)
	{
		if(System.currentTimeMillis() < ((xpGainATS*1000)+250) || thisplayer.getGameMode() == GameMode.CREATIVE)
			return;
		
		//Setup a timestamp of when xp was given
		xpGainATS = (int) (System.currentTimeMillis()/1000);
		
		double bonusModifier = 0;
		//String leaderName = "";
		
		if(inParty())
		{
			for(Player x : Party.getInstance().getPartyMembers(thisplayer))
			{
				if(x.isOnline() && !x.getName().equals(thisplayer.getName()) && Party.getInstance().isPartyLeader(x.getName(), this.getParty()))
				{
					//leaderName = x.getName();
					if(m.isNear(thisplayer.getLocation(), x.getLocation(), 25))
					{
						PlayerProfile PartyLeader = Users.getProfile(x);
						if(PartyLeader.getSkillLevel(skillType) >= this.getSkillLevel(skillType))
						{
							int leaderLevel = PartyLeader.getSkillLevel(skillType); 
							int difference = leaderLevel - this.getSkillLevel(skillType);
							bonusModifier = (difference*0.75D)/100D;
						}
					}
				}
			}
		}
		if(skillType == SkillType.ALL)
		{
			for(SkillType x : SkillType.values())
			{
				if(x == SkillType.ALL)
					continue;
				Bukkit.getPluginManager().callEvent(new McMMOPlayerXpGainEvent(Bukkit.getPlayer(playerName), x, newvalue));
				skillsXp.put(x, skillsXp.get(x)+newvalue);
			}
		} else {
			int xp = newvalue;
			
			switch(skillType)
			{
			case TAMING:
				xp=(int) (xp/LoadProperties.tamingxpmodifier);
				break;
			case MINING:
				xp=(int) (xp/LoadProperties.miningxpmodifier);
				break;
			case WOODCUTTING:
				xp=(int) (xp/LoadProperties.woodcuttingxpmodifier);
				break;
			case REPAIR:
				xp=(int) (xp/LoadProperties.repairxpmodifier);
				break;
			case HERBALISM:
				xp=(int) (xp/LoadProperties.herbalismxpmodifier);
				break;
			case ACROBATICS:
				xp=(int) (xp/LoadProperties.acrobaticsxpmodifier);
				break;
			case SWORDS:
				xp=(int) (xp/LoadProperties.swordsxpmodifier);
				break;
			case ARCHERY:
				xp=(int) (xp/LoadProperties.archeryxpmodifier);
				break;
			case UNARMED:
				xp=(int) (xp/LoadProperties.unarmedxpmodifier);
				break;
			case EXCAVATION:
				xp=(int) (xp/LoadProperties.excavationxpmodifier);
				break;
			case AXES:
				xp=(int) (xp/LoadProperties.axesxpmodifier);
				break;
			case FISHING:
				xp=(int) (xp/LoadProperties.fishingxpmodifier);
				break;
			}
			xp=xp*LoadProperties.xpGainMultiplier;
			
			if(bonusModifier > 0)
			{
				if(bonusModifier >= 2)
					bonusModifier = 2;
				
				double trueBonus = bonusModifier * xp;
				xp+=trueBonus;
			}
			Bukkit.getPluginManager().callEvent(new McMMOPlayerXpGainEvent(Bukkit.getPlayer(playerName), skillType, xp));
			skillsXp.put(skillType, skillsXp.get(skillType)+xp);
			lastgained = skillType;
		}
	}
	
	public void removeXP(SkillType skillType, int newvalue)
	{
		if(skillType == SkillType.ALL)
		{
			skillsXp.put(SkillType.TAMING, skillsXp.get(SkillType.TAMING)-newvalue);
			skillsXp.put(SkillType.MINING, skillsXp.get(SkillType.MINING)-newvalue);
			skillsXp.put(SkillType.WOODCUTTING, skillsXp.get(SkillType.WOODCUTTING)-newvalue);
			skillsXp.put(SkillType.REPAIR, skillsXp.get(SkillType.REPAIR)-newvalue);
			skillsXp.put(SkillType.HERBALISM, skillsXp.get(SkillType.HERBALISM)-newvalue);
			skillsXp.put(SkillType.ACROBATICS, skillsXp.get(SkillType.ACROBATICS)-newvalue);
			skillsXp.put(SkillType.SWORDS, skillsXp.get(SkillType.SWORDS)-newvalue);
			skillsXp.put(SkillType.ARCHERY, skillsXp.get(SkillType.ARCHERY)-newvalue);
			skillsXp.put(SkillType.UNARMED, skillsXp.get(SkillType.UNARMED)-newvalue);
			skillsXp.put(SkillType.EXCAVATION, skillsXp.get(SkillType.EXCAVATION)-newvalue);
			skillsXp.put(SkillType.AXES, skillsXp.get(SkillType.AXES)-newvalue);
			skillsXp.put(SkillType.FISHING, skillsXp.get(SkillType.FISHING)-newvalue);
		} else {
			skillsXp.put(skillType, skillsXp.get(skillType)-newvalue);
		}
	}
	public void acceptInvite()
	{
		party = invite;
		invite = "";
	}
	public void modifyInvite(String invitename)
	{
		invite = invitename;
	}
	public String getInvite() { return invite; }
	
	public void modifyskill(SkillType skillType, int newvalue)
	{
		if(skillType == SkillType.ALL)
		{
			skills.put(SkillType.TAMING, newvalue);
			skills.put(SkillType.MINING, newvalue);
			skills.put(SkillType.WOODCUTTING, newvalue);
			skills.put(SkillType.REPAIR, newvalue);
			skills.put(SkillType.HERBALISM, newvalue);
			skills.put(SkillType.ACROBATICS, newvalue);
			skills.put(SkillType.SWORDS, newvalue);
			skills.put(SkillType.ARCHERY, newvalue);
			skills.put(SkillType.UNARMED, newvalue);
			skills.put(SkillType.EXCAVATION, newvalue);
			skills.put(SkillType.AXES, newvalue);
			skills.put(SkillType.FISHING, newvalue);
			
			skillsXp.put(SkillType.TAMING, 0);
			skillsXp.put(SkillType.MINING, 0);
			skillsXp.put(SkillType.WOODCUTTING, 0);
			skillsXp.put(SkillType.REPAIR, 0);
			skillsXp.put(SkillType.HERBALISM, 0);
			skillsXp.put(SkillType.ACROBATICS, 0);
			skillsXp.put(SkillType.SWORDS, 0);
			skillsXp.put(SkillType.ARCHERY, 0);
			skillsXp.put(SkillType.UNARMED, 0);
			skillsXp.put(SkillType.EXCAVATION, 0);
			skillsXp.put(SkillType.AXES, 0);
			skillsXp.put(SkillType.FISHING, 0);
		} else {
			skills.put(skillType, newvalue);
			skillsXp.put(skillType, 0);
		}
		save(); //This will make mctop update for MySQL
	}
	
	public void addLevels(SkillType skillType, int levels)
	{
		if(skillType == SkillType.ALL)
		{
			skills.put(SkillType.TAMING, skills.get(SkillType.TAMING)+levels);
			skills.put(SkillType.MINING, skills.get(SkillType.MINING)+levels);
			skills.put(SkillType.WOODCUTTING, skills.get(SkillType.WOODCUTTING)+levels);
			skills.put(SkillType.REPAIR, skills.get(SkillType.REPAIR)+levels);
			skills.put(SkillType.HERBALISM, skills.get(SkillType.HERBALISM)+levels);
			skills.put(SkillType.ACROBATICS, skills.get(SkillType.ACROBATICS)+levels);
			skills.put(SkillType.SWORDS, skills.get(SkillType.SWORDS)+levels);
			skills.put(SkillType.ARCHERY, skills.get(SkillType.ARCHERY)+levels);
			skills.put(SkillType.UNARMED, skills.get(SkillType.UNARMED)+levels);
			skills.put(SkillType.EXCAVATION, skills.get(SkillType.EXCAVATION)+levels);
			skills.put(SkillType.AXES, skills.get(SkillType.AXES)+levels);
			skills.put(SkillType.FISHING, skills.get(SkillType.FISHING)+levels);
			
			skillsXp.put(SkillType.TAMING, 0);
			skillsXp.put(SkillType.MINING, 0);
			skillsXp.put(SkillType.WOODCUTTING, 0);
			skillsXp.put(SkillType.REPAIR, 0);
			skillsXp.put(SkillType.HERBALISM, 0);
			skillsXp.put(SkillType.ACROBATICS, 0);
			skillsXp.put(SkillType.SWORDS, 0);
			skillsXp.put(SkillType.ARCHERY, 0);
			skillsXp.put(SkillType.UNARMED, 0);
			skillsXp.put(SkillType.EXCAVATION, 0);
			skillsXp.put(SkillType.AXES, 0);
			skillsXp.put(SkillType.FISHING, 0);
		} else {
			skills.put(skillType, skills.get(skillType)+levels);
			skillsXp.put(skillType, 0);
		}
	}
	
	public Integer getXpToLevel(SkillType skillType)
	{
		return (int) ((1020+(skills.get(skillType) *  20)));
	}
    
	//Store the player's party
    public void setParty(String newParty)
    {
    	party = newParty;
    }
    //Retrieve the player's party
    public String getParty() {return party;}
            //Remove party
    public void removeParty() {
    	party = null;
    }
    //Retrieve whether or not the player is in a party
    public boolean inParty() 
    {
    	if(party != null && !party.equals("") && !party.equals("null")){
    		return true;
    	} else {
    		return false;
    	}
    }
    
    //Retrieve whether or not the player has an invite
    public boolean hasPartyInvite() {
    	if(invite != null && !invite.equals("") && !invite.equals("null")){
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public String getPlayerName()
    {
        return playerName;
    }
}	
