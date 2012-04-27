package com.gmail.nossr50.runnables;

import com.gmail.nossr50.Users;

public class RemoveProfileFromMemoryTask implements Runnable {
    private String playerName = null;

    public RemoveProfileFromMemoryTask(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public void run() {
        //Check if the profile still exists (stuff like MySQL reconnection removes profiles)
        if (Users.players.containsKey(playerName.toLowerCase())) {
            Users.getProfileByName(playerName.toLowerCase()).save(); //We save here so players don't quit/reconnect to cause lag
            Users.removeUserByName(playerName.toLowerCase());
        }
    }
}
