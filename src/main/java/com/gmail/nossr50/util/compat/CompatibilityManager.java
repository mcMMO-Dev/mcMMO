package com.gmail.nossr50.util.compat;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.compat.layers.bungee.AbstractBungeeSerializerCompatibilityLayer;
import com.gmail.nossr50.util.compat.layers.bungee.BungeeLegacySerializerCompatibilityLayer;
import com.gmail.nossr50.util.compat.layers.bungee.BungeeModernSerializerCompatibilityLayer;
import com.gmail.nossr50.util.compat.layers.persistentdata.AbstractPersistentDataLayer;
import com.gmail.nossr50.util.compat.layers.persistentdata.SpigotPersistentDataLayer_1_13;
import com.gmail.nossr50.util.compat.layers.persistentdata.SpigotPersistentDataLayer_1_14;
import com.gmail.nossr50.util.compat.layers.skills.AbstractMasterAnglerCompatibility;
import com.gmail.nossr50.util.compat.layers.skills.MasterAnglerCompatibilityLayer;
import com.gmail.nossr50.util.compat.layers.world.WorldCompatibilityLayer;
import com.gmail.nossr50.util.compat.layers.world.WorldCompatibilityLayer_1_16_4;
import com.gmail.nossr50.util.nms.NMSVersion;
import com.gmail.nossr50.util.platform.MinecraftGameVersion;
import com.gmail.nossr50.util.text.StringUtils;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 *
 * These classes are a band-aid solution for adding NMS support into 2.1.XXX
 * In 2.2 we are switching to modules and that will clean things up significantly
 *
 */
//TODO: I need to rewrite this crap
public class CompatibilityManager {
    private @NotNull HashMap<CompatibilityType, Boolean> supportedLayers;
    private boolean isFullyCompatibleServerSoftware = true; //true if all compatibility layers load successfully
    private final @NotNull MinecraftGameVersion minecraftGameVersion;
    private final @NotNull NMSVersion nmsVersion;

    /* Compatibility Layers */
//    private PlayerAttackCooldownExploitPreventionLayer playerAttackCooldownExploitPreventionLayer;
    private AbstractPersistentDataLayer persistentDataLayer;
    private AbstractBungeeSerializerCompatibilityLayer bungeeSerializerCompatibilityLayer;
    private AbstractMasterAnglerCompatibility masterAnglerCompatibility;
    private WorldCompatibilityLayer worldCompatibilityLayer;

    public CompatibilityManager(@NotNull MinecraftGameVersion minecraftGameVersion) {
        mcMMO.p.getLogger().info("Loading compatibility layers...");
        this.minecraftGameVersion = minecraftGameVersion;
        this.nmsVersion = determineNMSVersion();
        init();
        mcMMO.p.getLogger().info("Finished loading compatibility layers.");
    }

    private void init() {
        initSupportedLayersMap();
        initCompatibilityLayers();
    }

    private void initSupportedLayersMap() {
        supportedLayers = new HashMap<>(); //Init map

        for(CompatibilityType compatibilityType : CompatibilityType.values()) {
            supportedLayers.put(compatibilityType, false); //All layers are set to false when initialized
        }
    }

    /**
     * Initialize all necessary compatibility layers
     * For any unsupported layers, load a dummy layer
     */
    private void initCompatibilityLayers() {
        initPersistentDataLayer();
        initBungeeSerializerLayer();
        initMasterAnglerLayer();
        initWorldCompatibilityLayer();

        isFullyCompatibleServerSoftware = true;
    }

    private void initWorldCompatibilityLayer() {
        if(minecraftGameVersion.isAtLeast(1, 17, 0)) {
            worldCompatibilityLayer = new WorldCompatibilityLayer_1_16_4();
        } else {
            worldCompatibilityLayer = new WorldCompatibilityLayer() {
                @Override
                public int getMinWorldHeight(@NotNull World world) {
                    return WorldCompatibilityLayer.super.getMinWorldHeight(world);
                }

                @Override
                public int getMaxWorldHeight(@NotNull World world) {
                    return WorldCompatibilityLayer.super.getMaxWorldHeight(world);
                }
            };
        }
    }

    private void initMasterAnglerLayer() {
        if(minecraftGameVersion.isAtLeast(1, 16, 3)) {
            masterAnglerCompatibility = new MasterAnglerCompatibilityLayer();
        } else {
            masterAnglerCompatibility = null;
        }
    }

