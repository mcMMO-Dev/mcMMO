package com.gmail.nossr50.locale;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.assertj.core.api.Assertions.assertThat;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.mcMMO;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Covers the swap {@code reloadLocale()} performs.
 *
 * <p>The bundles and the string cache they feed are replaced as one immutable snapshot. They
 * used to be separate static fields mutated in place, which gave a reader on another thread
 * two ways to be wrong: bundles from one locale paired with a cache still holding the other
 * locale's strings, or a cached string that outlived the file it came from. Every chat
 * message, notification and command reply reads through here, and on Folia they do it from
 * region threads while the reload runs on the main one.</p>
 */
class LocaleLoaderSnapshotTest extends MMOTestEnvironment {
    private static final Logger logger = Logger.getLogger(
            LocaleLoaderSnapshotTest.class.getName());
    private static final String KEY = "Commands.Disabled";

    private Path overrideFile;

    @BeforeEach
    void setUp() throws Exception {
        mockBaseEnvironment(logger);
        overrideFile = new File(mcMMO.getLocalesDirectory(), "locale_override.properties")
                .toPath();
        forgetTheLoadedSnapshot();
    }

    @AfterEach
    void tearDown() throws Exception {
        // The snapshot is static and outlives this class. Leaving a clean one loaded beats
        // leaving null behind, which would make the next reader build one from an mcMMO
        // nobody has mocked and write a stray override file into the working directory.
        Files.deleteIfExists(overrideFile);
        LocaleLoader.reloadLocale();
        cleanUpStaticMocks();
    }

    private void forgetTheLoadedSnapshot() throws Exception {
        final Field snapshotField = LocaleLoader.class.getDeclaredField("localeSnapshot");
        snapshotField.setAccessible(true);
        snapshotField.set(null, null);
    }

    private void writeOverride(String value) throws Exception {
        Files.writeString(overrideFile, KEY + "=" + value + System.lineSeparator(),
                StandardCharsets.UTF_8);
    }

    /**
     * Replaces the override with a complete file in one step. A plain write would let a
     * reload read the file mid-truncation and miss the key for reasons that have nothing to
     * do with what is being tested.
     */
    private void swapOverride(String value) throws Exception {
        final Path staged = overrideFile.resolveSibling("staged_override.properties");
        Files.writeString(staged, KEY + "=" + value + System.lineSeparator(),
                StandardCharsets.UTF_8);
        Files.move(staged, overrideFile, REPLACE_EXISTING, ATOMIC_MOVE);
    }

    @Nested
    class ReloadingTheLocale {

        @Test
        void publishesTheNewStringsRatherThanTheCachedOnes() throws Exception {
            // Given - a string that has already been read once, so it is cached
            writeOverride("first");
            LocaleLoader.reloadLocale();
            assertThat(LocaleLoader.getString(KEY)).isEqualTo("first");

            // When - the file changes and the locale is reloaded
            writeOverride("second");
            LocaleLoader.reloadLocale();

            // Then - the new value is served, not the one cached before the reload
            assertThat(LocaleLoader.getString(KEY)).isEqualTo("second");
        }

        /**
         * Consumers that build their own derived data from locale strings watch this
         * counter to know when to rebuild, so a reload that swapped the strings without
         * bumping it would leave them showing the previous locale indefinitely.
         */
        @Test
        void bumpsTheGenerationSoDerivedDataKnowsToRebuild() throws Exception {
            // Given - the generation as it stands after a first load
            writeOverride("first");
            LocaleLoader.reloadLocale();
            final int before = LocaleLoader.getLocaleGeneration();

            // When - the locale is reloaded
            LocaleLoader.reloadLocale();

            // Then - the counter has moved on
            assertThat(LocaleLoader.getLocaleGeneration()).isGreaterThan(before);
        }

        @Test
        void fallsBackToTheBundledStringWhenTheOverrideDropsAKey() throws Exception {
            // Given - the value the shipped locale carries for this key
            LocaleLoader.reloadLocale();
            final String bundled = LocaleLoader.getString(KEY);
            assertThat(bundled).isNotBlank().doesNotStartWith("!");

            // And - an override supplying its own value for it
            writeOverride("overridden");
            LocaleLoader.reloadLocale();
            assertThat(LocaleLoader.getString(KEY)).isEqualTo("overridden");

            // When - the override no longer carries that key
            Files.writeString(overrideFile, "Some.Other.Key=value" + System.lineSeparator(),
                    StandardCharsets.UTF_8);
            LocaleLoader.reloadLocale();

            // Then - the shipped string answers, rather than the stale override value
            assertThat(LocaleLoader.getString(KEY)).isEqualTo(bundled);
        }

        /**
         * The reason the bundles and their cache are one immutable record. A read that landed
         * on a half-finished swap would come back with the "!key!" miss marker or throw. This
         * cannot tell which of the two snapshots a read came from, only that it was a whole
         * one, so it catches a torn swap and not a stale one.
         */
        @Test
        void readersOnOtherThreadsAlwaysSeeAWholeLocale() throws Exception {
            // Given - four threads reading the same key in a loop
            swapOverride("first");
            LocaleLoader.reloadLocale();
            final int readers = 4;
            final CountDownLatch startLine = new CountDownLatch(1);
            final CountDownLatch finished = new CountDownLatch(readers);
            final Set<String> observed = ConcurrentHashMap.newKeySet();
            final AtomicReference<Throwable> failure = new AtomicReference<>();
            final ExecutorService threads = Executors.newFixedThreadPool(readers);

            for (int reader = 0; reader < readers; reader++) {
                threads.execute(() -> {
                    try {
                        startLine.await();
                        for (int read = 0; read < 2000; read++) {
                            observed.add(LocaleLoader.getString(KEY));
                        }
                    } catch (Throwable thrown) {
                        failure.compareAndSet(null, thrown);
                    } finally {
                        finished.countDown();
                    }
                });
            }

            // When - the locale is reloaded underneath them, over and over
            startLine.countDown();
            for (int reload = 0; reload < 40; reload++) {
                swapOverride(reload % 2 == 0 ? "second" : "first");
                LocaleLoader.reloadLocale();
            }
            final boolean allDone = finished.await(30, TimeUnit.SECONDS);
            threads.shutdownNow();

            // Then - every read came back with one of the two whole values
            assertThat(failure).hasValue(null);
            assertThat(allDone).isTrue();
            assertThat(observed).isNotEmpty().isSubsetOf("first", "second");
        }
    }

    @Nested
    class TheFirstRead {

        @Test
        void loadsTheLocaleWithoutAnExplicitReload() throws Exception {
            // Given - a locale that has never been loaded in this test
            writeOverride("lazy");

            // When - a string is asked for
            final String message = LocaleLoader.getString(KEY);

            // Then - the locale loaded itself on demand
            assertThat(message).isEqualTo("lazy");
        }
    }
}
