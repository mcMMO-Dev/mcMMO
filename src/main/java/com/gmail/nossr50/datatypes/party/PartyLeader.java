package com.gmail.nossr50.datatypes.party;

import java.util.UUID;

public class PartyLeader {
    private String playerName;
    private UUID uuid;

    public PartyLeader(String playerName, UUID uuid) {
        this.playerName = playerName;
        this.uuid = uuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public UUID getUniqueId() {
        return uuid;
    }
}
