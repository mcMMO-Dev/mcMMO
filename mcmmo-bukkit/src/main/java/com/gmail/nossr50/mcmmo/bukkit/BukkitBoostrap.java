package com.gmail.nossr50.mcmmo.bukkit;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcmmo.api.platform.PlatformProvider;
import com.gmail.nossr50.mcmmo.api.platform.util.MetadataStore;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class BukkitBoostrap extends JavaPlugin implements PlatformProvider {

    private mcMMO core = new mcMMO(this);

    @Override
    public @NotNull Logger getLogger() {
        return super.getLogger();
    }

    @Override
    public void tearDown() {
        core.debug("Canceling all tasks...");
        getServer().getScheduler().cancelTasks(this); // This removes our tasks
        core.debug("Unregister all events...");
        HandlerList.unregisterAll(this); // Cancel event registrations
    }

    @Override
    public MetadataStore getMetadataStore() {
        return null;
    }
}
