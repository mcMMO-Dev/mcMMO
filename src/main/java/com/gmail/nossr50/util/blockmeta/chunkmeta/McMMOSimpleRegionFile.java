/*
 * This file is part of SpoutPlugin.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutPlugin is licensed under the GNU Lesser General Public License.
 *
 * SpoutPlugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutPlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.gmail.nossr50.util.blockmeta.chunkmeta;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class McMMOSimpleRegionFile {
    private RandomAccessFile file;
    private final int[] dataStart = new int[1024];
    private final int[] dataActualLength = new int[1024];
    private final int[] dataLength = new int[1024];
    private final ArrayList<Boolean> inuse = new ArrayList<Boolean>();
    private int segmentSize;
    private int segmentMask;
    private final int rx;
    private final int rz;
    private final int defaultSegmentSize;
    private final File parent;
    @SuppressWarnings("unused")
    private long lastAccessTime = System.currentTimeMillis();
    @SuppressWarnings("unused")
    private static long TIMEOUT_TIME = 300000; // 5 min

    public McMMOSimpleRegionFile(File f, int rx, int rz) {
        this(f, rx, rz, 10);
    }

    public McMMOSimpleRegionFile(File f, int rx, int rz, int defaultSegmentSize) {
        this.rx = rx;
        this.rz = rz;
        this.defaultSegmentSize = defaultSegmentSize;
        this.parent = f;

        lastAccessTime = System.currentTimeMillis();
        if (file == null) {
            try {
                this.file = new RandomAccessFile(parent, "rw");

                if (file.length() < 4096 * 3) {
                    for (int i = 0; i < 1024 * 3; i++) {
                        file.writeInt(0);
                    }
                    file.seek(4096 * 2);
                    file.writeInt(defaultSegmentSize);
                }

                file.seek(4096 * 2);

                this.segmentSize = file.readInt();
                this.segmentMask = (1 << segmentSize) - 1;

                int reservedSegments = this.sizeToSegments(4096 * 3);

                for (int i = 0; i < reservedSegments; i++) {
                    while (inuse.size() <= i) {
                        inuse.add(false);
                    }
                    inuse.set(i, true);
                }

                file.seek(0);

                for (int i = 0; i < 1024; i++) {
                    dataStart[i] = file.readInt();
                }

                for (int i = 0; i < 1024; i++) {
                    dataActualLength[i] = file.readInt();
                    dataLength[i] = sizeToSegments(dataActualLength[i]);
                    setInUse(i, true);
                }

                extendFile();
            }
            catch (IOException fnfe) {
                throw new RuntimeException(fnfe);
            }
        }
    }

    public synchronized final RandomAccessFile getFile() {
        lastAccessTime = System.currentTimeMillis();
        if (file == null) {
            try {
                this.file = new RandomAccessFile(parent, "rw");

                if (file.length() < 4096 * 3) {
                    for (int i = 0; i < 1024 * 3; i++) {
                        file.writeInt(0);
                    }
                    file.seek(4096 * 2);
                    file.writeInt(defaultSegmentSize);
                }

                file.seek(4096 * 2);

                this.segmentSize = file.readInt();
                this.segmentMask = (1 << segmentSize) - 1;

                int reservedSegments = this.sizeToSegments(4096 * 3);

                for (int i = 0; i < reservedSegments; i++) {
                    while (inuse.size() <= i) {
                        inuse.add(false);
                    }
                    inuse.set(i, true);
                }

                file.seek(0);

                for (int i = 0; i < 1024; i++) {
                    dataStart[i] = file.readInt();
                }

                for (int i = 0; i < 1024; i++) {
                    dataActualLength[i] = file.readInt();
                    dataLength[i] = sizeToSegments(dataActualLength[i]);
                    setInUse(i, true);
                }

                extendFile();
            }
            catch (IOException fnfe) {
                throw new RuntimeException(fnfe);
            }
        }
        return file;
    }

    public synchronized boolean testCloseTimeout() {
        /*
        if (System.currentTimeMillis() - TIMEOUT_TIME > lastAccessTime) {
            close();
            return true;
        }
         */
        return false;
    }

    public synchronized DataOutputStream getOutputStream(int x, int z) {
        int index = getChunkIndex(x, z);
        return new DataOutputStream(new DeflaterOutputStream(new McMMOSimpleChunkBuffer(this, index)));
    }

    public synchronized DataInputStream getInputStream(int x, int z) throws IOException {
        int index = getChunkIndex(x, z);
        int actualLength = dataActualLength[index];

        if (actualLength == 0) {
            return null;
        }

        byte[] data = new byte[actualLength];

        getFile().seek(dataStart[index] << segmentSize);
        getFile().readFully(data);
        return new DataInputStream(new InflaterInputStream(new ByteArrayInputStream(data)));
    }

    synchronized void write(int index, byte[] buffer, int size) throws IOException {
        int oldStart = setInUse(index, false);
        int start = findSpace(oldStart, size);
        getFile().seek(start << segmentSize);
        getFile().write(buffer, 0, size);
        dataStart[index] = start;
        dataActualLength[index] = size;
        dataLength[index] = sizeToSegments(size);
        setInUse(index, true);
        saveFAT();
    }

    public synchronized void close() {
        try {
            if (file != null) {
                file.seek(4096 * 2);
                file.close();
            }

            file = null;
        }
        catch (IOException ioe) {
            throw new RuntimeException("Unable to close file", ioe);
        }
    }

    private synchronized int setInUse(int index, boolean used) {
        if (dataActualLength[index] == 0) {
            return dataStart[index];
        }

        int start = dataStart[index];
        int end = start + dataLength[index];

        for (int i = start; i < end; i++) {
            while (i > inuse.size() - 1) {
                inuse.add(false);
            }

            Boolean old = inuse.set(i, used);
            if (old != null && old == used) {
                if (old) {
                    throw new IllegalStateException("Attempting to overwrite an in-use segment");
                }

                throw new IllegalStateException("Attempting to delete empty segment");
            }
        }

        return dataStart[index];
    }

    private synchronized void extendFile() throws IOException {
        long extend = (-getFile().length()) & segmentMask;

        getFile().seek(getFile().length());

        while ((extend--) > 0) {
            getFile().write(0);
        }
    }

    private synchronized int findSpace(int oldStart, int size) {
        int segments = sizeToSegments(size);

        boolean oldFree = true;
        for (int i = oldStart; i < inuse.size() && i < oldStart + segments; i++) {
            if (inuse.get(i)) {
                oldFree = false;
                break;
            }
        }

        if (oldFree) {
            return oldStart;
        }

        int start = 0;
        int end = 0;

        while (end < inuse.size()) {
            if (inuse.get(end)) {
                end++;
                start = end;
            }
            else {
                end++;
            }

            if (end - start >= segments) {
                return start;
            }
        }

        return start;
    }

    private synchronized int sizeToSegments(int size) {
        if (size <= 0) {
            return 1;
        }

        return ((size - 1) >> segmentSize) + 1;
    }

    private synchronized Integer getChunkIndex(int x, int z) {
        if (rx != (x >> 5) || rz != (z >> 5)) {
            throw new RuntimeException(x + ", " + z + " not in region " + rx + ", " + rz);
        }

        x = x & 0x1F;
        z = z & 0x1F;

        return (x << 5) + z;
    }

    private synchronized void saveFAT() throws IOException {
        getFile().seek(0);
        for (int i = 0; i < 1024; i++) {
            getFile().writeInt(dataStart[i]);
        }

        for (int i = 0; i < 1024; i++) {
            getFile().writeInt(dataActualLength[i]);
        }
    }
}
