package com.gmail.nossr50.skills.axes;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.Misc;

public class AxeManager extends SkillManager {
    public AxeManager(Player player) {
        super(player, SkillType.AXES);
    }

    /**
     * Apply bonus to damage done by axes.
     *
     * @param event The event to modify
     */
    public void bonusDamage(EntityDamageByEntityEvent event) {
        if (Misc.isNPC(player)) {
            return;
        }

        AxeBonusDamageEventHandler eventHandler = new AxeBonusDamageEventHandler(this, event);

        eventHandler.calculateDamageBonus();
        eventHandler.modifyEventDamage();
    }
}
