package com.gmail.nossr50.skills.acrobatics;

import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Skills;

public class RollEventHandler extends AcrobaticsEventHandler{
    private boolean isGraceful;
    private int damageThreshold;

    protected RollEventHandler(AcrobaticsManager manager, EntityDamageEvent event) {
        super(manager, event);

        isGracefulRoll();
        calculateSkillModifier();
        calculateDamageThreshold();
        calculateModifiedDamage();
    }

    protected void calculateSkillModifier() {
        int skillModifer = manager.getSkillLevel();

        if (isGraceful) {
            skillModifer = skillModifer * 2;
        }

        skillModifer = Misc.skillCheck(skillModifer, Acrobatics.ROLL_MAX_BONUS_LEVEL);
        this.skillModifier = skillModifer;
    }

    protected void calculateModifiedDamage() {
        int modifiedDamage = damage - damageThreshold;

        if (modifiedDamage < 0) {
            modifiedDamage = 0;
        }

        this.modifiedDamage = modifiedDamage;
    }

    protected void modifyEventDamage() {
        event.setDamage(modifiedDamage);

        if (event.getDamage() == 0) {
            event.setCancelled(true);
        }
    }


    protected void sendAbilityMessage() {
        if (isGraceful) {
            player.sendMessage(LocaleLoader.getString("Acrobatics.Ability.Proc"));
        }
        else {
            player.sendMessage(LocaleLoader.getString("Acrobatics.Roll.Text"));
        }
    }


    protected void processXPGain(int xpGain) {
        Skills.xpProcessing(player, manager.getProfile(), SkillType.ACROBATICS, xpGain);
    }

    /**
     * Check if this is a graceful roll.
     */
    private void isGracefulRoll() {
        if (manager.getPermissionsHandler().canGracefulRoll()) {
            this.isGraceful = player.isSneaking();
        }
        else {
            this.isGraceful = false;
        }
    }

    /**
     * Calculate the damage threshold for this event.
     */
    private void calculateDamageThreshold() {
        int damageThreshold = 7;

        if (isGraceful) {
            damageThreshold = damageThreshold * 2;
        }

        this.damageThreshold = damageThreshold;
    }
}
