package com.gmail.nossr50.datatypes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.contrib.SpoutStuff;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;



public class PlayerProfile
{
    protected final Logger log = Logger.getLogger("Minecraft");
    
    //MISC
	private String party, myspawn, myspawnworld, invite;
	
	//TOGGLES
	private boolean placedAnvil = false, partyChatMode = false, adminChatMode = false, godMode = false, greenTerraMode, partyChatOnly = false, greenTerraInformed = true, berserkInformed = true, skullSplitterInformed = true, gigaDrillBreakerInformed = true, 
	superBreakerInformed = true, serratedStrikesInformed = true, treeFellerInformed = true, dead, abilityuse = true, treeFellerMode, superBreakerMode, gigaDrillBreakerMode, 
	serratedStrikesMode, hoePreparationMode = false, shovelPreparationMode = false, swordsPreparationMode = false, fistsPreparationMode = false, pickaxePreparationMode = false, axePreparationMode = false, skullSplitterMode, berserkMode;
	
	//TIMESTAMPS
	//ATS = (Time of) Activation Time Stamp
	//DATS = (Time of) Deactivation Time Stamp
	private int recentlyHurt = 0, archeryShotATS = 0, berserkATS = 0, berserkDATS = 0, gigaDrillBreakerATS = 0, gigaDrillBreakerDATS = 0,
	respawnATS = 0, mySpawnATS = 0, greenTerraATS = 0, greenTerraDATS = 0, superBreakerATS = 0, superBreakerDATS = 0, serratedStrikesATS = 0, serratedStrikesDATS = 0, treeFellerATS = 0, treeFellerDATS = 0, 
	skullSplitterATS = 0, skullSplitterDATS = 0, hoePreparationATS = 0, axePreparationATS = 0, pickaxePreparationATS = 0, fistsPreparationATS = 0, shovelPreparationATS = 0, swordsPreparationATS = 0;
	
	private SkillType lastgained = null;
	
	//MySQL STUFF
	private int xpbarinc=0, lastlogin=0, userid = 0, bleedticks = 0;
	
	//MAGIC STUFF
	private int mana = 0;
	private int greenDyeCycleSel = 0, greenDyeCycle = 0, blueDyeCycle = 0, blueDyeCycleSel = 0;
	public boolean dyeChanged = false;
	
	private String playername;
	
	//Time to HashMap this shiz
	HashMap<SkillType, Integer> skills = new HashMap<SkillType, Integer>(); //Skills and XP
	HashMap<SkillType, Integer> skillsXp = new HashMap<SkillType, Integer>(); //Skills and XP
	
    String location = "plugins/mcMMO/FlatFileStuff/mcmmo.users";
        
