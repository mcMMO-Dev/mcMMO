package com.gmail.nossr50.skills.axes;

import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class AxeBonusDamageEventHandler {
    private int skillLevel;
    private EntityDamageByEntityEvent event;
    private int damageBonus;

    public AxeBonusDamageEventHandler(AxeManager manager, EntityDamageByEntityEvent event) {
        this.skillLevel = manager.getSkillLevel();
        this.event = event;
    }

    protected void calculateDamageBonus() {
        int increaseLevel = Axes.bonusDamageMaxBonusLevel / Axes.bonusDamageMaxBonus;

        /* Add 1 DMG for every 50 skill levels (default value) */
        damageBonus = skillLevel / increaseLevel;

        if (damageBonus > Axes.bonusDamageMaxBonus) {
            damageBonus = Axes.bonusDamageMaxBonus;
        }
    }

    protected void modifyEventDamage() {
        int damage = event.getDamage();

        event.setDamage(damage + damageBonus);
    }
}
