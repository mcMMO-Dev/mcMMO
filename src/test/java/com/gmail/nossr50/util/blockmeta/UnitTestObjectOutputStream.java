package com.gmail.nossr50.util.blockmeta;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

class UnitTestObjectOutputStream extends ObjectOutputStream {

    public UnitTestObjectOutputStream(@NotNull OutputStream outputStream) throws IOException {
        super(outputStream);
    }

    @Override
    public void writeUTF(@NotNull String str) throws IOException {
        // Pretend to be the old class
        if (str.equals(LegacyChunkStore.class.getName()))
            str = "com.gmail.nossr50.util.blockmeta.chunkmeta.PrimitiveChunkStore";
        super.writeUTF(str);
    }

}
