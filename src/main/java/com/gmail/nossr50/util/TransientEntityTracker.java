package com.gmail.nossr50.util;

import com.gmail.nossr50.datatypes.skills.subskills.taming.CallOfTheWildType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.taming.TrackedTamingEntity;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import com.gmail.nossr50.util.text.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TransientEntityTracker {
    private final @NotNull HashMap<UUID, HashMap<CallOfTheWildType, HashSet<TrackedTamingEntity>>> perPlayerTransientEntityMap;
    private final @NotNull HashSet<LivingEntity> chunkLookupCache;

    public TransientEntityTracker() {
        perPlayerTransientEntityMap = new HashMap<>();
        chunkLookupCache = new HashSet<>();
    }

    public void initPlayer(@NotNull UUID playerUUID) {
        if (!isPlayerRegistered(playerUUID)) {
            registerPlayer(playerUUID);
        }
    }

    public void cleanupPlayer(@NotNull UUID playerUUID) {
        cleanupAllSummons(null, playerUUID);
    }

    public void cleanupPlayer(@NotNull Player player) {
        //First remove all entities related to this player
        cleanupAllSummons(player, player.getUniqueId());
    }

    private boolean isPlayerRegistered(@NotNull UUID playerUUID) {
        return perPlayerTransientEntityMap.get(playerUUID) != null;
    }

    private void registerPlayer(@NotNull UUID playerUUID) {
        perPlayerTransientEntityMap.put(playerUUID, new HashMap<CallOfTheWildType, HashSet<TrackedTamingEntity>>());

        for(CallOfTheWildType callOfTheWildType : CallOfTheWildType.values()) {
            perPlayerTransientEntityMap.get(playerUUID).put(callOfTheWildType, new HashSet<>());
        }
    }

    /**
     * Get the tracked transient entities map for a specific player
     *
     * @param playerUUID the target uuid of the player
     * @return the tracked entities map for the player, null if the player isn't registered
     */
    public @Nullable HashMap<CallOfTheWildType, HashSet<TrackedTamingEntity>> getPlayerTrackedEntityMap(@NotNull UUID playerUUID) {
        return perPlayerTransientEntityMap.get(playerUUID);
    }

    public void registerEntity(@NotNull UUID playerUUID, @NotNull TrackedTamingEntity trackedTamingEntity) {
        if(!isPlayerRegistered(playerUUID)) {
            mcMMO.p.getLogger().severe("Attempting to register entity to a player which hasn't been initialized!");
            initPlayer(playerUUID);
        }

        //Add to map entry
        getTrackedEntities(playerUUID, trackedTamingEntity.getCallOfTheWildType()).add(trackedTamingEntity);

        //Add to cache for chunk lookups
        addToChunkLookupCache(trackedTamingEntity);
    }

    /**
     * Get the tracked taming entities for a player
     * If the player isn't registered this will return null
     *
     * @param playerUUID the target uuid of the player
     * @param callOfTheWildType target type
     * @return the set of tracked entities for the player, null if the player isn't registered, the set can be empty
     */
    private @Nullable HashSet<TrackedTamingEntity> getTrackedEntities(@NotNull UUID playerUUID, @NotNull CallOfTheWildType callOfTheWildType) {
        HashMap<CallOfTheWildType, HashSet<TrackedTamingEntity>> playerEntityMap = getPlayerTrackedEntityMap(playerUUID);

        if(playerEntityMap == null)
            return null;

        return playerEntityMap.get(callOfTheWildType);
    }

    private void addToChunkLookupCache(@NotNull TrackedTamingEntity trackedTamingEntity) {
        chunkLookupCache.add(trackedTamingEntity.getLivingEntity());
    }

    public void unregisterEntity(@NotNull LivingEntity livingEntity) {
        chunkLookupCacheCleanup(livingEntity);
        perPlayerTransientMapCleanup(livingEntity);
    }

    private void chunkLookupCacheCleanup(@NotNull LivingEntity livingEntity) {
        chunkLookupCache.remove(livingEntity);
    }

    private void perPlayerTransientMapCleanup(@NotNull LivingEntity livingEntity) {
        for(UUID uuid : perPlayerTransientEntityMap.keySet()) {
            for(CallOfTheWildType callOfTheWildType : CallOfTheWildType.values()) {

                HashSet<TrackedTamingEntity> trackedEntities = getTrackedEntities(uuid, callOfTheWildType);

                if(trackedEntities == null)
                    continue;

                Iterator<TrackedTamingEntity> iterator = trackedEntities.iterator();
                while (iterator.hasNext()) {
                    if(iterator.next().getLivingEntity().equals(livingEntity)) {
                        iterator.remove();
                        return;
                    }
                }
            }
        }
    }

    public @NotNull List<LivingEntity> getAllTransientEntitiesInChunk(@NotNull Chunk chunk) {
        ArrayList<LivingEntity> matchingEntities = new ArrayList<>();

        for(LivingEntity livingEntity : chunkLookupCache) {
            if(livingEntity.getLocation().getChunk().equals(chunk)) {
                matchingEntities.add(livingEntity);
            }
        }

        return matchingEntities;
    }

    /*
     * Gross code below
     */

    /**
     * Get the amount of a summon currently active for a player
     * @param playerUUID target player
     * @param callOfTheWildType summon type
     * @return the amount of summons currently active for player of target type
     */
    public int getAmountCurrentlySummoned(@NotNull UUID playerUUID, @NotNull CallOfTheWildType callOfTheWildType) {
        HashSet<TrackedTamingEntity> trackedEntities = getTrackedEntities(playerUUID, callOfTheWildType);

        if(trackedEntities == null)
            return 0;

        return trackedEntities.size();
    }

    /**
     * Kills a summon and removes its metadata
     * Then it removes it from the tracker / chunk lookup cache
     *
     * @param livingEntity entity to remove
     * @param player associated player
     */
    public void removeSummon(@NotNull LivingEntity livingEntity, @Nullable Player player, boolean timeExpired) {
        //Kill the summon & remove it
        if(livingEntity.isValid()) {
            livingEntity.setHealth(0); //Should trigger entity death events
            livingEntity.remove();

            Location location = livingEntity.getLocation();

            if (location.getWorld() != null) {
                location.getWorld().playSound(location, Sound.BLOCK_FIRE_EXTINGUISH, 0.8F, 0.8F);
                ParticleEffectUtils.playCallOfTheWildEffect(livingEntity);
            }

            //Inform player of summon death
            if(player != null && player.isOnline()) {
                if(timeExpired) {
                    NotificationManager.sendPlayerInformationChatOnly(player, "Taming.Summon.COTW.TimeExpired", StringUtils.getPrettyEntityTypeString(livingEntity.getType()));
                } else {
                    NotificationManager.sendPlayerInformationChatOnly(player, "Taming.Summon.COTW.Removed", StringUtils.getPrettyEntityTypeString(livingEntity.getType()));
                }
            }
        }

        //Remove our metadata
        mcMMO.getCompatibilityManager().getPersistentDataLayer().removeMobFlags(livingEntity);

        //Clean from trackers
        unregisterEntity(livingEntity);
    }

    /**
     * Remove all tracked entities from existence if they currently exist
     * Clear the tracked entity lists afterwards
     *
     * @deprecated use {@link #cleanupAllSummons(Player, UUID)} instead
     */
    @Deprecated
    private void cleanupAllSummons(@NotNull UUID playerUUID) {
        cleanupAllSummons(Bukkit.getPlayer(playerUUID), playerUUID);
    }

    /**
     * Kills and cleans up all data related to all summoned entities for a player
     *
     * @param player used to send messages, can be null
     * @param playerUUID used to grab associated data, cannot be null
     */
    private void cleanupAllSummons(@Nullable Player player, @NotNull UUID playerUUID) {
        for(CallOfTheWildType callOfTheWildType : CallOfTheWildType.values()) {
            HashSet<TrackedTamingEntity> trackedEntities = getTrackedEntities(playerUUID, callOfTheWildType);

            if(trackedEntities == null)
                continue;

            for(TrackedTamingEntity trackedTamingEntity : trackedEntities) {
                //Remove from existence
                removeSummon(trackedTamingEntity.getLivingEntity(), player, false);
            }
        }
    }
}
