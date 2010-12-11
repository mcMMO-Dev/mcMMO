import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Scanner;

public class vMinecraftUsers {
    private static volatile vMinecraftUsers instance;
    protected static final Logger log = Logger.getLogger("Minecraft");
    private PropertiesFile properties;
    String location = "vminecraft.users";
    
    public static PlayerList players = new PlayerList();
    
    
    public void loadUsers(){
        File theDir = new File(location);
		if(!theDir.exists()){
			properties = new PropertiesFile(location);
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
			properties = new PropertiesFile(location);
			try {
				properties.load();
			} catch (IOException e) {
				log.log(Level.SEVERE, "Exception while loading " + location, e);
			}
		}
    }
    public boolean doesPlayerExist(String player) {
        try {
            Scanner scanner = new Scanner(new File(location));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("#") || line.equals("") || line.startsWith("﻿")) {
                    continue;
                }
                String[] split = line.split(":");
                if (!split[0].equalsIgnoreCase(player)) {
                    continue;
                }
                return true;
            }
            scanner.close();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Exception while reading " + location + " (Are you sure you formatted it correctly?)", e);
        }
        return false;
    }

	//=====================================================================
	//Function:	addUser
	//Input:	Player player: The player to create a profile for
	//Output:	none
	//Use:		Creates the player profile
	//=====================================================================
    public static void addUser(Player player){
    	players.addPlayer(player);
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
    
    public static vMinecraftUsers getInstance() {
		if (instance == null) {
			instance = new vMinecraftUsers();
		}
		return instance;
	}
    public static void getRow(){

    }
}

//=====================================================================
//Class:	PlayerList
//Use:		Encapsulates the player list
//Author:	cerevisiae
//=====================================================================
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
					   defaultColor;

        String location = "vminecraft.users";
		
		private ArrayList<String> ignoreList;
		private commandList aliasList;
		
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
            nickName = new String();
            tag = new String();
            suffix = new String();
			ignoreList = new ArrayList<String>();
            aliasList = new commandList();
            
            //Try to apply what we can
            try {
                Scanner scanner = new Scanner(new File(location));
                while (scanner.hasNextLine()) {
	                String line = scanner.nextLine();
	                if (line.startsWith("#") || line.equals("") || line.startsWith("﻿")) {
	                    continue;
	                }
	                String[] split = line.split(":");
	                    
	                //If the player name is equal to the name in the list
	                if (split.length > 0 && split[0].equalsIgnoreCase(player.getName())) {
	                	
		                //Get the tag from the 1st split
		                if (split.length >= 2)
		                	nickName = split[1];

		                //Get the tag from the 2nd split
			            if (split.length >= 3)
			            	suffix = split[2];
		
		                //Get the tag from the 3rd split
		                if (split.length >= 4)
		                    tag = (split[3]);
		                
		                //Add all the ignored people to the player's ignore list
		                if (split.length >= 5) {
		                	for(String name : split[4].split(","))
		                		ignoreList.add(name);
		                }
		                
		                //Get the alias list, from the 5th split
		                if (split.length >= 6) {
		                	//Loop through all the aliases
		                	for(String alias : split[5].split(","))
		                	{
		                		//Break apart the two parts of the alias
		                		String[] parts = alias.split("@");
		                		if(parts.length > 1)
		                		{
		                			//Register the alias to the player's aliasList
		                			aliasList.registerAlias(parts[0], parts[2]);
		                		}
		                	}
		                }
		                break;
	                }
	            }
	            scanner.close();
	        } catch (Exception e) {
	            log.log(Level.SEVERE, "Exception while reading "
	            		+ location + " (Are you sure you formatted it correctly?)", e);
	        }
	        save();
		}
		
        //=====================================================================
        // Function:    save
        // Input:       none
        // Output:      None
        // Use:         Writes current values of PlayerProfile to disk
		//				Call this function to save current values
        //=====================================================================
        public void save(){
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(location, true));
                Scanner scanner = new Scanner(new File(location));
                while (scanner.hasNextLine()) {
	                String line = scanner.nextLine();
	                if (line.startsWith("#") || line.equals("") || line.startsWith("﻿")) {
	                    continue;
	                }
	                String[] split = line.split(":");
	                if (!split[0].equalsIgnoreCase(playerName)) {
	                    continue;
	                }
	                String output =playerName + ":" + nickName + ":" + suffix + ":" + tag + ":";
	                for(String player : ignoreList)
	                	output += player + ",";
	                output += ":";
	                bw.write(output);
	            }
	            scanner.close();
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
		//Function:	addAlias
		//Input:	String command: The command to try to call
		//			String[] args: The arguments for the command
		//Output:	None
		//Use:		Adds a command
		//=====================================================================
		public void addAlias(String name, String callCommand)
		{
			aliasList.registerAlias(name, callCommand);
			save();
		}

		//=====================================================================
		//Function:	callAlias
		//Input:	String command: The command to try to call
		//			Player player: Checks if a player is ignored
		//			String[] args: The arguments for the command
		//Output:	int: Exit code
		//Use:		Attempts to call a command
		//=====================================================================
		public int callAlias(String command, Player player, String[] args)
		{
			try
			{
				//Attemt to call the function
				return aliasList.call(command, player, args);
			}
			catch (Throwable e)
			{
				//The function wasn't found, returns fail
				return EXIT_FAIL;
			}
		}

		//=====================================================================
		//Function:	setTag
		//Input:	String newTag: The tag to set for the player
		//Output:	None
		//Use:		Sets a player tag
		//=====================================================================
		public void setTag(String newTag){ tag = newTag; }

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
		public void setNick(String newNick){ nickName = newNick; }

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
		public void setSuffix(String newSuffix){ suffix = newSuffix; }

		//=====================================================================
		//Function:	getSuffix
		//Input:	None
		//Output:	String: The player suffix
		//Use:		Gets a player suffix
		//=====================================================================
		public String getSuffix() { return suffix; }

		//=====================================================================
		//Function:	setColor
		//Input:	String newTag: The color to set for the player
		//Output:	None
		//Use:		Sets a player color
		//=====================================================================
		public void setColor(String newColor){ defaultColor = newColor; }

		//=====================================================================
		//Function:	getColor
		//Input:	None
		//Output:	String: The player color
		//Use:		Gets a player color
		//=====================================================================
		public String getColor() { return defaultColor; }

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
			if(lastMessage != null)
				return etc.getServer().matchPlayer(lastMessage);
			return null;
		}
	}
}


