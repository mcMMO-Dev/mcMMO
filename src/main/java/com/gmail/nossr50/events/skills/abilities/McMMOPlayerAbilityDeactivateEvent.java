package com.gmail.nossr50.events.skills.abilities;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import org.bukkit.entity.Player;

public class McMMOPlayerAbilityDeactivateEvent extends McMMOPlayerAbilityEvent {
    public McMMOPlayerAbilityDeactivateEvent(Player player, PrimarySkillType primarySkillType, SuperAbilityType superAbilityType) {
        super(player, primarySkillType, superAbilityType);
    }
}
