package com.gmail.nossr50.skills.unarmed;

import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.utilities.SkillTools;

public class DeflectEventHandler {
    private UnarmedManager manager;
    private EntityDamageEvent event;
    protected int skillModifier;

    protected DeflectEventHandler(UnarmedManager manager, EntityDamageEvent event) {
        this.manager = manager;
        this.event = event;

        calculateSkillModifier();
    }

    protected void calculateSkillModifier() {
        this.skillModifier = SkillTools.skillCheck(manager.getSkillLevel(), Unarmed.deflectMaxBonusLevel);
    }

    protected void sendAbilityMessage() {
        manager.getMcMMOPlayer().getPlayer().sendMessage(LocaleLoader.getString("Combat.ArrowDeflect"));
    }

    protected void cancelEvent() {
        event.setCancelled(true);
    }
}
