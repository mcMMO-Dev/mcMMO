package com.gmail.nossr50.placeholders;

import org.bukkit.entity.Player;

public class PowerLevelCapPlaceholder implements Placeholder {
    private final PapiExpansion papiExpansion;

    public PowerLevelCapPlaceholder(PapiExpansion papiExpansion) {
        this.papiExpansion = papiExpansion;
    }

    @Override
    public String process(Player player, String params) {
        Integer cap = papiExpansion.getPowerCap(player);
        return (cap == null) ? "" : cap.toString();
    }

    @Override
    public String getName() {
        return "power_level_cap";
    }
}
