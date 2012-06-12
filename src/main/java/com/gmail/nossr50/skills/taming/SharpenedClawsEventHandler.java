package com.gmail.nossr50.skills.taming;

import org.bukkit.event.entity.EntityDamageEvent;

public class SharpenedClawsEventHandler {
    private EntityDamageEvent event;

    public SharpenedClawsEventHandler (EntityDamageEvent event) {
        this.event = event;
    }

    protected void modifyEventDamage() {
        event.setDamage(event.getDamage() + Taming.SHARPENED_CLAWS_BONUS);
    }
}
