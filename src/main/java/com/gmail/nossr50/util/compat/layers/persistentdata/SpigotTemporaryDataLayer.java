package com.gmail.nossr50.util.compat.layers.persistentdata;

import com.gmail.nossr50.datatypes.meta.UUIDMeta;
import com.gmail.nossr50.mcMMO;
import org.bukkit.block.Furnace;
import org.bukkit.metadata.Metadatable;

import java.util.UUID;

/**
 * Persistent Data API is unavailable
 */
public class SpigotTemporaryDataLayer extends AbstractPersistentDataLayer {

    private final String FURNACE_OWNER_METADATA_KEY = "mcMMO_furnace_owner";

    @Override
    public boolean initializeLayer() {
        return true;
    }

    @Override
    public UUID getFurnaceOwner(Furnace furnace) {
        Metadatable metadatable = (Metadatable) furnace;

        if(metadatable.getMetadata(FURNACE_OWNER_METADATA_KEY).size() > 0) {
            UUIDMeta uuidMeta = (UUIDMeta) metadatable.getMetadata(FURNACE_OWNER_METADATA_KEY).get(0);
            return (UUID) uuidMeta.value();
        } else {
            return null;
        }
    }

    @Override
    public void setFurnaceOwner(Furnace furnace, UUID uuid) {
        Metadatable metadatable = (Metadatable) furnace;

        if(metadatable.getMetadata(FURNACE_OWNER_METADATA_KEY).size() > 0) {
            metadatable.removeMetadata(FURNACE_OWNER_METADATA_KEY, mcMMO.p);
        }

        metadatable.setMetadata(FURNACE_OWNER_METADATA_KEY, new UUIDMeta(mcMMO.p, uuid));
    }
}
