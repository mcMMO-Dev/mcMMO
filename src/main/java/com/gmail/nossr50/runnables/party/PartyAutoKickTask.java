package com.gmail.nossr50.runnables.party;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.party.PartyManager;

public class PartyAutoKickTask extends BukkitRunnable {
    @Override
    public void run() {
        updatePartyMembers();
    }

    private void updatePartyMembers() {
        HashMap<OfflinePlayer, Party> toRemove = new HashMap<OfflinePlayer, Party>();
        List<String> processedPlayers = new ArrayList<String>();

        long currentTime = System.currentTimeMillis();
        long kickTime = 24L * 60L * 60L * 1000L * Config.getInstance().getAutoPartyKickTime();

        for (Iterator<Party> partyIterator = PartyManager.getParties().iterator(); partyIterator.hasNext();) {
            Party party = partyIterator.next();

            for (String memberName : party.getMembers()) {
                OfflinePlayer member = mcMMO.p.getServer().getOfflinePlayer(memberName);

                if ((currentTime - member.getLastPlayed() > kickTime) || processedPlayers.contains(memberName)) {
                    toRemove.put(member, party);
                }

                if (!processedPlayers.contains(memberName)) {
                    processedPlayers.add(memberName);
                }
            }
        }

        for (Entry<OfflinePlayer, Party> entry : toRemove.entrySet()) {
            PartyManager.removeFromParty(entry.getKey(), entry.getValue());
        }
    }
}
