package com.gmail.nossr50.skills.repair;

import static java.util.logging.Logger.getLogger;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

class RepairManagerTest extends MMOTestEnvironment {
    private static final java.util.logging.Logger logger = getLogger(
            RepairManagerTest.class.getName());

    private RepairManager repairManager;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        Mockito.when(advancedConfig.getArcaneForgingMaxEnchantLevel()).thenReturn(5);
        repairManager = new RepairManager(mmoPlayer);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @ParameterizedTest(name = "cap={0}, enchantLevel={1} -> {2}")
    @MethodSource("arcaneForgingCapCases")
    void arcaneForgingShouldRespectConfiguredCap(int maxEnchantLevel, int enchantLevel,
            int expectedLevel) {
        Mockito.when(advancedConfig.getArcaneForgingMaxEnchantLevel()).thenReturn(maxEnchantLevel);
        Mockito.when(ExperienceConfig.getInstance().allowUnsafeEnchantments()).thenReturn(false);

        assertEquals(expectedLevel, repairManager.getArcaneForgingEnchantLevel(enchantLevel));
    }

    @ParameterizedTest(name = "unsafe={0}, cap={1}, enchantLevel={2} -> {3}")
    @MethodSource("arcaneForgingUnsafeCases")
    void arcaneForgingShouldIgnoreConfiguredCapWhenUnsafeEnchantmentsAreAllowed(
            boolean unsafeEnchantments, int maxEnchantLevel, int enchantLevel,
            int expectedLevel) {
        Mockito.when(advancedConfig.getArcaneForgingMaxEnchantLevel()).thenReturn(maxEnchantLevel);
        Mockito.when(ExperienceConfig.getInstance().allowUnsafeEnchantments())
                .thenReturn(unsafeEnchantments);

        assertEquals(expectedLevel, repairManager.getArcaneForgingEnchantLevel(enchantLevel));
    }

    private static Stream<Arguments> arcaneForgingCapCases() {
        return Stream.of(
                Arguments.of(5, 1, 1),
                Arguments.of(5, 5, 5),
                Arguments.of(5, 10, 5),
                Arguments.of(10, 10, 10),
                Arguments.of(10, 15, 10),
                Arguments.of(0, 10, 0)
        );
    }

    private static Stream<Arguments> arcaneForgingUnsafeCases() {
        return Stream.of(
                Arguments.of(true, 5, 10, 10),
                Arguments.of(true, 0, 15, 15),
                Arguments.of(false, 10, 15, 10)
        );
    }
}