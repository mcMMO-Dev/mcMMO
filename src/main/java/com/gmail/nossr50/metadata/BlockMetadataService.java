package com.gmail.nossr50.metadata;

import com.gmail.nossr50.mcMMO;
import org.bukkit.block.Furnace;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static com.gmail.nossr50.metadata.MetadataService.NSK_FURNACE_UUID_LEAST_SIG;
import static com.gmail.nossr50.metadata.MetadataService.NSK_FURNACE_UUID_MOST_SIG;

public class BlockMetadataService {

    private final @NotNull mcMMO pluginRef;

    public BlockMetadataService(@NotNull mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    public @Nullable UUID getFurnaceOwner(@NotNull Furnace furnace) {
        //Get container from entity
        PersistentDataContainer dataContainer = ((PersistentDataHolder) furnace).getPersistentDataContainer();

        //Too lazy to make a custom data type for this stuff
        Long mostSigBits = dataContainer.get(NSK_FURNACE_UUID_MOST_SIG, PersistentDataType.LONG);
        Long leastSigBits = dataContainer.get(NSK_FURNACE_UUID_LEAST_SIG, PersistentDataType.LONG);

        if (mostSigBits != null && leastSigBits != null) {
            return new UUID(mostSigBits, leastSigBits);
        } else {
            return null;
        }
    }

    public void setFurnaceOwner(@NotNull Furnace furnace, @NotNull UUID uuid) {
        PersistentDataContainer dataContainer = ((PersistentDataHolder) furnace).getPersistentDataContainer();

        dataContainer.set(NSK_FURNACE_UUID_MOST_SIG, PersistentDataType.LONG, uuid.getMostSignificantBits());
        dataContainer.set(NSK_FURNACE_UUID_LEAST_SIG, PersistentDataType.LONG, uuid.getLeastSignificantBits());

        furnace.update();
    }


}
