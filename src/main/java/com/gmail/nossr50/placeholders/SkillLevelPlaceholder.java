package com.gmail.nossr50.placeholders;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.entity.Player;

public class SkillLevelPlaceholder implements Placeholder {
    private final PapiExpansion papiExpansion;
    private final PrimarySkillType skillType;

    public SkillLevelPlaceholder(PapiExpansion papiExpansion, PrimarySkillType skillType) {
        this.papiExpansion = papiExpansion;
        this.skillType = skillType;
    }

    @Override
    public String process(Player p, String params) {
        final Integer skillLevel = papiExpansion.getSkillLevel(skillType, p);
        return (skillLevel == null) ? "" : skillLevel.toString();
    }

    @Override
    public String getName() {
        return "level_" + skillType.toString().toLowerCase();
    }
}
