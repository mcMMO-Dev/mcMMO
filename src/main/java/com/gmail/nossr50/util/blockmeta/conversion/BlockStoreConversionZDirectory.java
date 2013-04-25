package com.gmail.nossr50.util.blockmeta.conversion;

import java.io.File;

import org.bukkit.scheduler.BukkitScheduler;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.blockmeta.ChunkletStore;
import com.gmail.nossr50.util.blockmeta.HashChunkletManager;
import com.gmail.nossr50.util.blockmeta.PrimitiveChunkletStore;
import com.gmail.nossr50.util.blockmeta.PrimitiveExChunkletStore;
import com.gmail.nossr50.util.blockmeta.chunkmeta.HashChunkManager;
import com.gmail.nossr50.util.blockmeta.chunkmeta.PrimitiveChunkStore;

public class BlockStoreConversionZDirectory implements Runnable {
    public int taskID, cx, cz, x, y, z, y2, xPos, zPos, cxPos, czPos;
    private String cxs, czs, chunkletName, chunkName;
    private org.bukkit.World world;
    private BukkitScheduler scheduler;
    private File xDir, dataDir;
    private HashChunkletManager manager;
    private HashChunkManager newManager;
    private ChunkletStore tempChunklet;
    private PrimitiveChunkletStore primitiveChunklet = null;
    private PrimitiveExChunkletStore primitiveExChunklet = null;
    private PrimitiveChunkStore currentChunk;
    private boolean[] oldArray, newArray;

    public BlockStoreConversionZDirectory() {
        this.taskID = -1;
    }

    public void start(org.bukkit.World world, File xDir, File dataDir) {
        this.world = world;
        this.scheduler = mcMMO.p.getServer().getScheduler();
        this.manager = new HashChunkletManager();
        this.newManager = (HashChunkManager) mcMMO.getPlaceStore();
        this.dataDir = dataDir;
        this.xDir = xDir;

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

        this.cxs = this.xDir.getName();
        this.czs = this.dataDir.getName();
        this.cx = 0;
        this.cz = 0;

        try {
            this.cx = Integer.parseInt(this.cxs);
            this.cz = Integer.parseInt(this.czs);
        }
        catch (Exception e) {
            this.dataDir.delete();
            stop();
            return;
        }

        this.manager.loadChunk(this.cx, this.cz, this.world);

        for (this.y = 0; this.y < (this.world.getMaxHeight() / 64); this.y++) {
            this.chunkletName = this.world.getName() + "," + this.cx + "," + this.cz + "," + this.y;
            this.tempChunklet = this.manager.store.get(this.chunkletName);

            if (this.tempChunklet instanceof PrimitiveChunkletStore) {
                this.primitiveChunklet = (PrimitiveChunkletStore) this.tempChunklet;
            }
            else if (this.tempChunklet instanceof PrimitiveExChunkletStore) {
                this.primitiveExChunklet = (PrimitiveExChunkletStore) this.tempChunklet;
            }

            if (this.tempChunklet == null) {
                continue;
            }

            this.chunkName = this.world.getName() + "," + this.cx + "," + this.cz;
            this.currentChunk = (PrimitiveChunkStore) this.newManager.store.get(this.chunkName);

            if (this.currentChunk != null) {
                this.xPos = this.cx * 16;
                this.zPos = this.cz * 16;

                for (this.x = 0; this.x < 16; this.x++) {
                    for (this.z = 0; this.z < 16; this.z++) {
                        this.cxPos = this.xPos + this.x;
                        this.czPos = this.zPos + this.z;

                        for (this.y2 = (64 * this.y); this.y2 < (64 * this.y + 64); this.y2++) {
                            try {
                                if (!this.manager.isTrue(this.cxPos, this.y2, this.czPos, this.world)) {
                                    continue;
                                }

                                this.newManager.setTrue(this.cxPos, this.y2, this.czPos, this.world);
                            }
                            catch (Exception e) {}
                        }
                    }
                }

                continue;
            }

            this.newManager.setTrue(this.cx * 16, 0, this.cz * 16, this.world);
            this.newManager.setFalse(this.cx * 16, 0, this.cz * 16, this.world);
            this.currentChunk = (PrimitiveChunkStore) this.newManager.store.get(this.chunkName);

            for (this.x = 0; this.x < 16; this.x++) {
                for (this.z = 0; this.z < 16; this.z++) {
                    if (this.primitiveChunklet != null) {
                        this.oldArray = this.primitiveChunklet.store[x][z];
                    }

                    if (this.primitiveExChunklet != null) {
                        this.oldArray = this.primitiveExChunklet.store[x][z];
                    }
                    else {
                        return;
                    }

                    this.newArray = this.currentChunk.store[x][z];

                    if (this.oldArray.length < 64) {
                        return;
                    }
                    else if (this.newArray.length < ((this.y * 64) + 64)) {
                        return;
                    }

                    System.arraycopy(this.oldArray, 0, this.newArray, (this.y * 64), 64);
                }
            }
        }

        this.manager.unloadChunk(this.cx, this.cz, this.world);
        this.newManager.unloadChunk(this.cx, this.cz, this.world);

        for (File yFile : dataDir.listFiles()) {
            if (!yFile.exists()) {
                continue;
            }

            yFile.delete();
        }

        stop();
    }

    public void stop() {
        if (this.taskID < 0) {
            return;
        }

        this.scheduler.cancelTask(taskID);
        this.taskID = -1;

        this.cxs = null;
        this.czs = null;
        this.chunkletName = null;
        this.chunkName = null;
        this.manager = null;
        this.xDir = null;
        this.dataDir = null;
        this.tempChunklet = null;
        this.primitiveChunklet = null;
        this.primitiveExChunklet = null;
        this.currentChunk = null;
    }
}
