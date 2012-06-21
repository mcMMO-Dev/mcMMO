package com.gmail.nossr50.skills.swords;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.runnables.BleedTimer;
import com.gmail.nossr50.util.Misc;

public class BleedEventHandler {
    private SwordsManager manager;
    private int skillLevel;
    private LivingEntity defender;

    protected int skillModifier;

    protected BleedEventHandler(SwordsManager manager, LivingEntity defender) {
        this.manager = manager;
        this.skillLevel = manager.getSkillLevel();
        this.defender = defender;

        calculateSkillModifier();
    }

    protected void calculateSkillModifier() {
        this.skillModifier = Misc.skillCheck(skillLevel, Swords.BLEED_MAX_BONUS_LEVEL);
    }

    protected void addBleedTicks() {
        int bleedTicks;

        if (skillLevel >= Swords.BLEED_MAX_BONUS_LEVEL) {
            bleedTicks = Swords.MAX_BLEED_TICKS;
        }
        else {
            bleedTicks = Swords.BASE_BLEED_TICKS;
        }

        BleedTimer.add(defender, bleedTicks);
    }

    protected void sendAbilityMessages() {
        manager.getPlayer().sendMessage(LocaleLoader.getString("Swords.Combat.Bleeding"));

        if (defender instanceof Player) {
            ((Player) defender).sendMessage(LocaleLoader.getString("Swords.Combat.Bleeding.Started"));
        }
    }
}
