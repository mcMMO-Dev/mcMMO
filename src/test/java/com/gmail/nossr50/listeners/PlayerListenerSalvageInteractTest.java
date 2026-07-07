package com.gmail.nossr50.listeners;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.salvage.SalvageManager;
import com.gmail.nossr50.skills.salvage.salvageables.SalvageableManager;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.RankUtils;
import java.util.logging.Logger;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Covers the salvage anvil interaction flow in {@link PlayerListener#onPlayerInteractLowest}.
 * The confirmation flow must never salvage an item the player was not prompted for, and while a
 * confirmation is pending the held item must not be usable (vanilla armor quick-equip would
 * otherwise swap the held piece with worn armor mid-confirmation).
 */
class PlayerListenerSalvageInteractTest extends MMOTestEnvironment {
    private static final Logger logger =
            getLogger(PlayerListenerSalvageInteractTest.class.getName());

    private PlayerListener playerListener;
    private SalvageManager salvageManager;
    private Block anvilBlock;
    private Block dirtBlock;
    private ItemStack helmet;
    private ItemStack chestplate;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        playerListener = new PlayerListener(mcMMO.p);

        when(generalConfig.getSalvageAnvilMaterial()).thenReturn(Material.GOLD_BLOCK);
        when(generalConfig.getRepairAnvilMaterial()).thenReturn(Material.IRON_BLOCK);
        when(generalConfig.getAbilitiesOnlyActivateWhenSneaking()).thenReturn(false);
        when(generalConfig.getSalvageConfirmRequired()).thenReturn(true);

        when(UserManager.hasPlayerDataKey(player)).thenReturn(true);
        when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        when(player.isSneaking()).thenReturn(false);

        when(Permissions.skillEnabled(player, PrimarySkillType.SALVAGE)).thenReturn(true);
        when(RankUtils.hasUnlockedSubskill(player, SubSkillType.SALVAGE_SCRAP_COLLECTOR))
                .thenReturn(true);

        final SalvageableManager salvageableManager = mock(SalvageableManager.class);
        when(mcMMO.getSalvageableManager()).thenReturn(salvageableManager);

        helmet = mockSalvageableItem(salvageableManager);
        chestplate = mockSalvageableItem(salvageableManager);
        when(helmet.isSimilar(helmet)).thenReturn(true);
        when(chestplate.isSimilar(chestplate)).thenReturn(true);

        anvilBlock = mock(Block.class);
        when(anvilBlock.getType()).thenReturn(Material.GOLD_BLOCK);
        when(anvilBlock.getLocation()).thenReturn(new Location(world, 0, 0, 0));

        dirtBlock = mock(Block.class);
        when(dirtBlock.getType()).thenReturn(Material.DIRT);

        // removeAbilityBoostsFromInventory iterates the inventory contents before a salvage
        when(playerInventory.getContents()).thenReturn(new ItemStack[0]);

        salvageManager = Mockito.spy(new SalvageManager(mmoPlayer));
        doReturn(salvageManager).when(mmoPlayer).getSalvageManager();
        doNothing().when(salvageManager).handleSalvage(any(), any());
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    private ItemStack mockSalvageableItem(SalvageableManager salvageableManager) {
        final ItemStack item = mock(ItemStack.class);
        when(item.getAmount()).thenReturn(1);
        when(item.clone()).thenReturn(item);
        when(salvageableManager.isSalvageable(item)).thenReturn(true);
        return item;
    }

    private PlayerInteractEvent fireRightClick(Action action, Block block, ItemStack heldItem) {
        when(playerInventory.getItemInMainHand()).thenReturn(heldItem);
        final PlayerInteractEvent event = new PlayerInteractEvent(player, action, heldItem, block,
                BlockFace.SELF, EquipmentSlot.HAND);
        playerListener.onPlayerInteractLowest(event);
        return event;
    }

    @Test
    void firstAnvilClickShouldPromptAndCancelInteraction() {
        // Given - a player holding a salvageable helmet with no salvage confirmation pending
        // When - the player right-clicks the salvage anvil
        final PlayerInteractEvent event =
                fireRightClick(Action.RIGHT_CLICK_BLOCK, anvilBlock, helmet);

        // Then - the interaction is fully cancelled so the helmet is not equipped
        assertThat(event.useInteractedBlock()).isEqualTo(Event.Result.DENY);
        assertThat(event.useItemInHand()).isEqualTo(Event.Result.DENY);
        // And - nothing is salvaged before the player confirms
        verify(salvageManager, never()).handleSalvage(any(), any());
    }

    @Test
    void secondAnvilClickShouldSalvageWhenSameItemIsStillHeld() {
        // Given - the player was prompted to confirm salvaging the helmet
        fireRightClick(Action.RIGHT_CLICK_BLOCK, anvilBlock, helmet);

        // When - the player right-clicks the anvil again while still holding the same helmet
        final PlayerInteractEvent event =
                fireRightClick(Action.RIGHT_CLICK_BLOCK, anvilBlock, helmet);

        // Then - the helmet is salvaged and the interaction stays cancelled
        verify(salvageManager).handleSalvage(any(), eq(helmet));
        assertThat(event.useInteractedBlock()).isEqualTo(Event.Result.DENY);
        assertThat(event.useItemInHand()).isEqualTo(Event.Result.DENY);
    }

    @Test
    void confirmationClickShouldNotSalvageWhenHeldItemChangedSincePrompt() {
        // Given - the player was prompted to confirm salvaging the helmet
        fireRightClick(Action.RIGHT_CLICK_BLOCK, anvilBlock, helmet);

        // When - vanilla armor quick-equip swapped the held item and the player clicks again
        // while holding the previously worn chestplate
        fireRightClick(Action.RIGHT_CLICK_BLOCK, anvilBlock, chestplate);

        // Then - the chestplate is not salvaged; the player must confirm the new item instead
        verify(salvageManager, never()).handleSalvage(any(), any());
    }

    @Test
    void rightClickAirShouldNotEquipHeldItemWhileConfirmationIsPending() {
        // Given - the player was prompted to confirm salvaging the helmet
        fireRightClick(Action.RIGHT_CLICK_BLOCK, anvilBlock, helmet);

        // When - the follow-up use-item interaction resolves as a right-click on air, which is
        // how the server reports the armor-equip attempt when its raytrace misses the anvil
        final PlayerInteractEvent event = fireRightClick(Action.RIGHT_CLICK_AIR, null, helmet);

        // Then - using the held item is denied so the helmet cannot be equipped
        assertThat(event.useItemInHand()).isEqualTo(Event.Result.DENY);
    }

    @Test
    void rightClickOtherBlockShouldNotEquipHeldItemWhileConfirmationIsPending() {
        // Given - the player was prompted to confirm salvaging the helmet
        fireRightClick(Action.RIGHT_CLICK_BLOCK, anvilBlock, helmet);

        // When - the player mis-clicks a nearby non-anvil block while holding the helmet
        final PlayerInteractEvent event =
                fireRightClick(Action.RIGHT_CLICK_BLOCK, dirtBlock, helmet);

        // Then - using the held item is denied so the helmet cannot be equipped
        assertThat(event.useItemInHand()).isEqualTo(Event.Result.DENY);
    }

    @Test
    void rightClickAirShouldNotDenyItemUseWhenNoConfirmationIsPending() {
        // Given - a player holding a salvageable helmet with no salvage confirmation pending
        // When - the player right-clicks air
        final PlayerInteractEvent event = fireRightClick(Action.RIGHT_CLICK_AIR, null, helmet);

        // Then - the interaction is left alone so vanilla equipping still works
        assertThat(event.useItemInHand()).isNotEqualTo(Event.Result.DENY);
    }

    @Test
    void rightClickAirShouldNotDenyItemUseWhenHeldItemDiffersFromPromptedItem() {
        // Given - the player was prompted to confirm salvaging the helmet
        fireRightClick(Action.RIGHT_CLICK_BLOCK, anvilBlock, helmet);

        // When - the player right-clicks air while holding a different piece of armor
        final PlayerInteractEvent event =
                fireRightClick(Action.RIGHT_CLICK_AIR, null, chestplate);

        // Then - the interaction is left alone so the other piece can still be equipped
        assertThat(event.useItemInHand()).isNotEqualTo(Event.Result.DENY);
    }
}
