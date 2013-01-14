package com.gmail.nossr50.skills.archery;

import org.bukkit.event.entity.EntityDamageEvent;

public class SkillShotEventHandler {
    private ArcheryManager manager;
    private EntityDamageEvent event;

    protected double damageBonusPercent;

    protected SkillShotEventHandler(ArcheryManager manager, EntityDamageEvent event) {
        this.manager = manager;
        this.event = event;
    }

    protected void calculateDamageBonus() {
        this.damageBonusPercent = ((manager.getSkillLevel() / Archery.skillShotIncreaseLevel) * Archery.skillShotIncreasePercentage);

        if (damageBonusPercent > Archery.skillShotMaxBonusPercentage) {
            damageBonusPercent = Archery.skillShotMaxBonusPercentage;
        }
    }

    protected void modifyEventDamage() {
        int damage = event.getDamage();
        int archeryBonus = (int) (damage * damageBonusPercent);

        event.setDamage(damage + archeryBonus);
    }
}
