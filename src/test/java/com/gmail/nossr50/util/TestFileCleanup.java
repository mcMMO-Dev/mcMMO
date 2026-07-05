package com.gmail.nossr50.util;

import java.io.File;

/**
 * Shared cleanup helper for tests that create temporary directories.
 */
public final class TestFileCleanup {
    private TestFileCleanup() {
    }

    public static void deleteRecursively(final File file) {
        if (file.isDirectory()) {
            final File[] children = file.listFiles();
            if (children != null) {
                for (final File child : children) {
                    deleteRecursively(child);
                }
            }
        }
        file.delete();
    }
}
