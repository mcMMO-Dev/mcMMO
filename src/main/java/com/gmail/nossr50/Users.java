/*
	This file is part of mcMMO.

    mcMMO is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    mcMMO is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with mcMMO.  If not, see <http://www.gnu.org/licenses/>.
*/
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