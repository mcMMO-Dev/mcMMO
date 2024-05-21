package com.gmail.nossr50.placeholders;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

public class PartyLeaderPlaceholder implements Placeholder {
    private final PapiExpansion papiExpansion;

    public PartyLeaderPlaceholder(PapiExpansion papiExpansion) {
        this.papiExpansion = papiExpansion;
    }

    @Override
    public String process(Player player, String params) {
        return StringUtils.trimToEmpty(papiExpansion.getPartyLeader(player));
    }

    @Override
    public String getName() {
        return "party_leader";
    }
}
