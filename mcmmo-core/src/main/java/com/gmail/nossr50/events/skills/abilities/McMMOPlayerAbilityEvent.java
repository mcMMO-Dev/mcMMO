package com.gmail.nossr50.events.skills.abilities;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.events.skills.McMMOPlayerSkillEvent;
import org.bukkit.entity.Player;

public class McMMOPlayerAbilityEvent extends McMMOPlayerSkillEvent {
    private SuperAbilityType superAbilityType;

    protected McMMOPlayerAbilityEvent(Player player, PrimarySkillType primarySkillType, SuperAbilityType superAbilityType) {
        super(player, primarySkillType);
        this.superAbilityType = superAbilityType;
    }

    public SuperAbilityType getSuperAbilityType() {
        return superAbilityType;
    }
}
