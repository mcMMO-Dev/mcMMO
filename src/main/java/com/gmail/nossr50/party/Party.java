package com.gmail.nossr50.party;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.mcLocale;


public class Party {
	/*
	 * This file is part of mmoMinecraft (http://code.google.com/p/mmo-minecraft/).
	 * 
	 * mmoMinecraft is free software: you can redistribute it and/or modify
	 * it under the terms of the GNU General Public License as published by
	 * the Free Software Foundation, either version 3 of the License, or
	 * (at your option) any later version.
	 *
	 * This program is distributed in the hope that it will be useful,
	 * but WITHOUT ANY WARRANTY; without even the implied warranty of
	 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	 * GNU General Public License for more details.

	 * You should have received a copy of the GNU General Public License
	 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
	 */
	
	public static String partyPlayersFile = mcMMO.maindirectory + File.separator + "FlatFileStuff" + File.separator + "partyPlayers";
	public static String partyLocksFile = mcMMO.maindirectory + File.separator + "FlatFileStuff" + File.separator + "partyLocks";
	public static String partyPasswordsFile = mcMMO.maindirectory + File.separator + "FlatFileStuff" + File.separator + "partyPasswords";

	HashMap<String, HashMap<String, Boolean>> partyPlayers = new HashMap<String, HashMap<String, Boolean>>();
	HashMap<String, Boolean> partyLocks = new HashMap<String, Boolean>();
	HashMap<String, String> partyPasswords = new HashMap<String, String>();
	
	private static mcMMO plugin;
	public Party(mcMMO instance) {
		new File(mcMMO.maindirectory + File.separator + "FlatFileStuff").mkdir();
    	plugin = instance;
    }
	private static volatile Party instance;
	
	public static Party getInstance() 
	{
    	if (instance == null) {
    		instance = new Party(plugin);
    	}
    	return instance;
    }
	
    public boolean inSameParty(Player playera, Player playerb){
    	if(Users.getProfile(playera).inParty() && Users.getProfile(playerb).inParty())
    	{
	        if(Users.getProfile(playera).getParty().equals(Users.getProfile(playerb).getParty()))
	        {
	            return true;
	        } else 
	        {
	            return false;
	        }
    	} else 
    	{
    		return false;
    	}
    }
    
	public int partyCount(Player player, Player[] players)
	{
        int x = 0;
        for(Player hurrdurr : players)
        {
        	if(player != null && hurrdurr != null)
        	{
        	if(Users.getProfile(player).getParty().equals(Users.getProfile(hurrdurr).getParty()))
        	x++;
        	}
        }
        return x;
    }
	
    public void informPartyMembers(Player player) 
    {
    	informPartyMembers(player, Bukkit.getServer().getOnlinePlayers());
    }
    
	
    public void informPartyMembers(Player player, Player[] players)
    {
        for(Player p : players)
        {
        	if(player != null && p != null)
        	{
                if(inSameParty(player, p) && !p.getName().equals(player.getName()))
                {
                	p.sendMessage(mcLocale.getString("Party.InformedOnJoin", new Object[] {player.getName()}));
                }
            }
        }
    }
    
    public ArrayList<Player> getPartyMembers(Player player)
    {
    	ArrayList<Player> players = new ArrayList<Player>();
    	
    	for(Player p : Bukkit.getServer().getOnlinePlayers())
        {
        	if(p.isOnline() && player != null && p != null)
        	{
                if(inSameParty(player, p) && !p.getName().equals(player.getName()))
                {
                	players.add(p);
                }
            }
        }
    	return players;
    }
    
    public void informPartyMembersOwnerChange(String newOwner) {
    	Player newOwnerPlayer = plugin.getServer().getPlayer(newOwner);
    	informPartyMembersOwnerChange(newOwnerPlayer, Bukkit.getServer().getOnlinePlayers());
    }
    
    public void informPartyMembersOwnerChange(Player newOwner, Player[] players) {
        for(Player p : players){
        	if(newOwner != null && p != null){
        		if(inSameParty(newOwner, p))
        		{
        			//TODO: Needs more locale.
        			p.sendMessage(newOwner.getName()+" is the new party owner.");
                }
        	}
        }
    }
    
    public void informPartyMembersQuit(Player player) 
    {
    	informPartyMembersQuit(player, Bukkit.getServer().getOnlinePlayers());
    }
    
    public void informPartyMembersQuit(Player player, Player[] players)
    {
        for(Player p : players){
        	if(player != null && p != null){
        		if(inSameParty(player, p) && !p.getName().equals(player.getName()))
        		{
        			p.sendMessage(mcLocale.getString("Party.InformedOnQuit", new Object[] {player.getName()}));
                }
        	}
        }
    }
    
