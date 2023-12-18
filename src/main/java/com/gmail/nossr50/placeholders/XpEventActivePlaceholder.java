package com.gmail.nossr50.placeholders;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.entity.Player;

public class XpEventActivePlaceholder implements Placeholder {
    private final PapiExpansion papiExpansion;

    public <S extends PrimarySkillType> XpEventActivePlaceholder(PapiExpansion papiExpansion) {
        this.papiExpansion = papiExpansion;
    }

    @Override
    public String process(Player player, String params) {
        return papiExpansion.isExpEventActive(player);
    }

    @Override
    public String getName() {
        return "is_xp_event_active";
    }
}
