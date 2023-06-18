package com.gmail.nossr50.placeholders;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.entity.Player;

public class SkillExpPlaceholder implements Placeholder {
    private final PapiExpansion papiExpansion;
    private final PrimarySkillType skill;

    public SkillExpPlaceholder(PapiExpansion papiExpansion, PrimarySkillType skill) {
        this.papiExpansion = papiExpansion;
        this.skill = skill;
    }


    @Override
    public String process(Player player, String params) {
        Integer exp = papiExpansion.getExp(skill, player);
        return (exp == null) ? "" : exp.toString();
    }

    @Override
    public String getName() {
        return "xp_" + skill.toString().toLowerCase();
    }
}
