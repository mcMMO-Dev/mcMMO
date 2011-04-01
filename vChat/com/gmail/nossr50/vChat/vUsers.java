package com.gmail.nossr50.vChat;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.*;

public class vUsers {
    private static volatile vUsers instance;
    protected static final Logger log = Logger.getLogger("Minecraft");
    String location = "vChat.users";
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
    
    public static vUsers getInstance() {
		if (instance == null) {
			instance = new vUsers();
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
	
	//=====================================================================
	//Class:	PlayerProfile
	//Use:		Encapsulates all commands for player options
	//Author:	cerevisiae
	//=====================================================================
	class PlayerProfile
	{
	    protected final Logger log = Logger.getLogger("Minecraft");
		private String playerName,
					   lastMessage,
					   nickName,
					   tag,
					   suffix,
					   prefix,
                        party;
		
		private boolean dead,
                        silent;
		
		char defaultColor;

        String location = "vChat.users";
		
		private ArrayList<String> ignoreList;
		//private commandList aliasList;
		
	    static final int EXIT_FAIL		= 0,
			 			 EXIT_SUCCESS	= 1,
			 			 EXIT_CONTINUE	= 2;

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
            tag = new String();
            nickName = new String();
            suffix = new String();
            prefix = new String();
            party = new String();
            party = null;
            defaultColor = 'f';
			ignoreList = new ArrayList<String>();
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
            		
        			//Get the tag
        			if(character.length > 1)
        				tag = character[1];
        			//Get the nickname
        			if(character.length > 2)
        				nickName = character[2];
        			//Get the suffix
        			if(character.length > 3)
        				suffix = character[3];
        			//Get the color
        			if(character.length > 4)
        				defaultColor = character[4].charAt(0);
        			//Ignore previously ignored players
        			if(character.length > 5)
        			{
        				String[] ignores = character[5].split(",");
        				if(ignores.length > 0)
        				{
        					for(String ignore : ignores)
        						ignoreList.add(ignore);
        				}
        			}
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
            			writer.append(tag + ":");
            			writer.append(nickName + ":");
            			writer.append(suffix + ":");
            			writer.append(defaultColor + ":");
            			writer.append(prefix + ":");
                                           			
            			int i = 0;
            			for(String ignore : ignoreList)
            			{
            				writer.append(ignore);
            				if(i < ignoreList.size() - 1)
            					writer.append(",");
            			}
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
                out.append("" + ":");
                out.append(nickName + ":");
                out.append(suffix + ":");
                out.append("f" + ":");
                out.append("f" + ":");
                
    			
    			int i = 0;
    			for(String ignore : ignoreList)
    			{
    				out.append(ignore);
    				if(i < ignoreList.size() - 1)
    					out.append(",");
    			}
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

		//=====================================================================
		//Function:	isIgnored
		//Input:	Player player: Checks if a player is ignored
		//Output:	boolean: If they're ignored
		//Use:		Finds if the specified player is in the ignore list
		//=====================================================================
		public boolean isIgnored(Player player){
			return ignoreList.contains(player.getName());
		}

		//=====================================================================
		//Function:	addIgnore
		//Input:	Player name: The player to ignore
		//Output:	boolean: If the player was successfully ignored
		//Use:		Ignores a player.
		//=====================================================================
		public boolean addIgnore(Player name)
		{
			if(!ignoreList.contains(name))
			{
				ignoreList.add(name.getName());
				save();
				return true;
			}
			return false;
		}

		//=====================================================================
		//Function:	removeIgnore
		//Input:	Player name: The player to unignore
		//Output:	boolean: If the player was successfully unignored
		//Use:		Stops ignoring a player.
		//=====================================================================
		public boolean removeIgnore(Player name)
		{
			if(ignoreList.contains(name.getName()))
			{
				ignoreList.remove(name.getName());
				save();
				return true;
			}
			return false;
		}

		//=====================================================================
		//Function:	removeIgnore
		//Input:	Player name: The player to unignore
		//Output:	boolean: If the player was successfully unignored
		//Use:		Stops ignoring a player.
		//=====================================================================
		public String[] listIgnore()
		{
			return ignoreList.toArray(new String[ignoreList.size()]);
		}

		//=====================================================================
		//Function:	setTag
		//Input:	String newTag: The tag to set for the player
		//Output:	None
		//Use:		Sets a player tag
		//=====================================================================
		public void setTag(String newTag)
		{
			tag = newTag;
			save();
		}
		//=====================================================================
		//Function:	getTag
		//Input:	None
		//Output:	String: The player tag
		//Use:		Gets a player tag
		//=====================================================================
		public String getTag() { return tag; }

		//=====================================================================
		//Function:	setNick
		//Input:	String newTag: The nickname to set for the player
		//Output:	None
		//Use:		Sets a player nickname
		//=====================================================================
		public void setNick(String newNick)
		{
			nickName = newNick;
			save();
		}
                
                public void setSilent(){
                    silent = true;
                }
                public void disableSilent(){
                    silent = false;
                }
                public boolean isSilent(){
                    return silent;
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

		//=====================================================================
		//Function:	getNick
		//Input:	None
		//Output:	String: The player nickname
		//Use:		Gets a player nickname
		//=====================================================================
		public String getNick() { return nickName; }

		//=====================================================================
		//Function:	setSuffix
		//Input:	String newTag: The suffix to set for the player
		//Output:	None
		//Use:		Sets a player suffix
		//=====================================================================
		public void setSuffix(String newSuffix)
		{
			suffix = newSuffix;
			save();
		}

		//=====================================================================
		//Function:	getSuffix
		//Input:	None
		//Output:	String: The player suffix
		//Use:		Gets a player suffix
		//=====================================================================
		public String getSuffix() { return suffix; }
		
		public void setPrefix(String newPrefix)
		{
			prefix = newPrefix;
			save();
		}
		
		public String getPrefix() {
			if(prefix != null && !prefix.equals("") && !prefix.equals("null")){
			return prefix; 
			} else {
				return "f";
			}
		}

		//=====================================================================
		//Function:	setColor
		//Input:	String newTag: The color to set for the player
		//Output:	None
		//Use:		Sets a player color
		//=====================================================================
		public void setColor(String newColor)
		{
			defaultColor = newColor.charAt(0);
			save();
		}

		//=====================================================================
		//Function:	getColor
		//Input:	None
		//Output:	String: The player color
		//Use:		Gets a player color
		//=====================================================================
		public String getColor() {return vPlayerListener.colorChange(defaultColor);}

		//=====================================================================
		//Function:	setMessage
		//Input:	String newName: The name of the player they last messaged
		//			or recieved a message from.
		//Output:	None
		//Use:		Sets a player tag
		//=====================================================================
		public void setMessage(Player newName){ lastMessage = newName.getName(); }

		//=====================================================================
		//Function:	getMessage
		//Input:	None
		//Output:	String: The player name
		//Use:		Gets the name of the player they last messaged or recieved
		//			a message from.
		//=====================================================================
		public Player getMessage()
		{
			    //if(lastMessage != null)
				//We need the bukkit equivalent of this
				//return matchPlayer(lastMessage);
				return null;
		}

		//=====================================================================
		//Function:	isDead
		//Input:	None
		//Output:	boolean: If the player is dead or not
		//Use:		Gets the player is dead or not.
		//=====================================================================
		public boolean isDead() {return dead;}

		//=====================================================================
		//Function:	isDead
		//Input:	boolean isded: if the player is dead or not.
		//Output:	None
		//Use:		Sets if the player is dead or not
		//=====================================================================
		public void isDead(boolean isded){dead = isded;}
	}
}



