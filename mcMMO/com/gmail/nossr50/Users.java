package com.gmail.nossr50;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.plugin.Plugin;

import com.gmail.nossr50.datatypes.PlayerProfile;


public class Users {
    private static volatile Users instance;
    protected static final Logger log = Logger.getLogger("Minecraft");
    String location = "plugins/mcMMO/mcmmo.users";
    
    //public static ArrayList<PlayerProfile> players;
    public static HashMap<Player, PlayerProfile> players = new HashMap<Player, PlayerProfile>();
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
				//writer.write("#Storage place for user information\r\n");
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
			try {
				load();
			} catch (IOException e) {
				log.log(Level.SEVERE, "Exception while loading " + location, e);
			}
		}
    }

	
    public static void addUser(Player player){
    	players.put(player, new PlayerProfile(player));
    }
    public static void clearUsers()
    {
    	players.clear();
    }


    public static void removeUser(Player player){    	
    	PlayerProfile PP = Users.getProfile(player);
    	if(PP != null){
	    	PP.save();
	    	if(players.containsKey(player))
	    		players.remove(player);
    	}
    }

    public static PlayerProfile getProfile(Player player){
    	return players.get(player);
    }
    
    public static Users getInstance() {
		if (instance == null) {
			instance = new Users();
		}
		return instance;
	}
    public static void getRow(){

    }

}