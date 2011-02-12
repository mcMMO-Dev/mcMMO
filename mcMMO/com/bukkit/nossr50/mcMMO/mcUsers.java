package com.bukkit.nossr50.mcMMO;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.entity.*;

public class mcUsers {
    private static volatile mcUsers instance;
    protected static final Logger log = Logger.getLogger("Minecraft");
    String location = "mcmmo.users";
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
    public static void removeUser(Player player){
    	players.removePlayer(player);
    }

	//=====================================================================
	//Function:	getProfile
	//Input:	Player player: The player to find the profile for
	//Output:	PlayerList.PlayerProfile: The profile
	//Use:		Gets the player profile
	//=====================================================================
    public static PlayerList.PlayerProfile getProfile(Player player){
    	return players.findProfile(player);
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
	public void removePlayer(Player player)
	{
		players.remove(findProfile(player));
	}

	//=====================================================================
	//Function:	findProfile
	//Input:	Player player: The player to find's profile
	//Output:	PlayerProfile: The profile of the specified player
	//Use:		Get the profile for the specified player
	//=====================================================================
	public PlayerProfile findProfile(Player player)
	{
		for(PlayerProfile ply : players)
		{
			if(ply.isPlayer(player))
				return ply;
		}
		return null;
	}
	
	class PlayerProfile
	{
	    protected final Logger log = Logger.getLogger("Minecraft");
		private String playerName, gather, wgather, woodcutting, repair, mining, party, myspawn, unarmed, herbalism, excavation,
		archery, swords, axes, acrobatics;
		private boolean dead;
		char defaultColor;

        String location = "mcmmo.users";
		
		
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
            party = new String();
            myspawn = new String();
            mining = new String();
            repair = new String();
            unarmed = new String();
            herbalism = new String();
            excavation = new String();
            archery = new String();
            swords = new String();
            axes = new String();
            acrobatics = new String();
            //mining = "0";
            wgather = new String();
            //wgather = "0";
            woodcutting = new String();
            //woodcutting = "0";
            gather = new String();
            //gather = "0";
            party = null;
            dead = false;
            
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
        			//Mining Gather
        			if(character.length > 4)
        				gather = character[4];
        			if(character.length > 5)
        				woodcutting = character[5];
        			if(character.length > 6)
        				wgather = character[6];
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
            			writer.append(gather+":");
            			writer.append(woodcutting+":");
            			writer.append(wgather+":");
            			writer.append(repair+":");
            			writer.append(unarmed+":");
            			writer.append(herbalism+":");
            			writer.append(excavation+":");
            			writer.append(archery+":");
            			writer.append(swords+":");
            			writer.append(axes+":");
            			writer.append(acrobatics+":");
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
                out.append(0+":"); //gather
                out.append(0+":"); //woodcutting
                out.append(0+":"); //wgather
                out.append(0+":"); //repair
                out.append(0+":"); //unarmed
                out.append(0+":"); //herbalism
                out.append(0+":"); //excavation
                out.append(0+":"); //archery
                out.append(0+":"); //swords
                out.append(0+":"); //axes
                out.append(0+":"); //acrobatics
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
		public boolean isPlayer(Player player)
		{
			return player.getName().equals(playerName);
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
		public void skillUpWoodcutting(int newskill){
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
		public String getRepair(){
			return repair;
		}
		public String getMining(){
			return mining;
		}
		public String getUnarmed(){
			return unarmed;
		}
		public String getHerbalism(){
			return herbalism;
		}
		public String getExcavation(){
			return excavation;
		}
		public String getArchery(){
			return archery;
		}
		public String getSwords(){
			return swords;
		}
		public String getAxes(){
			return axes;
		}
		public String getAcrobatics(){
			return acrobatics;
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
		public int getWoodCuttingint(){
			if(isInt(woodcutting)){
				int x = Integer.parseInt(woodcutting);
				return x;
			} else{
				return 0;
			}
		}
		public String getWoodCutting(){
			return woodcutting;
		}
		
		public void addwgather(int newgather)
		{
			int x = 0;
			if(isInt(wgather)){
			x = Integer.parseInt(wgather);
			}
			x += newgather;
			wgather = String.valueOf(x);
			save();
		}
		public void removewgather(int newgather){
			int x = 0;
			if(isInt(wgather)){
			x = Integer.parseInt(wgather);
			}
			x -= newgather;
			wgather = String.valueOf(x);
			save();
		}
		public void addgather(int newgather)
		{
			int x = 0;
			if(isInt(gather)){
			x = Integer.parseInt(gather);
			} else {
				x = 0;
			}
			x += newgather;
			gather = String.valueOf(x);
			save();
		}
		public void removegather(int newgather){
			int x = 0;
			if(isInt(gather)){
			x = Integer.parseInt(gather);
			}
			x -= newgather;
			gather = String.valueOf(x);
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
		//Returns player gather
		public String getgather() { return gather; }
		public String getwgather() { return wgather; }
		
		public int getwgatheramt() {
			if(isInt(wgather)){
			return Integer.parseInt(getwgather());
			} else {
				wgather = "0";
				save();
				return 0;
			}
		}
		public int getgatheramt() {
			if(isInt(gather)){
			return Integer.parseInt(getgather());
			} else {
				gather = "0";
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
                    if(party != null){
                        return true;
                    } else {
                        return false;
                    }
                }
                //Save a users spawn location
                public void setMySpawn(double x, double y, double z){
            		myspawn = x+","+y+","+z;
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
                	Location loc = player.getLocation();
                	if(isDouble(getX()) && isDouble(getY()) && isDouble(getX())){
            		loc.setX(Double.parseDouble(mcUsers.getProfile(player).getX()));
            		loc.setY(Double.parseDouble(mcUsers.getProfile(player).getY()));
            		loc.setZ(Double.parseDouble(mcUsers.getProfile(player).getZ()));
                	} else {
                		return null;
                	}
            		loc.setYaw(0);
            		loc.setPitch(0);
            		return loc;
                }
	}
	
}



