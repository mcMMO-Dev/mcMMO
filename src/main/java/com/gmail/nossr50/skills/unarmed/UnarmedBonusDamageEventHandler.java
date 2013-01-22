package com.gmail.nossr50.skills.unarmed;

import org.bukkit.event.entity.EntityDamageEvent;

public class UnarmedBonusDamageEventHandler {
    private UnarmedManager manager;
    private EntityDamageEvent event;

    protected int damageBonus;

    protected UnarmedBonusDamageEventHandler(UnarmedManager manager, EntityDamageEvent event) {
        this.manager = manager;
        this.event = event;
    }

    protected void calculateDamageBonus() {
        int damageBonus = 3 + (manager.getSkillLevel() / Unarmed.ironArmIncreaseLevel);

        if (damageBonus > Unarmed.ironArmBonusDamage) {
            damageBonus = Unarmed.ironArmBonusDamage;
        }

        this.damageBonus = damageBonus;
    }

    protected void modifyEventDamage() {
        int damage = event.getDamage();
        int unarmedBonus = damage + damageBonus;

        event.setDamage(damage + unarmedBonus);
    }
}
