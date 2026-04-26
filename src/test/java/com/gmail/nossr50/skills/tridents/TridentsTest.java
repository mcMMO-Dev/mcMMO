package com.gmail.nossr50.skills.tridents;

import static java.util.logging.Logger.getLogger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.util.skills.RankUtils;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class TridentsTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(TridentsTest.class.getName());

    TridentsManager tridentsManager;
    ItemStack trident;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);

        // setup player and player related mocks after everything else
        this.player = Mockito.mock(Player.class);
        Mockito.when(player.getUniqueId()).thenReturn(playerUUID);

        // wire inventory
        this.playerInventory = Mockito.mock(PlayerInventory.class);
        this.trident = new ItemStack(Material.TRIDENT);
        Mockito.when(playerInventory.getItemInMainHand()).thenReturn(trident);

        // Set up spy for manager
        tridentsManager = Mockito.spy(new TridentsManager(mmoPlayer));
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @Test
    void impaleDamageBonusShouldBeZeroAtRankZero() {
        Mockito.when(advancedConfig.getImpaleBaseDamage()).thenReturn(1.0D);
        Mockito.when(advancedConfig.getImpaleRankDamageMultiplier()).thenReturn(0.5D);
        Mockito.when(RankUtils.getRank(any(Player.class),
            Mockito.eq(SubSkillType.TRIDENTS_IMPALE))).thenReturn(0);

        assertEquals(0.0D, tridentsManager.impaleDamageBonus());
    }

    @Test
    void impaleDamageBonusShouldMatchRankOneBaseDamage() {
        Mockito.when(advancedConfig.getImpaleBaseDamage()).thenReturn(1.0D);
        Mockito.when(advancedConfig.getImpaleRankDamageMultiplier()).thenReturn(0.5D);
        Mockito.when(RankUtils.getRank(any(Player.class),
            Mockito.eq(SubSkillType.TRIDENTS_IMPALE))).thenReturn(1);

        assertEquals(1.0D, tridentsManager.impaleDamageBonus());
    }

    @Test
    void impaleDamageBonusShouldScaleWithRankAfterRankOne() {
        Mockito.when(advancedConfig.getImpaleBaseDamage()).thenReturn(1.0D);
        Mockito.when(advancedConfig.getImpaleRankDamageMultiplier()).thenReturn(0.5D);
        Mockito.when(RankUtils.getRank(any(Player.class),
            Mockito.eq(SubSkillType.TRIDENTS_IMPALE))).thenReturn(4);

        assertEquals(2.5D, tridentsManager.impaleDamageBonus());
    }
}
