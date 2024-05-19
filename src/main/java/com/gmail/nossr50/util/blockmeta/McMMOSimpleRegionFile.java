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
package com.gmail.nossr50.util.blockmeta;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.BitSet;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * File format:
 * bytes 0-4096 contain 1024 integer values representing the segment index of each chunk
 * bytes 4096-8192 contain 1024 integer values representing the byte length of each chunk
 * bytes 8192-8196 is the integer value of the segment exponent
 * bytes 8196-12288 are reserved for future use
 * bytes 12288+ contain the data segments, by default 1024 byte segments.
 * Chunk data is compressed and stored in 1 or more segments as needed.
 */
public class McMMOSimpleRegionFile {
    private static final int DEFAULT_SEGMENT_EXPONENT = 10; // TODO, analyze real world usage and determine if a smaller segment(512) is worth it or not. (need to know average chunkstore bytesize)
    private static final int DEFAULT_SEGMENT_SIZE = (int)Math.pow(2, DEFAULT_SEGMENT_EXPONENT); // 1024
    private static final int RESERVED_HEADER_BYTES = 12288; // This needs to be divisible by segment size
    private static final int NUM_CHUNKS = 1024; // 32x32
    private static final int SEEK_CHUNK_SEGMENT_INDICES = 0;
    private static final int SEEK_CHUNK_BYTE_LENGTHS = 4096;
    private static final int SEEK_FILE_INFO = 8192;
    // Chunk info
    private final int[] chunkSegmentIndex = new int[NUM_CHUNKS];
    private final int[] chunkNumBytes = new int[NUM_CHUNKS];
    private final int[] chunkNumSegments = new int[NUM_CHUNKS];

    // Segments
    private final BitSet segments = new BitSet(); // Used to denote which segments are in use or not

    // Segment size/mask
    private final int segmentExponent;
    private final int segmentMask;

    // File location
    private final @NotNull File parent;
    // File access
    private final RandomAccessFile file;

    // Region index
    private final int rx;
    private final int rz;

    public McMMOSimpleRegionFile(@NotNull File f, int rx, int rz) {
        this.rx = rx;
        this.rz = rz;
        this.parent = f;

        try {
            this.file = new RandomAccessFile(parent, "rw");

            // New file, write out header bytes
            if (file.length() < RESERVED_HEADER_BYTES) {
                file.write(new byte[RESERVED_HEADER_BYTES]);
                file.seek(SEEK_FILE_INFO);
                file.writeInt(DEFAULT_SEGMENT_EXPONENT);
            }

            file.seek(SEEK_FILE_INFO);
            this.segmentExponent = file.readInt();
            this.segmentMask = (1 << segmentExponent) - 1;

            // Mark reserved segments reserved
            int reservedSegments = this.bytesToSegments(RESERVED_HEADER_BYTES);
            segments.set(0, reservedSegments, true);

            // Read chunk header data
            file.seek(SEEK_CHUNK_SEGMENT_INDICES);
            for (int i = 0; i < NUM_CHUNKS; i++)
                chunkSegmentIndex[i] = file.readInt();

            file.seek(SEEK_CHUNK_BYTE_LENGTHS);
            for (int i = 0; i < NUM_CHUNKS; i++) {
                chunkNumBytes[i] = file.readInt();
                chunkNumSegments[i] = bytesToSegments(chunkNumBytes[i]);
                markChunkSegments(i, true);
            }

            fixFileLength();
        }
        catch (IOException fnfe) {
            throw new RuntimeException(fnfe);
        }
    }

    public synchronized @NotNull DataOutputStream getOutputStream(int x, int z) {
        int index = getChunkIndex(x, z); // Get chunk index
        return new DataOutputStream(new DeflaterOutputStream(new McMMOSimpleChunkBuffer(this, index)));
    }

    private static class McMMOSimpleChunkBuffer extends ByteArrayOutputStream {
        final McMMOSimpleRegionFile rf;
        final int index;

        McMMOSimpleChunkBuffer(McMMOSimpleRegionFile rf, int index) {
            super(DEFAULT_SEGMENT_SIZE);
            this.rf = rf;
            this.index = index;
        }

        @Override
        public void close() throws IOException {
            rf.write(index, buf, count);
        }
    }

