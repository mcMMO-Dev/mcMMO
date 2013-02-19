package com.gmail.nossr50.skills.swords;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.runnables.BleedTimer;
import com.gmail.nossr50.skills.utilities.SkillTools;

public class BleedEventHandler {
    private int skillLevel;
    private LivingEntity defender;
    protected int skillModifier;

    protected BleedEventHandler(SwordsManager manager, LivingEntity defender) {
        this.skillLevel = manager.getSkillLevel();
        this.defender = defender;

        calculateSkillModifier();
    }

    protected void calculateSkillModifier() {
        this.skillModifier = SkillTools.skillCheck(skillLevel, Swords.bleedMaxBonusLevel);
    }

    protected void addBleedTicks() {
        int bleedTicks;

        if (skillLevel >= Swords.bleedMaxBonusLevel) {
            bleedTicks = Swords.bleedMaxTicks;
        }
        else {
            bleedTicks = Swords.bleedBaseTicks;
        }

        BleedTimer.add(defender, bleedTicks);
    }

    protected void sendAbilityMessages() {
        if (defender instanceof Player) {
            ((Player) defender).sendMessage(LocaleLoader.getString("Swords.Combat.Bleeding.Started"));
        }
    }
}
