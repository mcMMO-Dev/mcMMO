package com.gmail.nossr50.util.blockmeta.conversion;

import java.io.File;

import org.bukkit.scheduler.BukkitScheduler;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.HiddenConfig;

public class BlockStoreConversionMain implements Runnable {
    private int taskID, i;
    private org.bukkit.World world;
    BukkitScheduler scheduler;
    File dataDir;
    File[] xDirs;
    BlockStoreConversionXDirectory[] converters;

    public BlockStoreConversionMain(org.bukkit.World world) {
        this.taskID = -1;
        this.world = world;
        this.scheduler = mcMMO.p.getServer().getScheduler();
        this.dataDir = new File(this.world.getWorldFolder(), "mcmmo_data");
        this.converters = new BlockStoreConversionXDirectory[HiddenConfig.getInstance().getConversionRate()];
    }

    public void start() {
        if (this.taskID >= 0) {
            return;
        }

        this.taskID = this.scheduler.scheduleSyncDelayedTask(mcMMO.p, this, 1);
        return;
    }

    @Override
    public void run() {
        if (!this.dataDir.exists()) {
            softStop();
            return;
        }

        if (!this.dataDir.isDirectory()) {
            this.dataDir.delete();
            softStop();
            return;
        }

        if (this.dataDir.listFiles().length <= 0) {
            this.dataDir.delete();
            softStop();
            return;
        }

        this.xDirs = this.dataDir.listFiles();

        for (this.i = 0; (this.i < HiddenConfig.getInstance().getConversionRate()) && (this.i < this.xDirs.length); this.i++) {
            if (this.converters[this.i] == null) {
                this.converters[this.i] = new BlockStoreConversionXDirectory();
            }

            this.converters[this.i].start(this.world, this.xDirs[this.i]);
        }

        softStop();
    }

    public void stop() {
        if (this.taskID < 0) {
            return;
        }

        this.scheduler.cancelTask(this.taskID);
        this.taskID = -1;
    }

    public void softStop() {
        stop();

        if (this.dataDir.exists() || this.dataDir.isDirectory()) {
            start();
            return;
        }

        mcMMO.p.getLogger().info("Finished converting the storage for " + world.getName() + ".");

        this.dataDir = null;
        this.xDirs = null;
        this.world = null;
        this.scheduler = null;
        this.converters = null;
        return;
    }
}
