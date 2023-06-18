package com.gmail.nossr50.placeholders;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.entity.Player;

public class SkillExpNeededPlaceholder implements Placeholder {

    private final PapiExpansion papiExpansion;
    private final PrimarySkillType skillType;

    public SkillExpNeededPlaceholder(PapiExpansion papiExpansion, PrimarySkillType skillType) {
        this.papiExpansion = papiExpansion;
        this.skillType = skillType;
    }

    @Override
    public String process(Player player, String params) {
        final Integer expNeeded = papiExpansion.getExpNeeded(skillType, player);
        return (expNeeded == null) ? "" : expNeeded.toString();
    }

    @Override
    public String getName() {
        return "xp_needed_" + skillType.toString().toLowerCase();
    }
}
