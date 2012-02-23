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
		Users.removeUser(player);
	}
}
