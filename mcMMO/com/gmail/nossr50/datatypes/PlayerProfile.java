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
import com.gmail.nossr50.mcMMO;



public class PlayerProfile
{
    protected final Logger log = Logger.getLogger("Minecraft");
    
    //MISC
	private String party, myspawn, myspawnworld, invite;
	
	//TOGGLES
	private boolean partyChatMode = false, adminChatMode = false, godMode = false, greenTerraMode, partyChatOnly = false, greenTerraInformed = true, berserkInformed = true, skullSplitterInformed = true, gigaDrillBreakerInformed = true, 
	superBreakerInformed = true, serratedStrikesInformed = true, treeFellerInformed = true, dead, abilityuse = true, treeFellerMode, superBreakerMode, gigaDrillBreakerMode, 
	serratedStrikesMode, hoePreparationMode = false, shovelPreparationMode = false, swordsPreparationMode = false, fistsPreparationMode = false, pickaxePreparationMode = false, axePreparationMode = false, skullSplitterMode, berserkMode;
	
	//TIMESTAMPS
	//ATS = (Time of) Activation Time Stamp
	//DATS = (Time of) Deactivation Time Stamp
	private long recentlyHurt = 0, archeryShotATS = 0, berserkATS = 0, berserkDATS = 0, gigaDrillBreakerATS = 0, gigaDrillBreakerDATS = 0,
	respawnATS = 0, mySpawnATS = 0, greenTerraATS = 0, greenTerraDATS = 0, superBreakerATS = 0, superBreakerDATS = 0, serratedStrikesATS = 0, serratedStrikesDATS = 0, treeFellerATS = 0, treeFellerDATS = 0, 
	skullSplitterATS = 0, skullSplitterDATS = 0, hoePreparationATS = 0, axePreparationATS = 0, pickaxePreparationATS = 0, fistsPreparationATS = 0, shovelPreparationATS = 0, swordsPreparationATS = 0;
	
	//MySQL STUFF
	private int lastlogin=0, userid = 0, bleedticks = 0;
	
	//MAGIC STUFF
	private int mana = 0;
	private int greenDyeCycleSel = 0, greenDyeCycle = 0, blueDyeCycle = 0, blueDyeCycleSel = 0;
	public boolean dyeChanged = false;
	
	private String playername;
	
	//Time to HashMap this shiz
	HashMap<String, Integer> stats = new HashMap<String, Integer>(); //Skills and XP
    String location = "plugins/mcMMO/mcmmo.users";
        
