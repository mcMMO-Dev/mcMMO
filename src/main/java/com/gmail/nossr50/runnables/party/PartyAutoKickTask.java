package com.gmail.nossr50.runnables.party;

import com.gmail.nossr50.datatypes.party.Party;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

public class PartyAutoKickTask extends BukkitRunnable {
    private final static long KICK_TIME = 24L * 60L * 60L * 1000L * pluginRef.getConfigManager().getConfigParty().getPartyCleanup().getPartyAutoKickHoursInterval();

    @Override
    public void run() {
        HashMap<OfflinePlayer, Party> toRemove = new HashMap<>();
        List<UUID> processedPlayers = new ArrayList<>();

        long currentTime = System.currentTimeMillis();

        for (Party party : pluginRef.getPartyManager().getParties()) {
            for (UUID memberUniqueId : party.getMembers().keySet()) {
                OfflinePlayer member = pluginRef.getServer().getOfflinePlayer(memberUniqueId);
                boolean isProcessed = processedPlayers.contains(memberUniqueId);

                if ((!member.isOnline() && (currentTime - member.getLastPlayed() > KICK_TIME)) || isProcessed) {
                    toRemove.put(member, party);
                }

                if (!isProcessed) {
                    processedPlayers.add(memberUniqueId);
                }
            }
        }

        for (Entry<OfflinePlayer, Party> entry : toRemove.entrySet()) {
            pluginRef.getPartyManager().removeFromParty(entry.getKey(), entry.getValue());
        }
    }
}
