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
        int damageBonus = 3 + (manager.getSkillLevel() / Unarmed.BONUS_DAMAGE_INCREASE_LEVEL);

        if (damageBonus > Unarmed.BONUS_DAMAGE_MAX_BONUS_MODIFIER) {
            damageBonus = Unarmed.BONUS_DAMAGE_MAX_BONUS_MODIFIER;
        }

        this.damageBonus = damageBonus;
    }

    protected void modifyEventDamage() {
        int damage = event.getDamage();
        int unarmedBonus = damage + damageBonus;

        event.setDamage(damage + unarmedBonus);
    }
}
