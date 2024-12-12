package com.gmail.nossr50.skills.tridents;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

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
}
