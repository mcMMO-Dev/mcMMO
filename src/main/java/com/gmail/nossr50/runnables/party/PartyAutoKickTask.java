package com.gmail.nossr50.runnables.party;

import java.util.Iterator;

import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.party.PartyManager;

public class PartyAutoKickTask extends BukkitRunnable {
    @Override
    public void run() {
        updatePartyMembers();
    }

    private void updatePartyMembers() {
        long currentTime = System.currentTimeMillis();
        long kickTime = 24L * 60L * 60L * 1000L * Config.getInstance().getAutoPartyKickTime();

        for (Iterator<Party> partyIterator = PartyManager.getParties().iterator(); partyIterator.hasNext();) {
            Party party = partyIterator.next();

            for (Iterator<OfflinePlayer> memberIterator = party.getMembers().iterator(); memberIterator.hasNext();) {
                OfflinePlayer member = memberIterator.next();

                if (currentTime - member.getLastPlayed() > kickTime) {
                    PartyManager.removeFromParty(member, party);
                }
            }
        }
    }
}