	public PlayerProfile(Player player)
	{
		
		//Setup the HashMap for the skills
		skills.put(SkillType.SORCERY, 0);
		skillsXp.put(SkillType.SORCERY, 0);
		
		skills.put(SkillType.UNARMED, 0);
		skillsXp.put(SkillType.UNARMED, 0);
		
		skills.put(SkillType.TAMING, 0);
		skillsXp.put(SkillType.TAMING, 0);
		
		skills.put(SkillType.MINING, 0);
		skillsXp.put(SkillType.MINING, 0);
		
		skills.put(SkillType.WOODCUTTING, 0);
		skillsXp.put(SkillType.WOODCUTTING, 0);
		
		skills.put(SkillType.REPAIR, 0);
		skillsXp.put(SkillType.REPAIR, 0);
		
		skills.put(SkillType.HERBALISM, 0);
		skillsXp.put(SkillType.HERBALISM, 0);
		
		skills.put(SkillType.EXCAVATION, 0);
		skillsXp.put(SkillType.EXCAVATION, 0);
		
		skills.put(SkillType.ARCHERY, 0);
		skillsXp.put(SkillType.ARCHERY, 0);
		
		skills.put(SkillType.SWORDS, 0);
		skillsXp.put(SkillType.SWORDS, 0);
		
		skills.put(SkillType.AXES, 0);
		skillsXp.put(SkillType.AXES, 0);
		
		skills.put(SkillType.ACROBATICS, 0);
		skillsXp.put(SkillType.ACROBATICS, 0);
		
		mana = getMaxMana();
		
		playername = player.getName();
		if (LoadProperties.useMySQL) 
		{
			if(!loadMySQL(player)) {
				addMySQLPlayer(player);
				loadMySQL(player);//This is probably not needed anymore, could just delete
			}
		} else {
			if(!load()) { addPlayer(); }			
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
	
	public boolean loadMySQL(Player p) 
	{
		Integer id = 0;
		id = mcMMO.database.GetInt("SELECT id FROM "+LoadProperties.MySQLtablePrefix+"users WHERE user = '" + p.getName() + "'");
		if(id == 0)
			return false;
		this.userid = id;
		if (id > 0) {
			HashMap<Integer, ArrayList<String>> users = mcMMO.database.Read("SELECT lastlogin, party FROM "+LoadProperties.MySQLtablePrefix+"users WHERE id = " + id);
				//lastlogin = Integer.parseInt(users.get(1).get(0));
				party = users.get(1).get(1);
			HashMap<Integer, ArrayList<String>> spawn = mcMMO.database.Read("SELECT world, x, y, z FROM "+LoadProperties.MySQLtablePrefix+"spawn WHERE user_id = " + id);
				myspawnworld = spawn.get(1).get(0);
				myspawn = spawn.get(1).get(1) + "," + spawn.get(1).get(2) + "," + spawn.get(1).get(3);				
			HashMap<Integer, ArrayList<String>> cooldowns = mcMMO.database.Read("SELECT mining, woodcutting, unarmed, herbalism, excavation, swords, axes FROM "+LoadProperties.MySQLtablePrefix+"cooldowns WHERE user_id = " + id);
			/*
			 * I'm still learning MySQL, this is a fix for adding a new table
			 * its not pretty but it works
			 */
			if(cooldowns.get(1) == null)
			{
				mcMMO.database.Write("INSERT INTO "+LoadProperties.MySQLtablePrefix+"cooldowns (user_id) VALUES ("+id+")");
			}
			else
			{
				superBreakerDATS = Integer.valueOf(cooldowns.get(1).get(0));
				treeFellerDATS = Integer.valueOf(cooldowns.get(1).get(1));
				berserkDATS = Integer.valueOf(cooldowns.get(1).get(2));
				greenTerraDATS = Integer.valueOf(cooldowns.get(1).get(3));
				gigaDrillBreakerDATS = Integer.valueOf(cooldowns.get(1).get(4));
				serratedStrikesDATS = Integer.valueOf(cooldowns.get(1).get(5));
				skullSplitterDATS = Integer.valueOf(cooldowns.get(1).get(6));
			}
			HashMap<Integer, ArrayList<String>> stats = mcMMO.database.Read("SELECT taming, mining, repair, woodcutting, unarmed, herbalism, excavation, archery, swords, axes, acrobatics FROM "+LoadProperties.MySQLtablePrefix+"skills WHERE user_id = " + id);
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
			HashMap<Integer, ArrayList<String>> experience = mcMMO.database.Read("SELECT taming, mining, repair, woodcutting, unarmed, herbalism, excavation, archery, swords, axes, acrobatics FROM "+LoadProperties.MySQLtablePrefix+"experience WHERE user_id = " + id);
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
			return true;
		}
		else {
			return false;
		}		
	}
	public void addMySQLPlayer(Player p) {
		Integer id = 0;
		mcMMO.database.Write("INSERT INTO "+LoadProperties.MySQLtablePrefix+"users (user, lastlogin) VALUES ('" + p.getName() + "'," + System.currentTimeMillis() / 1000 +")");
		id = mcMMO.database.GetInt("SELECT id FROM "+LoadProperties.MySQLtablePrefix+"users WHERE user = '" + p.getName() + "'");
		mcMMO.database.Write("INSERT INTO "+LoadProperties.MySQLtablePrefix+"cooldowns (user_id) VALUES ("+id+")");
		mcMMO.database.Write("INSERT INTO "+LoadProperties.MySQLtablePrefix+"spawn (user_id) VALUES ("+id+")");
		mcMMO.database.Write("INSERT INTO "+LoadProperties.MySQLtablePrefix+"skills (user_id) VALUES ("+id+")");
		mcMMO.database.Write("INSERT INTO "+LoadProperties.MySQLtablePrefix+"experience (user_id) VALUES ("+id+")");
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

        		if(!character[0].equals(playername)){continue;}
        		
    			//Get Mining
    			if(character.length > 1 && m.isInt(character[1]))
    				skills.put(SkillType.MINING, Integer.valueOf(character[1]));
    			//Myspawn
    			if(character.length > 2)
    				myspawn = character[2];
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
    			if(character.length > 23 && m.isInt(character[23]))
    				myspawnworld = character[23];
    			if(character.length > 24 && m.isInt(character[24]))
    				skills.put(SkillType.TAMING, Integer.valueOf(character[24]));
    			if(character.length > 25 && m.isInt(character[25]))
    				skillsXp.put(SkillType.TAMING, Integer.valueOf(character[25]));
    			if(character.length > 26)
    				berserkDATS = Integer.valueOf(character[26]);
    			if(character.length > 27)
    				gigaDrillBreakerDATS = Integer.valueOf(character[27]);
    			if(character.length > 28)
    				treeFellerDATS = Integer.valueOf(character[28]);
    			if(character.length > 29)
    				greenTerraDATS = Integer.valueOf(character[29]);
    			if(character.length > 30)
    				serratedStrikesDATS = Integer.valueOf(character[30]);
    			if(character.length > 31)
    				skullSplitterDATS = Integer.valueOf(character[31]);
    			if(character.length > 32)
    				superBreakerDATS = Integer.valueOf(character[32]);
            	in.close();
    			return true;
        	}
        	in.close();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Exception while reading "
            		+ location + " (Are you sure you formatted it correctly?)", e);
        }
        return false;
	}
	
    public void save()
    {
    	Long timestamp = System.currentTimeMillis()/1000; //Convert to seconds
    	// if we are using mysql save to database
    	if (LoadProperties.useMySQL) 
    	{
    		mcMMO.database.Write("UPDATE "+LoadProperties.MySQLtablePrefix+"users SET lastlogin = " + timestamp.intValue() + " WHERE id = " + this.userid);
    		mcMMO.database.Write("UPDATE "+LoadProperties.MySQLtablePrefix+"users SET party = '"+this.party+"' WHERE id = " +this.userid);
    		mcMMO.database.Write("UPDATE "+LoadProperties.MySQLtablePrefix+"spawn SET world = '" + this.myspawnworld + "', x = " +getX()+", y = "+getY()+", z = "+getZ()+" WHERE user_id = "+this.userid);
    		mcMMO.database.Write("UPDATE "+LoadProperties.MySQLtablePrefix+"cooldowns SET "
    				+" mining = "+(superBreakerDATS)
    				+", woodcutting = "+(treeFellerDATS)
    				+", unarmed = "+(berserkDATS)
    				+", herbalism = "+(greenTerraDATS)
    				+", excavation = "+(gigaDrillBreakerDATS)
    				+", swords = " +(serratedStrikesDATS)
    				+", axes = "+(skullSplitterDATS)
    				+" WHERE user_id = "+this.userid);
    		mcMMO.database.Write("UPDATE "+LoadProperties.MySQLtablePrefix+"skills SET "
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
    				+" WHERE user_id = "+this.userid);
    		mcMMO.database.Write("UPDATE "+LoadProperties.MySQLtablePrefix+"experience SET "
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
	        		if(!line.split(":")[0].equalsIgnoreCase(playername))
	        		{
	                    writer.append(line).append("\r\n");
	                    
	                //Otherwise write the new player information
	        		} else {
	        			writer.append(playername + ":");
	        			writer.append(skills.get(SkillType.MINING) + ":");
	        			writer.append(myspawn + ":");
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
	        			writer.append(myspawnworld+":");
	        			writer.append(skills.get(SkillType.TAMING) + ":");
	        			writer.append(skillsXp.get(SkillType.TAMING) + ":");
	        			//Need to store the DATS of abilities nao
	        			//Berserk, Gigadrillbreaker, Tree Feller, Green Terra, Serrated Strikes, Skull Splitter, Super Breaker
	        			writer.append(String.valueOf(berserkDATS)+":");
	        			writer.append(String.valueOf(gigaDrillBreakerDATS)+":");
	        			writer.append(String.valueOf(treeFellerDATS)+":");
	        			writer.append(String.valueOf(greenTerraDATS)+":");
	        			writer.append(String.valueOf(serratedStrikesDATS)+":");
	        			writer.append(String.valueOf(skullSplitterDATS)+":");
	        			writer.append(String.valueOf(superBreakerDATS)+":");
	        			writer.append("\r\n");                   			
	        		}
	        	}
	        	in.close();
	        	//Write the new file
	            FileWriter out = new FileWriter(location);
	            out.write(writer.toString());
	            out.close();
	        } catch (Exception e) {
	                log.log(Level.SEVERE, "Exception while writing to " + location + " (Are you sure you formatted it correctly?)", e);
	        }
    	}
	}
    public void addPlayer()
    {
        try {
        	//Open the file to write the player
        	FileWriter file = new FileWriter(location, true);
            BufferedWriter out = new BufferedWriter(file);
            
            //Add the player to the end
            out.append(playername + ":");
            out.append(0 + ":"); //mining
            out.append(myspawn+":");
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
            out.append(myspawnworld+":");
            out.append(0+":"); //taming
            out.append(0+":"); //tamingXP
            out.append(0+":"); //DATS
            out.append(0+":"); //DATS
            out.append(0+":"); //DATS
            out.append(0+":"); //DATS
            out.append(0+":"); //DATS
            out.append(0+":"); //DATS
            out.append(0+":"); //DATS

            //Add more in the same format as the line above
            
			out.newLine();
			out.close();
        } catch (Exception e) {
                log.log(Level.SEVERE, "Exception while writing to " + location + " (Are you sure you formatted it correctly?)", e);
        }
    }
    public int getXpBarInc()
    {
    	return xpbarinc;
    }
    public void setXpBarInc(int newvalue)
    {
    	xpbarinc = newvalue;
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
    
    public void setMana(int newvalue)
    {
    	mana = newvalue;
    }

    public int getCurrentMana()
    {
    	return mana;
    }
    public int getMaxMana()
    {
    	if(skills.get(SkillType.SORCERY) < 50)
    		return 5;
    	return skills.get(SkillType.SORCERY) / 10;
    }
    public void setDyeChanged(Boolean bool)
    {
    	dyeChanged = bool;
    }
    public boolean getDyeChanged()
    {
    	return dyeChanged;
    }
    public void setBlueDyeCycle(int newvalue)
    {
    	blueDyeCycle = newvalue;
    }
    public int getBlueDyeCycle()
    {
    	return blueDyeCycle;
    }
    public void setBlueDyeCycleSel(int newvalue)
    {
    	blueDyeCycleSel = newvalue;
    }
    public int getBlueDyeCycleSel()
    {
    	return blueDyeCycleSel;
    }
    public void setGreenDyeCycle(int newvalue)
    {
    	greenDyeCycle = newvalue;
    }
    public int getGreenDyeCycle()
    {
    	return greenDyeCycle;
    }
    public void setGreenDyeCycleSel(int newvalue)
    {
    	greenDyeCycleSel = newvalue;
    }
    public int getGreenDyeCycleSel()
    {
    	return greenDyeCycleSel;
    }

	public boolean isPlayer(String player)
	{
		return player.equals(playername);
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
	public long getMySpawnATS(){
		return mySpawnATS;
	}
	public void setMySpawnATS(long newvalue)
	{
		mySpawnATS = (int) (newvalue/1000);
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
	}
	public void addBleedTicks(Integer newvalue){
		bleedticks+=newvalue;
	}
	/*
	 * EXPLOIT PREVENTION
	 */
	public long getRespawnATS() {return respawnATS;}
	public void setRespawnATS(long newvalue) {respawnATS = (int) (newvalue/1000);}
	
	/*
	 * ARCHERY NERF STUFF
	 */
	public long getArcheryShotATS() {return archeryShotATS;}
	public void setArcheryShotATS(long newvalue) {archeryShotATS = (int) (newvalue/1000);}
	
	/*
	 * HOE PREPARATION
	 */
	public boolean getHoePreparationMode(){
		return hoePreparationMode;
	}
	public void setHoePreparationMode(Boolean bool){
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
	public void setSwordsPreparationMode(Boolean bool){
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
	public void setShovelPreparationMode(Boolean bool){
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
	public void setFistsPreparationMode(Boolean bool){
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
	public void setAxePreparationMode(Boolean bool){
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
	public void setPickaxePreparationMode(Boolean bool){
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
	public void setGreenTerraInformed(Boolean bool){
		greenTerraInformed = bool;
	}
	public boolean getGreenTerraMode(){
		return greenTerraMode;
	}
	public void setGreenTerraMode(Boolean bool){
		greenTerraMode = bool;
	}
	public long getGreenTerraActivatedTimeStamp() {return greenTerraATS;}
	public void setGreenTerraActivatedTimeStamp(Long newvalue){
		greenTerraATS = (int) (newvalue/1000);
	}
	public long getGreenTerraDeactivatedTimeStamp() {return greenTerraDATS;}
	public void setGreenTerraDeactivatedTimeStamp(Long newvalue){
		greenTerraDATS = (int) (newvalue/1000);
		save();
	}
	/*
	 * BERSERK MODE
	 */
	public boolean getBerserkInformed() {return berserkInformed;}
	public void setBerserkInformed(Boolean bool){
		berserkInformed = bool;
	}
	public boolean getBerserkMode(){
		return berserkMode;
	}
	public void setBerserkMode(Boolean bool){
		berserkMode = bool;
	}
	public long getBerserkActivatedTimeStamp() {return berserkATS;}
	public void setBerserkActivatedTimeStamp(Long newvalue){
		berserkATS = (int) (newvalue/1000);
	}
	public long getBerserkDeactivatedTimeStamp() {return berserkDATS;}
	public void setBerserkDeactivatedTimeStamp(Long newvalue){
		berserkDATS = (int) (newvalue/1000);
		save();
	}
	/*
	 * SKULL SPLITTER
	 */
	public boolean getSkullSplitterInformed() {return skullSplitterInformed;}
	public void setSkullSplitterInformed(Boolean bool){
		skullSplitterInformed = bool;
	}
	public boolean getSkullSplitterMode(){
		return skullSplitterMode;
	}
	public void setSkullSplitterMode(Boolean bool){
		skullSplitterMode = bool;
	}
	public long getSkullSplitterActivatedTimeStamp() {return skullSplitterATS;}
	public void setSkullSplitterActivatedTimeStamp(Long newvalue){
		skullSplitterATS = (int) (newvalue/1000);
	}
	public long getSkullSplitterDeactivatedTimeStamp() {return skullSplitterDATS;}
	public void setSkullSplitterDeactivatedTimeStamp(Long newvalue){
		skullSplitterDATS = (int) (newvalue/1000);
		save();
	}
	/*
	 * SERRATED STRIKES
	 */
	public boolean getSerratedStrikesInformed() {return serratedStrikesInformed;}
	public void setSerratedStrikesInformed(Boolean bool){
		serratedStrikesInformed = bool;
	}
	public boolean getSerratedStrikesMode(){
		return serratedStrikesMode;
	}
	public void setSerratedStrikesMode(Boolean bool){
		serratedStrikesMode = bool;
	}
	public long getSerratedStrikesActivatedTimeStamp() {return serratedStrikesATS;}
	public void setSerratedStrikesActivatedTimeStamp(Long newvalue){
		serratedStrikesATS = (int) (newvalue/1000);
	}
	public long getSerratedStrikesDeactivatedTimeStamp() {return serratedStrikesDATS;}
	public void setSerratedStrikesDeactivatedTimeStamp(Long newvalue){
		serratedStrikesDATS = (int) (newvalue/1000);
		save();
	}
	/*
	 * GIGA DRILL BREAKER
	 */
	public boolean getGigaDrillBreakerInformed() {return gigaDrillBreakerInformed;}
	public void setGigaDrillBreakerInformed(Boolean bool){
		gigaDrillBreakerInformed = bool;
	}
	public boolean getGigaDrillBreakerMode(){
		return gigaDrillBreakerMode;
	}
	public void setGigaDrillBreakerMode(Boolean bool){
		gigaDrillBreakerMode = bool;
	}
	public long getGigaDrillBreakerActivatedTimeStamp() {return gigaDrillBreakerATS;}
	public void setGigaDrillBreakerActivatedTimeStamp(Long newvalue){
		gigaDrillBreakerATS = (int) (newvalue/1000);
	}
	public long getGigaDrillBreakerDeactivatedTimeStamp() {return gigaDrillBreakerDATS;}
	public void setGigaDrillBreakerDeactivatedTimeStamp(Long newvalue){
		gigaDrillBreakerDATS = (int) (newvalue/1000);
		save();
	}
	/*
	 * TREE FELLER STUFF
	 */
	public boolean getTreeFellerInformed() {return treeFellerInformed;}
	public void setTreeFellerInformed(Boolean bool){
		treeFellerInformed = bool;
	}
	public boolean getTreeFellerMode(){
		return treeFellerMode;
	}
	public void setTreeFellerMode(Boolean bool){
		treeFellerMode = bool;
	}
	public long getTreeFellerActivatedTimeStamp() {return treeFellerATS;}
	public void setTreeFellerActivatedTimeStamp(Long newvalue){
		treeFellerATS = (int) (newvalue/1000);
	}
	public long getTreeFellerDeactivatedTimeStamp() {return treeFellerDATS;}
	public void setTreeFellerDeactivatedTimeStamp(Long newvalue){
		treeFellerDATS = (int) (newvalue/1000);
		save();
	}
	/*
	 * MINING
	 */
	public boolean getSuperBreakerInformed() {return superBreakerInformed;}
	public void setSuperBreakerInformed(Boolean bool){
		superBreakerInformed = bool;
	}
	public boolean getSuperBreakerMode(){
		return superBreakerMode;
	}
	public void setSuperBreakerMode(Boolean bool){
		superBreakerMode = bool;
	}
	public long getSuperBreakerActivatedTimeStamp() {return superBreakerATS;}
	public void setSuperBreakerActivatedTimeStamp(Long newvalue){
		superBreakerATS = (int) (newvalue/1000);
	}
	public long getSuperBreakerDeactivatedTimeStamp() {return superBreakerDATS;}
	public void setSuperBreakerDeactivatedTimeStamp(Long newvalue){
		superBreakerDATS = (int) (newvalue/1000);
		save();
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
		save();
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
	public void addXP(SkillType skillType, int newvalue)
	{
		if(skillType == SkillType.ALL)
		{
			skillsXp.put(SkillType.TAMING, skillsXp.get(SkillType.TAMING)+newvalue);
			skillsXp.put(SkillType.MINING, skillsXp.get(SkillType.MINING)+newvalue);
			skillsXp.put(SkillType.WOODCUTTING, skillsXp.get(SkillType.WOODCUTTING)+newvalue);
			skillsXp.put(SkillType.REPAIR, skillsXp.get(SkillType.REPAIR)+newvalue);
			skillsXp.put(SkillType.HERBALISM, skillsXp.get(SkillType.HERBALISM)+newvalue);
			skillsXp.put(SkillType.ACROBATICS, skillsXp.get(SkillType.ACROBATICS)+newvalue);
			skillsXp.put(SkillType.SWORDS, skillsXp.get(SkillType.SWORDS)+newvalue);
			skillsXp.put(SkillType.ARCHERY, skillsXp.get(SkillType.ARCHERY)+newvalue);
			skillsXp.put(SkillType.UNARMED, skillsXp.get(SkillType.UNARMED)+newvalue);
			skillsXp.put(SkillType.EXCAVATION, skillsXp.get(SkillType.EXCAVATION)+newvalue);
			skillsXp.put(SkillType.AXES, skillsXp.get(SkillType.AXES)+newvalue);
			skillsXp.put(SkillType.SORCERY, skillsXp.get(SkillType.SORCERY)+newvalue);
		} else {
			skillsXp.put(skillType, skillsXp.get(skillType)+newvalue);
			
			SkillType prevLastGained = lastgained;
			
			lastgained = skillType;
			
			//In case of an xp bar switch
			if(prevLastGained != skillType || prevLastGained == null)
				xpbarinc = SpoutStuff.getXpInc(this.getSkillXpLevel(lastgained), this.getXpToLevel(lastgained));
		}
		//save();
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
			skillsXp.put(SkillType.SORCERY, skillsXp.get(SkillType.SORCERY)-newvalue);
		} else {
			skillsXp.put(skillType, skillsXp.get(skillType)-newvalue);
		}
		//save();
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
			skills.put(SkillType.SORCERY, newvalue);
			
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
			skillsXp.put(SkillType.SORCERY, 0);
		} else {
			skills.put(skillType, newvalue);
			skillsXp.put(skillType, newvalue);
		}
		save();
	}
	public Integer getXpToLevel(SkillType skillType)
	{
		switch(skillType)
		{
		case SORCERY:
			return 100+(skills.get(skillType) * LoadProperties.sorceryxpmodifier * LoadProperties.globalxpmodifier);
		case TAMING:
			return 100+(skills.get(skillType) * LoadProperties.tamingxpmodifier * LoadProperties.globalxpmodifier);
		case MINING:
			return 100+(skills.get(skillType) * LoadProperties.miningxpmodifier * LoadProperties.globalxpmodifier);
		case WOODCUTTING:
			return 100+(skills.get(skillType) * LoadProperties.woodcuttingxpmodifier * LoadProperties.globalxpmodifier);
		case REPAIR:
			return 100+(skills.get(skillType) * LoadProperties.repairxpmodifier * LoadProperties.globalxpmodifier);
		case HERBALISM:
			return 100+(skills.get(skillType) * LoadProperties.herbalismxpmodifier * LoadProperties.globalxpmodifier);
		case ACROBATICS:
			return 100+(skills.get(skillType) * LoadProperties.acrobaticsxpmodifier * LoadProperties.globalxpmodifier);
		case SWORDS:
			return 100+(skills.get(skillType) * LoadProperties.swordsxpmodifier * LoadProperties.globalxpmodifier);
		case ARCHERY:
			return 100+(skills.get(skillType) * LoadProperties.archeryxpmodifier * LoadProperties.globalxpmodifier);
		case UNARMED:
			return 100+(skills.get(skillType) * LoadProperties.unarmedxpmodifier * LoadProperties.globalxpmodifier);
		case EXCAVATION:
			return 100+(skills.get(skillType) * LoadProperties.excavationxpmodifier * LoadProperties.globalxpmodifier);
		case AXES:
			return 100+(skills.get(skillType) * LoadProperties.axesxpmodifier * LoadProperties.globalxpmodifier);
		default:
			return null;
		}
	}   
    
	//Store the player's party
    public void setParty(String newParty)
    {
    	party = newParty;
    	save();
    }
    //Retrieve the player's party
    public String getParty() {return party;}
            //Remove party
    public void removeParty() {
    	party = null;
    	save();
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
    public String getMySpawnWorld(Plugin plugin){
    	if(myspawnworld != null && !myspawnworld.equals("") && !myspawnworld.equals("null")){
    		return myspawnworld;
    	} else {
    		return plugin.getServer().getWorlds().get(0).toString();
    	}
    }
    //Save a users spawn location
    public void setMySpawn(double x, double y, double z, String myspawnworldlocation){
    	myspawn = x+","+y+","+z;
    	myspawnworld = myspawnworldlocation;
    	save();
    }
    public String getX(){
    	if(myspawn != null)
    	{
    	String[] split = myspawn.split(",");
    	return split[0];
    	} 
    	else
    		return null;
    }
    public String getY(){
    	if(myspawn != null)
    	{
    	String[] split = myspawn.split(",");
    	return split[1];
    	} 
    	else
    		return null;
    }
    public String getZ(){
    	if(myspawn != null)
    	{
    	String[] split = myspawn.split(",");
    	return split[2];
    	} 
    	else
    		return null;
    }
    public boolean isDead(){
    	return dead;
    }
    public Location getMySpawn(Player player)
    {
    	Location loc = null;
    	if(myspawn != null)
    	{
    		if(m.isDouble(getX()) && m.isDouble(getY()) && m.isDouble(getZ()))
    				loc = new Location(player.getWorld(),(Double.parseDouble(getX())), Double.parseDouble(getY()), Double.parseDouble(getZ()));
    		else
    			return null;
    	} else
    		return null;
    	
    	loc.setYaw(0);
    	loc.setPitch(0);
    	if(loc.getX() != 0 && loc.getY() != 0 && loc.getZ() != 0 && loc.getWorld() != null){
    		return loc;
    	} else {
    		return null;
    	}
    }
}	
