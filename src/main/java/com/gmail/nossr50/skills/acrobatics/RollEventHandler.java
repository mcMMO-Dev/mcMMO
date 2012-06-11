package com.gmail.nossr50.skills.acrobatics;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Skills;

public class RollEventHandler {
    private AcrobaticsManager manager;
    private Player player;
    private AcrobaticsPermissionsHandler permHandler;

    private EntityDamageEvent event;
    private int damage;

    private boolean isGraceful;
    private int skillModifier;
    private int damageThreshold;
    private int modifiedDamage;

    protected RollEventHandler(AcrobaticsManager manager, EntityDamageEvent event) {
        this.manager = manager;
        this.player = manager.getPlayer();
        this.permHandler = manager.getPermissionsHandler();
        this.event = event;
        this.damage = event.getDamage();
        this.isGraceful = isGracefulRoll();
        this.skillModifier = calculateSkillModifier();
        this.damageThreshold = calculateDamageThreshold();
        this.modifiedDamage = calculateModifiedDamage(damage);
    }

    private boolean isGracefulRoll() {
        if (permHandler.canGracefulRoll()) {
            return player.isSneaking();
        }
        else {
            return false;
        }
    }

    private int calculateSkillModifier() {
        int skillModifer = manager.getSkillLevel();

        if (isGraceful) {
            skillModifer = skillModifer * 2;
        }

        skillModifer = Misc.skillCheck(skillModifer, Acrobatics.ROLL_MAX_BONUS_LEVEL);
        return skillModifer;
    }

    private int calculateDamageThreshold() {
        int damageThreshold = 7;

        if (isGraceful) {
            damageThreshold = damageThreshold * 2;
        }

        return damageThreshold;
    }

    private int calculateModifiedDamage(int initialDamage) {
        int modifiedDamage = initialDamage - damageThreshold;

        if (modifiedDamage < 0) {
            modifiedDamage = 0;
        }

        return modifiedDamage;
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

    protected void processFallXPGain() {
        processXPGain(damage * Acrobatics.FALL_XP_MODIFIER);
    }

    protected void processRollXPGain() {
        processXPGain(damage * Acrobatics.ROLL_XP_MODIFIER);
    }

    private void processXPGain(int xpGain) {
        if (permHandler.canGainXP()) {
            Skills.xpProcessing(player, manager.getProfile(), SkillType.ACROBATICS, xpGain);
        }
    }

    protected boolean isFatal(int damage) {
        if (player.getHealth() - damage < 1) {
            return true;
        }
        else {
            return false;
        }
    }

    protected boolean isGraceful() {
        return isGraceful;
    }

    protected int getSkillModifier() {
        return skillModifier;
    }

    protected int getModifiedDamage() {
        return modifiedDamage;
    }
}
