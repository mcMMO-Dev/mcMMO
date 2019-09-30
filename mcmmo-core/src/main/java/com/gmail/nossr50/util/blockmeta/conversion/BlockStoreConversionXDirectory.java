package com.gmail.nossr50.util.blockmeta.conversion;

import com.gmail.nossr50.core.ChunkConversionOptions;
import com.gmail.nossr50.mcMMO;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;

public class BlockStoreConversionXDirectory implements Runnable {
    BukkitScheduler scheduler;
    File dataDir;
    File[] zDirs;
    BlockStoreConversionZDirectory[] converters;
    private int taskID;
    private org.bukkit.World world;
    private final mcMMO pluginRef;

    public BlockStoreConversionXDirectory(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
        this.taskID = -1;
    }

    public void start(org.bukkit.World world, File dataDir) {
        this.world = world;
        this.scheduler = pluginRef.getServer().getScheduler();
        this.converters = new BlockStoreConversionZDirectory[ChunkConversionOptions.getConversionRate()];
        this.dataDir = dataDir;

        if (this.taskID >= 0) {
            return;
        }

        this.taskID = this.scheduler.runTaskLater(pluginRef, this, 1).getTaskId();
    }

    @Override
    public void run() {
        if (!this.dataDir.exists()) {
            stop();
            return;
        }

        if (!this.dataDir.isDirectory()) {
            this.dataDir.delete();
            stop();
            return;
        }

        if (this.dataDir.listFiles().length <= 0) {
            this.dataDir.delete();
            stop();
            return;
        }

        this.zDirs = this.dataDir.listFiles();

        for (int i = 0; (i < ChunkConversionOptions.getConversionRate()) && (i < this.zDirs.length); i++) {
            if (this.converters[i] == null) {
                this.converters[i] = new BlockStoreConversionZDirectory(pluginRef);
            }

            this.converters[i].start(this.world, this.dataDir, this.zDirs[i]);
        }

        stop();
    }

    public void stop() {
        if (this.taskID < 0) {
            return;
        }

        this.scheduler.cancelTask(this.taskID);
        this.taskID = -1;

        this.dataDir = null;
        this.zDirs = null;
        this.world = null;
        this.scheduler = null;
        this.converters = null;
    }
}
