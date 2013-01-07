package com.gmail.nossr50.skills.unarmed;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class UnarmedManager {
    private Player player;
    private PlayerProfile profile;
    private int skillLevel;

    public UnarmedManager (Player player) {
        this.player = player;
        this.profile = Users.getProfile(player);
        this.skillLevel = profile.getSkillLevel(SkillType.UNARMED);
    }

    /**
     * Check for disarm.
     *
     * @param defender The defending player
     */
    public void disarmCheck(Player defender) {
        if(player == null)
            return;

        if (!Permissions.disarm(player)) {
            return;
        }

        DisarmEventHandler eventHandler = new DisarmEventHandler(this, defender);

        if (eventHandler.isHoldingItem()) {
            eventHandler.calculateSkillModifier();

            int disarmChanceMax = AdvancedConfig.getInstance().getDisarmChanceMax();
            int disarmMaxLevel = AdvancedConfig.getInstance().getDisarmMaxBonusLevel();
            int randomChance = 100;

            if (Permissions.luckyUnarmed(player)) {
                randomChance = (int) (randomChance * 0.75);
            }

            final float chance = (float) (((double) disarmChanceMax / (double) disarmMaxLevel) * skillLevel);
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

        if (!Permissions.deflect(player)) {
            return;
        }

        DeflectEventHandler eventHandler = new DeflectEventHandler(this, event);

        int deflectChanceMax = AdvancedConfig.getInstance().getDeflectChanceMax();
        int deflectMaxLevel = AdvancedConfig.getInstance().getDeflectMaxBonusLevel();
        int randomChance = 100;

        if (Permissions.luckyUnarmed(player)) {
            randomChance = (int) (randomChance * 0.75);
        }

        final float chance = (float) (((double) deflectChanceMax / (double) deflectMaxLevel) * skillLevel);
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

        if (!Permissions.unarmedBonus(player)) {
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

        if (!Permissions.ironGrip(defender)) {
            return false;
        }

        IronGripEventHandler eventHandler = new IronGripEventHandler(this, defender);

        int ironGripChanceMax = AdvancedConfig.getInstance().getIronGripChanceMax();
        int ironGripMaxLevel = AdvancedConfig.getInstance().getIronGripMaxBonusLevel();
        int randomChance = 100;

        if (Permissions.luckyUnarmed(defender)) {
            randomChance = (int) (randomChance * 0.75);
        }

        final float chance = (float) (((double) ironGripChanceMax / (double) ironGripMaxLevel) * skillLevel);
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
