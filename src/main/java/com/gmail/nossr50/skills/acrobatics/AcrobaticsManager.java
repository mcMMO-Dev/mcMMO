package com.gmail.nossr50.skills.acrobatics;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.util.Users;

public class AcrobaticsManager {
    private Player player;
    private PlayerProfile profile;
    private int skillLevel;
    private AcrobaticsPermissionsHandler permHandler;

    public AcrobaticsManager (Player player) {
        this.player = player;
        this.profile = Users.getProfile(player);
        this.skillLevel = profile.getSkillLevel(SkillType.ACROBATICS);
        this.permHandler = new AcrobaticsPermissionsHandler(player);
    }

    /**
     * Check for fall damage reduction.
     *
     * @param event The event to check
     */
    public void rollCheck(EntityDamageEvent event) {
        if (!permHandler.hasRollPermissions()) {
            return;
        }

        RollEventHandler eventHandler = new RollEventHandler(this, event);

        if (Acrobatics.getRandom().nextInt(1000) <= eventHandler.skillModifier && !eventHandler.isFatal(eventHandler.modifiedDamage)) {
            eventHandler.modifyEventDamage();
            eventHandler.sendAbilityMessage();
            eventHandler.processXPGain(eventHandler.damage * Acrobatics.ROLL_XP_MODIFIER);
        }
        else if (!eventHandler.isFatal(event.getDamage()) && permHandler.canGainXP()){
            eventHandler.processXPGain(eventHandler.damage * Acrobatics.FALL_XP_MODIFIER);
        }
    }

    /**
     * Check for dodge damage reduction.
     *
     * @param event The event to check
     */
    public void dodgeCheck(EntityDamageByEntityEvent event) {
        if (!permHandler.canDodge()) {
            return;
        }

        DodgeEventHandler eventHandler = new DodgeEventHandler(this, event);

        if (Acrobatics.getRandom().nextInt(4000) <= eventHandler.skillModifier && !eventHandler.isFatal(eventHandler.modifiedDamage)) {
            eventHandler.modifyEventDamage();
            eventHandler.sendAbilityMessage();

            if (System.currentTimeMillis() >= profile.getRespawnATS() + 5 && permHandler.canGainXP()) {
                eventHandler.processXPGain(eventHandler.damage * Acrobatics.DODGE_XP_MODIFIER);
            }
        }
    }

    protected Player getPlayer() {
        return player;
    }

    protected PlayerProfile getProfile() {
        return profile;
    }

    protected int getSkillLevel() {
        return skillLevel;
    }

    protected AcrobaticsPermissionsHandler getPermissionsHandler() {
        return permHandler;
    }
}