	public PlayerProfile(Player player)
	{
		
		//Setup the HashMap for the skills
		stats.put("sorcery", 0);
		stats.put("sorceryXP", 0);
		
		stats.put("unarmed", 0);
		stats.put("unarmedXP", 0);
		
		stats.put("taming", 0);
		stats.put("tamingXP", 0);
		
		stats.put("mining", 0);
		stats.put("miningXP", 0);
		
		stats.put("woodcutting", 0);
		stats.put("woodcuttingXP", 0);
		
		stats.put("repair", 0);
		stats.put("repairXP", 0);
		
		stats.put("herbalism", 0);
		stats.put("herbalismXP", 0);
		
		stats.put("excavation", 0);
		stats.put("excavationXP", 0);
		
		stats.put("archery", 0);
		stats.put("archeryXP", 0);
		
		stats.put("swords", 0);
		stats.put("swordsXP", 0);
		
		stats.put("axes", 0);
		stats.put("axesXP", 0);
		
		stats.put("acrobatics", 0);
		stats.put("acrobaticsXP", 0);
		
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
				superBreakerDATS = Long.valueOf(cooldowns.get(1).get(0)) * 1000;
				treeFellerDATS = Long.valueOf(cooldowns.get(1).get(1)) * 1000;
				berserkDATS = Long.valueOf(cooldowns.get(1).get(2)) * 1000;
				greenTerraDATS = Long.valueOf(cooldowns.get(1).get(3)) * 1000;
				gigaDrillBreakerDATS = Long.valueOf(cooldowns.get(1).get(4)) * 1000;
				serratedStrikesDATS = Long.valueOf(cooldowns.get(1).get(5)) * 1000;
				skullSplitterDATS = Long.valueOf(cooldowns.get(1).get(6)) * 1000;
			}
			HashMap<Integer, ArrayList<String>> skills = mcMMO.database.Read("SELECT taming, mining, repair, woodcutting, unarmed, herbalism, excavation, archery, swords, axes, acrobatics FROM "+LoadProperties.MySQLtablePrefix+"skills WHERE user_id = " + id);
				stats.put("taming", Integer.valueOf(skills.get(1).get(0)));
				stats.put("mining", Integer.valueOf(skills.get(1).get(1)));
				stats.put("repair", Integer.valueOf(skills.get(1).get(2)));
				stats.put("woodcutting", Integer.valueOf(skills.get(1).get(3)));
				stats.put("unarmed", Integer.valueOf(skills.get(1).get(4)));
				stats.put("herbalism", Integer.valueOf(skills.get(1).get(5)));
				stats.put("excavation", Integer.valueOf(skills.get(1).get(6)));
				stats.put("archery", Integer.valueOf(skills.get(1).get(7)));
				stats.put("swords", Integer.valueOf(skills.get(1).get(8)));
				stats.put("axes", Integer.valueOf(skills.get(1).get(9)));
				stats.put("acrobatics", Integer.valueOf(skills.get(1).get(10)));
			HashMap<Integer, ArrayList<String>> experience = mcMMO.database.Read("SELECT taming, mining, repair, woodcutting, unarmed, herbalism, excavation, archery, swords, axes, acrobatics FROM "+LoadProperties.MySQLtablePrefix+"experience WHERE user_id = " + id);
				stats.put("tamingXP", Integer.valueOf(experience.get(1).get(0)));
				stats.put("miningXP", Integer.valueOf(experience.get(1).get(1)));
				stats.put("repairXP", Integer.valueOf(experience.get(1).get(2)));
				stats.put("woodcuttingXP", Integer.valueOf(experience.get(1).get(3)));
				stats.put("unarmedXP", Integer.valueOf(experience.get(1).get(4)));
				stats.put("herbalismXP", Integer.valueOf(experience.get(1).get(5)));
				stats.put("excavationXP", Integer.valueOf(experience.get(1).get(6)));
				stats.put("archeryXP", Integer.valueOf(experience.get(1).get(7)));
				stats.put("swordsXP", Integer.valueOf(experience.get(1).get(8)));
				stats.put("axesXP", Integer.valueOf(experience.get(1).get(9)));
				stats.put("acrobaticsXP", Integer.valueOf(experience.get(1).get(10)));
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
    			if(character.length > 1 && isInt(character[1]))
    				stats.put("mining", Integer.valueOf(character[1]));
    			//Myspawn
    			if(character.length > 2)
    				myspawn = character[2];
    			//Party
    			if(character.length > 3)
    				party = character[3];
    			//Mining XP
    			if(character.length > 4 && isInt(character[4]))
    				stats.put("miningXP", Integer.valueOf(character[4]));
    			if(character.length > 5 && isInt(character[5]))
    				stats.put("woodcutting", Integer.valueOf(character[5]));
    			if(character.length > 6 && isInt(character[6]))
    				stats.put("woodcuttingXP", Integer.valueOf(character[6]));
    			if(character.length > 7 && isInt(character[7]))
    				stats.put("repair", Integer.valueOf(character[7]));
    			if(character.length > 8 && isInt(character[8]))
    				stats.put("unarmed",  Integer.valueOf(character[8]));
    			if(character.length > 9 && isInt(character[9]))
    				stats.put("herbalism", Integer.valueOf(character[9]));
    			if(character.length > 10 && isInt(character[10]))
    				stats.put("excavation", Integer.valueOf(character[10]));
    			if(character.length > 11 && isInt(character[11]))
    				stats.put("archery", Integer.valueOf(character[11]));
    			if(character.length > 12 && isInt(character[12]))
    				stats.put("swords", Integer.valueOf(character[12]));
    			if(character.length > 13 && isInt(character[13]))
    				stats.put("axes", Integer.valueOf(character[13]));
    			if(character.length > 14 && isInt(character[14]))
    				stats.put("acrobatics", Integer.valueOf(character[14]));
    			if(character.length > 15 && isInt(character[15]))
    				stats.put("repairXP", Integer.valueOf(character[15]));
    			if(character.length > 16 && isInt(character[16]))
    				stats.put("unarmedXP", Integer.valueOf(character[16]));
    			if(character.length > 17 && isInt(character[17]))
    				stats.put("herbalismXP", Integer.valueOf(character[17]));
    			if(character.length > 18 && isInt(character[18]))
    				stats.put("excavationXP", Integer.valueOf(character[18]));
    			if(character.length > 19 && isInt(character[19]))
    				stats.put("archeryXP", Integer.valueOf(character[19]));
    			if(character.length > 20 && isInt(character[20]))
    				stats.put("swordsXP", Integer.valueOf(character[20]));
    			if(character.length > 21 && isInt(character[21]))
    				stats.put("axesXP", Integer.valueOf(character[21]));
    			if(character.length > 22 && isInt(character[22]))
    				stats.put("acrobaticsXP", Integer.valueOf(character[22]));
    			if(character.length > 23 && isInt(character[23]))
    				myspawnworld = character[23];
    			if(character.length > 24 && isInt(character[24]))
    				stats.put("taming", Integer.valueOf(character[24]));
    			if(character.length > 25 && isInt(character[25]))
    				stats.put("tamingXP", Integer.valueOf(character[25]));
    			if(character.length > 26)
    				berserkDATS = Long.valueOf(character[26]) * 1000;
    			if(character.length > 27)
    				gigaDrillBreakerDATS = Long.valueOf(character[27]) * 1000;
    			if(character.length > 28)
    				treeFellerDATS = Long.valueOf(character[28]) * 1000;
    			if(character.length > 29)
    				greenTerraDATS = Long.valueOf(character[29]) * 1000;
    			if(character.length > 30)
    				serratedStrikesDATS = Long.valueOf(character[30]) * 1000;
    			if(character.length > 31)
    				skullSplitterDATS = Long.valueOf(character[31]) * 1000;
    			if(character.length > 32)
    				superBreakerDATS = Long.valueOf(character[32]) * 1000;
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
    	if (LoadProperties.useMySQL) {
    		mcMMO.database.Write("UPDATE "+LoadProperties.MySQLtablePrefix+"users SET lastlogin = " + timestamp.intValue() + " WHERE id = " + this.userid);
    		mcMMO.database.Write("UPDATE "+LoadProperties.MySQLtablePrefix+"users SET party = '"+this.party+"' WHERE id = " +this.userid);
    		mcMMO.database.Write("UPDATE "+LoadProperties.MySQLtablePrefix+"spawn SET world = '" + this.myspawnworld + "', x = " +getX()+", y = "+getY()+", z = "+getZ()+" WHERE user_id = "+this.userid);
    		mcMMO.database.Write("UPDATE "+LoadProperties.MySQLtablePrefix+"cooldowns SET "
    				+" mining = "+(superBreakerDATS/1000)
    				+", woodcutting = "+(treeFellerDATS/1000)
    				+", unarmed = "+(berserkDATS/1000)
    				+", herbalism = "+(greenTerraDATS/1000)
    				+", excavation = "+(gigaDrillBreakerDATS/1000)
    				+", swords = " +(serratedStrikesDATS/1000)
    				+", axes = "+(skullSplitterDATS/1000)
    				+" WHERE user_id = "+this.userid);
    		mcMMO.database.Write("UPDATE "+LoadProperties.MySQLtablePrefix+"skills SET "
    				+"  taming = "+stats.get("taming")
    				+", mining = "+stats.get("mining")
    				+", repair = "+stats.get("repair")
    				+", woodcutting = "+stats.get("woodcutting")
    				+", unarmed = "+stats.get("unarmed")
    				+", herbalism = "+stats.get("herbalism")
    				+", excavation = "+stats.get("excavation")
    				+", archery = " +stats.get("archery")
    				+", swords = " +stats.get("swords")
    				+", axes = "+stats.get("axes")
    				+", acrobatics = "+stats.get("acrobatics")
    				+" WHERE user_id = "+this.userid);
    		mcMMO.database.Write("UPDATE "+LoadProperties.MySQLtablePrefix+"experience SET "
    				+"  taming = "+stats.get("tamingXP")
    				+", mining = "+stats.get("miningXP")
    				+", repair = "+stats.get("repairXP")
    				+", woodcutting = "+stats.get("woodcuttingXP")
    				+", unarmed = "+stats.get("unarmedXP")
    				+", herbalism = "+stats.get("herbalismXP")
    				+", excavation = "+stats.get("excavationXP")
    				+", archery = " +stats.get("archeryXP")
    				+", swords = " +stats.get("swordsXP")
    				+", axes = "+stats.get("axesXP")
    				+", acrobatics = "+stats.get("acrobaticsXP")
    				+" WHERE user_id = "+this.userid);
    		
    	} else {
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
	        			writer.append(stats.get("mining") + ":");
	        			writer.append(myspawn + ":");
	        			writer.append(party+":");
	        			writer.append(stats.get("miningXP") + ":");
	        			writer.append(stats.get("woodcutting") + ":");
	        			writer.append(stats.get("woodcuttingXP") + ":");
	        			writer.append(stats.get("repair") + ":");
	        			writer.append(stats.get("unarmed") + ":");
	        			writer.append(stats.get("herbalism") + ":");
	        			writer.append(stats.get("excavation") + ":");
	        			writer.append(stats.get("archery") + ":");
	        			writer.append(stats.get("swords") + ":");
	        			writer.append(stats.get("axes") + ":");
	        			writer.append(stats.get("acrobatics") + ":");
	        			writer.append(stats.get("repairXP") + ":");
	        			writer.append(stats.get("unarmedXP") + ":");
	        			writer.append(stats.get("herbalismXP") + ":");
	        			writer.append(stats.get("excavationXP") + ":");
	        			writer.append(stats.get("archeryXP") + ":");
	        			writer.append(stats.get("swordsXP") + ":");
	        			writer.append(stats.get("axesXP") + ":");
	        			writer.append(stats.get("acrobaticsXP") + ":");
	        			writer.append(myspawnworld+":");
	        			writer.append(stats.get("taming") + ":");
	        			writer.append(stats.get("tamingXP") + ":");
	        			//Need to store the DATS of abilities nao
	        			//Berserk, Gigadrillbreaker, Tree Feller, Green Terra, Serrated Strikes, Skull Splitter, Super Breaker
	        			writer.append(String.valueOf(berserkDATS/1000)+":");
	        			writer.append(String.valueOf(gigaDrillBreakerDATS/1000)+":");
	        			writer.append(String.valueOf(treeFellerDATS/1000)+":");
	        			writer.append(String.valueOf(greenTerraDATS/1000)+":");
	        			writer.append(String.valueOf(serratedStrikesDATS/1000)+":");
	        			writer.append(String.valueOf(skullSplitterDATS/1000)+":");
	        			writer.append(String.valueOf(superBreakerDATS/1000)+":");
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
    
    public boolean getAdminChatMode() {return adminChatMode;}
    public boolean getPartyChatMode() {return partyChatMode;}
    public boolean getGodMode() {return godMode;}
    
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
    	if(stats.get("sorcery") < 50)
    		return 5;
    	return stats.get("sorcery") / 10;
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
	public void toggleAbilityUse(){
		if(abilityuse == false){
			abilityuse = true;
		} else {
			abilityuse = false;
		}
	}
	public long getMySpawnATS(){
		return mySpawnATS;
	}
	public void setMySpawnATS(long newvalue){
		mySpawnATS = newvalue;
	}
	public void decreaseBleedTicks(){
		if(bleedticks >= 1){
			bleedticks--;
		}
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
	public void setRespawnATS(long newvalue) {respawnATS = newvalue;}
	
	/*
	 * ARCHERY NERF STUFF
	 */
	public long getArcheryShotATS() {return archeryShotATS;}
	public void setArcheryShotATS(long newvalue) {archeryShotATS = newvalue;}
	
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
		hoePreparationATS = newvalue;
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
		swordsPreparationATS = newvalue;
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
		shovelPreparationATS = newvalue;
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
		fistsPreparationATS = newvalue;
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
		axePreparationATS = newvalue;
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
		pickaxePreparationATS = newvalue;
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
		greenTerraATS = newvalue;
	}
	public long getGreenTerraDeactivatedTimeStamp() {return greenTerraDATS;}
	public void setGreenTerraDeactivatedTimeStamp(Long newvalue){
		greenTerraDATS = newvalue;
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
		berserkATS = newvalue;
	}
	public long getBerserkDeactivatedTimeStamp() {return berserkDATS;}
	public void setBerserkDeactivatedTimeStamp(Long newvalue){
		berserkDATS = newvalue;
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
		skullSplitterATS = newvalue;
	}
	public long getSkullSplitterDeactivatedTimeStamp() {return skullSplitterDATS;}
	public void setSkullSplitterDeactivatedTimeStamp(Long newvalue){
		skullSplitterDATS = newvalue;
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
		serratedStrikesATS = newvalue;
	}
	public long getSerratedStrikesDeactivatedTimeStamp() {return serratedStrikesDATS;}
	public void setSerratedStrikesDeactivatedTimeStamp(Long newvalue){
		serratedStrikesDATS = newvalue;
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
		gigaDrillBreakerATS = newvalue;
	}
	public long getGigaDrillBreakerDeactivatedTimeStamp() {return gigaDrillBreakerDATS;}
	public void setGigaDrillBreakerDeactivatedTimeStamp(Long newvalue){
		gigaDrillBreakerDATS = newvalue;
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
		treeFellerATS = newvalue;
	}
	public long getTreeFellerDeactivatedTimeStamp() {return treeFellerDATS;}
	public void setTreeFellerDeactivatedTimeStamp(Long newvalue){
		treeFellerDATS = newvalue;
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
		superBreakerATS = newvalue;
	}
	public long getSuperBreakerDeactivatedTimeStamp() {return superBreakerDATS;}
	public void setSuperBreakerDeactivatedTimeStamp(Long newvalue){
		superBreakerDATS = newvalue;
		save();
	}
	public long getRecentlyHurt(){
		return recentlyHurt;
	}
	public void setRecentlyHurt(long newvalue){
		recentlyHurt = newvalue;
	}
	public void skillUp(String skillname, int newvalue)
	{
		stats.put(skillname, stats.get(skillname)+newvalue);
		save();
	}
	public void skillUpSorcery (int newskill){
		skillUp("sorcery", newskill);
	}
	public void skillUpTaming(int newskill){
		skillUp("taming", newskill);
	}
	public void skillUpAxes(int newskill){
		skillUp("axes", newskill);
	}
	public void skillUpAcrobatics(int newskill){
		skillUp("acrobatics", newskill);
	}
	public void skillUpSwords(int newskill){
		skillUp("swords", newskill);
	}
	public void skillUpArchery(int newskill){
		skillUp("archery", newskill);
	}
	public void skillUpRepair(int newskill){
		skillUp("repair", newskill);
	}
	public void skillUpMining(int newskill){
		skillUp("mining", newskill);
	}
	public void skillUpUnarmed(int newskill){
		skillUp("unarmed", newskill);
	}
	public void skillUpHerbalism(int newskill){
		skillUp("herbalism", newskill);
	}
	public void skillUpExcavation(int newskill){
		skillUp("excavation", newskill);
	}
	public void skillUpWoodCutting(int newskill){
		skillUp("woodcutting", newskill);
	}
	public String getSkillToString(String skillname){
		return String.valueOf(stats.get(skillname));
	}
	public Integer getSkill(String skillname){
		return stats.get(skillname);
	}
	public void resetSkillXp(String skillname){
		stats.put(skillname+"XP", 0);
	}
	public void addXP(String skillname, int newvalue)
	{
		if(skillname.toLowerCase().equals("all")){
			stats.put("tamingXP", stats.get("tamingXP")+newvalue);
			stats.put("miningXP", stats.get("miningXP")+newvalue);
			stats.put("woodcuttingXP", stats.get("woodcuttingXP")+newvalue);
			stats.put("repairXP", stats.get("repairXP")+newvalue);
			stats.put("herbalismXP", stats.get("herbalismXP")+newvalue);
			stats.put("acrobaticsXP", stats.get("acrobaticsXP")+newvalue);
			stats.put("swordsXP", stats.get("swordsXP")+newvalue);
			stats.put("archeryXP", stats.get("archeryXP")+newvalue);
			stats.put("unarmedXP", stats.get("unarmedXP")+newvalue);
			stats.put("excavationXP", stats.get("excavationXP")+newvalue);
			stats.put("axesXP", stats.get("axesXP")+newvalue);
			stats.put("sorcery", stats.get("sorceryXP")+newvalue);
		} else {
			stats.put(skillname.toLowerCase()+"XP", stats.get(skillname.toLowerCase()+"XP")+newvalue);
		}
		//save();
	}
	public void addSorceryXP(int newXP)
	{
		addXP("sorcery", newXP);
	}
	public void addTamingXP(int newXP)
	{
		addXP("taming", newXP);
	}
	public void addAcrobaticsXP(int newXP)
	{
		addXP("acrobatics", newXP);
	}
	public void addAxesXP(int newXP)
	{
		addXP("axes", newXP);
	}
	public void addSwordsXP(int newXP)
	{
		addXP("swords", newXP);
	}
	public void addArcheryXP(int newXP)
	{
		addXP("archery", newXP);
	}
	public void addExcavationXP(int newXP)
	{
		addXP("excavation", newXP);
	}
	public void addHerbalismXP(int newXP)
	{
		addXP("herbalism", newXP);
	}
	public void addRepairXP(int newXP)
	{
		addXP("repair", newXP);
	}
	public void addUnarmedXP(int newXP)
	{
		addXP("unarmed", newXP);
	}
	public void addWoodcuttingXP(int newXP)
	{
		addXP("woodcutting", newXP);
	}
	public void addMiningXP(int newXP)
	{
		addXP("mining", newXP);
	}
	public void removeXP(String skillname, int newvalue)
	{
		if(skillname.toLowerCase().equals("all")){
			stats.put("tamingXP", stats.get("tamingXP")-newvalue);
			stats.put("miningXP", stats.get("miningXP")-newvalue);
			stats.put("woodcuttingXP", stats.get("woodcuttingXP")-newvalue);
			stats.put("repairXP", stats.get("repairXP")-newvalue);
			stats.put("herbalismXP", stats.get("herbalismXP")-newvalue);
			stats.put("acrobaticsXP", stats.get("acrobaticsXP")-newvalue);
			stats.put("swordsXP", stats.get("swordsXP")-newvalue);
			stats.put("archeryXP", stats.get("archeryXP")-newvalue);
			stats.put("unarmedXP", stats.get("unarmedXP")-newvalue);
			stats.put("excavationXP", stats.get("excavationXP")-newvalue);
			stats.put("axesXP", stats.get("axesXP")-newvalue);
			stats.put("sorceryXP", stats.get("sorceryXP")-newvalue);
		} else {
			stats.put(skillname.toLowerCase()+"XP", stats.get(skillname.toLowerCase()+"XP")-newvalue);
		}
		save();
	}
	public void removeSorceryXP(int newXP){
		removeXP("sorcery", newXP);
	}
	public void removeTamingXP(int newXP){
		removeXP("taming", newXP);
	}
	public void removeWoodCuttingXP(int newXP){
		removeXP("woodcutting", newXP);
	}
	public void removeMiningXP(int newXP){
		removeXP("mining", newXP);
	}
	public void removeRepairXP(int newXP){
		removeXP("repair", newXP);
	}
	public void removeUnarmedXP(int newXP){
		removeXP("unarmed", newXP);
	}
	public void removeHerbalismXP(int newXP){
		removeXP("herbalism", newXP);
	}
	public void removeExcavationXP(int newXP){
		removeXP("excavation", newXP);
	}
	public void removeArcheryXP(int newXP){
		removeXP("archery", newXP);
	}
	public void removeSwordsXP(int newXP){
		removeXP("swords", newXP);
	}
	public void removeAxesXP(int newXP){
		removeXP("axes", newXP);
	}
	public void removeAcrobaticsXP(int newXP){
		removeXP("acrobatics", newXP);
	}

	public boolean isInt(String string){
		try {
		    int x = Integer.parseInt(string);
		}
		catch(NumberFormatException nFE) {
		    return false;
		}
		return true;
	}
	public boolean isDouble(String string){
		try {
		    Double x = Double.valueOf(string);
		}
		catch(NumberFormatException nFE) {
		    return false;
		}
		return true;
	}
	public void acceptInvite(){
		party = invite;
		invite = "";
	}
	public void modifyInvite(String invitename){
		invite = invitename;
	}
	public String getInvite() { return invite; }
	
	public void modifyskill(int newvalue, String skillname){
		if(skillname.toLowerCase().equals("all")){
			stats.put("sorcery", newvalue);
			stats.put("taming", newvalue);
			stats.put("mining", newvalue);
			stats.put("woodcutting", newvalue);
			stats.put("repair", newvalue);
			stats.put("herbalism", newvalue);
			stats.put("acrobatics", newvalue);
			stats.put("swords", newvalue);
			stats.put("archery", newvalue);
			stats.put("unarmed", newvalue);
			stats.put("excavation", newvalue);
			stats.put("axes", newvalue);
			
			stats.put("tamingXP", 0);
			stats.put("sorceryXP", 0);
			stats.put("miningXP", 0);
			stats.put("woodcuttingXP", 0);
			stats.put("repairXP", 0);
			stats.put("herbalismXP", 0);
			stats.put("acrobaticsXP", 0);
			stats.put("swordsXP", 0);
			stats.put("archeryXP", 0);
			stats.put("unarmedXP", 0);
			stats.put("excavationXP", 0);
			stats.put("axesXP", 0);
		} else {
			stats.put(skillname, newvalue);
			stats.put(skillname+"XP", newvalue);
		}
		save();
	}
	public Integer getXpToLevel(String skillname){
		if(skillname.equals("sorcery")){
			return ((stats.get("sorcery") + 50) * LoadProperties.sorceryxpmodifier) * LoadProperties.globalxpmodifier;
		}
		if(skillname.equals("taming")){
			return ((stats.get("taming") + 50) * LoadProperties.tamingxpmodifier) * LoadProperties.globalxpmodifier;
		}
		if(skillname.equals("mining")){
			return ((stats.get("mining") + 50) * LoadProperties.miningxpmodifier) * LoadProperties.globalxpmodifier;
		}
		if(skillname.equals("woodcutting")){
			return ((stats.get("woodcutting") + 50) * LoadProperties.woodcuttingxpmodifier) * LoadProperties.globalxpmodifier;
		}
		if(skillname.equals("repair")){
			return ((stats.get("repair") + 50) * LoadProperties.repairxpmodifier) * LoadProperties.globalxpmodifier;
		}
		if(skillname.equals("herbalism")){
			return ((stats.get("herbalism") + 50) * LoadProperties.herbalismxpmodifier) * LoadProperties.globalxpmodifier;
		}
		if(skillname.equals("acrobatics")){
			return ((stats.get("acrobatics") + 50) * LoadProperties.acrobaticsxpmodifier) * LoadProperties.globalxpmodifier;
		}
		if(skillname.equals("swords")){
			return ((stats.get("swords") + 50) * LoadProperties.swordsxpmodifier) * LoadProperties.globalxpmodifier;
		}
		if(skillname.equals("archery")){
			return ((stats.get("archery") + 50) * LoadProperties.archeryxpmodifier) * LoadProperties.globalxpmodifier;
		}
		if(skillname.equals("unarmed")){
			return ((stats.get("unarmed") + 50) * LoadProperties.unarmedxpmodifier) * LoadProperties.globalxpmodifier;
		}
		if(skillname.equals("excavation")){
			return ((stats.get("excavation") + 50) * LoadProperties.excavationxpmodifier) * LoadProperties.globalxpmodifier;
		}
		if(skillname.equals("axes")){
			return ((stats.get("axes") + 50) * LoadProperties.axesxpmodifier) * LoadProperties.globalxpmodifier;
		} else {
			System.out.println("ELSE CONDITIONS MET");
			return 0;
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
    public boolean inParty() {
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
    		if(isDouble(getX()) && isDouble(getY()) && isDouble(getZ()))
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
