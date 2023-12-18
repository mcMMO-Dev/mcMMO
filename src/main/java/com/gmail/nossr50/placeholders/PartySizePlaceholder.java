package com.gmail.nossr50.placeholders;

import org.bukkit.entity.Player;

public class PartySizePlaceholder implements Placeholder {
    private final PapiExpansion papiExpansion;

    public PartySizePlaceholder(PapiExpansion papiExpansion) {
        this.papiExpansion = papiExpansion;
    }

    @Override
    public String process(Player player, String params) {
        Integer partySize = papiExpansion.getPartySize(player);
        return (partySize == null) ? "" : partySize.toString();
    }

    @Override
    public String getName() {
        return "party_size";
    }
}
