package com.gmail.nossr50.placeholders;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.entity.Player;

public class SkillExpRemainingPlaceholder implements Placeholder {
    private final PapiExpansion papiExpansion;
    private final PrimarySkillType skill;

    public SkillExpRemainingPlaceholder(PapiExpansion papiExpansion, PrimarySkillType skill) {
        this.papiExpansion = papiExpansion;
        this.skill = skill;
    }

    @Override
    public String process(Player player, String params) {
        final Integer expRemaining = papiExpansion.getExpRemaining(skill, player);
        return (expRemaining == null) ? "" : expRemaining.toString();
    }

    @Override
    public String getName() {
        return "xp_remaining_" + skill.toString().toLowerCase();
    }
}
