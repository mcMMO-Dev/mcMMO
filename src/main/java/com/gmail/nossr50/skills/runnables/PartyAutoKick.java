package com.gmail.nossr50.skills.runnables;

import java.util.ArrayList;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.party.PartyManager;

public class PartyAutoKick implements Runnable {

    @Override
    public void run() {
        updatePartyMembers();
    }

    private void updatePartyMembers() {
        long currentTime = System.currentTimeMillis();
        long kickTime = 24L * 60L * 60L * 1000L * Config.getInstance().getAutoPartyKickTime();

        ArrayList<Party> parties = new ArrayList<Party>(PartyManager.getParties());

        for (Party party : parties) {
            ArrayList<String> members = new ArrayList<String>(party.getMembers());
            for (String member : members) {
                long lastPlayed = mcMMO.p.getServer().getOfflinePlayer(member).getLastPlayed();

                if (currentTime - lastPlayed > kickTime) {
                    System.out.println("Removing " + member + " from " + party.getName()); // Debug, remove this later
                    PartyManager.removeFromParty(member, party);
                }
            }
        }
    }
}
