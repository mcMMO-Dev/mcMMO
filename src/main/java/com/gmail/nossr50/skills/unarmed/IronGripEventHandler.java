package com.gmail.nossr50.skills.unarmed;

import org.bukkit.entity.Player;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Users;

public class IronGripEventHandler {
    private UnarmedManager manager;
    private Player defender;
    protected int skillModifier;

    protected IronGripEventHandler(UnarmedManager manager, Player defender) {
        this.manager = manager;
        this.defender = defender;

        calculateSkillModifier();
    }

    protected void calculateSkillModifier() {
        this.skillModifier = Misc.skillCheck(Users.getProfile(defender).getSkillLevel(SkillType.UNARMED), Unarmed.ironGripMaxBonusLevel);
    }

    protected void sendAbilityMessages() {
        defender.sendMessage(LocaleLoader.getString("Unarmed.Ability.IronGrip.Defender"));
        manager.getPlayer().sendMessage(LocaleLoader.getString("Unarmed.Ability.IronGrip.Attacker"));
    }
}
