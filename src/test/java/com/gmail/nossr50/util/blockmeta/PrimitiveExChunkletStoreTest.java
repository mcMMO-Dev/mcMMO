package com.gmail.nossr50.util.blockmeta;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class PrimitiveExChunkletStoreTest {
    byte addresses[][] = new byte[16][16];

    @Before
    public void populateAddresses() {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                addresses[x][z] = PrimitiveExChunkletStore.makeAddressByte(x, z);
            }
        }
    }

    @Test
    public void addressMakeTest() {
        assertEquals(addresses[0][0], 0);
        assertEquals(addresses[15][15], -1);
    }

    @Test
    public void addressReverseTest() {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                assertEquals(x, PrimitiveExChunkletStore.addressByteX(addresses[x][z]));
                assertEquals(z, PrimitiveExChunkletStore.addressByteZ(addresses[x][z]));
            }
        }
    }
}
