package com.gmail.nossr50.runnables;

import java.util.HashSet;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Combat;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.mcLocale;

public class mcBleedTimer implements Runnable {
    private final mcMMO plugin;

    private static HashSet<LivingEntity> bleedList = new HashSet<LivingEntity>();
    private static HashSet<LivingEntity> bleedAddList = new HashSet<LivingEntity>();
    private static HashSet<LivingEntity> bleedRemoveList = new HashSet<LivingEntity>();

    private static boolean lock = false;

    public mcBleedTimer(final mcMMO plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        updateBleedList();

        // Player bleed simulation
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player == null) {
                continue;
            }

            PlayerProfile PP = Users.getProfile(player);
            if (PP == null) {
                continue;
            }

            if (PP.getBleedTicks() >= 1) {

                //Never kill with Bleeding
                if (player.getHealth() - 2 < 0) {
                    if (player.getHealth() - 1 > 0) {
                        Combat.dealDamage(player, 1);
                    }
                }
                else {
                    Combat.dealDamage(player, 2);
                }

                PP.decreaseBleedTicks();
                
                if (PP.getBleedTicks() == 0) {
                    player.sendMessage(mcLocale.getString("Swords.StoppedBleeding"));
                }
            }
        }

        // Non-player bleed simulation
        bleedSimulate();
    }

    private void bleedSimulate() {
        lock = true;

        // Bleed monsters/animals
        for (LivingEntity entity : bleedList) {
            if ((entity == null || entity.isDead())) {
                remove(entity);
                continue;
            }
            else {
                Combat.dealDamage(entity, 2);
            }
        }

        // Unlock list now that we are done
        lock = false;
    }

    private void updateBleedList() {
        if (lock) {
            plugin.getLogger().warning("mcBleedTimer attempted to update the bleedList but the list was locked!");
        }
        else {
            bleedList.removeAll(bleedRemoveList);
            bleedRemoveList.clear();

            bleedList.addAll(bleedAddList);
            bleedAddList.clear();
        }
    }

    /**
     * Remove a LivingEntity from the bleedList if it is in it
     * 
     * @param entity LivingEntity to remove
     */
    public static void remove(LivingEntity entity) {
        if (lock) {
            if (!bleedRemoveList.contains(entity)) {
                bleedRemoveList.add(entity);
            }
        }
        else {
            if (bleedList.contains(entity)) {
                bleedList.remove(entity);
            }
        }
    }

    /**
     * Add a LivingEntity to the bleedList if it is not in it.
     *
     * @param entity LivingEntity to add
     */
    public static void add(LivingEntity entity) {
        if (lock) {
            if (!bleedAddList.contains(entity)) {
                bleedAddList.add(entity);
            }
        }
        else {
            if (!bleedList.contains(entity)){
                bleedList.add(entity);
            }
        }
    }

    /**
     * Check to see if a LivingEntity is in the bleedList
     * 
     * @param entity LivingEntity to check if in the bleedList
     * @return true if in the list, false if not
     */
    public static boolean contains(LivingEntity entity) {
        return (bleedList.contains(entity) || bleedAddList.contains(entity));
    }
}
