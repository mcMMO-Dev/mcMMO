package com.gmail.nossr50.util.blockmeta.conversion;

import com.gmail.nossr50.core.ChunkConversionOptions;
import com.gmail.nossr50.mcMMO;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;

public class BlockStoreConversionMain implements Runnable {
    BukkitScheduler scheduler;
    File dataDir;
    File[] xDirs;
    BlockStoreConversionXDirectory[] converters;
    private int taskID;
    private org.bukkit.World world;
    private final mcMMO pluginRef;

    public BlockStoreConversionMain(mcMMO pluginRef, org.bukkit.World world) {
        this.pluginRef = pluginRef;
        this.taskID = -1;
        this.world = world;
        this.scheduler = pluginRef.getServer().getScheduler();
        this.dataDir = new File(this.world.getWorldFolder(), "mcmmo_data");
        this.converters = new BlockStoreConversionXDirectory[ChunkConversionOptions.getConversionRate()];
    }

    public void start() {
        if (this.taskID >= 0) {
            return;
        }

        this.taskID = this.scheduler.runTaskLater(pluginRef, this, 1).getTaskId();
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

        for (int i = 0; (i < ChunkConversionOptions.getConversionRate()) && (i < this.xDirs.length); i++) {
            if (this.converters[i] == null) {
                this.converters[i] = new BlockStoreConversionXDirectory();
            }

            this.converters[i].start(this.world, this.xDirs[i]);
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

        pluginRef.getLogger().info("Finished converting the storage for " + world.getName() + ".");

        this.dataDir = null;
        this.xDirs = null;
        this.world = null;
        this.scheduler = null;
        this.converters = null;
    }
}
