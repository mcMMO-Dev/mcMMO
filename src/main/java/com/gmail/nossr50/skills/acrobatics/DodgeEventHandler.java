package com.gmail.nossr50.skills.acrobatics;

import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Skills;

public class DodgeEventHandler extends AcrobaticsEventHandler{
    protected DodgeEventHandler(AcrobaticsManager manager, EntityDamageByEntityEvent event) {
        super(manager, event);

        calculateSkillModifier();
        calculateModifiedDamage();
    }

    protected void calculateSkillModifier() {
        this.skillModifier = Misc.skillCheck(manager.getSkillLevel(), Acrobatics.DODGE_MAX_BONUS_LEVEL);
    }

    protected void calculateModifiedDamage() {
        int modifiedDamage = damage / 2;

        if (modifiedDamage <= 0) {
            modifiedDamage = 1;
        }

        this.modifiedDamage = modifiedDamage;
    }

    protected void modifyEventDamage() {
        event.setDamage(modifiedDamage);
    }

    protected void sendAbilityMessage() {
        player.sendMessage(LocaleLoader.getString("Acrobatics.Combat.Proc"));
    }

    protected void processXPGain(int xp) {
        PlayerProfile profile = manager.getProfile();

        if (System.currentTimeMillis() >= profile.getRespawnATS() + 5) {
            Skills.xpProcessing(player, profile, SkillType.ACROBATICS, xp);
        }
    }
}
