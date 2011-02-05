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
                writer.write("#username:nickname:suffix:tag:ignore,list,names:alias,commands,here\r\n");
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
		private String playerName, gather, party, myspawn;
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
            gather = new String();
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
            		
        			//Get gather
        			if(character.length > 1)
        				gather = character[1];
        			if(character.length > 2)
        				myspawn = character[2];
        			if(character.length > 3)
        				party = character[3];
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
            			writer.append(gather + ":");
            			writer.append(myspawn + ":");
            			writer.append(party+":");
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
                out.append(gather + ":");
                out.append(myspawn+":");
                out.append(party+":");
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
		
		public void addgather(int newgather)
		{
			int x = 0;
			if(isInt(gather)){
			x = Integer.parseInt(gather);
			}
			x += newgather;
			gather = String.valueOf(x);
			save();
		}

		public boolean isInt(String string){
			try {
			    int x = Integer.parseInt(gather);
			    return true;
			}
			catch(NumberFormatException nFE) {
			    return false;
			}
		}
		//Returns player gather
		public String getgather() { return gather; }
                
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
            		loc.setX(Double.parseDouble(mcUsers.getProfile(player).getX()));
            		loc.setY(Double.parseDouble(mcUsers.getProfile(player).getY()));
            		loc.setZ(Double.parseDouble(mcUsers.getProfile(player).getZ()));
            		loc.setYaw(0);
            		loc.setPitch(0);
            		return loc;
                }
	}
	
}



