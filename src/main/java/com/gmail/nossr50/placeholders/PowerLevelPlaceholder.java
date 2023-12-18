package com.gmail.nossr50.placeholders;

import org.bukkit.entity.Player;

public class PowerLevelPlaceholder implements Placeholder {
    private final PapiExpansion papiExpansion;

    public PowerLevelPlaceholder(PapiExpansion papiExpansion) {
        this.papiExpansion = papiExpansion;
    }

    @Override
    public String process(Player player, String params) {
        Integer powerLevel = papiExpansion.getPowerLevel(player);
        return (powerLevel == null) ? "" : powerLevel.toString();
    }

    @Override
    public String getName() {
        return "power_level";
    }
}
