package com.gmail.nossr50.placeholders;

import org.bukkit.entity.Player;

public class PartyIsMemberPlaceholder implements Placeholder {

    private final PapiExpansion papiExpansion;

    public PartyIsMemberPlaceholder(PapiExpansion papiExpansion) {
        this.papiExpansion = papiExpansion;
    }

    @Override
    public String process(Player player, String params) {
        return (papiExpansion.getPartyName(player) == null) ? "false" : "true";
    }

    @Override
    public String getName() {
        return "in_party";
    }
}
