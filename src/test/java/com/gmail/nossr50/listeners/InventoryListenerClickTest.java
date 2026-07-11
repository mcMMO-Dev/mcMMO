package com.gmail.nossr50.listeners;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.alchemy.Alchemy;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import java.util.Set;
import java.util.logging.Logger;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Covers the container click and drag handlers against non-player viewers: getWhoClicked
 * returns a HumanEntity, and plugins can open container inventories for human-shaped entities
 * that are not players.
 */
class InventoryListenerClickTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(InventoryListenerClickTest.class.getName());

    private InventoryListener inventoryListener;
    private HumanEntity nonPlayerViewer;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(logger);
        inventoryListener = new InventoryListener(mcMMO.p);
        nonPlayerViewer = mock(HumanEntity.class);
        when(nonPlayerViewer.getWorld()).thenReturn(world);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    /**
     * Regression coverage for furnace clicks by non-player entities: the handler previously
     * cast getWhoClicked to Player unchecked and crashed instead of ignoring the click.
     */
    @Test
    void furnaceClickByNonPlayerViewerShouldBeIgnoredWithoutCrashing() {
        // Given - a furnace inventory clicked by a non-player HumanEntity
        final InventoryClickEvent event = mock(InventoryClickEvent.class);
        when(event.getWhoClicked()).thenReturn(nonPlayerViewer);
        when(event.getInventory()).thenReturn(mock(FurnaceInventory.class));

        // When - the click is handled
        // Then - the click is ignored without an exception
        assertThatCode(() -> inventoryListener.onInventoryClickEventNormal(event))
                .doesNotThrowAnyException();
    }

    /**
     * The brewing-stand click branch had the same unchecked Player cast as the furnace branch.
     */
    @Test
    void brewingStandClickByNonPlayerViewerShouldBeIgnoredWithoutCrashing() {
        // Given - a brewing stand inventory clicked by a non-player HumanEntity
        final InventoryClickEvent event = mock(InventoryClickEvent.class);
        when(event.getWhoClicked()).thenReturn(nonPlayerViewer);
        when(event.getInventory()).thenReturn(mock(BrewerInventory.class));

        // When - the click is handled
        // Then - the click is ignored without an exception
        assertThatCode(() -> inventoryListener.onInventoryClickEventNormal(event))
                .doesNotThrowAnyException();
    }

    /**
     * The ingredient drag handler also cast the viewer to Player once the drag touched the
     * ingredient slot of an empty brewing stand.
     */
    @Test
    void brewingStandDragByNonPlayerViewerShouldBeIgnoredWithoutCrashing() {
        // Given - a drag over the ingredient slot of an empty brewing stand by a non-player
        final BrewerInventory brewerInventory = mock(BrewerInventory.class);
        when(brewerInventory.getHolder()).thenReturn(mock(BrewingStand.class));
        final InventoryDragEvent event = mock(InventoryDragEvent.class);
        when(event.getWhoClicked()).thenReturn(nonPlayerViewer);
        when(event.getInventory()).thenReturn(brewerInventory);
        when(event.getInventorySlots()).thenReturn(Set.of(Alchemy.INGREDIENT_SLOT));
        when(event.getCursor()).thenReturn(mock(ItemStack.class));
        mockedUserManager.when(() -> UserManager.hasPlayerDataKey(nonPlayerViewer))
                .thenReturn(true);
        mockedPermissions.when(() -> Permissions.isSubSkillEnabled(nonPlayerViewer,
                SubSkillType.ALCHEMY_CONCOCTIONS)).thenReturn(true);

        // When - the drag is handled
        // Then - the drag is ignored without an exception
        assertThatCode(() -> inventoryListener.onInventoryDragEvent(event))
                .doesNotThrowAnyException();
    }
}
