package com.gmail.nossr50.events.abilities;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;

public abstract class McMMOPlayerAbilityEvent extends PlayerEvent {
    private AbilityType ability;
    private boolean useParticleEffects;

    @Deprecated
    protected McMMOPlayerAbilityEvent(Player player, SkillType skill) {
        super(player);
        ability = skill.getAbility();
        useParticleEffects = true;
    }

    protected McMMOPlayerAbilityEvent(Player player, AbilityType ability) {
        super(player);
        this.ability = ability;
        this.useParticleEffects = true;
    }

    protected McMMOPlayerAbilityEvent(Player player, AbilityType ability, boolean useParticleEffects) {
        super(player);
        this.ability = ability;
        this.useParticleEffects = useParticleEffects;
    }

    public AbilityType getAbility() {
        return ability;
    }

    public boolean useParticleEffects() {
        return useParticleEffects;
    }

    public void shouldUseParticleEffects(boolean useParticleEffects) {
        this.useParticleEffects = useParticleEffects;
    }

    /** Rest of file is required boilerplate for custom events **/
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
