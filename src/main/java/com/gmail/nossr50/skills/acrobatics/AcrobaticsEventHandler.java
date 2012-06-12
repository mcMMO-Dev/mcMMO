package com.gmail.nossr50.skills.acrobatics;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public abstract class AcrobaticsEventHandler {
    protected AcrobaticsManager manager;
    protected Player player;

    protected EntityDamageEvent event;
    protected int damage;
    protected int skillModifier;
    protected int modifiedDamage;

    protected AcrobaticsEventHandler(AcrobaticsManager manager, EntityDamageEvent event) {
        this.manager = manager;
        this.player = manager.getPlayer();
        this.event = event;
        this.damage = event.getDamage();
    }

    /**
     * Calculate the skill modifier applied for this event.
     */
    protected abstract void calculateSkillModifier();

    /**
     * Calculate the modified damage for this event.
     */
    protected abstract void calculateModifiedDamage();

    /**
     * Modify the damage dealt by this event.
     */
    protected abstract void modifyEventDamage();

    /**
     * Send the ability message for this event.
     */
    protected abstract void sendAbilityMessage();

    /**
     * Process XP gain from this event.
     */
    protected abstract void processXPGain(int xp);

    /**
     * Check to ensure you're not gaining XP after you die.
     *
     * @param damage The damage to be dealt
     * @return true if the damage is fatal, false otherwise
     */
    protected boolean isFatal(int damage) {
        if (player.getHealth() - damage < 1) {
            return true;
        }
        else {
            return false;
        }
    }
}
