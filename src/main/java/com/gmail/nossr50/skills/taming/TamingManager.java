package com.gmail.nossr50.skills.taming;

import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class TamingManager {
    private Player player;
    private PlayerProfile profile;
    private int skillLevel;
    private Permissions permissionsInstance;

    public TamingManager (Player player) {
        this.player = player;
        this.profile = Users.getProfile(player);
        this.skillLevel = profile.getSkillLevel(SkillType.TAMING);
        this.permissionsInstance = Permissions.getInstance();
    }

    /**
     * Apply the Fast Food Service ability.
     *
     * @param wolf The wolf using the ability
     * @param damage The damage being absorbed by the wolf
     */
    public void fastFoodService(Wolf wolf, int damage) {
        if (!permissionsInstance.fastFoodService(player)) {
            return;
        }

        if (skillLevel >= Taming.FAST_FOOD_SERVICE_ACTIVATION_LEVEL) {
            if (Taming.getRandom().nextInt(100) < Taming.FAST_FOOD_SERVICE_ACTIVATION_CHANCE) {
                FastFoodServiceEventHandler eventHandler = new FastFoodServiceEventHandler(wolf);

                eventHandler.modifyHealth(damage);
            }
        }
    }

    /**
     * Apply the Sharpened Claws ability.
     *
     * @param event The event to modify
     */
    public void sharpenedClaws(EntityDamageEvent event) {
        if (!permissionsInstance.sharpenedClaws(player)) {
            return;
        }

        if (skillLevel >= Taming.SHARPENED_CLAWS_ACTIVATION_LEVEL) {
            SharpenedClawsEventHandler eventHandler = new SharpenedClawsEventHandler(event);

            eventHandler.modifyEventDamage();
        }
    }

    /**
     * Apply the Gore ability.
     *
     * @param event The event to modify
     */
    public void gore(EntityDamageEvent event) {
        if (!permissionsInstance.gore(player)) {
            return;
        }

        GoreEventHandler eventHandler = new GoreEventHandler(this, event);

        if (Taming.getRandom().nextInt(1000) <= eventHandler.skillModifier) {
            eventHandler.modifyEventDamage();
            eventHandler.applyBleed();
            eventHandler.sendAbilityMessage();
        }
    }

    protected int getSkillLevel() {
        return skillLevel;
    }

    protected Player getPlayer() {
        return player;
    }
}
