package com.gmail.nossr50.util;

import com.gmail.nossr50.datatypes.skills.subskills.taming.CallOfTheWildType;
import com.gmail.nossr50.skills.taming.TrackedTamingEntity;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import com.gmail.nossr50.util.text.StringUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.gmail.nossr50.util.MobMetadataUtils.removeMobFlags;
import static java.util.stream.Collectors.toSet;

public class TransientEntityTracker {
    private final @NotNull Map<UUID, HashSet<TrackedTamingEntity>>
            playerSummonedEntityTracker;

    public TransientEntityTracker() {
        playerSummonedEntityTracker = new ConcurrentHashMap<>();
    }

    public @Nullable Set<TrackedTamingEntity> getPlayerSummonedEntities(@NotNull UUID playerUUID) {
        return playerSummonedEntityTracker.get(playerUUID);
    }

    public void initPlayer(@NotNull Player player) {
        playerSummonedEntityTracker.computeIfAbsent(player.getUniqueId(), __ -> new HashSet<>());
    }

    public void cleanupPlayer(@NotNull Player player) {
        cleanPlayer(player, player.getUniqueId());
    }

    public @NotNull List<LivingEntity> getAllTransientEntitiesInChunk(@NotNull Chunk chunk) {
        return playerSummonedEntityTracker.values().stream()
                .flatMap(Collection::stream)
                .map(TrackedTamingEntity::getLivingEntity)
                .filter(livingEntity -> livingEntity.getLocation().getChunk() == chunk)
                .toList();
    }

    public int summonCountForPlayerOfType(@NotNull UUID playerUUID, @NotNull CallOfTheWildType callOfTheWildType) {
        return getTrackedEntities(playerUUID, callOfTheWildType).size();
    }

    public void addSummon(@NotNull UUID playerUUID, @NotNull TrackedTamingEntity trackedTamingEntity) {
        playerSummonedEntityTracker.computeIfAbsent(playerUUID, __ -> new HashSet<>())
                .add(trackedTamingEntity);
    }

    public void removeSummon(@NotNull LivingEntity livingEntity, @Nullable Player player, boolean timeExpired) {
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
                    NotificationManager.sendPlayerInformationChatOnly(player, "Taming.Summon.COTW.TimeExpired",
                            StringUtils.getPrettyEntityTypeString(livingEntity.getType()));
                } else {
                    NotificationManager.sendPlayerInformationChatOnly(player, "Taming.Summon.COTW.Removed",
                            StringUtils.getPrettyEntityTypeString(livingEntity.getType()));
                }
            }
        }

        //Remove our metadata
        removeMobFlags(livingEntity);

        //Clean from trackers
        remove(livingEntity);
    }

    private void cleanPlayer(@Nullable Player player, @NotNull UUID playerUUID) {
        cleanupAllSummons(playerUUID, player);
        playerSummonedEntityTracker.remove(playerUUID);
    }

    public boolean isTransient(@NotNull LivingEntity livingEntity) {
        return playerSummonedEntityTracker.values().stream().anyMatch(
                trackedEntities -> trackedEntities.stream()
                        .anyMatch(trackedTamingEntity -> trackedTamingEntity.getLivingEntity().equals(livingEntity)));
    }


    private @NotNull Set<TrackedTamingEntity> getTrackedEntities(@NotNull UUID playerUUID,
                                                                 @NotNull CallOfTheWildType callOfTheWildType) {
        final HashSet<TrackedTamingEntity> entities
                = playerSummonedEntityTracker.computeIfAbsent(playerUUID, __ -> new HashSet<>());
        return entities.stream()
                .filter(trackedTamingEntity -> trackedTamingEntity.getCallOfTheWildType() == callOfTheWildType)
                .collect(toSet());
    }

    private void remove(@NotNull LivingEntity livingEntity) {
        playerSummonedEntityTracker.values().forEach(trackedEntities -> {
            Iterator<TrackedTamingEntity> iterator = trackedEntities.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().getLivingEntity().equals(livingEntity)) {
                        iterator.remove();
                        return;
                    }
                }
            });
    }

    private void cleanupAllSummons(@NotNull UUID playerUUID, @Nullable Player player) {
        if (playerSummonedEntityTracker.get(playerUUID) == null) {
            return;
        }

        playerSummonedEntityTracker.get(playerUUID).forEach(trackedTamingEntity -> {
            removeSummon(trackedTamingEntity.getLivingEntity(), player, false);
        });
    }
}
