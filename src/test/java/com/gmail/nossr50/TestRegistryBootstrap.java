package com.gmail.nossr50;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEFAULTS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

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
     *
     * <p>The minting lives in the mock's default answer rather than a {@code when} stub: the
     * mock is created inside the {@code Bukkit.getRegistry} interception (mid class init),
     * and Mockito's stubbing machinery is thread-local state that cannot be trusted while an
     * intercepted invocation is being answered — a {@code when} registered there can silently
     * fail to bind, leaving lookups returning null and poisoning the registry-backed class
     * for the whole JVM. A default answer is baked in at creation and needs no stubbing
     * machinery; explicit per-key {@code when} stubs from test bodies still override it.</p>
     */
    @SuppressWarnings("unchecked")
    public static <T extends Keyed> Registry<T> registryFor(Class<T> type) {
        return (Registry<T>) REGISTRY_MOCKS.computeIfAbsent(type, keyedType ->
                mock(Registry.class, withSettings().defaultAnswer(invocation -> {
                    // Covers get(NamespacedKey) and, on newer APIs, getOrThrow(NamespacedKey)
                    final String methodName = invocation.getMethod().getName();
                    if ((methodName.equals("get") || methodName.equals("getOrThrow"))
                            && invocation.getArguments().length == 1
                            && invocation.getArgument(0) instanceof NamespacedKey) {
                        return mock((Class<?>) keyedType);
                    }
                    return RETURNS_DEFAULTS.answer(invocation);
                })));
    }
}
