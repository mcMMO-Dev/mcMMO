package com.gmail.nossr50.placeholders;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.entity.Player;

public class SkillXpRatePlaceholder implements Placeholder {
    private final PapiExpansion papiExpansion;
    private final PrimarySkillType skillType;

    public SkillXpRatePlaceholder(PapiExpansion papiExpansion, PrimarySkillType skillType) {
        this.papiExpansion = papiExpansion;
        this.skillType = skillType;
    }

    @Override
    public String process(Player p, String params) {
        final String skillLevel = papiExpansion.getSkillXpRate(skillType, p);
        return (skillLevel == null) ? "" : skillLevel;
    }

    @Override
    public String getName() {
        return "xprate_" + skillType.toString().toLowerCase();
    }
}
