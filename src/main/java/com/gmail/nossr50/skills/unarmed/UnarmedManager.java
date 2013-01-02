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
        this.skillLevel = profile.getSkillLevel(SkillType.UNARMED);
        this.permissionsInstance =  Permissions.getInstance();
    }

    /**
     * Check for disarm.
     *
     * @param defender The defending player
     */
    public void disarmCheck(Player defender) {
        if(player == null)
            return;

        if(permissionsInstance == null)
            return;

        if (!permissionsInstance.disarm(player)) {
            return;
        }

        DisarmEventHandler eventHandler = new DisarmEventHandler(this, defender);

        if (eventHandler.isHoldingItem()) {
            eventHandler.calculateSkillModifier();

            int randomChance = 100;

            if (player.hasPermission("mcmmo.perks.lucky.unarmed")) {
                randomChance = (int) (randomChance * 0.75);
            }

            float chance = (float) (((double) Unarmed.DISARM_MAX_CHANCE / (double) Unarmed.DISARM_MAX_BONUS_LEVEL) * skillLevel);
            if (chance > Unarmed.DISARM_MAX_CHANCE) chance = Unarmed.DISARM_MAX_CHANCE;

            if (chance > Unarmed.getRandom().nextInt(randomChance)) {
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
        if(player == null)
            return;

        if(permissionsInstance == null)
            return;

        if (!permissionsInstance.deflect(player)) {
            return;
        }

        DeflectEventHandler eventHandler = new DeflectEventHandler(this, event);

        int randomChance = 100;

        if (player.hasPermission("mcmmo.perks.lucky.unarmed")) {
            randomChance = (int) (randomChance * 0.75);
        }

        float chance = (float) (((double) Unarmed.DEFLECT_MAX_CHANCE / (double) Unarmed.DEFLECT_MAX_BONUS_LEVEL) * skillLevel);
        if (chance > Unarmed.DEFLECT_MAX_CHANCE) chance = Unarmed.DEFLECT_MAX_CHANCE;

        if (chance > Unarmed.getRandom().nextInt(randomChance)) {
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
        if(player == null)
            return;

        if(permissionsInstance == null)
            return;

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
        if(defender == null)
            return false;

        if(permissionsInstance == null)
            return false;

        if (!permissionsInstance.ironGrip(defender)) {
            return false;
        }

        IronGripEventHandler eventHandler = new IronGripEventHandler(this, defender);

        int randomChance = 100;

        if (defender.hasPermission("mcmmo.perks.lucky.unarmed")) {
            randomChance = (int) (randomChance * 0.75);
        }

        float chance = (float) (((double) Unarmed.IRON_GRIP_MAX_CHANCE / (double) Unarmed.IRON_GRIP_MAX_BONUS_LEVEL) * skillLevel);
        if (chance > Unarmed.IRON_GRIP_MAX_CHANCE) chance = Unarmed.IRON_GRIP_MAX_CHANCE;

        if (chance > Unarmed.getRandom().nextInt(randomChance)) {
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
