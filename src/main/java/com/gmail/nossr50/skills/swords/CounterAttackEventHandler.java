package com.gmail.nossr50.skills.swords;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Combat;
import com.gmail.nossr50.util.ItemChecks;
import com.gmail.nossr50.util.Misc;

public class CounterAttackEventHandler {
    private SwordsManager manager;
    private Player player;
    private LivingEntity attacker;
    private int damage;

    protected int skillModifier;

    protected CounterAttackEventHandler(SwordsManager manager, LivingEntity attacker, int damage) {
        this.manager = manager;
        this.player = manager.getPlayer();
        this.attacker = attacker;
        this.damage = damage;
    }

    protected boolean isHoldingSword() {
        return ItemChecks.isSword(player.getItemInHand());
    }

    protected void calculateSkillModifier() {
        this.skillModifier = Misc.skillCheck(manager.getSkillLevel(), Swords.COUNTER_ATTACK_MAX_BONUS_LEVEL);
    }

    protected void dealDamage() {
        Combat.dealDamage(attacker, damage / Swords.COUNTER_ATTACK_MODIFIER);
    }

    protected void sendAbilityMessages() {
        player.sendMessage(LocaleLoader.getString("Swords.Combat.Countered"));

        if (attacker instanceof Player) {
            ((Player) attacker).sendMessage(LocaleLoader.getString("Swords.Combat.Counter.Hit"));
        }
    }
}
