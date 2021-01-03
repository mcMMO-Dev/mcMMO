package com.gmail.nossr50;

import org.jetbrains.annotations.NotNull;

import java.io.File;

//TODO: Move generic test stuff here
public class TestUtil {
    public static void recursiveDelete(@NotNull File directoryToBeDeleted) {
        if (directoryToBeDeleted.isDirectory()) {
            for (File file : directoryToBeDeleted.listFiles()) {
                recursiveDelete(file);
            }
        }
        directoryToBeDeleted.delete();
    }
}
