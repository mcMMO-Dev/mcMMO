package com.gmail.nossr50.skills.axes;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;

public class CriticalHitEventHandler {
    private AxeManager manager;
    private EntityDamageByEntityEvent event;
    private int damage;

    protected Entity defender;
    protected int skillModifier;

    public CriticalHitEventHandler(AxeManager manager, EntityDamageByEntityEvent event) {
        this.manager = manager;
        this.event = event;
        this.defender = event.getEntity();
        this.damage = event.getDamage();

        calculateSkillModifier();
    }

    protected void modifyEventDamage() {
        if (defender instanceof Player) {
            event.setDamage((int) (damage * Axes.criticalHitPVPModifier));
        }
        else {
            event.setDamage((int) (damage * Axes.criticalHitPVEModifier));
        }
    }

    protected void sendAbilityMessages() {
        manager.getPlayer().sendMessage(LocaleLoader.getString("Axes.Combat.CriticalHit"));

        if (defender instanceof Player) {
            ((Player) defender).sendMessage(LocaleLoader.getString("Axes.Combat.CritStruck"));
        }
    }

    private void calculateSkillModifier() {
        this.skillModifier = Misc.skillCheck(manager.getSkillLevel(), Axes.criticalHitMaxBonusLevel);
    }
}
