package com.gmail.nossr50.util;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static com.gmail.nossr50.util.MetadataService.NSK_CONTAINER_UUID_LEAST_SIG;
import static com.gmail.nossr50.util.MetadataService.NSK_CONTAINER_UUID_MOST_SIG;
import static java.util.Objects.requireNonNull;

public class ContainerMetadataUtils {

    public static void changeContainerOwnership(@Nullable BlockState blockState, @Nullable Player player) {
        // no-op when the blockState is null or player is null
        if (blockState == null || player == null) {
            return;
        }

        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        /*
            Debug output
         */
        printOwnershipGainDebug(blockState, mmoPlayer);
        printOwnershipLossDebug(blockState);
        setOwner(blockState, player.getUniqueId());
    }

    public static void printOwnershipGainDebug(@NotNull BlockState blockState, @Nullable McMMOPlayer mmoPlayer) {
        if (mmoPlayer != null && mmoPlayer.isDebugMode()) {
            mmoPlayer.getPlayer().sendMessage("Container ownership " +
                    ChatColor.GREEN +"gained " + ChatColor.RESET +
                    "at location: " + blockState.getLocation().toString());
        }
    }

    public static void printOwnershipLossDebug(BlockState blockState) {
        OfflinePlayer containerOwner = getContainerOwner(blockState);

        if (containerOwner != null && containerOwner.isOnline()) {
            final McMMOPlayer mmoContainerOwner = UserManager.getPlayer(containerOwner.getPlayer());

            if (mmoContainerOwner != null) {
                if (mmoContainerOwner.isDebugMode()) {
                    mmoContainerOwner.getPlayer().sendMessage("Container ownership " +
                            ChatColor.RED + "lost " + ChatColor.RESET +
                            "at location: " + blockState.getLocation().toString());
                }
            }
        }
    }

    public static @Nullable OfflinePlayer getContainerOwner(BlockState container) {
        if (container instanceof PersistentDataHolder persistentDataHolder) {
            final UUID uuid = getOwner(persistentDataHolder);

            if (uuid != null) {
                return Bukkit.getOfflinePlayer(uuid);
            }
        }

        return null;
    }


    public static boolean isContainerOwned(BlockState blockState) {
        return getContainerOwner(blockState) != null;
    }

    public static void processContainerOwnership(BlockState blockState, Player player) {
        // no-op when the blockState is null or player is null
        if (blockState == null || player == null) {
            return;
        }

        if (getContainerOwner(blockState) != null) {
            if (getContainerOwner(blockState).getUniqueId().equals(player.getUniqueId()))
                return;
        }

        changeContainerOwnership(blockState, player);
    }

    public static @Nullable UUID getOwner(@NotNull PersistentDataHolder persistentDataHolder) {
        //Get container from entity
        final PersistentDataContainer dataContainer = persistentDataHolder.getPersistentDataContainer();

        //Too lazy to make a custom data type for this stuff
        final Long mostSigBits = dataContainer.get(NSK_CONTAINER_UUID_MOST_SIG, PersistentDataType.LONG);
        final Long leastSigBits = dataContainer.get(NSK_CONTAINER_UUID_LEAST_SIG, PersistentDataType.LONG);

        if (mostSigBits != null && leastSigBits != null) {
            return new UUID(mostSigBits, leastSigBits);
        } else {
            return null;
        }
    }

    public static void setOwner(@NotNull BlockState blockState, @NotNull UUID uuid) {
        PersistentDataContainer dataContainer = ((PersistentDataHolder) blockState).getPersistentDataContainer();

        dataContainer.set(NSK_CONTAINER_UUID_MOST_SIG, PersistentDataType.LONG, uuid.getMostSignificantBits());
        dataContainer.set(NSK_CONTAINER_UUID_LEAST_SIG, PersistentDataType.LONG, uuid.getLeastSignificantBits());

        blockState.update();
    }
}
