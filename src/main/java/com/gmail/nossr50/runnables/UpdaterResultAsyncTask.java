package com.gmail.nossr50.runnables;

import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;

import net.gravitydevelopment.updater.mcmmo.Updater;

public class UpdaterResultAsyncTask extends BukkitRunnable {
    private mcMMO plugin;

    public UpdaterResultAsyncTask(mcMMO plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Updater updater = new Updater(plugin, 31030, plugin.mcmmo, Updater.UpdateType.NO_DOWNLOAD, false);

        if (updater.getResult() != Updater.UpdateResult.UPDATE_AVAILABLE) {
            plugin.setUpdateAvailable(false);
            return;
        }

        if (updater.getLatestType().equals("beta") && !Config.getInstance().getPreferBeta()) {
            plugin.setUpdateAvailable(false);
            return;
        }

        plugin.setUpdateAvailable(true);
        plugin.getLogger().info(LocaleLoader.getString("UpdateChecker.Outdated"));
        plugin.getLogger().info(LocaleLoader.getString("UpdateChecker.NewAvailable"));
    }
}
