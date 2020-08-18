package com.gmail.nossr50.datatypes.party;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PartyInvite {

    private final String partyName;
    private Party invitingParty;
    private final UUID inviterUUID;
    private final UUID targetUUID;

    public PartyInvite(@NotNull String partyName, @NotNull Party invitingParty, @NotNull UUID inviterUUID, @NotNull UUID targetUUID) {
        this.partyName = partyName;
        this.invitingParty = invitingParty;
        this.inviterUUID = inviterUUID;
        this.targetUUID = targetUUID;
    }

    /**
     * The name of the Party who made this invite
     * @return
     */
    public String getPartyName() {
        return partyName;
    }

    /**
     * The UUID of the player who created this invite
     * @return
     */
    public @NotNull UUID getInviterUUID() {
        return inviterUUID;
    }

    /**
     * The target of this party invite
     * @return the target player for this invite
     */
    public @NotNull UUID getTargetPlayer() {
        return targetUUID;
    }

    public @Nullable OfflinePlayer getTargetOfflinePlayer() {
        return Bukkit.getOfflinePlayer(targetUUID);
    }
}
