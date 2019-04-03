package com.gmail.nossr50.util.blockmeta.conversion;

import com.gmail.nossr50.config.ChunkConversionOptions;
import com.gmail.nossr50.mcMMO;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;

public class BlockStoreConversionXDirectory implements Runnable {
    private int taskID;
    private org.bukkit.World world;
    BukkitScheduler scheduler;
    File dataDir;
    File[] zDirs;
    BlockStoreConversionZDirectory[] converters;

    public BlockStoreConversionXDirectory() {
        this.taskID = -1;
    }

    public void start(org.bukkit.World world, File dataDir) {
        this.world = world;
        this.scheduler = mcMMO.p.getServer().getScheduler();
        this.converters = new BlockStoreConversionZDirectory[ChunkConversionOptions.getConversionRate()];
        this.dataDir = dataDir;

        if (this.taskID >= 0) {
            return;
        }

        this.taskID = this.scheduler.runTaskLater(mcMMO.p, this, 1).getTaskId();
        return;
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
                this.converters[i] = new BlockStoreConversionZDirectory();
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
