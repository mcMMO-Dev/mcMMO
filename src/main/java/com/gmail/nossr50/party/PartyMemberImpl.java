package com.gmail.nossr50.party;

import com.neetgames.mcmmo.party.PartyMemberRank;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PartyMemberImpl {
    private String playerName;
    private final @NotNull UUID playerUUID;
    private @Nullable OfflinePlayer offlinePlayer;
    private @NotNull PartyMemberRank partyMemberRank;
    private @Nullable PartyTeleportRecord partyTeleportRecord;
    private int itemShareModifier;

    public PartyMemberImpl(@NotNull UUID playerUUID, @NotNull PartyMemberRank partyMemberRank) {
        this.playerUUID = playerUUID;
        this.partyMemberRank = partyMemberRank;
    }

    public OfflinePlayer getOfflinePlayer() {
        if(offlinePlayer == null)
            offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);


        return offlinePlayer;
    }

    public @NotNull PartyMemberRank getPartyMemberRank() {
        return partyMemberRank;
    }

    public void setPartyMemberRank(@NotNull PartyMemberRank partyMemberRank) {
        this.partyMemberRank = partyMemberRank;
    }

    public @NotNull UUID getUniqueId() {
        return playerUUID;
    }

    @NotNull
    public String getName() {
        if(playerName == null) {
            if(getOfflinePlayer().getName() != null)
                playerName = getOfflinePlayer().getName();
        }

        if(playerName == null)
            return playerUUID.toString();
        else
            return playerName;
    }

    public int getItemShareModifier() {
        if (itemShareModifier < 10) {
            setItemShareModifier(10);
        }

        return itemShareModifier;
    }

    public void setItemShareModifier(int modifier) {
        itemShareModifier = Math.max(10, modifier);
    }

    public @Nullable PartyTeleportRecord getPartyTeleportRecord() {
        return partyTeleportRecord;
    }

}
