package com.gmail.nossr50.util;

import com.gmail.nossr50.datatypes.skills.subskills.taming.CallOfTheWildType;
import com.gmail.nossr50.skills.taming.TrackedTamingEntity;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import com.gmail.nossr50.util.text.StringUtils;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.gmail.nossr50.util.MobMetadataUtils.removeMobFlags;

public class TransientEntityTracker {
    //These two are updated in step with each other
    private final @NotNull HashMap<UUID, HashMap<CallOfTheWildType, HashSet<TrackedTamingEntity>>> perPlayerTransientEntityMap;
    private final @NotNull HashSet<LivingEntity> chunkLookupCache;

    public TransientEntityTracker() {
        perPlayerTransientEntityMap = new HashMap<>();
        chunkLookupCache = new HashSet<>();
    }

    public synchronized @NotNull HashSet<LivingEntity> getChunkLookupCache() {
        return chunkLookupCache;
    }

    public synchronized @NotNull HashMap<UUID, HashMap<CallOfTheWildType, HashSet<TrackedTamingEntity>>> getPerPlayerTransientEntityMap() {
        return perPlayerTransientEntityMap;
    }

    public synchronized void initPlayer(@NotNull Player player) {
        if (!isPlayerRegistered(player.getUniqueId())) {
            registerPlayer(player.getUniqueId());
        }
    }

    /**
     * Removes a player from the tracker
     *
     * @param playerUUID target player
     */
    public synchronized void cleanupPlayer(@NotNull UUID playerUUID) {
        cleanPlayer(null, playerUUID);
    }

    /**
     * Removes a player from the tracker
     *
     * @param player target player
     */
    public synchronized void cleanupPlayer(@NotNull Player player) {
        cleanPlayer(player, player.getUniqueId());
    }

    /**
     * Removes a player from the tracker
     *
     * @param player target player
     * @param playerUUID target player UUID
     */
    private void cleanPlayer(@Nullable Player player, @NotNull UUID playerUUID) {
        cleanupAllSummons(player, player.getUniqueId());
        removePlayerFromMap(playerUUID);
    }

    private void removePlayerFromMap(@NotNull UUID playerUUID) {
        getPerPlayerTransientEntityMap().remove(playerUUID);
    }

    /**
     * Checks if a player has already been registered
     * Being registered constitutes having necessary values initialized in our per-player map
     *
     * @param playerUUID target player
     * @return true if the player is registered
     */
    private synchronized boolean isPlayerRegistered(@NotNull UUID playerUUID) {
        return getPerPlayerTransientEntityMap().get(playerUUID) != null;
    }

    /**
     * Register a player to our tracker, which initializes the necessary values in our per-player map
     *
     * @param playerUUID player to register
     */
    private synchronized void registerPlayer(@NotNull UUID playerUUID) {
        getPerPlayerTransientEntityMap().put(playerUUID, new HashMap<>());

        for(CallOfTheWildType callOfTheWildType : CallOfTheWildType.values()) {
            getPerPlayerTransientEntityMap().get(playerUUID).put(callOfTheWildType, new HashSet<>());
        }
    }

    /**
     * Get the tracked transient entities map for a specific player
     *
     * @param playerUUID the target uuid of the player
     * @return the tracked entities map for the player, null if the player isn't registered
     */
    public synchronized @Nullable HashMap<CallOfTheWildType, HashSet<TrackedTamingEntity>> getPlayerTrackedEntityMap(@NotNull UUID playerUUID) {
        return getPerPlayerTransientEntityMap().get(playerUUID);
    }

    /**
     * Registers an entity to a player
     * This includes registration to our per-player map and our chunk lookup cache
     *
     * @param playerUUID target player's UUID
     * @param trackedTamingEntity target entity
     */
    public synchronized void registerEntity(@NotNull UUID playerUUID, @NotNull TrackedTamingEntity trackedTamingEntity) {
        //Add to map entry
        getTrackedEntities(playerUUID, trackedTamingEntity.getCallOfTheWildType()).add(trackedTamingEntity);

        //Add to cache for chunk lookups
        addToChunkLookupCache(trackedTamingEntity);
    }

    /**
     * Checks if a living entity is a summon
     *
     * @param livingEntity target livinig entity
     * @return true if target living entity is a summon
     */
    public synchronized boolean isTransientSummon(@NotNull LivingEntity livingEntity) {
        return getChunkLookupCache().contains(livingEntity);
    }

    /**
     * Get the tracked taming entities for a player
     * If the player isn't registered this will return null
     *
     * @param playerUUID the target uuid of the player
     * @param callOfTheWildType target type
     * @return the set of tracked entities for the player, null if the player isn't registered, the set can be empty
     */
    private synchronized @Nullable HashSet<TrackedTamingEntity> getTrackedEntities(@NotNull UUID playerUUID, @NotNull CallOfTheWildType callOfTheWildType) {
        HashMap<CallOfTheWildType, HashSet<TrackedTamingEntity>> playerEntityMap = getPlayerTrackedEntityMap(playerUUID);

        if (playerEntityMap == null)
            return null;

        return playerEntityMap.get(callOfTheWildType);
    }

