package com.gmail.nossr50.datatypes.party;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PartyMember {
    private final OfflinePlayer offlinePlayer;
    private PartyMemberRank partyMemberRank;
    private final PartyTeleportRecord partyTeleportRecord;

    private int itemShareModifier;

    public PartyMember(OfflinePlayer offlinePlayer, PartyMemberRank partyMemberRank) {
        this.offlinePlayer = offlinePlayer;
        this.partyMemberRank = partyMemberRank;
        this.partyTeleportRecord = new PartyTeleportRecord();
    }

    public OfflinePlayer getOfflinePlayer() {
        return offlinePlayer;
    }

    public PartyMemberRank getPartyMemberRank() {
        return partyMemberRank;
    }

    public void setPartyMemberRank(PartyMemberRank partyMemberRank) {
        this.partyMemberRank = partyMemberRank;
    }

    public @NotNull UUID getUniqueId() {
        return offlinePlayer.getUniqueId();
    }

    @Nullable
    public String getName() {
        return offlinePlayer.getName();
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

    public PartyTeleportRecord getPartyTeleportRecord() {
        return partyTeleportRecord;
    }

}