    public void removeFromParty(Player player, PlayerProfile PP) 
    {
    	//Stop NPE... hopefully
    	if(!isParty(PP.getParty()) || !isInParty(player, PP))
    		addToParty(player, PP, PP.getParty(), false);
    		
    	informPartyMembersQuit(player);
    	String party = PP.getParty();
    	if(isPartyLeader(player.getName(), party)) 
    	{
    		if(isPartyLocked(party)) {
    			unlockParty(party);
    		}
    	}
    	
    	this.partyPlayers.get(party).remove(player.getName());
    	if(isPartyEmpty(party)) deleteParty(party);
		PP.removeParty();
		savePartyPlayers();
    }
    
    public void addToParty(Player player, PlayerProfile PP, String newParty, Boolean invite) {
    	newParty = newParty.replace(":", ".");
    	addToParty(player, PP, newParty, invite, null);
    }

    
    public void addToParty(Player player, PlayerProfile PP, String newParty, Boolean invite, String password) 
    {
    	//Fix for FFS
    	newParty = newParty.replace(":", ".");
    	
    	//Don't care about passwords on invites
    	if(!invite) 
    	{
    		//Don't care about passwords if it isn't locked
    		if(isPartyLocked(newParty)) 
    		{
    			if(isPartyPasswordProtected(newParty)) 
    			{
	    			if(password == null) 
	    			{
	    				//TODO: Needs more locale.
	    				player.sendMessage("This party requires a password. Use /party <party> <password> to join it.");
	    				return;
	    			} else if(!password.equalsIgnoreCase(getPartyPassword(newParty))) 
	    			{
	    				//TODO: Needs more locale.
	    				player.sendMessage("Party password incorrect.");
	    				return;
	    			}
    			} else 
    			{
    				//TODO: Needs more locale.
    				player.sendMessage("Party is locked.");
    				return;
    			}
    		}
    	} else 
    	{
			PP.acceptInvite();
    	}
    	//New party?
    	if(!isParty(newParty)) 
    	{
    		putNestedEntry(this.partyPlayers, newParty, player.getName(), true);
    		
    		//Get default locking behavior from config?
    		this.partyLocks.put(newParty, false);
    		this.partyPasswords.put(newParty, null);
    		saveParties();
    	} else 
    	{		
    		putNestedEntry(this.partyPlayers, newParty, player.getName(), false);

    		savePartyPlayers();
    	}
		PP.setParty(newParty);
		informPartyMembers(player);
		
		if(!invite) 
		{
			player.sendMessage(mcLocale.getString("mcPlayerListener.JoinedParty", new Object[] { newParty }));
		} else 
		{
			player.sendMessage(mcLocale.getString("mcPlayerListener.InviteAccepted", new Object[]{ PP.getParty() }));
		}
    }
    
    private static <U,V,W> W putNestedEntry(
            HashMap<U,HashMap<V,W>> nest,
            U nestKey,
            V nestedKey,
            W nestedValue)
    {
        HashMap<V,W> nested = nest.get(nestKey);

        if (nested == null) {
            nested = new HashMap<V,W>();
            nest.put(nestKey, nested);
        }

        return nested.put(nestedKey, nestedValue);        
    }
    
    public void dump(Player player) {
    	player.sendMessage(partyPlayers.toString());
    	player.sendMessage(partyLocks.toString());
    	player.sendMessage(partyPasswords.toString());
    	Iterator<String> i = partyPlayers.keySet().iterator();
    	while(i.hasNext()) {
    		String nestkey = i.next();
    		player.sendMessage(nestkey);
    		Iterator<String> j = partyPlayers.get(nestkey).keySet().iterator();
    		while(j.hasNext()) {
    			String nestedkey = j.next();
    			player.sendMessage("."+nestedkey);
    			if(partyPlayers.get(nestkey).get(nestedkey)) {
    				player.sendMessage("..True");
    			} else {
    				player.sendMessage("..False");
    			}
    		}
    	}
    }
    
    public void lockParty(String partyName) {
    	this.partyLocks.put(partyName, true);
    	savePartyLocks();
    }
    
    public void unlockParty(String partyName) {
    	this.partyLocks.put(partyName, false);
    	savePartyLocks();
    }
    
    public void deleteParty(String partyName) {
    	this.partyPlayers.remove(partyName);
    	this.partyLocks.remove(partyName);
    	this.partyPasswords.remove(partyName);
    	saveParties();
    }
    
    public void setPartyPassword(String partyName, String password) {
    	if(password.equalsIgnoreCase("\"\"")) password = null;
    	this.partyPasswords.put(partyName, password);
    	savePartyPasswords();
    }
    
