package com.gmail.nossr50.skills.acrobatics;

import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Skills;

public class RollEventHandler extends AcrobaticsEventHandler {
    private boolean isGraceful;
    private int damageThreshold;

    protected RollEventHandler(AcrobaticsManager manager, EntityDamageEvent event) {
        super(manager, event);

        isGracefulRoll();
        calculateSkillModifier();
        calculateDamageThreshold();
        calculateModifiedDamage();
    }

    @Override
    protected void calculateSkillModifier() {
        int skillModifer = manager.getSkillLevel();

        if (isGraceful) {
            skillModifer = skillModifer * 2;
        }

        skillModifer = Misc.skillCheck(skillModifer, Acrobatics.ROLL_MAX_BONUS_LEVEL);
        this.skillModifier = skillModifer;
    }

    @Override
    protected void calculateModifiedDamage() {
        int modifiedDamage = damage - damageThreshold;

        if (modifiedDamage < 0) {
            modifiedDamage = 0;
        }

        this.modifiedDamage = modifiedDamage;
    }

    @Override
    protected void modifyEventDamage() {
        event.setDamage(modifiedDamage);

        if (event.getDamage() == 0) {
            event.setCancelled(true);
        }
    }


    @Override
    protected void sendAbilityMessage() {
        if (isGraceful) {
            player.sendMessage(LocaleLoader.getString("Acrobatics.Ability.Proc"));
        }
        else {
            player.sendMessage(LocaleLoader.getString("Acrobatics.Roll.Text"));
        }
    }


    @Override
    protected void processXPGain(int xpGain) {
        Skills.xpProcessing(player, manager.getProfile(), SkillType.ACROBATICS, xpGain);
    }

    /**
     * Check if this is a graceful roll.
     */
    private void isGracefulRoll() {
        if (Permissions.getInstance().gracefulRoll(player)) {
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