    private synchronized void write(int index, byte[] buffer, int size) throws IOException {
        int oldSegmentIndex = chunkSegmentIndex[index]; // Get current segment index
        markChunkSegments(index, false); // Clear our old segments
        int newSegmentIndex = findContiguousSegments(oldSegmentIndex, size); // Find contiguous segments to save to
        file.seek((long) newSegmentIndex << segmentExponent); // Seek to file location
        file.write(buffer, 0, size); // Write data
        // update in memory info
        chunkSegmentIndex[index] = newSegmentIndex;
        chunkNumBytes[index] = size;
        chunkNumSegments[index] = bytesToSegments(size);
        // Mark segments in use
        markChunkSegments(index, true);
        // Update header info
        file.seek(SEEK_CHUNK_SEGMENT_INDICES + (4L * index));
        file.writeInt(chunkSegmentIndex[index]);
        file.seek(SEEK_CHUNK_BYTE_LENGTHS + (4L * index));
        file.writeInt(chunkNumBytes[index]);
    }

    public synchronized @Nullable DataInputStream getInputStream(int x, int z) throws IOException {
        int index = getChunkIndex(x, z); // Get chunk index
        int byteLength = chunkNumBytes[index]; // Get byte length of data

        // No bytes
        if (byteLength == 0)
            return null;

        byte[] data = new byte[byteLength];

        file.seek((long) chunkSegmentIndex[index] << segmentExponent); // Seek to file location
        file.readFully(data); // Read in the data
        return new DataInputStream(new InflaterInputStream(new ByteArrayInputStream(data)));
    }

    public synchronized void close() {
        try {
            file.close();
            segments.clear();
        }
        catch (IOException ioe) {
            throw new RuntimeException("Unable to close file", ioe);
        }
    }

    private synchronized void markChunkSegments(int index, boolean inUse) {
        // No bytes used
        if (chunkNumBytes[index] == 0)
            return;

        int start = chunkSegmentIndex[index];
        int end = start + chunkNumSegments[index];

        // If we are writing, assert we don't write over any in-use segments
        if (inUse) {
            int nextSetBit = segments.nextSetBit(start);
            if (nextSetBit != -1 && nextSetBit < end)
                throw new IllegalStateException("Attempting to overwrite an in-use segment");
        }

        segments.set(start, end, inUse);
    }

    private synchronized void fixFileLength() throws IOException {
        int fileLength = (int)file.length();
        int extend = -fileLength & segmentMask; // how many bytes do we need to be divisible by segment size

        // Go to end of file
        file.seek(fileLength);
        // Append bytes
        file.write(new byte[extend], 0, extend);
    }

    private synchronized int findContiguousSegments(int hint, int size) {
        if (size == 0)
            return 0; // Zero byte data will not claim any chunks anyways

        int segments = bytesToSegments(size); // Number of segments we need

        // Check the hinted location (previous location of chunk) most of the time we can fit where we were.
        boolean oldFree = true;
        for (int i = hint; i < this.segments.size() && i < hint + segments; i++) {
            if (this.segments.get(i)) {
                oldFree = false;
                break;
            }
        }

        // We fit!
        if (oldFree)
            return hint;

        // Find somewhere to put us
        int start = 0;
        int current = 0;

        while (current < this.segments.size()) {
            boolean segmentInUse = this.segments.get(current); // check if segment is in use
            current++; // Move up a segment

            // Move up start if the segment was in use
            if (segmentInUse)
                start = current;

            // If we have enough segments now, return
            if (current - start >= segments)
                return start;
        }

        // Return the end of the segments (will expand to fit them)
        return start;
    }

    private synchronized int bytesToSegments(int bytes) {
        if (bytes <= 0)
            return 1;

        return ((bytes - 1) >> segmentExponent) + 1; // ((bytes - 1) / segmentSize) + 1
    }

    private synchronized int getChunkIndex(int x, int z) {
        if (rx != (x >> 5) || rz != (z >> 5))
            throw new IndexOutOfBoundsException();

        x = x & 0x1F; // 5 bits (mod 32)
        z = z & 0x1F; // 5 bits (mod 32)

        return (x << 5) + z; // x in the upper 5 bits, z in the lower 5 bits
    }
}
