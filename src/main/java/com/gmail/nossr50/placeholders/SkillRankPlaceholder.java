package com.gmail.nossr50.placeholders;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.entity.Player;

public class SkillRankPlaceholder implements Placeholder {
    private final PapiExpansion papiExpansion;
    private final PrimarySkillType skill;

    public SkillRankPlaceholder(PapiExpansion papiExpansion, PrimarySkillType skill) {
        this.papiExpansion = papiExpansion;
        this.skill = skill;
    }

    @Override
    public String process(Player player, String params) {
        Integer rank = papiExpansion.getRank(skill, player);
        return (rank == null) ? "" : rank.toString();
    }

    @Override
    public String getName() {
        return "rank_" + skill.toString().toLowerCase();
    }
}
