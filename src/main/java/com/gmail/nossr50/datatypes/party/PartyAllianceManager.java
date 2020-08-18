package com.gmail.nossr50.datatypes.party;

public class PartyAllianceManager {
    private Party ally;
    private Party allianceInvite;

    public void setPartyAllianceInvite(Party allianceInvite) {
        this.allianceInvite = allianceInvite;
    }

    public Party getPartyAllianceInvite() {
        return allianceInvite;
    }

    public boolean hasPartyAllianceInvite() {
        return (allianceInvite != null);
    }

    public void removePartyAllianceInvite() {
        allianceInvite = null;
    }

    public void setAlly(Party ally) {
        this.ally = ally;
    }
}
