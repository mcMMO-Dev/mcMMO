package com.gmail.nossr50.skills.acrobatics;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class AcrobaticsManager {
    private Player player;
    private PlayerProfile profile;
    private int skillLevel;
    private Permissions permissionInstance;

    public AcrobaticsManager (Player player) {
        this.player = player;
        this.profile = Users.getProfile(player);
        this.skillLevel = profile.getSkillLevel(SkillType.ACROBATICS);
        this.permissionInstance = Permissions.getInstance();
    }

    /**
     * Check for fall damage reduction.
     *
     * @param event The event to check
     */
    public void rollCheck(EntityDamageEvent event) {
        if(player == null)
            return;

        if (!permissionInstance.roll(player)) {
            return;
        }

        if(Config.getInstance().getAcrobaticsAFKDisabled() && player.isInsideVehicle())
            return;

        RollEventHandler eventHandler = new RollEventHandler(this, event);

        int randomChance = 100;

        if (player.hasPermission("mcmmo.perks.lucky.acrobatics")) {
            randomChance = (int) (randomChance * 0.75);
        }

        float chance = (float) (((double) Acrobatics.ROLL_MAX_CHANCE / (double) Acrobatics.ROLL_MAX_BONUS_LEVEL) * skillLevel);
        if (chance > Acrobatics.ROLL_MAX_CHANCE) chance = Acrobatics.ROLL_MAX_CHANCE;
        if (eventHandler.isGraceful) {
        	chance = (float) (((double) Acrobatics.GRACEFUL_MAX_CHANCE / (double) Acrobatics.GRACEFUL_MAX_BONUS_LEVEL) * skillLevel);
        	if (chance > Acrobatics.GRACEFUL_MAX_CHANCE) chance = Acrobatics.GRACEFUL_MAX_CHANCE;
        }

        if (chance > Acrobatics.getRandom().nextInt(randomChance) && !eventHandler.isFatal(eventHandler.modifiedDamage)) {
            eventHandler.modifyEventDamage();
            eventHandler.sendAbilityMessage();
            eventHandler.processXPGain(eventHandler.damage * Acrobatics.ROLL_XP_MODIFIER);
        }
        else if (!eventHandler.isFatal(event.getDamage())) {
            eventHandler.processXPGain(eventHandler.damage * Acrobatics.FALL_XP_MODIFIER);
        }
    }

    /**
     * Check for dodge damage reduction.
     *
     * @param event The event to check
     */
    public void dodgeCheck(EntityDamageEvent event) {
        if(player == null)
            return;

        if (!permissionInstance.dodge(player)) {
            return;
        }

        DodgeEventHandler eventHandler = new DodgeEventHandler(this, event);

        int randomChance = 100;

        if (player.hasPermission("mcmmo.perks.lucky.acrobatics")) {
            randomChance = (int) (randomChance * 0.75);
        }

        float chance = (float) (((double) Acrobatics.DODGE_MAX_CHANCE / (double) Acrobatics.DODGE_MAX_BONUS_LEVEL) * skillLevel);
        if (chance > Acrobatics.DODGE_MAX_CHANCE) chance = Acrobatics.DODGE_MAX_CHANCE;

        if (chance > Acrobatics.getRandom().nextInt(randomChance) && !eventHandler.isFatal(eventHandler.modifiedDamage)) {
            eventHandler.modifyEventDamage();
            eventHandler.sendAbilityMessage();
            eventHandler.processXPGain(eventHandler.damage * Acrobatics.DODGE_XP_MODIFIER);
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
}
