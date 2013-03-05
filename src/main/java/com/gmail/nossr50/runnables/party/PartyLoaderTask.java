package com.gmail.nossr50.runnables.party;

import com.gmail.nossr50.party.PartyManager;

public class PartyLoaderTask implements Runnable {
    @Override
    public void run() {
        PartyManager.loadParties();
    }
}
