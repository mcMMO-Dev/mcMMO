package com.gmail.nossr50.skills.taming;

import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class ThickFurEventHandler {
    private DamageCause cause;
    private EntityDamageEvent event;
    private Wolf wolf;

    protected ThickFurEventHandler (EntityDamageEvent event, DamageCause cause) {
        this.cause = cause;
        this.event = event;
        this.wolf = (Wolf) event.getEntity();
    }

    protected void modifyEventDamage() {
        switch (cause) {
        case FIRE_TICK:
            wolf.setFireTicks(0);
            break;

        case ENTITY_ATTACK:
        case PROJECTILE:
            event.setDamage(event.getDamage() / Taming.THICK_FUR_MODIFIER);
            break;

        default:
            break;
        }
    }
}
