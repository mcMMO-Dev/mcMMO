package com.gmail.nossr50.skills.archery;

import org.bukkit.event.entity.EntityDamageEvent;

public class ArcheryBonusDamageEventHandler {
    private ArcheryManager manager;
    private EntityDamageEvent event;

    protected double damageBonusPercent;

    protected ArcheryBonusDamageEventHandler(ArcheryManager manager, EntityDamageEvent event) {
        this.manager = manager;
        this.event = event;
    }

    protected void calculateDamageBonus() {
        double damageBonus = ((manager.getSkillLevel() / Archery.BONUS_DAMAGE_INCREASE_LEVEL) * Archery.BONUS_DAMAGE_INCREASE_PERCENT);

        if (damageBonus > Archery.BONUS_DAMAGE_MAX_BONUS_PERCENTAGE) {
            damageBonus = Archery.BONUS_DAMAGE_MAX_BONUS_PERCENTAGE;
        }

        this.damageBonusPercent = damageBonus;
    }

    protected void modifyEventDamage() {
        int damage = event.getDamage();
        int archeryBonus = (int) (damage * damageBonusPercent);

        event.setDamage(damage + archeryBonus);
    }
}
