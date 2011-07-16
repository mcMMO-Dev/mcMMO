package com.gmail.nossr50.party;

import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.locale.mcLocale;


public class Party 
{
	private static mcMMO plugin;
	public Party(mcMMO instance) 
	{
    	plugin = instance;
    }
	private static volatile Party instance;
	public static Party getInstance() 
	{
    	if (instance == null) 
    	{
    	instance = new Party(plugin);
    	}
    	return instance;
    	}
    public boolean inSameParty(Player playera, Player playerb){
    	if(Users.getProfile(playera) == null || Users.getProfile(playerb) == null)
    	{
    		Users.addUser(playera);
    		Users.addUser(playerb);
    	}
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
    public void informPartyMembers(Player player, Player[] players)
    {
        int x = 0;
        for(Player p : players)
        {
        	if(player != null && p != null)
        	{
                if(inSameParty(player, p) && !p.getName().equals(player.getName()))
                {
                p.sendMessage(mcLocale.getString("Party.InformedOnJoin", new Object[] {player.getName()}));
                x++;
                }
            }
        }
    }
    public void informPartyMembersQuit(Player player, Player[] players)
    {
        int x = 0;
        for(Player p : players){
        	if(player != null && p != null){
        		if(inSameParty(player, p) && !p.getName().equals(player.getName()))
        		{
        			p.sendMessage(mcLocale.getString("Party.InformedOnQuit", new Object[] {player.getName()}));
        			x++;
                }
        	}
        }
    }

}
