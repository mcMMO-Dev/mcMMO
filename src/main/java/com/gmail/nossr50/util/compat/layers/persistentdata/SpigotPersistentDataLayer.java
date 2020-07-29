package com.gmail.nossr50.util.compat.layers.persistentdata;

import com.gmail.nossr50.mcMMO;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Furnace;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SpigotPersistentDataLayer extends AbstractPersistentDataLayer {

    private NamespacedKey furnaceOwner_MostSig_Key;
    private NamespacedKey furnaceOwner_LeastSig_Key;

    @Override
    public boolean initializeLayer() {
        initNamespacedKeys();
        return true;
    }

    private void initNamespacedKeys() {
        furnaceOwner_MostSig_Key = getNamespacedKey("furnace_uuid_most_sig");
        furnaceOwner_LeastSig_Key = getNamespacedKey("furnace_uuid_least_sig");
    }

    @NotNull
    public NamespacedKey getNamespacedKey(String key) {
        return new NamespacedKey(mcMMO.p, key);
    }

    @Override
    public @Nullable UUID getFurnaceOwner(Furnace furnace) {
        //Get container from entity
        PersistentDataContainer dataContainer = ((PersistentDataHolder) furnace).getPersistentDataContainer();

        //Too lazy to make a custom data type for this stuff
        Long mostSigBits = dataContainer.get(furnaceOwner_MostSig_Key, PersistentDataType.LONG);
        Long leastSigBits = dataContainer.get(furnaceOwner_LeastSig_Key, PersistentDataType.LONG);

        if(mostSigBits != null && leastSigBits != null) {
            return new UUID(mostSigBits, leastSigBits);
        } else {
            return null;
        }
    }

    @Override
    public void setFurnaceOwner(Furnace furnace, UUID uuid) {
        PersistentDataContainer dataContainer = ((PersistentDataHolder) furnace).getPersistentDataContainer();

        dataContainer.set(furnaceOwner_MostSig_Key, PersistentDataType.LONG, uuid.getMostSignificantBits());
        dataContainer.set(furnaceOwner_LeastSig_Key, PersistentDataType.LONG, uuid.getLeastSignificantBits());

        furnace.update();
    }
}