    /**
     * Adds an entity to our chunk lookup cache
     *
     * @param trackedTamingEntity target tracked taming entity
     */
    private synchronized void addToChunkLookupCache(@NotNull TrackedTamingEntity trackedTamingEntity) {
        getChunkLookupCache().add(trackedTamingEntity.getLivingEntity());
    }

    /**
     * Removes an entity from our tracker
     * This includes removal from our per-player map and our chunk lookup cache
     *
     * @param livingEntity target entity
     */
    private void unregisterEntity(@NotNull LivingEntity livingEntity) {
        chunkLookupCacheCleanup(livingEntity);
        perPlayerTransientMapCleanup(livingEntity);
    }

    /**
     * Removes an entity from our chunk lookup cache
     *
     * @param livingEntity target entity
     */
    private void chunkLookupCacheCleanup(@NotNull LivingEntity livingEntity) {
        getChunkLookupCache().remove(livingEntity);
    }

    /**
     * Clean a living entity from our tracker
     * Iterates over all players and their registered entities
     * Doesn't do any kind of failure checking, if it doesn't find any player with a registered entity nothing bad happens or is reported
     * However it should never happen like that, so maybe we could consider adding some failure to execute checking in the future
     *
     * @param livingEntity
     */
    private void perPlayerTransientMapCleanup(@NotNull LivingEntity livingEntity) {
        for(UUID uuid : getPerPlayerTransientEntityMap().keySet()) {
            for(CallOfTheWildType callOfTheWildType : CallOfTheWildType.values()) {

                HashSet<TrackedTamingEntity> trackedEntities = getTrackedEntities(uuid, callOfTheWildType);

                if (trackedEntities == null)
                    continue;

                Iterator<TrackedTamingEntity> iterator = trackedEntities.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().getLivingEntity().equals(livingEntity)) {
                        iterator.remove();
                        return;
                    }
                }
            }
        }
    }

    /**
     * Get all transient entities that exist in a specific chunk
     *
     * @param chunk the chunk to match
     * @return a list of transient entities that are located in the provided chunk
     */
    public synchronized @NotNull List<LivingEntity> getAllTransientEntitiesInChunk(@NotNull Chunk chunk) {
        ArrayList<LivingEntity> matchingEntities = new ArrayList<>();

        for(LivingEntity livingEntity : getChunkLookupCache()) {
            if (livingEntity.getLocation().getChunk().equals(chunk)) {
                matchingEntities.add(livingEntity);
            }
        }

        return matchingEntities;
    }

    /**
     * Get the amount of a summon currently active for a player
     *
     * @param playerUUID target player
     * @param callOfTheWildType summon type
     * @return the amount of summons currently active for player of target type
     */
    public synchronized int getAmountCurrentlySummoned(@NotNull UUID playerUUID, @NotNull CallOfTheWildType callOfTheWildType) {
        HashSet<TrackedTamingEntity> trackedEntities = getTrackedEntities(playerUUID, callOfTheWildType);

        if (trackedEntities == null)
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
    public synchronized void removeSummon(@NotNull LivingEntity livingEntity, @Nullable Player player, boolean timeExpired) {
        //Kill the summon & remove it
        if (livingEntity.isValid()) {
            livingEntity.setHealth(0); //Should trigger entity death events
            livingEntity.remove();

            Location location = livingEntity.getLocation();

            if (location.getWorld() != null) {
                location.getWorld().playSound(location, Sound.BLOCK_FIRE_EXTINGUISH, 0.8F, 0.8F);
                ParticleEffectUtils.playCallOfTheWildEffect(livingEntity);
            }

            //Inform player of summon death
            if (player != null && player.isOnline()) {
                if (timeExpired) {
                    NotificationManager.sendPlayerInformationChatOnly(player, "Taming.Summon.COTW.TimeExpired", StringUtils.getPrettyEntityTypeString(livingEntity.getType()));
                } else {
                    NotificationManager.sendPlayerInformationChatOnly(player, "Taming.Summon.COTW.Removed", StringUtils.getPrettyEntityTypeString(livingEntity.getType()));
                }
            }
        }

        //Remove our metadata
        removeMobFlags(livingEntity);

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

            if (trackedEntities == null) {
                continue;
            }

            ImmutableSet<TrackedTamingEntity> immutableSet = ImmutableSet.copyOf(trackedEntities);

            for(TrackedTamingEntity trackedTamingEntity : immutableSet) {
                //Remove from existence
                removeSummon(trackedTamingEntity.getLivingEntity(), player, false);
            }

        }
    }
}
