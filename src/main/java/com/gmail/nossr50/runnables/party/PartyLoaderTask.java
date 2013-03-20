package com.gmail.nossr50.runnables.party;

import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.party.PartyManager;

public class PartyLoaderTask extends BukkitRunnable {
    @Override
    public void run() {
        PartyManager.loadParties();
    }
}
