package com.gmail.nossr50.runnables;

import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;

public class RemoveProfileFromMemoryTask implements Runnable {
	private Player player;

	public RemoveProfileFromMemoryTask(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
	    //Check if the profile still exists (stuff like MySQL reconnection removes profiles)
	    if(Users.players.containsKey(player.getName().toLowerCase())) {
	        Users.getProfile(player.getName()).save(); //We save here so players don't quit/reconnect to cause lag
	        Users.removeUserByName(player.getName());
	    }
	}
}
