package com.gmail.nossr50;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.mockito.MockedStatic;

/**
 * Makes Bukkit's registry-backed types (PotionEffectType, Enchantment, ...) usable off-server.
 *
 * <p>Registry constants normally resolve through {@code Bukkit.getRegistry} during their
 * class initialization, which explodes without a running server and poisons the class for the
 * whole JVM. This bootstrap stubs {@code Bukkit.getRegistry} to hand out one mock registry per
 * registry type whose {@code get} mints a Mockito mock of that type per lookup. The JVM's
 * recursive-initialization rule lets those mocks be created while the registry class's own
 * static initializer is still running.</p>
 *
 * <p>Call {@link #bootstrap} in a test's setup while the harness's Bukkit static mock is
 * active, BEFORE anything touches a registry-backed class. The registry mocks are JVM-global
 * (class initialization only happens once), so per-test stubbing must go through
 * {@link #registryFor} and remember that stubs on these shared mocks outlive the test.</p>
 */
public final class TestRegistryBootstrap {
    private static final Map<Class<?>, Registry<?>> REGISTRY_MOCKS = new ConcurrentHashMap<>();
    private static volatile boolean coreClassesInitialized;

    private TestRegistryBootstrap() {
    }

    public static void bootstrap(MockedStatic<Bukkit> mockedBukkit) {
        mockedBukkit.when(() -> Bukkit.getRegistry(any()))
                .thenAnswer(invocation -> registryFor(invocation.getArgument(0)));

        if (!coreClassesInitialized) {
            try {
                // Force the registry holder and its heaviest consumers to initialize while
                // the answer above is active
                Class.forName("org.bukkit.Registry");
                Class.forName("org.bukkit.potion.PotionEffectType");
                Class.forName("org.bukkit.enchantments.Enchantment");
            } catch (ClassNotFoundException e) {
                throw new AssertionError(e);
            }
            coreClassesInitialized = true;
        }
    }

    /**
     * The shared registry mock for a registry-backed type. Lookups mint a fresh mock of the
     * type unless a test stubs a specific key.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T extends Keyed> Registry<T> registryFor(Class<T> type) {
        return (Registry<T>) REGISTRY_MOCKS.computeIfAbsent(type, keyedType -> {
            final Registry mockRegistry = mock(Registry.class);
            when(mockRegistry.get(any(NamespacedKey.class)))
                    .thenAnswer(lookup -> mock((Class<?>) keyedType));
            return mockRegistry;
        });
    }
}