    public void setPartyLeader(String partyName, String playerName) {
    	Iterator<String> i = partyPlayers.get(partyName).keySet().iterator();
    	while(i.hasNext()) {
    		String playerKey = i.next();
    		if(playerKey.equalsIgnoreCase(playerName)) {
    			partyPlayers.get(partyName).put(playerName, true);
    			informPartyMembersOwnerChange(playerName);
    			//TODO: Needs more locale.
    			plugin.getServer().getPlayer(playerName).sendMessage("You are now the party owner.");
    			continue;
    		}
    		if(partyPlayers.get(partyName).get(playerKey)) {
    			//TODO: Needs more locale.
    			plugin.getServer().getPlayer(playerKey).sendMessage("You are no longer party owner.");
    			partyPlayers.get(partyName).put(playerKey, false);
    		}
    	}
    }
    
    public String getPartyPassword(String partyName) {
    	return this.partyPasswords.get(partyName);
    }
    
    public boolean canInvite(Player player, PlayerProfile PP) {
    	return (isPartyLocked(PP.getParty()) && !isPartyLeader(player.getName(), PP.getParty())) ? false : true;
    }
    
    public boolean isParty(String partyName) {
    	return this.partyPlayers.containsKey(partyName);
    }

    public boolean isPartyEmpty(String partyName) {
    	return this.partyPlayers.get(partyName).isEmpty();
    }
    
    public boolean isPartyLeader(String playerName, String partyName) {
    	if(this.partyPlayers.get(partyName) != null)
    	{
	    	if(this.partyPlayers.get(partyName).get(playerName) == null) return false;
	    	return this.partyPlayers.get(partyName).get(playerName);
    	}
    	else
    		return false;
    }
    
    public boolean isPartyLocked(String partyName) {
    	if(this.partyLocks.get(partyName) ==  null) return false;
    	return this.partyLocks.get(partyName);
    }
    
    public boolean isPartyPasswordProtected(String partyName) {
    	return !(this.partyPasswords.get(partyName) == null);
    }
    
    public boolean isInParty(Player player, PlayerProfile PP) {
    	return partyPlayers.get(PP.getParty()).containsKey(player.getName());
    }
    
    @SuppressWarnings("unchecked")
	public void loadParties() {
    	if(new File(partyPlayersFile).exists()) {
			try {
				ObjectInputStream obj = new ObjectInputStream(new FileInputStream(partyPlayersFile));
				this.partyPlayers = (HashMap<String, HashMap<String, Boolean>>)obj.readObject();
			} catch (FileNotFoundException e) { e.printStackTrace();
			} catch (EOFException e) { Bukkit.getLogger().info("partyPlayersFile empty.");
			} catch (IOException e) { e.printStackTrace();
			} catch (ClassNotFoundException e) { e.printStackTrace(); }
    	}

    	if(new File(partyLocksFile).exists()) {
			try {
				ObjectInputStream obj = new ObjectInputStream(new FileInputStream(partyLocksFile));
				this.partyLocks = (HashMap<String, Boolean>)obj.readObject();
			} catch (FileNotFoundException e) { e.printStackTrace();
			} catch (EOFException e) { Bukkit.getLogger().info("partyLocksFile empty.");
			} catch (IOException e) { e.printStackTrace();
			} catch (ClassNotFoundException e) { e.printStackTrace(); }
    	}

    	if(new File(partyPasswordsFile).exists()) {
			try {
				ObjectInputStream obj = new ObjectInputStream(new FileInputStream(partyPasswordsFile));
				this.partyPasswords = (HashMap<String, String>)obj.readObject();
			} catch (FileNotFoundException e) { e.printStackTrace();
			} catch (EOFException e) { Bukkit.getLogger().info("partyPasswordsFile empty.");
			} catch (IOException e) { e.printStackTrace();
			} catch (ClassNotFoundException e) { e.printStackTrace(); }
    	}
    }
    
    public void saveParties() {
    	savePartyPlayers();
    	savePartyLocks();
    	savePartyPasswords();
    }
    
    public void savePartyPlayers() {
    	try {
			new File(partyPlayersFile).createNewFile();
			ObjectOutputStream obj = new ObjectOutputStream(new FileOutputStream(partyPlayersFile));
			obj.writeObject(this.partyPlayers);
			obj.close();
		} catch (FileNotFoundException e) { e.printStackTrace();
		} catch (IOException e) { e.printStackTrace(); }
    }
    
    public void savePartyLocks() {
    	try {
			new File(partyLocksFile).createNewFile();
			ObjectOutputStream obj = new ObjectOutputStream(new FileOutputStream(partyLocksFile));
			obj.writeObject(this.partyLocks);
			obj.close();
		} catch (FileNotFoundException e) { e.printStackTrace();
		} catch (IOException e) { e.printStackTrace(); }
    }
    
    public void savePartyPasswords() {
    	try {
			new File(partyPasswordsFile).createNewFile();
			ObjectOutputStream obj = new ObjectOutputStream(new FileOutputStream(partyPasswordsFile));
			obj.writeObject(this.partyPasswords);
			obj.close();
		} catch (FileNotFoundException e) { e.printStackTrace();
		} catch (IOException e) { e.printStackTrace(); }
    }
}
