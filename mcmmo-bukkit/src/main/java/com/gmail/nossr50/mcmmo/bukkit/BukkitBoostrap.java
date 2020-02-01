package com.gmail.nossr50.mcmmo.bukkit;

import com.gmail.nossr50.listeners.BlockListener;
import com.gmail.nossr50.listeners.EntityListener;
import com.gmail.nossr50.listeners.InventoryListener;
import com.gmail.nossr50.listeners.PlayerListener;
import com.gmail.nossr50.listeners.SelfListener;
import com.gmail.nossr50.listeners.WorldListener;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcmmo.api.platform.PlatformProvider;
import com.gmail.nossr50.mcmmo.api.platform.ServerSoftwareType;
import com.gmail.nossr50.mcmmo.api.platform.scheduler.PlatformScheduler;
import com.gmail.nossr50.mcmmo.api.platform.util.MetadataStore;
import com.gmail.nossr50.mcmmo.bukkit.platform.scheduler.BukkitPlatformScheduler;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.logging.Logger;

public class BukkitBoostrap extends JavaPlugin implements PlatformProvider {

    private mcMMO core = new mcMMO(this);
    private final BukkitPlatformScheduler scheduler = new BukkitPlatformScheduler(this);

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

    @Override
    public String getVersion() {
        return this.getVersion();
    }

    @Override
    public void earlyInit() {
        registerEvents();

    }

    @Override
    public boolean isSupported(boolean print) {
        boolean ret = getServerType() != ServerSoftwareType.CRAFTBUKKIT;
        if (!ret) {
            Bukkit
                    .getScheduler()
                    .scheduleSyncRepeatingTask(this,
                            () -> getLogger().severe("You are running an outdated version of " + getServerType() + ", mcMMO will not work unless you update to a newer version!"),
                            20, 20 * 60 * 30);

            if (getServerType() == ServerSoftwareType.CRAFTBUKKIT) {
                Bukkit.getScheduler()
                        .scheduleSyncRepeatingTask(this,
                                () -> getLogger().severe("We have detected you are using incompatible server software, our best guess is that you are using CraftBukkit. mcMMO requires Spigot or Paper, if you are not using CraftBukkit, you will still need to update your custom server software before mcMMO will work."),
                                20, 20 * 60 * 30);
            }
        }

        return ret;
    }

    @Override
    public ServerSoftwareType getServerType() {
        if (Bukkit.getVersion().toLowerCase(Locale.ENGLISH).contains("paper"))
            return ServerSoftwareType.PAPER;
        else if (Bukkit.getVersion().toLowerCase(Locale.ENGLISH).contains("spigot"))
            return ServerSoftwareType.SPIGOT;
        else
            return ServerSoftwareType.CRAFTBUKKIT;
    }

    @Override
    public void printUnsupported() {

    }

    @Override
    public PlatformScheduler getScheduler() {
        return scheduler;
    }

    @Override
    public void checkMetrics() {
        //If anonymous statistics are enabled then use them
        if (core.getConfigManager().getConfigMetrics().isAllowAnonymousUsageStatistics()) {
            Metrics metrics;
            metrics = new Metrics(this);
            metrics.addCustomChart(new Metrics.SimplePie("version", this::getVersion));

            int levelScaleModifier = core.getConfigManager().getConfigLeveling().getConfigSectionLevelingGeneral().getConfigSectionLevelScaling().getCosmeticLevelScaleModifier();

            if (levelScaleModifier == 10)
                metrics.addCustomChart(new Metrics.SimplePie("scaling", () -> "Standard"));
            else if (levelScaleModifier == 1)
                metrics.addCustomChart(new Metrics.SimplePie("scaling", () -> "Retro"));
            else
                metrics.addCustomChart(new Metrics.SimplePie("scaling", () -> "Custom"));
        }
    }


    private void registerEvents() {
        PluginManager pluginManager = getServer().getPluginManager();

        // Register events
        pluginManager.registerEvents(new PlayerListener(core), this);
        pluginManager.registerEvents(new BlockListener(core), this);
        pluginManager.registerEvents(new EntityListener(core), this);
        pluginManager.registerEvents(new InventoryListener(core), this);
        pluginManager.registerEvents(new SelfListener(core), this);
        pluginManager.registerEvents(new WorldListener(core), this);
    }
}
