package com.gmail.nossr50.skills.swords;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.utilities.CombatTools;
import com.gmail.nossr50.util.Misc;

public class CounterAttackEventHandler {
    private SwordsManager manager;
    private LivingEntity attacker;
    private int damage;
    protected int skillModifier;

    protected CounterAttackEventHandler(SwordsManager manager, LivingEntity attacker, int damage) {
        this.manager = manager;
        this.attacker = attacker;
        this.damage = damage;
    }

    protected void calculateSkillModifier() {
        this.skillModifier = Misc.skillCheck(manager.getSkillLevel(), Swords.counterAttackMaxBonusLevel);
    }

    protected void dealDamage() {
        CombatTools.dealDamage(attacker, damage / Swords.counterAttackModifier);
    }

    protected void sendAbilityMessages() {
        manager.getMcMMOPlayer().getPlayer().sendMessage(LocaleLoader.getString("Swords.Combat.Countered"));

        if (attacker instanceof Player) {
            ((Player) attacker).sendMessage(LocaleLoader.getString("Swords.Combat.Counter.Hit"));
        }
    }
}
