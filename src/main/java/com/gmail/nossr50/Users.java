package com.gmail.nossr50;

import java.io.*;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.HashMap;

import org.bukkit.entity.*;
import com.gmail.nossr50.datatypes.PlayerProfile;


public class Users {
    private static volatile Users instance;
    protected static final Logger log = Logger.getLogger("Minecraft");
    String location = "plugins/mcMMO/FlatFileStuff/mcmmo.users";
    String directory = "plugins/mcMMO/FlatFileStuff/";
    String directoryb = "plugins/mcMMO/FlatFileStuff/Leaderboards/";
    
    //public static ArrayList<PlayerProfile> players;
    public static HashMap<Player, PlayerProfile> players = new HashMap<Player, PlayerProfile>();
    private Properties properties = new Properties();
    
    //To load
    public void load() throws IOException {
        properties.load(new FileInputStream(location));
    }
    //To save
    public void save() 
    {
        try 
        {
	        properties.store(new FileOutputStream(location), null);
	        }catch(IOException ex) {
	        }
    }
    
    public void loadUsers()
    {
    	new File(directory).mkdir();
    	new File(directoryb).mkdir();
        File theDir = new File(location);
		if(!theDir.exists())
		{
			try {
				FileWriter writer = new FileWriter(theDir);
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }

	
    public static void addUser(Player player)
    {
    	if(!players.containsKey(player)) 
    		players.put(player, new PlayerProfile(player.getName()));
    }
    public static void clearUsers()
    {
    	players.clear();
    }
    public static HashMap<Player, PlayerProfile> getProfiles(){
    	return players;
    }
    
    public static void removeUser(Player player)
    {    	
    	//Only remove PlayerProfile if user is offline and we have it in memory
    	if(!player.isOnline() && players.containsKey(player))
    	{
	    	players.get(player).save();
	    	players.remove(player);
    	}
    }
    
    public static void removeUserByName(String playerName)
    {
        Player target = null;
        for(Player player : players.keySet())
        {
            PlayerProfile PP = players.get(player);
            if(PP.getPlayerName().equals(playerName))
            {
                target = player;
            }
        }
        
        players.remove(target);
    }

    public static PlayerProfile getProfile(Player player){
    	if(players.get(player) != null)
    		return players.get(player);
    	else
    	{
    		players.put(player, new PlayerProfile(player.getName()));
    		return players.get(player);
    	}
    }
    
    public static PlayerProfile getOfflineProfile(String playerName){
        return new PlayerProfile(playerName, false);
    }
    
    public static Users getInstance() {
		if (instance == null) {
			instance = new Users();
		}
		return instance;
	}

}