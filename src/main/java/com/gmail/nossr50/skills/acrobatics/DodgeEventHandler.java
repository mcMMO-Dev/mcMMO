package com.gmail.nossr50.skills.acrobatics;

import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Skills;

public class DodgeEventHandler extends AcrobaticsEventHandler {
    protected DodgeEventHandler(AcrobaticsManager manager, EntityDamageEvent event) {
        super(manager, event);

        calculateSkillModifier();
        calculateModifiedDamage();
    }

    @Override
    protected void calculateSkillModifier() {
        this.skillModifier = Misc.skillCheck(manager.getSkillLevel(), Acrobatics.DODGE_MAX_BONUS_LEVEL);
    }

    @Override
    protected void calculateModifiedDamage() {
        int modifiedDamage = damage / 2;

        if (modifiedDamage <= 0) {
            modifiedDamage = 1;
        }

        this.modifiedDamage = modifiedDamage;
    }

    @Override
    protected void modifyEventDamage() {
        event.setDamage(modifiedDamage);
    }

    @Override
    protected void sendAbilityMessage() {
        player.sendMessage(LocaleLoader.getString("Acrobatics.Combat.Proc"));
    }

    @Override
    protected void processXPGain(int xp) {
        PlayerProfile profile = manager.getProfile();

        if (System.currentTimeMillis() >= profile.getRespawnATS() + 5) {
            Skills.xpProcessing(player, profile, SkillType.ACROBATICS, xp);
        }
    }
}
