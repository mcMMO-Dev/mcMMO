package com.gmail.nossr50.events.skills.herbalism;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.skills.McMMOPlayerSkillEvent;

public abstract class McMMOPlayerHerbalismEvent extends McMMOPlayerSkillEvent {
    public McMMOPlayerHerbalismEvent(Player player) {
        super(player, SkillType.HERBALISM);
    }
}
