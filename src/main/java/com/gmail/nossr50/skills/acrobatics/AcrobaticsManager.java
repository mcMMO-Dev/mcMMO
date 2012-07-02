package com.gmail.nossr50.skills.acrobatics;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class AcrobaticsManager {
    private Player player;
    private PlayerProfile profile;
    private int skillLevel;
    private Permissions permissionInstance = Permissions.getInstance();

    public AcrobaticsManager (Player player) {
        this.player = player;
        this.profile = Users.getProfile(player);
        this.skillLevel = profile.getSkillLevel(SkillType.ACROBATICS);
    }

    /**
     * Check for fall damage reduction.
     *
     * @param event The event to check
     */
    public void rollCheck(EntityDamageEvent event) {
        if (!permissionInstance.roll(player)) {
            return;
        }

        RollEventHandler eventHandler = new RollEventHandler(this, event);

        int randomChance = 1000;

        if (player.hasPermission("mcmmo.perks.lucky.acrobatics")) {
            randomChance = (int) (randomChance * 0.75);
        }

        if (Acrobatics.getRandom().nextInt(randomChance) <= eventHandler.skillModifier && !eventHandler.isFatal(eventHandler.modifiedDamage)) {
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
        if (!permissionInstance.dodge(player)) {
            return;
        }

        DodgeEventHandler eventHandler = new DodgeEventHandler(this, event);

        int randomChance = 4000;

        if (player.hasPermission("mcmmo.perks.lucky.acrobatics")) {
            randomChance = (int) (randomChance * 0.75);
        }

        if (Acrobatics.getRandom().nextInt(randomChance) <= eventHandler.skillModifier && !eventHandler.isFatal(eventHandler.modifiedDamage)) {
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
