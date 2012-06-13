package com.gmail.nossr50.skills.archery;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;


public class ArcheryManager {
    private Player player;
    private PlayerProfile profile;
    private int skillLevel;
    private Permissions permissionsInstance;

    public ArcheryManager (Player player) {
        this.player = player;
        this.profile = Users.getProfile(player);
        this.skillLevel = profile.getSkillLevel(SkillType.TAMING);
        this.permissionsInstance =  Permissions.getInstance();
    }

    /**
     * Track arrows fired for later retrieval.
     *
     * @param livingEntity Entity damaged by the arrow
     */
    public void trackArrows(LivingEntity livingEntity) {
        if (!permissionsInstance.trackArrows(player)) {
            return;
        }

        ArrowTrackingEventHandler eventHandler = new ArrowTrackingEventHandler(this, livingEntity);

        if (Archery.getRandom().nextInt(1000) <= eventHandler.skillModifier) {
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
        if (!permissionsInstance.daze(player)) {
            return;
        }

        DazeEventHandler eventHandler = new DazeEventHandler(this, event, defender);

        if (Archery.getRandom().nextInt(2000) <= eventHandler.skillModifier) {
            eventHandler.handleDazeEffect();
            eventHandler.sendAbilityMessages();
        }
    }

//    public void retrieveArrows() {
//        if (!permissionsInstance.trackArrows(player)) {
//            return;
//        }
//
//        if ()
//        for (Iterator<Map.Entry<Entity, Integer>> it = arrowTracker.entrySet().iterator() ; it.hasNext() ; ) { //This is a wee bit confusing...
//            Entry<Entity, Integer> entry = it.next();
//
//            if (entry.getKey() == entity) { //Shouldn't we be using .equals() here?
//                Misc.dropItems(entity.getLocation(), new ItemStack(Material.ARROW), entry.getValue());
//                it.remove();
//                return;
//            }
//        }
//    }

    protected int getSkillLevel() {
        return skillLevel;
    }

    protected Player getPlayer() {
        return player;
    }
}
