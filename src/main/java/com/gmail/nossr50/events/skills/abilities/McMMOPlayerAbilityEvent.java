package com.gmail.nossr50.events.skills.abilities;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.events.skills.McMMOPlayerSkillEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.entity.Player;

import java.util.Objects;

public class McMMOPlayerAbilityEvent extends McMMOPlayerSkillEvent {
    private final SuperAbilityType ability;

    /**
     * Create a new McMMOPlayerAbilityEvent.
     *
     * @param player The player involved in this event
     * @param skill The skill involved in this event
     * @deprecated Use {@link #McMMOPlayerAbilityEvent(McMMOPlayer, PrimarySkillType)} instead
     */
    @Deprecated(forRemoval = true, since = "2.2.010")
    protected McMMOPlayerAbilityEvent(Player player, PrimarySkillType skill) {
        super(Objects.requireNonNull(UserManager.getPlayer(player)), skill);
        ability = mcMMO.p.getSkillTools().getSuperAbility(skill);
    }

    /**
     * Create a new McMMOPlayerAbilityEvent.
     * @param mmoPlayer The McMMOPlayer involved in this event
     * @param skill The skill involved in this event
     */
    protected McMMOPlayerAbilityEvent(McMMOPlayer mmoPlayer, PrimarySkillType skill) {
        super(mmoPlayer, skill);
        ability = mcMMO.p.getSkillTools().getSuperAbility(skill);
    }

    public SuperAbilityType getAbility() {
        return ability;
    }
}
