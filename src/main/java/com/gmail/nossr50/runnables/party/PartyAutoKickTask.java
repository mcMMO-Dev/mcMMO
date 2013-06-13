package com.gmail.nossr50.runnables.party;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.party.PartyManager;

public class PartyAutoKickTask extends BukkitRunnable {
    private final static long KICK_TIME = 24L * 60L * 60L * 1000L * Config.getInstance().getAutoPartyKickTime();

    @Override
    public void run() {
        HashMap<OfflinePlayer, Party> toRemove = new HashMap<OfflinePlayer, Party>();
        List<String> processedPlayers = new ArrayList<String>();

        long currentTime = System.currentTimeMillis();

        for (Party party : PartyManager.getParties()) {
            for (String memberName : party.getMembers()) {
                OfflinePlayer member = mcMMO.p.getServer().getOfflinePlayer(memberName);
                boolean isProcessed = processedPlayers.contains(memberName);

                if ((!member.isOnline() && (currentTime - member.getLastPlayed() > KICK_TIME)) || isProcessed) {
                    toRemove.put(member, party);
                }

                if (!isProcessed) {
                    processedPlayers.add(memberName);
                }
            }
        }

        for (Entry<OfflinePlayer, Party> entry : toRemove.entrySet()) {
            PartyManager.removeFromParty(entry.getKey(), entry.getValue());
        }
    }
}
