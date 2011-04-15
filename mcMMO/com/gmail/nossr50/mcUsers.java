package com.gmail.nossr50;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.plugin.Plugin;


public class mcUsers {
    private static volatile mcUsers instance;
    protected static final Logger log = Logger.getLogger("Minecraft");
    String location = "plugins/mcMMO/mcmmo.users";
    public static PlayerList players = new PlayerList();
    private Properties properties = new Properties();
    
    //To load
    public void load() throws IOException {
        properties.load(new FileInputStream(location));
    }
    //To save
    public void save() {
        try {
        properties.store(new FileOutputStream(location), null);
        }catch(IOException ex) {
        }
    }
    
    
    public void loadUsers(){
        File theDir = new File(location);
		if(!theDir.exists()){
			//properties = new PropertiesFile(location);
			FileWriter writer = null;
			try {
				writer = new FileWriter(location);
				writer.write("#Storage place for user information\r\n");
			} catch (Exception e) {
				log.log(Level.SEVERE, "Exception while creating " + location, e);
			} finally {
				try {
					if (writer != null) {
						writer.close();
					}
				} catch (IOException e) {
					log.log(Level.SEVERE, "Exception while closing writer for " + location, e);
				}
			}

		} else {
			//properties = new PropertiesFile(location);
			try {
				load();
			} catch (IOException e) {
				log.log(Level.SEVERE, "Exception while loading " + location, e);
			}
		}
    }

	//=====================================================================
	//Function:	addUser
	//Input:	Player player: The player to create a profile for
	//Output:	none
	//Use:		Loads the profile for the specified player
	//=====================================================================
    public static void addUser(Player player){
    	players.addPlayer(player);
    }

	//=====================================================================
	//Function:	removeUser
	//Input:	Player player: The player to stop following
	//Output:	none
	//Use:		Creates the player profile
	//=====================================================================
    public static void removeUser(String playername){
    	players.removePlayer(playername);
    }

	//=====================================================================
	//Function:	getProfile
	//Input:	Player player: The player to find the profile for
	//Output:	PlayerList.PlayerProfile: The profile
	//Use:		Gets the player profile
	//=====================================================================
    public static PlayerList.PlayerProfile getProfile(String playername){
    	return players.findProfile(playername);
    }
    
    public static mcUsers getInstance() {
		if (instance == null) {
			instance = new mcUsers();
		}
		return instance;
	}
    public static void getRow(){

    }
}
class PlayerList
{       
    protected static final Logger log = Logger.getLogger("Minecraft");
	ArrayList<PlayerProfile> players;
	
	//=====================================================================
	//Function:	PlayerList
	//Input:	Player player: The player to create a profile object for
	//Output:	none
	//Use:		Initializes the ArrayList
	//=====================================================================
	public PlayerList() { players = new ArrayList<PlayerProfile>(); }

	//=====================================================================
	//Function:	addPlayer
	//Input:	Player player: The player to add
	//Output:	None
	//Use:		Add a profile of the specified player
	//=====================================================================
	public void addPlayer(Player player)
	{
		players.add(new PlayerProfile(player));
	}

	//=====================================================================
	//Function:	removePlayer
	//Input:	Player player: The player to remove
	//Output:	None
	//Use:		Remove the profile of the specified player
	//=====================================================================
	public void removePlayer(String playername)
	{
		players.remove(findProfile(playername));
	}

	//=====================================================================
	//Function:	findProfile
	//Input:	Player player: The player to find's profile
	//Output:	PlayerProfile: The profile of the specified player
	//Use:		Get the profile for the specified player
	//=====================================================================
	public PlayerProfile findProfile(String playername)
	{
		for(PlayerProfile ply : players)
		{
			if(ply.isPlayer(playername))
				return ply;
		}
		return null;
	}
	
	class PlayerProfile
	{
	    protected final Logger log = Logger.getLogger("Minecraft");
		private String taming, tamingXP, playerName, miningXP, woodCuttingXP, woodcutting, repair, mining, party, myspawn, myspawnworld, unarmed, herbalism, excavation,
		archery, swords, axes, invite, acrobatics, repairXP, unarmedXP, herbalismXP, excavationXP, archeryXP, swordsXP, axesXP, acrobaticsXP;
		private boolean greenTerraMode, partyChatOnly = false, greenTerraInformed = true, berserkInformed = true, skullSplitterInformed = true, gigaDrillBreakerInformed = true, superBreakerInformed = true, serratedStrikesInformed = true, treeFellerInformed = true, dead, abilityuse = true, treeFellerMode, superBreakerMode, gigaDrillBreakerMode, serratedStrikesMode, hoePreparationMode, shovelPreparationMode, swordsPreparationMode, fistsPreparationMode, pickaxePreparationMode, axePreparationMode, skullSplitterMode, berserkMode;
		private long gigaDrillBreakerCooldown = 0, berserkCooldown = 0, superBreakerCooldown = 0, skullSplitterCooldown = 0, serratedStrikesCooldown = 0,
		greenTerraCooldown = 0, treeFellerCooldown = 0, recentlyHurt = 0, archeryShotATS = 0, berserkATS = 0, berserkDATS = 0, gigaDrillBreakerATS = 0, gigaDrillBreakerDATS = 0,
		respawnATS = 0, mySpawnATS = 0, greenTerraATS = 0, greenTerraDATS = 0, superBreakerATS = 0, superBreakerDATS = 0, serratedStrikesATS = 0, serratedStrikesDATS = 0, treeFellerATS = 0, treeFellerDATS = 0, 
		skullSplitterATS = 0, skullSplitterDATS = 0, hoePreparationATS = 0, axePreparationATS = 0, pickaxePreparationATS = 0, fistsPreparationATS = 0, shovelPreparationATS = 0, swordsPreparationATS = 0;
		private int berserkTicks = 0, bleedticks = 0, greenTerraTicks = 0, gigaDrillBreakerTicks = 0, superBreakerTicks = 0, serratedStrikesTicks = 0, skullSplitterTicks = 0, treeFellerTicks = 0;
		//ATS = (Time of) Activation Time Stamp
		//DATS = (Time of) Deactivation Time Stamp
		Player thisplayer;
		char defaultColor;

