package com.gmail.nossr50.placeholders;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.entity.Player;

public class XpRatePlaceholder implements Placeholder {
    private final PapiExpansion papiExpansion;

    public <S extends PrimarySkillType> XpRatePlaceholder(PapiExpansion papiExpansion) {
        this.papiExpansion = papiExpansion;
    }

    @Override
    public String process(Player player, String params) {
        return papiExpansion.getXpRate(player);
    }

    @Override
    public String getName() {
        return "xprate";
    }
}
