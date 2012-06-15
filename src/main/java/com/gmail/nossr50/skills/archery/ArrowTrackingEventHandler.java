package com.gmail.nossr50.skills.archery;

import org.bukkit.entity.LivingEntity;

import com.gmail.nossr50.util.Misc;

public class ArrowTrackingEventHandler {
    private ArcheryManager manager;
    private LivingEntity entity;

    protected int skillModifier;

    protected ArrowTrackingEventHandler (ArcheryManager manager, LivingEntity entity) {
        this.manager = manager;
        this.entity = entity;

        calculateSkillModifier();
    }

    protected void calculateSkillModifier() {
        this.skillModifier = Misc.skillCheck(manager.getSkillLevel(), Archery.ARROW_TRACKING_MAX_BONUS_LEVEL);
    }

    protected void addToTracker() {
        Archery.incrementTrackerValue(entity);
    }
}