        String location = "plugins/mcMMO/mcmmo.users";
		
		
		//=====================================================================
		//Function:	PlayerProfile
		//Input:	Player player: The player to create a profile object for
		//Output:	none
		//Use:		Loads settings for the player or creates them if they don't
		//			exist.
		//=====================================================================
		public PlayerProfile(Player player)
		{
            //Declare things
			playerName = player.getName();
			thisplayer = player;
            party = new String();
            myspawn = new String();
            myspawnworld = new String();
            mining = new String();
            repair = new String();
            repairXP = new String();
            unarmed = new String();
            unarmedXP = new String();
            herbalism = new String();
            herbalismXP = new String();
            excavation = new String();
            excavationXP = new String();
            archery = new String();
            archeryXP = new String();
            swords = new String();
            swordsXP = new String();
            axes = new String();
            axesXP = new String();
            acrobatics = new String();
            acrobaticsXP = new String();
            taming = new String();
            tamingXP = new String();
            invite = new String();
            //mining = "0";
            woodCuttingXP = new String();
            //woodCuttingXP = "0";
            woodcutting = new String();
            //woodcutting = "0";
            miningXP = new String();
            //XP = "0";
            party = null;
            dead = false;
            treeFellerMode = false;
            //Try to load the player and if they aren't found, append them
            if(!load())
            	addPlayer();
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
        			if(character.length > 1)
        				mining = character[1];
        			//Myspawn
        			if(character.length > 2)
        				myspawn = character[2];
        			//Party
        			if(character.length > 3)
        				party = character[3];
        			//Mining XP
        			if(character.length > 4)
        				miningXP = character[4];
        			if(character.length > 5)
        				woodcutting = character[5];
        			if(character.length > 6)
        				woodCuttingXP = character[6];
        			if(character.length > 7)
        				repair = character[7];
        			if(character.length > 8)
        				unarmed = character[8];
        			if(character.length > 9)
        				herbalism = character[9];
        			if(character.length > 10)
        				excavation = character[10];
        			if(character.length > 11)
        				archery = character[11];
        			if(character.length > 12)
        				swords = character[12];
        			if(character.length > 13)
        				axes = character[13];
        			if(character.length > 14)
        				acrobatics = character[14];
        			if(character.length > 15)
        				repairXP = character[15];
        			if(character.length > 16)
        				unarmedXP = character[16];
        			if(character.length > 17)
        				herbalismXP = character[17];
        			if(character.length > 18)
        				excavationXP = character[18];
        			if(character.length > 19)
        				archeryXP = character[19];
        			if(character.length > 20)
        				swordsXP = character[20];
        			if(character.length > 21)
        				axesXP = character[21];
        			if(character.length > 22)
        				acrobaticsXP = character[22];
        			if(character.length > 23)
        				myspawnworld = character[23];
        			if(character.length > 24)
        				taming = character[24];
        			if(character.length > 25)
        				tamingXP = character[25];
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
		
        //=====================================================================
        // Function:    save
        // Input:       none
        // Output:      None
        // Use:         Writes current values of PlayerProfile to disk
		//				Call this function to save current values
        //=====================================================================
        public void save()
        {
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
            			writer.append(mining + ":");
            			writer.append(myspawn + ":");
            			writer.append(party+":");
            			writer.append(miningXP+":");
            			writer.append(woodcutting+":");
            			writer.append(woodCuttingXP+":");
            			writer.append(repair+":");
            			writer.append(unarmed+":");
            			writer.append(herbalism+":");
            			writer.append(excavation+":");
            			writer.append(archery+":");
            			writer.append(swords+":");
            			writer.append(axes+":");
            			writer.append(acrobatics+":");
            			writer.append(repairXP+":");
            			writer.append(unarmedXP+":");
            			writer.append(herbalismXP+":");
            			writer.append(excavationXP+":");
            			writer.append(archeryXP+":");
            			writer.append(swordsXP+":");
            			writer.append(axesXP+":");
            			writer.append(acrobaticsXP+":");
            			writer.append(myspawnworld+":");
            			writer.append(taming+":");
            			writer.append(tamingXP+":");
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
        public void addPlayer()
        {
            try {
            	//Open the file to write the player
            	FileWriter file = new FileWriter(location, true);
                BufferedWriter out = new BufferedWriter(file);
                
                //Add the player to the end
                out.append(playerName + ":");
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
                out.append(thisplayer.getWorld().getName());
                out.append(0+":"); //taming
                out.append(0+":"); //tamingXP
                //Add more in the same format as the line above
                
    			out.newLine();
    			out.close();
	        } catch (Exception e) {
                    log.log(Level.SEVERE, "Exception while writing to " + location + " (Are you sure you formatted it correctly?)", e);
	        }
        }

		//=====================================================================
		//Function:	isPlayer
		//Input:	None
		//Output:	Player: The player this profile belongs to
		//Use:		Finds if this profile belongs to a specified player
		//=====================================================================
		public boolean isPlayer(String player)
		{
			return player.equals(playerName);
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
		public Boolean hasCooldowns(){
			if((treeFellerCooldown + superBreakerCooldown) >= 1){
				return true;
			} else {
				return false;
			}
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
		}
		public void setGreenTerraCooldown(Long newvalue){
			greenTerraCooldown = newvalue;
		}
		public long getGreenTerraCooldown(){
			return greenTerraCooldown;
		}
		public void setGreenTerraTicks(Integer newvalue){greenTerraTicks = newvalue;}
		public int getGreenTerraTicks(){return greenTerraTicks;}
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
		}
		public void setBerserkCooldown(Long newvalue){
			berserkCooldown = newvalue;
		}
		public long getBerserkCooldown(){
			return berserkCooldown;
		}
		public void setBerserkTicks(Integer newvalue){berserkTicks = newvalue;}
		public int getBerserkTicks(){return berserkTicks;}
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
		}
		public void setSkullSplitterCooldown(Long newvalue){
			skullSplitterCooldown = newvalue;
		}
		public long getSkullSplitterCooldown(){
			return skullSplitterCooldown;
		}
		public void setSkullSplitterTicks(Integer newvalue){skullSplitterTicks = newvalue;}
		public int getSkullSplitterTicks(){return skullSplitterTicks;}
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
		}
		public void setSerratedStrikesCooldown(Long newvalue){
			serratedStrikesCooldown = newvalue;
		}
		public long getSerratedStrikesCooldown(){
			return serratedStrikesCooldown;
		}
		public void setSerratedStrikesTicks(Integer newvalue){serratedStrikesTicks = newvalue;}
		public int getSerratedStrikesTicks(){return serratedStrikesTicks;}
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
		}
		public void setGigaDrillBreakerCooldown(Long newvalue){
			gigaDrillBreakerCooldown = newvalue;
		}
		public long getGigaDrillBreakerCooldown(){
			return gigaDrillBreakerCooldown;
		}
		public void setGigaDrillBreakerTicks(Integer newvalue){gigaDrillBreakerTicks = newvalue;}
		public int getGigaDrillBreakerTicks(){return gigaDrillBreakerTicks;}
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
		}
		public void setTreeFellerCooldown(Long newvalue){
			treeFellerCooldown = newvalue;
		}
		public long getTreeFellerCooldown(){
			return treeFellerCooldown;
		}
		public void setTreeFellerTicks(Integer newvalue){treeFellerTicks = newvalue;}
		public int getTreeFellerTicks(){return treeFellerTicks;}
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
		}
		public void setSuperBreakerCooldown(Long newvalue){
			superBreakerCooldown = newvalue;
		}
		public long getSuperBreakerCooldown(){
			return superBreakerCooldown;
		}
		public void setSuperBreakerTicks(Integer newvalue){superBreakerTicks = newvalue;}
		public int getSuperBreakerTicks(){return superBreakerTicks;}
		
		public long getRecentlyHurt(){
			return recentlyHurt;
		}
		public void setRecentlyHurt(long newvalue){
			recentlyHurt = newvalue;
		}
		public void skillUpTaming(int newskill){
			int x = 0;
			if(taming != null){
			if(isInt(taming)){
			x = Integer.parseInt(taming);
			}else {
				taming = "0";
				x = Integer.parseInt(taming);
			}
			}
			x += newskill;
			taming = Integer.toString(x);
			save();
		}
		public void skillUpAxes(int newskill){
			int x = 0;
			if(axes != null){
			if(isInt(axes)){
			x = Integer.parseInt(axes);
			}else {
				axes = "0";
				x = Integer.parseInt(axes);
			}
			}
			x += newskill;
			axes = Integer.toString(x);
			save();
		}
		public void skillUpAcrobatics(int newskill){
			int x = 0;
			if(acrobatics != null){
			if(isInt(acrobatics)){
			x = Integer.parseInt(acrobatics);
			}else {
				acrobatics = "0";
				x = Integer.parseInt(acrobatics);
			}
			}
			x += newskill;
			acrobatics = Integer.toString(x);
			save();
		}
		public void skillUpSwords(int newskill){
			int x = 0;
			if(swords != null){
			if(isInt(swords)){
			x = Integer.parseInt(swords);
			}else {
				swords = "0";
				x = Integer.parseInt(swords);
			}
			}
			x += newskill;
			swords = Integer.toString(x);
			save();
		}
		public void skillUpArchery(int newskill){
			int x = 0;
			if(archery != null){
			if(isInt(archery)){
			x = Integer.parseInt(archery);
			}else {
				archery = "0";
				x = Integer.parseInt(archery);
			}
			}
			x += newskill;
			archery = Integer.toString(x);
			save();
		}
		public void skillUpRepair(int newskill){
			int x = 0;
			if(repair != null){
			if(isInt(repair)){
			x = Integer.parseInt(repair);
			}else {
				repair = "0";
				x = Integer.parseInt(repair);
			}
			}
			x += newskill;
			repair = Integer.toString(x);
			save();
		}
		public void skillUpMining(int newmining){
			int x = 0;
			if(mining != null){
			if(isInt(mining)){
			x = Integer.parseInt(mining);
			}else {
				mining = "0";
				x = Integer.parseInt(mining);
			}
			}
			x += newmining;
			mining = Integer.toString(x);
			save();
		}
		public void skillUpUnarmed(int newskill){
			int x = 0;
			if(unarmed != null){
			if(isInt(unarmed)){
			x = Integer.parseInt(unarmed);
			}else {
				unarmed = "0";
				x = Integer.parseInt(unarmed);
			}
			}
			x += newskill;
			unarmed = Integer.toString(x);
			save();
		}
		public void skillUpHerbalism(int newskill){
			int x = 0;
			if(herbalism != null){
			if(isInt(herbalism)){
			x = Integer.parseInt(herbalism);
			}else {
				herbalism = "0";
				x = Integer.parseInt(herbalism);
			}
			}
			x += newskill;
			herbalism = Integer.toString(x);
			save();
		}
		public void skillUpExcavation(int newskill){
			int x = 0;
			if(excavation != null){
			if(isInt(excavation)){
			x = Integer.parseInt(excavation);
			}else {
				excavation = "0";
				x = Integer.parseInt(excavation);
			}
			}
			x += newskill;
			excavation = Integer.toString(x);
			save();
		}
		public void skillUpWoodCutting(int newskill){
			int x = 0;
			if(woodcutting != null){
			if(isInt(woodcutting)){
			x = Integer.parseInt(woodcutting);
			}else {
				woodcutting = "0";
				x = Integer.parseInt(woodcutting);
			}
			}
			x += newskill;
			woodcutting = Integer.toString(x);
			save();
		}
		public String getTaming(){
			if(taming != null && !taming.equals("") && !taming.equals("null")){
			return taming;
			} else {
				return "0";
			}
		}
		public String getRepair(){
			if(repair != null && !repair.equals("") && !repair.equals("null")){
			return repair;
			} else {
				return "0";
			}
		}
		public String getMining(){
			if(mining != null && !mining.equals("") && !mining.equals("null")){
				return mining;
				} else {
					return "0";
				}
		}
		public String getUnarmed(){
			if(unarmed != null && !unarmed.equals("") && !unarmed.equals("null")){
				return unarmed;
				} else {
					return "0";
				}
		}
		public String getHerbalism(){
			if(herbalism != null && !herbalism.equals("") && !herbalism.equals("null")){
				return herbalism;
				} else {
					return "0";
				}
		}
		public String getExcavation(){
			if(excavation != null && !excavation.equals("") && !excavation.equals("null")){
				return excavation;
				} else {
					return "0";
				}
		}
		public String getArchery(){
			if(archery != null && !archery.equals("") && !archery.equals("null")){
				return archery;
				} else {
					return "0";
				}
		}
		public String getSwords(){
			if(swords != null && !swords.equals("") && !swords.equals("null")){
				return swords;
				} else {
					return "0";
				}
		}
		public String getAxes(){
			if(axes != null && !axes.equals("") && !axes.equals("null")){
				return axes;
				} else {
					return "0";
				}
		}
		public String getAcrobatics(){
			if(acrobatics != null && !acrobatics.equals("") && !acrobatics.equals("null")){
				return acrobatics;
				} else {
					return "0";
				}
		}
		public int getTamingInt(){
			if(isInt(taming)){
				int x = Integer.parseInt(taming);
				return x;
			} else{
				return 0;
			}
		}
		public int getMiningInt(){
			if(isInt(mining)){
				int x = Integer.parseInt(mining);
				return x;
			} else{
				return 0;
			}
		}
		public int getUnarmedInt(){
			if(isInt(unarmed)){
				int x = Integer.parseInt(unarmed);
				return x;
			} else{
				return 0;
			}
		}
		public int getArcheryInt(){
			if(isInt(archery)){
				int x = Integer.parseInt(archery);
				return x;
			} else{
				return 0;
			}
		}
		public int getSwordsInt(){
			if(isInt(swords)){
				int x = Integer.parseInt(swords);
				return x;
			} else{
				return 0;
			}
		}
		public int getAxesInt(){
			if(isInt(axes)){
				int x = Integer.parseInt(axes);
				return x;
			} else{
				return 0;
			}
		}
		public int getAcrobaticsInt(){
			if(isInt(acrobatics)){
				int x = Integer.parseInt(acrobatics);
				return x;
			} else{
				return 0;
			}
		}
		public int getHerbalismInt(){
			if(isInt(herbalism)){
				int x = Integer.parseInt(herbalism);
				return x;
			} else{
				return 0;
			}
		}
		public int getExcavationInt(){
			if(isInt(excavation)){
				int x = Integer.parseInt(excavation);
				return x;
			} else{
				return 0;
			}
		}
		public int getRepairInt(){
			if(isInt(repair)){
				int x = Integer.parseInt(repair);
				return x;
			} else{
				return 0;
			}
		}
		public int getWoodCuttingInt(){
			if(isInt(woodcutting)){
				int x = Integer.parseInt(woodcutting);
				return x;
			} else{
				return 0;
			}
		}
		public String getWoodCutting(){
			if(woodcutting != null && !woodcutting.equals("") && !woodcutting.equals("null")){
				return woodcutting;
				} else {
					return "0";
				}
		}
		/*
		 * EXPERIENCE STUFF
		 */
		public void clearTamingXP(){
			tamingXP = "0";
		}
		public void clearRepairXP(){
			repairXP = "0";
		}
		public void clearUnarmedXP(){
			unarmedXP = "0";
		}
		public void clearHerbalismXP(){
			herbalismXP = "0";
		}
		public void clearExcavationXP(){
			excavationXP = "0";
		}
		public void clearArcheryXP(){
			archeryXP = "0";
		}
		public void clearSwordsXP(){
			swordsXP = "0";
		}
		public void clearAxesXP(){
			axesXP = "0";
		}
		public void clearAcrobaticsXP(){
			acrobaticsXP = "0";
		}
		public void addTamingXP(int newXP)
		{
			int x = 0;
			if(isInt(tamingXP)){
			x = Integer.parseInt(tamingXP);
			}
			x += newXP;
			acrobaticsXP = String.valueOf(x);
			save();
		}
		public void addAcrobaticsXP(int newXP)
		{
			int x = 0;
			if(isInt(acrobaticsXP)){
			x = Integer.parseInt(acrobaticsXP);
			}
			x += newXP;
			acrobaticsXP = String.valueOf(x);
			save();
		}
		public void addAxesXP(int newXP)
		{
			int x = 0;
			if(isInt(axesXP)){
			x = Integer.parseInt(axesXP);
			}
			x += newXP;
			axesXP = String.valueOf(x);
			save();
		}
		public void addSwordsXP(int newXP)
		{
			int x = 0;
			if(isInt(swordsXP)){
			x = Integer.parseInt(swordsXP);
			}
			x += newXP;
			swordsXP = String.valueOf(x);
			save();
		}
		public void addArcheryXP(int newXP)
		{
			int x = 0;
			if(isInt(archeryXP)){
			x = Integer.parseInt(archeryXP);
			}
			x += newXP;
			archeryXP = String.valueOf(x);
			save();
		}
		public void addExcavationXP(int newXP)
		{
			int x = 0;
			if(isInt(excavationXP)){
			x = Integer.parseInt(excavationXP);
			}
			x += newXP;
			excavationXP = String.valueOf(x);
			save();
		}
		public void addHerbalismXP(int newXP)
		{
			int x = 0;
			if(isInt(herbalismXP)){
			x = Integer.parseInt(herbalismXP);
			}
			x += newXP;
			herbalismXP = String.valueOf(x);
			save();
		}
		public void addRepairXP(int newXP)
		{
			int x = 0;
			if(isInt(repairXP)){
			x = Integer.parseInt(repairXP);
			}
			x += newXP;
			repairXP = String.valueOf(x);
			save();
		}
		public void addUnarmedXP(int newXP)
		{
			int x = 0;
			if(isInt(unarmedXP)){
			x = Integer.parseInt(unarmedXP);
			}
			x += newXP;
			unarmedXP = String.valueOf(x);
			save();
		}
		public void addWoodcuttingXP(int newXP)
		{
			int x = 0;
			if(isInt(woodCuttingXP)){
			x = Integer.parseInt(woodCuttingXP);
			}
			x += newXP;
			woodCuttingXP = String.valueOf(x);
			save();
		}
		public void removeTamingXP(int newXP){
			int x = 0;
			if(isInt(tamingXP)){
			x = Integer.parseInt(tamingXP);
			}
			x -= newXP;
			tamingXP = String.valueOf(x);
			save();
		}
		public void removeWoodCuttingXP(int newXP){
			int x = 0;
			if(isInt(woodCuttingXP)){
			x = Integer.parseInt(woodCuttingXP);
			}
			x -= newXP;
			woodCuttingXP = String.valueOf(x);
			save();
		}
		public void addMiningXP(int newXP)
		{
			int x = 0;
			if(isInt(miningXP)){
			x = Integer.parseInt(miningXP);
			} else {
				x = 0;
			}
			x += newXP;
			miningXP = String.valueOf(x);
			save();
		}
		public void removeMiningXP(int newXP){
			int x = 0;
			if(isInt(miningXP)){
			x = Integer.parseInt(miningXP);
			}
			x -= newXP;
			miningXP = String.valueOf(x);
			save();
		}
		public void removeRepairXP(int newXP){
			int x = 0;
			if(isInt(repairXP)){
			x = Integer.parseInt(repairXP);
			}
			x -= newXP;
			repairXP = String.valueOf(x);
			save();
		}
		public void removeUnarmedXP(int newXP){
			int x = 0;
			if(isInt(unarmedXP)){
			x = Integer.parseInt(unarmedXP);
			}
			x -= newXP;
			unarmedXP = String.valueOf(x);
			save();
		}
		public void removeHerbalismXP(int newXP){
			int x = 0;
			if(isInt(herbalismXP)){
			x = Integer.parseInt(herbalismXP);
			}
			x -= newXP;
			herbalismXP = String.valueOf(x);
			save();
		}
		public void removeExcavationXP(int newXP){
			int x = 0;
			if(isInt(excavationXP)){
			x = Integer.parseInt(excavationXP);
			}
			x -= newXP;
			excavationXP = String.valueOf(x);
			save();
		}
		public void removeArcheryXP(int newXP){
			int x = 0;
			if(isInt(archeryXP)){
			x = Integer.parseInt(archeryXP);
			}
			x -= newXP;
			archeryXP = String.valueOf(x);
			save();
		}
		public void removeSwordsXP(int newXP){
			int x = 0;
			if(isInt(swordsXP)){
			x = Integer.parseInt(swordsXP);
			}
			x -= newXP;
			swordsXP = String.valueOf(x);
			save();
		}
		public void removeAxesXP(int newXP){
			int x = 0;
			if(isInt(axesXP)){
			x = Integer.parseInt(axesXP);
			}
			x -= newXP;
			axesXP = String.valueOf(x);
			save();
		}
		public void removeAcrobaticsXP(int newXP){
			int x = 0;
			if(isInt(acrobaticsXP)){
			x = Integer.parseInt(acrobaticsXP);
			}
			x -= newXP;
			acrobaticsXP = String.valueOf(x);
			save();
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
			save();
		}
		public void modifyInvite(String invitename){
			invite = invitename;
		}
		//Returns player XP
		public String getTamingXP(){
			if(tamingXP != null && !tamingXP.equals("") && !tamingXP.equals("null")){
				return tamingXP;
				} else {
					return "0";
				}
		}
		public String getMiningXP(){
			if(miningXP != null && !miningXP.equals("") && !miningXP.equals("null")){
				return miningXP;
				} else {
					return "0";
				}
		}
		public String getInvite() { return invite; }
		public String getWoodCuttingXP(){
			if(woodCuttingXP != null && !woodCuttingXP.equals("") && !woodCuttingXP.equals("null")){
				return woodCuttingXP;
				} else {
					return "0";
				}
		}
		public String getRepairXP(){
			if(repairXP != null && !repairXP.equals("") && !repairXP.equals("null")){
				return repairXP;
				} else {
					return "0";
				}
		}
		public String getHerbalismXP(){
			if(herbalismXP != null && !herbalismXP.equals("") && !herbalismXP.equals("null")){
				return herbalismXP;
				} else {
					return "0";
				}
		}
		public String getExcavationXP(){
			if(excavationXP != null && !excavationXP.equals("") && !excavationXP.equals("null")){
				return excavationXP;
				} else {
					return "0";
				}
		}
		public String getArcheryXP(){
			if(archeryXP != null && !archeryXP.equals("") && !archeryXP.equals("null")){
				return archeryXP;
				} else {
					return "0";
				}
		}
		public String getSwordsXP(){
			if(swordsXP != null && !swordsXP.equals("") && !swordsXP.equals("null")){
				return swordsXP;
				} else {
					return "0";
				}
		}
		public String getAxesXP(){
			if(axesXP != null && !axesXP.equals("") && !axesXP.equals("null")){
				return axesXP;
				} else {
					return "0";
				}
		}
		public String getAcrobaticsXP(){
			if(acrobaticsXP != null && !acrobaticsXP.equals("") && !acrobaticsXP.equals("null")){
				return acrobaticsXP;
				} else {
					return "0";
				}
		}
		public String getUnarmedXP(){
			if(unarmedXP != null && !unarmedXP.equals("") && !unarmedXP.equals("null")){
				return unarmedXP;
				} else {
					return "0";
				}
		}
		public int getTamingXPInt() {
			if(isInt(tamingXP)){
			return Integer.parseInt(tamingXP);
			} else {
				tamingXP = "0";
				save();
				return 0;
			}
		}
		public int getWoodCuttingXPInt() {
			if(isInt(woodCuttingXP)){
			return Integer.parseInt(woodCuttingXP);
			} else {
				woodCuttingXP = "0";
				save();
				return 0;
			}
		}
		public int getRepairXPInt() {
			if(isInt(repairXP)){
			return Integer.parseInt(repairXP);
			} else {
				repairXP = "0";
				save();
				return 0;
			}
		}
		public int getUnarmedXPInt() {
			if(isInt(unarmedXP)){
			return Integer.parseInt(unarmedXP);
			} else {
				unarmedXP = "0";
				save();
				return 0;
			}
		}
		public int getHerbalismXPInt() {
			if(isInt(herbalismXP)){
			return Integer.parseInt(herbalismXP);
			} else {
				herbalismXP = "0";
				save();
				return 0;
			}
		}
		public int getExcavationXPInt() {
			if(isInt(excavationXP)){
			return Integer.parseInt(excavationXP);
			} else {
				excavationXP = "0";
				save();
				return 0;
			}
		}
		public int getArcheryXPInt() {
			if(isInt(archeryXP)){
			return Integer.parseInt(archeryXP);
			} else {
				archeryXP = "0";
				save();
				return 0;
			}
		}
		public int getSwordsXPInt() {
			if(isInt(swordsXP)){
			return Integer.parseInt(swordsXP);
			} else {
				swordsXP = "0";
				save();
				return 0;
			}
		}
		public int getAxesXPInt() {
			if(isInt(axesXP)){
			return Integer.parseInt(axesXP);
			} else {
				axesXP = "0";
				save();
				return 0;
			}
		}
		public int getAcrobaticsXPInt() {
			if(isInt(acrobaticsXP)){
			return Integer.parseInt(acrobaticsXP);
			} else {
				acrobaticsXP = "0";
				save();
				return 0;
			}
		}
		public void addXpToSkill(int newvalue, String skillname){
			if(!isInt(tamingXP))
				tamingXP = String.valueOf(0);
			if(!isInt(miningXP))
				miningXP = String.valueOf(0);
			if(!isInt(woodCuttingXP))
				woodCuttingXP = String.valueOf(0);
			if(!isInt(repairXP))
				repairXP = String.valueOf(0);
			if(!isInt(herbalismXP))
				herbalismXP = String.valueOf(0);
			if(!isInt(acrobaticsXP))
				acrobaticsXP = String.valueOf(0);
			if(!isInt(swordsXP))
				swordsXP = String.valueOf(0);
			if(!isInt(archeryXP))
				archeryXP = String.valueOf(0);
			if(!isInt(unarmedXP))
				unarmedXP = String.valueOf(0);
			if(!isInt(excavationXP))
				excavationXP = String.valueOf(0);
			if(!isInt(axesXP))
				axesXP = String.valueOf(0);
			
			if(skillname.toLowerCase().equals("taming")){
				tamingXP = String.valueOf(Integer.valueOf(tamingXP)+newvalue);
			}
			if(skillname.toLowerCase().equals("mining")){
				miningXP = String.valueOf(Integer.valueOf(miningXP)+newvalue);
			}
			if(skillname.toLowerCase().equals("woodcutting")){
				woodCuttingXP = String.valueOf(Integer.valueOf(woodCuttingXP)+newvalue);
			}
			if(skillname.toLowerCase().equals("repair")){
				repairXP = String.valueOf(Integer.valueOf(repairXP)+newvalue);
			}
			if(skillname.toLowerCase().equals("herbalism")){
				herbalismXP = String.valueOf(Integer.valueOf(herbalismXP)+newvalue);
			}
			if(skillname.toLowerCase().equals("acrobatics")){
				acrobaticsXP = String.valueOf(Integer.valueOf(acrobaticsXP)+newvalue);
			}
			if(skillname.toLowerCase().equals("swords")){
				swordsXP = String.valueOf(Integer.valueOf(swordsXP)+newvalue);
			}
			if(skillname.toLowerCase().equals("archery")){
				archeryXP = String.valueOf(Integer.valueOf(archeryXP)+newvalue);
			}
			if(skillname.toLowerCase().equals("unarmed")){
				unarmedXP = String.valueOf(Integer.valueOf(unarmedXP)+newvalue);
			}
			if(skillname.toLowerCase().equals("excavation")){
				excavationXP = String.valueOf(Integer.valueOf(excavationXP)+newvalue);
			}
			if(skillname.toLowerCase().equals("axes")){
				axesXP = String.valueOf(Integer.valueOf(axesXP)+newvalue);
			}
			if(skillname.toLowerCase().equals("all")){
				tamingXP = String.valueOf(Integer.valueOf(tamingXP)+newvalue);
				miningXP = String.valueOf(Integer.valueOf(miningXP)+newvalue);
				woodCuttingXP = String.valueOf(Integer.valueOf(woodCuttingXP)+newvalue);
				repairXP = String.valueOf(Integer.valueOf(repairXP)+newvalue);
				herbalismXP = String.valueOf(Integer.valueOf(herbalismXP)+newvalue);
				acrobaticsXP = String.valueOf(Integer.valueOf(acrobaticsXP)+newvalue);
				swordsXP = String.valueOf(Integer.valueOf(swordsXP)+newvalue);
				archeryXP = String.valueOf(Integer.valueOf(archeryXP)+newvalue);
				unarmedXP = String.valueOf(Integer.valueOf(unarmedXP)+newvalue);
				excavationXP = String.valueOf(Integer.valueOf(excavationXP)+newvalue);
				axesXP = String.valueOf(Integer.valueOf(axesXP)+newvalue);
			}
			save();
			mcSkills.XpCheck(thisplayer);
		}
		public void modifyskill(int newvalue, String skillname){
			if(skillname.toLowerCase().equals("taming")){
				 taming = String.valueOf(newvalue);
			}
			if(skillname.toLowerCase().equals("mining")){
				 mining = String.valueOf(newvalue);
			}
			if(skillname.toLowerCase().equals("woodcutting")){
				 woodcutting = String.valueOf(newvalue);
			}
			if(skillname.toLowerCase().equals("repair")){
				 repair = String.valueOf(newvalue);
			}
			if(skillname.toLowerCase().equals("herbalism")){
				 herbalism = String.valueOf(newvalue);
			}
			if(skillname.toLowerCase().equals("acrobatics")){
				 acrobatics = String.valueOf(newvalue);
			}
			if(skillname.toLowerCase().equals("swords")){
				 swords = String.valueOf(newvalue);
			}
			if(skillname.toLowerCase().equals("archery")){
				 archery = String.valueOf(newvalue);
			}
			if(skillname.toLowerCase().equals("unarmed")){
				 unarmed = String.valueOf(newvalue);
			}
			if(skillname.toLowerCase().equals("excavation")){
				 excavation = String.valueOf(newvalue);
			}
			if(skillname.toLowerCase().equals("axes")){
				axes = String.valueOf(newvalue);
			}
			if(skillname.toLowerCase().equals("all")){
				taming = String.valueOf(newvalue);
				mining = String.valueOf(newvalue);
				woodcutting = String.valueOf(newvalue);
				repair = String.valueOf(newvalue);
				herbalism = String.valueOf(newvalue);
				acrobatics = String.valueOf(newvalue);
				swords = String.valueOf(newvalue);
				archery = String.valueOf(newvalue);
				unarmed = String.valueOf(newvalue);
				excavation = String.valueOf(newvalue);
				axes = String.valueOf(newvalue);
			}
			save();
		}
		public Integer getXpToLevel(String skillname){
			if(skillname.equals("taming")){
				return ((getTamingInt() + 50) * mcLoadProperties.tamingxpmodifier) * mcLoadProperties.globalxpmodifier;
			}
			if(skillname.equals("mining")){
				return ((getMiningInt() + 50) * mcLoadProperties.miningxpmodifier) * mcLoadProperties.globalxpmodifier;
			}
			if(skillname.equals("woodcutting")){
				return ((getWoodCuttingInt() + 50) * mcLoadProperties.woodcuttingxpmodifier) * mcLoadProperties.globalxpmodifier;
			}
			if(skillname.equals("repair")){
				return ((getRepairInt() + 50) * mcLoadProperties.repairxpmodifier) * mcLoadProperties.globalxpmodifier;
			}
			if(skillname.equals("herbalism")){
				return ((getHerbalismInt() + 50) * mcLoadProperties.herbalismxpmodifier) * mcLoadProperties.globalxpmodifier;
			}
			if(skillname.equals("acrobatics")){
				return ((getAcrobaticsInt() + 50) * mcLoadProperties.acrobaticsxpmodifier) * mcLoadProperties.globalxpmodifier;
			}
			if(skillname.equals("swords")){
				return ((getSwordsInt() + 50) * mcLoadProperties.swordsxpmodifier) * mcLoadProperties.globalxpmodifier;
			}
			if(skillname.equals("archery")){
				return ((getArcheryInt() + 50) * mcLoadProperties.archeryxpmodifier) * mcLoadProperties.globalxpmodifier;
			}
			if(skillname.equals("unarmed")){
				return ((getUnarmedInt() + 50) * mcLoadProperties.unarmedxpmodifier) * mcLoadProperties.globalxpmodifier;
			}
			if(skillname.equals("excavation")){
				return ((getExcavationInt() + 50) * mcLoadProperties.excavationxpmodifier) * mcLoadProperties.globalxpmodifier;
			}
			if(skillname.equals("axes")){
				return ((getAxesInt() + 50) * mcLoadProperties.axesxpmodifier) * mcLoadProperties.globalxpmodifier;
			} else {
				return 0;
			}
		}
		public int getMiningXPInt() {
			if(isInt(miningXP)){
			return Integer.parseInt(miningXP);
			} else {
				miningXP = "0";
				save();
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
        	String[] split = myspawn.split(",");
        	String x = split[0];
        	return x;
        }
        public String getY(){
        	String[] split = myspawn.split(",");
        	String y = split[1];
        	return y;
        }
        public String getZ(){
        	String[] split = myspawn.split(",");
        	String z = split[2];
        	return z;
        }
        public void setDead(boolean x){
        	dead = x;
        	save();
        }
        public boolean isDead(){
        	return dead;
        }
        public Location getMySpawn(Player player){
        	Location loc = player.getWorld().getSpawnLocation();
        	if(isDouble(getX()) && isDouble(getY()) && isDouble(getX())){
        		loc.setX(Double.parseDouble(mcUsers.getProfile(player.getName()).getX()));
        		loc.setY(Double.parseDouble(mcUsers.getProfile(player.getName()).getY()));
        		loc.setZ(Double.parseDouble(mcUsers.getProfile(player.getName()).getZ()));
        	} else {
        		return null;
        	}
        	loc.setYaw(0);
        	loc.setPitch(0);
        	return loc;
        }
	}	
}



