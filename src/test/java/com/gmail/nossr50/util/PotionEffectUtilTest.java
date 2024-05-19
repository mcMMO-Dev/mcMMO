package com.gmail.nossr50.util;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.compat.CompatibilityManager;
import com.gmail.nossr50.util.platform.MinecraftGameVersion;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static com.gmail.nossr50.util.PotionEffectUtil.getNauseaPotionEffectType;
import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class PotionEffectUtilTest {
    private MockedStatic<mcMMO> mockedStaticMcMMO;
    private static final java.util.logging.Logger logger = getLogger(PotionEffectUtilTest.class.getName());

    @BeforeEach
    void setUp() {
        mockedStaticMcMMO = mockStatic(mcMMO.class);
        mcMMO.p = mock(mcMMO.class);
        when(mcMMO.p.getLogger()).thenReturn(logger);
        CompatibilityManager compatibilityManager = mock(CompatibilityManager.class);
        MinecraftGameVersion minecraftGameVersion = mock(MinecraftGameVersion.class);
        when(compatibilityManager.getMinecraftGameVersion()).thenReturn(minecraftGameVersion);
        when(minecraftGameVersion.isAtLeast(1, 20, 5)).thenReturn(false);
        when(mcMMO.getCompatibilityManager()).thenReturn(compatibilityManager);
    }

    @AfterEach
    void tearDown() {
        mockedStaticMcMMO.close();
    }

    @Test
    @Tag("skip")
    void testGetNauseaPotionEffectType() {
        // TODO: Test only works on older versions since we aren't properly mocking the spigot registry
        final PotionEffectType nausea = getNauseaPotionEffectType();
        assertNotNull(nausea);
        assertThat(nausea).isEqualTo(PotionEffectType.NAUSEA);
    }

    @Test
    @Tag("skip")
    void testGetHastePotionEffectType() {
        // TODO: Test only works on older versions since we aren't properly mocking the spigot registry
        final PotionEffectType haste = PotionEffectUtil.getHastePotionEffectType();
        assertNotNull(haste);
        assertThat(haste).isEqualTo(PotionEffectType.HASTE);
    }
}