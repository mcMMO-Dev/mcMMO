package com.gmail.nossr50.util.blockmeta.conversion;

import com.gmail.nossr50.core.ChunkConversionOptions;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcmmo.api.platform.scheduler.Task;

import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;

public class BlockStoreConversionXDirectory implements Runnable {
    BukkitScheduler scheduler;
    File dataDir;
    File[] zDirs;
    BlockStoreConversionZDirectory[] converters;
    private Task task;
    private org.bukkit.World world;
    private final mcMMO pluginRef;

    public BlockStoreConversionXDirectory(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    public void start(org.bukkit.World world, File dataDir) {
        this.world = world;
        this.converters = new BlockStoreConversionZDirectory[ChunkConversionOptions.getConversionRate()];
        this.dataDir = dataDir;

        if (this.task != null) {
            return;
        }

        this.task = pluginRef.getPlatformProvider().getScheduler().getTaskBuilder()
                            .setDelay(1L)
                            .setTask(this)
                            .schedule();
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
        if (this.task == null) {
            return;
        }

        this.task.cancel();
        this.task = null;

        this.dataDir = null;
        this.zDirs = null;
        this.world = null;
        this.scheduler = null;
        this.converters = null;
    }
}
