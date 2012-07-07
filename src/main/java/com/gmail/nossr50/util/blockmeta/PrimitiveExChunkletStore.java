package com.gmail.nossr50.util.blockmeta;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class PrimitiveExChunkletStore implements ChunkletStore, Externalizable {
	private static final long serialVersionUID = 8603603827094383873L;

    /** X, Z, Y */
    private boolean[][][] store = new boolean[16][16][64];

    @Override
    public boolean isTrue(int x, int y, int z) {
        return store[x][z][y];
    }

    @Override
    public void setTrue(int x, int y, int z) {
        store[x][z][y] = true;
    }

    @Override
    public void setFalse(int x, int y, int z) {
        store[x][z][y] = false;
    }

    @Override
    public boolean isEmpty() {
        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                for(int y = 0; y < 64; y++) {
                    if(store[x][z][y]) return false;
                }
            }
        }
        return true;
    }

    @Override
    public void copyFrom(ChunkletStore otherStore) {
        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                for(int y = 0; y < 64; y++) {
                    store[x][z][y] = otherStore.isTrue(x, y, z);
                }
            }
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        byte[] buffer = new byte[2304]; // 2304 is 16*16*9
        int bufferIndex = 0;

        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                for(int y = 0; y < 64; y++) {
                    if(store[x][z][y]) {
                        byte[] temp = constructColumn(x, z);

                        for(int i = 0; i < 9; i++) {
                            buffer[bufferIndex] = temp[i];
                            bufferIndex++;
                        }

                        break;
                    }
                }
            }
        }

        out.write(buffer, 0, bufferIndex);
        out.flush();
    }

    // For this we assume that store has been initialized to be all false by now
    @Override
    public void readExternal(ObjectInput in) throws IOException {
        byte[] temp = new byte[9];

        // Could probably reorganize this loop to print nasty things if it does not equal 9 or -1
        while(in.read(temp, 0, 9) == 9) {
            int x = addressByteX(temp[0]);
            int z = addressByteZ(temp[0]);
            boolean[] yColumn = new boolean[64];

            for(int i = 0; i < 8; i++) {
                for(int j = 0; j < 8; j++) {
                    yColumn[j + (i * 8)] = (temp[i + 1] & (1 << j)) != 0;
                }
            }

            store[x][z] = yColumn;
        }
    }

    /*
     * The column: An array of 9 bytes which represent all y values for a given (x,z) Chunklet-coordinate
     *
     * The first byte is an address byte, this provides the x and z values.
     * The next 8 bytes are all y values from 0 to 63, with each byte containing 8 bits of true/false data
     *
     * Each of these 8 bytes address to a y value from right to left
     *
     * Examples:
     * 00000001 represents that the lowest y value in this byte is true, all others are off
     * 10000000 represents that the highest y value in this byte is true, all others are off
     * 10000001 represents that the lowest and highest y values in this byte are true, all others are off
     *
     * Full columns:
     * See comment on Address byte for information on how to use that byte
     *
     * Example:
     * ADDRESS_BYTE 10000000 00000001 00000000 00000000 00000000 00000000 00000000 00000000
     *  - x, z from ADDRESS_BYTE
     *  - The next byte contains data from 0 to 7
     *    - 1 is set in the highest bit position, this is 7 in y coordinate
     *  - The next byte contains data from 8 to 15
     *    - 1 is set in the lowest bit position, this is 8 in the y coordinate
     *  Therefore, for this column: There are true values at (x, 7, z) and (x, 8, z)
     */
    private byte[] constructColumn(int x, int z) {
        byte[] column = new byte[9];
        int index = 1;

        column[0] = makeAddressByte(x, z);

        for (int i = 0; i < 8; i++){
            byte yCompressed = 0x0;
            int subColumnIndex = 8 * i;
            int subColumnEnd = subColumnIndex + 8;

            for(int y = subColumnIndex; y < subColumnEnd; y++) {
                if(store[x][z][y]) {
                    yCompressed |= 1 << (y % 8);
                }
            }

            column[index] = yCompressed;
            index++;
        }

        return column;
    }

    /*
     * The address byte: A single byte which contains x and z values which correspond to the x and z Chunklet-coordinates
     *
     * In Chunklet-coordinates, the only valid values are 0-15, so we can fit both into a single byte.
     *
     * The top 4 bits of the address byte are for the x value
     * The bottom 4 bits of the address byte are for the z value
     *
     * Examples:
     * An address byte with a value 00000001 would be split like so:
     *  - x = 0000 = 0
     *  - z = 0001 = 1
     *  => Chunklet coordinates (0, 1)
     *
     * 01011111
     *  - x = 0101 = 5
     *  - z = 1111 = 15
     *  => Chunklet coordinates (5, 15)
     */
    private static byte makeAddressByte(int x, int z) {
        return (byte) ((x << 4) + z);
    }

    private static int addressByteX(byte address) {
        return (address & 0xF0) >>> 4;
    }

    private static int addressByteZ(byte address) {
        return address & 0x0F;
    }
}
