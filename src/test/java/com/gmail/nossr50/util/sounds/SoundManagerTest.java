package com.gmail.nossr50.util.sounds;

import static java.util.logging.Logger.getLogger;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.config.SoundConfig;
import com.gmail.nossr50.mcMMO;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class SoundManagerTest {
    private static final Logger logger = getLogger(SoundManagerTest.class.getName());

    private MockedStatic<mcMMO> mcMMOMock;
    private MockedStatic<Bukkit> bukkitMock;
    private MockedStatic<SoundConfig> soundConfigMock;
    private SoundConfig soundConfig;

    @BeforeEach
    void setUp() {
        mcMMOMock = mockStatic(mcMMO.class);
        mcMMO.p = mock(mcMMO.class);
        when(mcMMO.p.getLogger()).thenReturn(logger);

        // org.bukkit.Registry initialization needs a server registry lookup
        bukkitMock = mockStatic(Bukkit.class);
        bukkitMock.when(() -> Bukkit.getRegistry(any())).thenAnswer(
                invocation -> mock(Registry.class));

        soundConfig = mock(SoundConfig.class);
        soundConfigMock = mockStatic(SoundConfig.class);
        soundConfigMock.when(SoundConfig::getInstance).thenReturn(soundConfig);
    }

    @AfterEach
    void tearDown() {
        soundConfigMock.close();
        bukkitMock.close();
        mcMMOMock.close();
    }

    /**
     * Regression coverage for the Cripple sound: on servers where the mace smash sound doesn't
     * exist, the lookup used to substitute the anvil place sound (and on modern servers the
     * default sound ID also pointed at the anvil). Cripple must play the mace smash ground
     * sound or stay silent, never the anvil.
     */
    @Test
    void crippleShouldNeverPlayTheAnvilSound() {
        // Given - the Cripple sound is enabled with no custom sound ID configured
        when(soundConfig.getIsEnabled(SoundType.CRIPPLE)).thenReturn(true);
        when(soundConfig.getSound(SoundType.CRIPPLE)).thenReturn("");
        final Player player = mock(Player.class);
        final Location location = mock(Location.class);

        // When - the Cripple sound is sent
        SoundManager.sendSound(player, location, SoundType.CRIPPLE);

        // Then - the anvil sound is never used as a stand-in
        verify(player, never()).playSound(any(Location.class), eq(Sound.BLOCK_ANVIL_PLACE),
                any(SoundCategory.class), anyFloat(), anyFloat());
    }
}
