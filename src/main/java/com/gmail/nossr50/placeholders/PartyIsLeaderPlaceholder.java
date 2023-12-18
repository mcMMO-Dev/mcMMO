package com.gmail.nossr50.placeholders;

import org.bukkit.entity.Player;

public class PartyIsLeaderPlaceholder implements Placeholder {

    private final PapiExpansion papiExpansion;

    public PartyIsLeaderPlaceholder(PapiExpansion papiExpansion) {
        this.papiExpansion = papiExpansion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String process(Player player, String params) {
        String leader = papiExpansion.getPartyLeader(player);
        return (leader.equals(player.getName())) ? "true" : "false";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "is_party_leader";
    }
}
