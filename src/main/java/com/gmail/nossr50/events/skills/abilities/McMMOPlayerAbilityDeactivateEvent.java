package com.gmail.nossr50.events.skills.abilities;

import static java.util.Objects.requireNonNull;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class McMMOPlayerAbilityDeactivateEvent extends McMMOPlayerAbilityEvent {
    @Deprecated(forRemoval = true, since = "2.2.010")
    public McMMOPlayerAbilityDeactivateEvent(@NotNull Player player,
            @NotNull PrimarySkillType skill) {
        this(requireNonNull(UserManager.getPlayer(player)), skill);
    }

    public McMMOPlayerAbilityDeactivateEvent(@NotNull McMMOPlayer mmoPlayer,
            @NotNull PrimarySkillType skill) {
        super(mmoPlayer, skill);
    }
}
