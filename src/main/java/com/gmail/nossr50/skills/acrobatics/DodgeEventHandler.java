package com.gmail.nossr50.skills.acrobatics;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Skills;

public class DodgeEventHandler {
    private AcrobaticsManager manager;
    private Player player;

    private EntityDamageByEntityEvent event;
    private int damage;

    private int skillModifier;
    private int modifiedDamage;

    protected DodgeEventHandler(AcrobaticsManager manager, EntityDamageByEntityEvent event) {
        this.manager = manager;
        this.player = manager.getPlayer();
        this.event = event;
        this.damage = event.getDamage();
        this.skillModifier = calculateSkillModifier();
        this.modifiedDamage = calculateModifiedDamage(damage);
    }

    private int calculateSkillModifier() {
        return Misc.skillCheck(manager.getSkillLevel(), Acrobatics.DODGE_MAX_BONUS_LEVEL);
    }

    private int calculateModifiedDamage(int initialDamage) {
        int modifiedDamage = initialDamage / 2;

        if (modifiedDamage <= 0) {
            modifiedDamage = 1;
        }

        return modifiedDamage;
    }

    protected void modifyEventDamage() {
        event.setDamage(modifiedDamage);
    }

    protected void sendAbilityMessage() {
        player.sendMessage(LocaleLoader.getString("Acrobatics.Combat.Proc"));
    }

    protected void processXP() {
        if (manager.getPermissionsHandler().canGainXP()) {
            Skills.xpProcessing(player, manager.getProfile(), SkillType.ACROBATICS, damage * Acrobatics.DODGE_XP_MODIFIER);
        }
    }

    protected int getSkillModifier() {
        return skillModifier;
    }
}
