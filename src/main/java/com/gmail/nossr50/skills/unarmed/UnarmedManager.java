package com.gmail.nossr50.skills.unarmed;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class UnarmedManager {
    private Player player;
    private PlayerProfile profile;
    private int skillLevel;
    private Permissions permissionsInstance;

    public UnarmedManager (Player player) {
        this.player = player;
        this.profile = Users.getProfile(player);
        this.skillLevel = profile.getSkillLevel(SkillType.TAMING);
        this.permissionsInstance =  Permissions.getInstance();
    }

    /**
     * Check for disarm.
     *
     * @param defender The defending player
     */
    public void disarmCheck(Player defender) {
        if (!permissionsInstance.disarm(player)) {
            return;
        }

        DisarmEventHandler eventHandler = new DisarmEventHandler(this, defender);

        if (eventHandler.isHoldingItem()) {
            eventHandler.calculateSkillModifier();

            if (Unarmed.getRandom().nextInt(3000) < eventHandler.skillModifier) {
                if (!hasIronGrip(defender)) {
                    eventHandler.sendAbilityMessage();
                    eventHandler.handleDisarm();
                }
            }
        }
    }

    /**
     * Check for arrow deflection.
     *
     * @param defender The defending player
     * @param event The event to modify
     */
    public void deflectCheck(EntityDamageEvent event) {
        if (!permissionsInstance.deflect(player)) {
            return;
        }

        DeflectEventHandler eventHandler = new DeflectEventHandler(this, event);

        if (Unarmed.getRandom().nextInt(2000) < eventHandler.skillModifier) {
            eventHandler.cancelEvent();
            eventHandler.sendAbilityMessage();
        }
    }

    /**
     * Handle Unarmed bonus damage.
     *
     * @param event The event to modify.
     */
    public void bonusDamage(EntityDamageEvent event) {
        if (!permissionsInstance.unarmedBonus(player)) {
            return;
        }

        UnarmedBonusDamageEventHandler eventHandler = new UnarmedBonusDamageEventHandler(this, event);

        eventHandler.calculateDamageBonus();
        eventHandler.modifyEventDamage();
    }

    /**
     * Check Iron Grip ability success
     *
     * @param defender The defending player
     * @return true if the defender was not disarmed, false otherwise
     */
    private boolean hasIronGrip(Player defender) {
        //TODO: Add permission for Iron Grip

        IronGripEventHandler eventHandler = new IronGripEventHandler(this, defender);

        if (Unarmed.getRandom().nextInt(1000) < eventHandler.skillModifier) {
            eventHandler.sendAbilityMessages();
            return true;
        }
        else {
            return false;
        }
    }

    protected int getSkillLevel() {
        return skillLevel;
    }

    protected Player getPlayer() {
        return player;
    }
}
