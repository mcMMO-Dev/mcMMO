package com.gmail.nossr50.datatypes.experience.context;

import com.neetgames.jmal.LivingEntity;
import com.neetgames.mcmmo.experience.context.CombatExperienceContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CombatExperienceContextImpl implements CombatExperienceContext {

    private final @NotNull LivingEntity livingEntity;

    public CombatExperienceContextImpl(@NotNull LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
    }

    @Nullable
    @Override
    public Object getContext() {
        return livingEntity;
    }

    /**
     * Get the {@link LivingEntity} involved in this experience context
     *
     * @return the {@link LivingEntity} involved in this experience context
     */
    public @NotNull LivingEntity getLivingEntity() {
        return livingEntity;
    }
}
