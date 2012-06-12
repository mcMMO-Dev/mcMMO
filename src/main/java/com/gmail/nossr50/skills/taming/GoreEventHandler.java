package com.gmail.nossr50.skills.taming;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.runnables.BleedTimer;
import com.gmail.nossr50.util.Misc;

public class GoreEventHandler {
    private TamingManager manager;
    private EntityDamageEvent event;
    private Entity entity;
    protected int skillModifier;

    protected GoreEventHandler(TamingManager manager, EntityDamageEvent event) {
        this.manager = manager;
        this.event = event;
        this.entity = event.getEntity();
        calculateSkillModifier();
    }

    protected void calculateSkillModifier() {
        this.skillModifier = Misc.skillCheck(manager.getSkillLevel(), Taming.GORE_MAX_BONUS_LEVEL);
    }

    protected void modifyEventDamage() {
        event.setDamage(event.getDamage() * Taming.GORE_MULTIPLIER);
    }

    protected void sendAbilityMessage() {
        if (entity instanceof Player) {
            ((Player) entity).sendMessage(LocaleLoader.getString("Combat.StruckByGore"));
        }

        manager.getPlayer().sendMessage(LocaleLoader.getString("Combat.Gore"));
    }

    protected void applyBleed() {
        BleedTimer.add((LivingEntity) entity, Taming.GORE_BLEED_TICKS);
    }
}