    private void initBungeeSerializerLayer() {
        if(minecraftGameVersion.isAtLeast(1, 16, 0)) {
            bungeeSerializerCompatibilityLayer = new BungeeModernSerializerCompatibilityLayer();
        } else {
            bungeeSerializerCompatibilityLayer = new BungeeLegacySerializerCompatibilityLayer();
        }

        supportedLayers.put(CompatibilityType.BUNGEE_SERIALIZER, true);
    }

    private void initPersistentDataLayer() {
        if(minecraftGameVersion.isAtLeast(1, 14, 2)) {
            persistentDataLayer = new SpigotPersistentDataLayer_1_14();
        } else {

            persistentDataLayer = new SpigotPersistentDataLayer_1_13();
        }

        supportedLayers.put(CompatibilityType.PERSISTENT_DATA, true);
    }

    //TODO: move to text manager
    public void reportCompatibilityStatus(@NotNull CommandSender commandSender) {
        if(isFullyCompatibleServerSoftware) {
            commandSender.sendMessage(LocaleLoader.getString("mcMMO.Template.Prefix",
                    "mcMMO is fully compatible with the currently running server software."));
        } else {
            //TODO: Better messages for each incompatible layer
            for(CompatibilityType compatibilityType : CompatibilityType.values()) {
                if(!supportedLayers.get(compatibilityType)) {
                    commandSender.sendMessage(LocaleLoader.getString("mcMMO.Template.Prefix",
                            LocaleLoader.getString("Compatibility.Layer.Unsupported",  StringUtils.getCapitalized(compatibilityType.toString()))));
                }
            }
        }

        commandSender.sendMessage(LocaleLoader.getString("mcMMO.Template.Prefix", "NMS Status - " + nmsVersion.toString()));
    }

    public boolean isCompatibilityLayerOperational(@NotNull CompatibilityType compatibilityType) {
        return supportedLayers.get(compatibilityType);
    }

    public boolean isFullyCompatibleServerSoftware() {
        return isFullyCompatibleServerSoftware;
    }

    public @NotNull NMSVersion getNmsVersion() {
        return nmsVersion;
    }

    private @NotNull NMSVersion determineNMSVersion() {
        //This bit here helps prevent mcMMO breaking if it isn't updated but the game continues to update
        if(minecraftGameVersion.isAtLeast(1, 17, 0)) {
            return NMSVersion.NMS_1_17;
        }

        //Messy but it works
        if (minecraftGameVersion.getMajorVersion().asInt() == 1) {
            switch (minecraftGameVersion.getMinorVersion().asInt()) {
                case 12:
                    return NMSVersion.NMS_1_12_2;
                case 13:
                    return NMSVersion.NMS_1_13_2;
                case 14:
                    return NMSVersion.NMS_1_14_4;
                case 15:
                    return NMSVersion.NMS_1_15_2;
                case 16:
                    if (minecraftGameVersion.getPatchVersion().asInt() == 1) {
                        return NMSVersion.NMS_1_16_1;
                    } else if(minecraftGameVersion.getPatchVersion().asInt() == 2) {
                        return NMSVersion.NMS_1_16_2;
                    } else if(minecraftGameVersion.getPatchVersion().asInt() == 3) {
                        return NMSVersion.NMS_1_16_3;
                    } else if(minecraftGameVersion.getPatchVersion().asInt() == 4) {
                        return NMSVersion.NMS_1_16_4;
                    } else if(minecraftGameVersion.getPatchVersion().asInt() >= 5) {
                        return NMSVersion.NMS_1_16_5;
                    }
                case 17:
                    return NMSVersion.NMS_1_17;
            }
        }

        return NMSVersion.UNSUPPORTED;
    }

    public AbstractBungeeSerializerCompatibilityLayer getBungeeSerializerCompatibilityLayer() {
        return bungeeSerializerCompatibilityLayer;
    }

    public AbstractPersistentDataLayer getPersistentDataLayer() {
        return persistentDataLayer;
    }

    public @Nullable AbstractMasterAnglerCompatibility getMasterAnglerCompatibilityLayer() {
        return masterAnglerCompatibility;
    }

    public @NotNull WorldCompatibilityLayer getWorldCompatibilityLayer() {
        return worldCompatibilityLayer;
    }

    public @Nullable MinecraftGameVersion getMinecraftGameVersion() {
        return minecraftGameVersion;
    }
}
