package com.gmail.nossr50.skills.archery;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class ArcheryManager {
    private Player player;
    private PlayerProfile profile;
    private int skillLevel;

    public ArcheryManager (Player player) {
        this.player = player;
        this.profile = Users.getProfile(player);
        this.skillLevel = profile.getSkillLevel(SkillType.ARCHERY);
    }

    /**
     * Track arrows fired for later retrieval.
     *
     * @param livingEntity Entity damaged by the arrow
     */
    public void trackArrows(LivingEntity livingEntity) {
        if (Misc.isCitizensNPC(player) || !Permissions.trackArrows(player)) {
            return;
        }

        ArrowTrackingEventHandler eventHandler = new ArrowTrackingEventHandler(this, livingEntity);

        int randomChance = 100;
        if (Permissions.luckyArchery(player)) {
            randomChance = (int) (randomChance * 0.75);
        }

        float chance = ((float) Archery.ARROW_TRACKING_MAX_BONUS / Archery.ARROW_TRACKING_MAX_BONUS_LEVEL) * eventHandler.skillModifier;

        if (chance > Archery.getRandom().nextInt(randomChance)) {
            eventHandler.addToTracker();
        }
    }

    /**
     * Check for Daze.
     *
     * @param defender Defending player
     * @param event The event to modify
     */
    public void dazeCheck(Player defender, EntityDamageEvent event) {
        if (Misc.isCitizensNPC(player) || !Permissions.daze(player)) {
            return;
        }

        DazeEventHandler eventHandler = new DazeEventHandler(this, event, defender);

        int randomChance = 100;
        if (Permissions.luckyArchery(player)) {
            randomChance = (int) (randomChance * 0.75);
        }

        float chance = ((float) Archery.DAZE_MAX_BONUS / Archery.DAZE_MAX_BONUS_LEVEL) * eventHandler.skillModifier;

        if (chance > Archery.getRandom().nextInt(randomChance)) {
            eventHandler.handleDazeEffect();
            eventHandler.sendAbilityMessages();
        }
    }

    /**
     * Handle archery bonus damage.
     *
     * @param event The event to modify.
     */
    public void bonusDamage(EntityDamageEvent event) {
        if (Misc.isCitizensNPC(player) || !Permissions.archeryBonus(player)) {
            return;
        }

        if (skillLevel >= Archery.BONUS_DAMAGE_INCREASE_LEVEL) {
            ArcheryBonusDamageEventHandler eventHandler = new ArcheryBonusDamageEventHandler(this, event);

            eventHandler.calculateDamageBonus();
            eventHandler.modifyEventDamage();
        }
    }

    protected int getSkillLevel() {
        return skillLevel;
    }

    protected Player getPlayer() {
        return player;
    }
}
