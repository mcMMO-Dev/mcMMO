package com.gmail.nossr50.skills.taming;

import org.bukkit.event.entity.EntityDamageEvent;

public class ShockProofEventHandler {
    private EntityDamageEvent event;

    protected ShockProofEventHandler (EntityDamageEvent event) {
        this.event = event;
    }

    protected void modifyEventDamage() {
        event.setDamage(event.getDamage() / Taming.SHOCK_PROOF_MODIFIER);
    }
}
