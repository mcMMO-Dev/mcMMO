package com.gmail.nossr50.mcmmo.api.platform;

import com.gmail.nossr50.mcmmo.api.data.MMOEntity;
import com.gmail.nossr50.mcmmo.api.platform.scheduler.PlatformScheduler;
import com.gmail.nossr50.mcmmo.api.platform.util.MetadataStore;
import com.gmail.nossr50.mcmmo.api.platform.util.MobHealthBarManager;

import java.io.File;
import java.util.UUID;
import java.util.logging.Logger;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.CommandManager;
import co.aikar.commands.CommandOperationContext;

public interface PlatformProvider<E> {

    Logger getLogger();

    void tearDown();

    MetadataStore getMetadataStore();

    File getDataFolder();

    String getVersion();

    void earlyInit();

    boolean isSupported(boolean print);

    default boolean isSupported() {
        return isSupported(false);
    };

    ServerSoftwareType getServerType();

    void onLoad();

    void printUnsupported();

    PlatformScheduler getScheduler();

    void checkMetrics();

    MobHealthBarManager getHealthBarManager();

    @Deprecated
    void registerCustomRecipes();

    CommandManager getCommandManager();



    // EVIL - EVILNESS FROM BEYOND THIS POINT - EVIL
    // THOU HAST BEEN WARNED
    @Deprecated
    MMOEntity<?> getEntity(UUID uniqueId);

    @Deprecated
    MMOEntity<?> getEntity(E uniqueId);

    @Deprecated
    Object getChimaeraWing();
}
