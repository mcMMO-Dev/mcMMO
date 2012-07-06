package com.gmail.nossr50.util.blockmeta;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class PrimitiveChunkletStore implements ChunkletStore {
    private static final long serialVersionUID = -3453078050608607478L;

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
                    yCompressed |= 1 << y % 8; 
                }
            }

            column[index] = yCompressed;
            index++;
        }

        return column;
    }

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
