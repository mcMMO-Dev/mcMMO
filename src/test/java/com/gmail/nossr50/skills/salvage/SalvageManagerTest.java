package com.gmail.nossr50.skills.salvage;

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

class SalvageManagerTest extends MMOTestEnvironment {
    private static final java.util.logging.Logger logger = getLogger(
            SalvageManagerTest.class.getName());

    private SalvageManager salvageManager;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        Mockito.when(advancedConfig.getArcaneSalvageMaxEnchantLevel()).thenReturn(5);
        salvageManager = new SalvageManager(mmoPlayer);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @ParameterizedTest(name = "cap={0}, enchantLevel={1} -> {2}")
    @MethodSource("arcaneSalvageCapCases")
    void arcaneSalvageShouldRespectConfiguredCap(int maxEnchantLevel, int enchantLevel,
            int expectedLevel) {
        Mockito.when(advancedConfig.getArcaneSalvageMaxEnchantLevel()).thenReturn(maxEnchantLevel);
        Mockito.when(ExperienceConfig.getInstance().allowUnsafeEnchantments()).thenReturn(false);

        assertEquals(expectedLevel, salvageManager.getArcaneSalvageEnchantLevel(enchantLevel));
    }

    @ParameterizedTest(name = "unsafe={0}, cap={1}, enchantLevel={2} -> {3}")
    @MethodSource("arcaneSalvageUnsafeCases")
    void arcaneSalvageShouldIgnoreConfiguredCapWhenUnsafeEnchantmentsAreAllowed(
            boolean unsafeEnchantments, int maxEnchantLevel, int enchantLevel,
            int expectedLevel) {
        Mockito.when(advancedConfig.getArcaneSalvageMaxEnchantLevel()).thenReturn(maxEnchantLevel);
        Mockito.when(ExperienceConfig.getInstance().allowUnsafeEnchantments())
                .thenReturn(unsafeEnchantments);

        assertEquals(expectedLevel, salvageManager.getArcaneSalvageEnchantLevel(enchantLevel));
    }

    private static Stream<Arguments> arcaneSalvageCapCases() {
        return Stream.of(
                Arguments.of(5, 1, 1),
                Arguments.of(5, 5, 5),
                Arguments.of(5, 10, 5),
                Arguments.of(10, 10, 10),
                Arguments.of(10, 15, 10),
                Arguments.of(0, 10, 0)
        );
    }

    private static Stream<Arguments> arcaneSalvageUnsafeCases() {
        return Stream.of(
                Arguments.of(true, 5, 10, 10),
                Arguments.of(true, 0, 15, 15),
                Arguments.of(false, 10, 15, 10)
        );
    }
}
