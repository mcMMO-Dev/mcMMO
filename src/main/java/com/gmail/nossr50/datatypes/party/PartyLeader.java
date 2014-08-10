package com.gmail.nossr50.datatypes.party;

import java.util.UUID;

public class PartyLeader {
    private UUID uuid;
    private String playerName;

    public PartyLeader(UUID uuid, String playerName) {
        this.uuid = uuid;
        this.playerName = playerName;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getPlayerName() {
        return playerName;
    }
}
