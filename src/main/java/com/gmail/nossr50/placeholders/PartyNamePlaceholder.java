package com.gmail.nossr50.placeholders;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

public class PartyNamePlaceholder implements Placeholder {
    private final PapiExpansion papiExpansion;

    public PartyNamePlaceholder(PapiExpansion papiExpansion) {
        this.papiExpansion = papiExpansion;
    }

    @Override
    public String process(Player player, String params) {
        return StringUtils.trimToEmpty(papiExpansion.getPartyName(player));
    }

    @Override
    public String getName() {
        return "party_name";
    }
}
