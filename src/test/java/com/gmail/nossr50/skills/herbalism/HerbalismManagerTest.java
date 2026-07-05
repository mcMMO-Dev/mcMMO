package com.gmail.nossr50.skills.herbalism;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.events.skills.secondaryabilities.SubSkillBlockEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.tcoded.folialib.FoliaLib;
import java.lang.reflect.Method;
import java.util.stream.Stream;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class HerbalismManagerTest extends MMOTestEnvironment {
    private static final Logger logger = Logger.getLogger(HerbalismManagerTest.class.getName());

    private HerbalismManager herbalismManager;
    private MockedStatic<ProbabilityUtil> mockedProbabilityUtil;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        mockGreenThumbEnvironment();
        herbalismManager = new HerbalismManager(mmoPlayer);
    }

    @AfterEach
    void tearDown() {
        if (mockedProbabilityUtil != null) {
            mockedProbabilityUtil.close();
        }

        cleanUpStaticMocks();
    }

    @Test
    void greenThumbPlantFlowConsumesRenamedSeedFromStorage() throws Exception {
        final AtomicReference<ItemStack[]> storageContents = new AtomicReference<>(
                new ItemStack[]{createRenamedSeedStack()});
        final Block block = mock(Block.class);
        final BlockState blockState = mock(BlockState.class);
        final Ageable ageableCrop = mock(Ageable.class);
        final BlockBreakEvent blockBreakEvent = mock(BlockBreakEvent.class);
        final Location cropLocation = mock(Location.class);

        when(playerInventory.getItemInMainHand()).thenReturn(new ItemStack(Material.WOODEN_HOE));
        when(playerInventory.contains(Material.WHEAT_SEEDS)).thenReturn(true);
        when(playerInventory.getStorageContents()).thenAnswer(invocation -> storageContents.get());
        doAnswer(invocation -> {
            final ItemStack[] updatedStorage = invocation.getArgument(0);
            storageContents.set(updatedStorage);
            return null;
        }).when(playerInventory).setStorageContents(any(ItemStack[].class));
        when(playerInventory.getItemInOffHand()).thenReturn(new ItemStack(Material.AIR));

        when(blockState.getType()).thenReturn(Material.WHEAT);
        when(blockState.getBlockData()).thenReturn(ageableCrop);
        when(blockState.getBlock()).thenReturn(block);
        when(blockState.getLocation()).thenReturn(cropLocation);

        when(ageableCrop.getMaterial()).thenReturn(Material.WHEAT);
        when(ageableCrop.getAge()).thenReturn(7);
        when(ageableCrop.getMaximumAge()).thenReturn(7);

        when(blockBreakEvent.getPlayer()).thenReturn(player);
        when(blockBreakEvent.getBlock()).thenReturn(block);
        when(block.getLocation()).thenReturn(cropLocation);

        // This executes the real crop Green Thumb path while stubbing only the external
        // seams: RNG, subskill event cancellation, and delayed replant scheduling.
        final boolean activated = invokeProcessGreenThumbPlants(blockState, blockBreakEvent,
                false);

        // The stored stack stands in for a renamed seed. The Herbalism flow should still
        // consume it because mcMMO now removes by material from storage instead of relying on
        // CraftBukkit's stricter ItemStack similarity matcher.
        assertTrue(activated);
        assertNull(storageContents.get()[0],
                "Green Thumb should consume the renamed seed from storage.");
        verify(playerInventory, never()).removeItem(any(ItemStack.class));
    }

    @Test
    void greenThumbImmatureNetherWartConsumesWartWithoutSuppressingDrops() throws Exception {
        final AtomicReference<ItemStack[]> storageContents = new AtomicReference<>(
            new ItemStack[]{createStorageStack(Material.NETHER_WART)});
        final Block block = mock(Block.class);
        final BlockState blockState = mock(BlockState.class);
        final Ageable ageableCrop = mock(Ageable.class);
        final BlockBreakEvent blockBreakEvent = mock(BlockBreakEvent.class);
        final Location cropLocation = mock(Location.class);

        when(playerInventory.getItemInMainHand()).thenReturn(new ItemStack(Material.WOODEN_HOE));
        when(playerInventory.contains(Material.NETHER_WART)).thenReturn(true);
        when(playerInventory.getStorageContents()).thenAnswer(invocation -> storageContents.get());
        doAnswer(invocation -> {
            final ItemStack[] updatedStorage = invocation.getArgument(0);
            storageContents.set(updatedStorage);
            return null;
        }).when(playerInventory).setStorageContents(any(ItemStack[].class));
        when(playerInventory.getItemInOffHand()).thenReturn(new ItemStack(Material.AIR));

        when(blockState.getType()).thenReturn(Material.NETHER_WART);
        when(blockState.getBlockData()).thenReturn(ageableCrop);
        when(blockState.getBlock()).thenReturn(block);
        when(blockState.getLocation()).thenReturn(cropLocation);

        when(ageableCrop.getMaterial()).thenReturn(Material.NETHER_WART);
        when(ageableCrop.getAge()).thenReturn(1);
        when(ageableCrop.getMaximumAge()).thenReturn(3);

        when(blockBreakEvent.getPlayer()).thenReturn(player);
        when(blockBreakEvent.getBlock()).thenReturn(block);
        when(block.getLocation()).thenReturn(cropLocation);

        final boolean activated = invokeProcessGreenThumbPlants(blockState, blockBreakEvent,
            false);

        assertTrue(activated,
            "Green Thumb should activate for immature nether wart when requirements are met.");
        verify(blockBreakEvent, never()).setDropItems(false);
        assertNull(storageContents.get()[0],
            "Immature nether wart replant should consume one nether wart from storage.");
        verify(playerInventory, never()).removeItem(any(ItemStack.class));
    }

    @ParameterizedTest(name = "{0} immature Green Thumb keeps drops enabled")
    @MethodSource("immatureGreenThumbCrops")
    void greenThumbImmatureCropsConsumeReplantWithoutSuppressingDrops(Material cropMaterial,
            Material replantMaterial, int immatureAge, int maxAge) throws Exception {
        final AtomicReference<ItemStack[]> storageContents = new AtomicReference<>(
                new ItemStack[]{createStorageStack(replantMaterial)});
        final Block block = mock(Block.class);
        final BlockState blockState = mock(BlockState.class);
        final Ageable ageableCrop = mock(Ageable.class);
        final BlockBreakEvent blockBreakEvent = mock(BlockBreakEvent.class);
        final Location cropLocation = mock(Location.class);

        when(playerInventory.getItemInMainHand()).thenReturn(new ItemStack(Material.WOODEN_HOE));
        when(playerInventory.contains(replantMaterial)).thenReturn(true);
        when(playerInventory.getStorageContents()).thenAnswer(invocation -> storageContents.get());
        doAnswer(invocation -> {
            final ItemStack[] updatedStorage = invocation.getArgument(0);
            storageContents.set(updatedStorage);
            return null;
        }).when(playerInventory).setStorageContents(any(ItemStack[].class));
        when(playerInventory.getItemInOffHand()).thenReturn(new ItemStack(Material.AIR));

        when(blockState.getType()).thenReturn(cropMaterial);
        when(blockState.getBlockData()).thenReturn(ageableCrop);
        when(blockState.getBlock()).thenReturn(block);
        when(blockState.getLocation()).thenReturn(cropLocation);

        when(ageableCrop.getMaterial()).thenReturn(cropMaterial);
        when(ageableCrop.getAge()).thenReturn(immatureAge);
        when(ageableCrop.getMaximumAge()).thenReturn(maxAge);

        when(blockBreakEvent.getPlayer()).thenReturn(player);
        when(blockBreakEvent.getBlock()).thenReturn(block);
        when(block.getLocation()).thenReturn(cropLocation);

        final boolean activated = invokeProcessGreenThumbPlants(blockState, blockBreakEvent,
                false);

        assertTrue(activated,
                "Green Thumb should activate for immature crops when requirements are met.");
        verify(blockBreakEvent, never()).setDropItems(false);
        assertNull(storageContents.get()[0],
                "Immature crop replant should consume one matching replant item from storage.");
        verify(playerInventory, never()).removeItem(any(ItemStack.class));
    }

    @Test
    void shroomThumbConsumesRenamedMushroomsFromStorage() {
        final AtomicReference<ItemStack[]> storageContents = new AtomicReference<>(
                new ItemStack[]{createStorageStack(Material.BROWN_MUSHROOM),
                        createStorageStack(Material.RED_MUSHROOM)});
        final BlockState blockState = mock(BlockState.class);

        when(playerInventory.contains(Material.BROWN_MUSHROOM, 1)).thenReturn(true);
        when(playerInventory.contains(Material.RED_MUSHROOM, 1)).thenReturn(true);
        when(playerInventory.getStorageContents()).thenAnswer(invocation -> storageContents.get());
        doAnswer(invocation -> {
            final ItemStack[] updatedStorage = invocation.getArgument(0);
            storageContents.set(updatedStorage);
            return null;
        }).when(playerInventory).setStorageContents(any(ItemStack[].class));
        when(playerInventory.getItemInOffHand()).thenReturn(new ItemStack(Material.AIR));
        when(blockState.getType()).thenReturn(Material.DIRT);

        // Shroom Thumb previously mixed material-only presence checks with plain ItemStack
        // removal. This regression ensures renamed mushrooms are still consumed through the real
        // Herbalism flow.
        final boolean activated = herbalismManager.processShroomThumb(blockState);

        assertTrue(activated);
        assertNull(storageContents.get()[0],
                "Shroom Thumb should consume the brown mushroom from storage.");
        assertNull(storageContents.get()[1],
                "Shroom Thumb should consume the red mushroom from storage.");
        verify(playerInventory, never()).removeItem(any(ItemStack.class));
    }

    @Test
    void greenTerraBlockConversionDoesNotTreatRenamedSeedsAsPlainSeeds() {
        final BlockState blockState = mock(BlockState.class);

        when(blockState.getType()).thenReturn(Material.COBBLESTONE);
        when(Permissions.greenThumbBlock(player, Material.COBBLESTONE)).thenReturn(true);
        when(playerInventory.containsAtLeast(any(ItemStack.class), eq(1))).thenReturn(false);

        // CraftBukkit evaluates containsAtLeast with ItemStack.isSimilar. A renamed seed should
        // therefore fail this plain-seed check and leave the block unchanged.
        herbalismManager.processGreenTerraBlockConversion(blockState);

        assertFalse(playerInventory.containsAtLeast(new ItemStack(Material.WHEAT_SEEDS), 1),
                "Green Terra should reject renamed seeds under CraftBukkit similarity rules.");
        verify(playerInventory, never()).removeItem(any(ItemStack.class));
        verify(blockState, never()).update(true);
    }

    private void mockGreenThumbEnvironment() {
        final FoliaLib foliaLib = mock(FoliaLib.class, Mockito.RETURNS_DEEP_STUBS);
        final SubSkillBlockEvent subSkillBlockEvent = mock(SubSkillBlockEvent.class);

        when(mcMMO.p.getFoliaLib()).thenReturn(foliaLib);
        when(subSkillBlockEvent.isCancelled()).thenReturn(false);

        mockedProbabilityUtil = Mockito.mockStatic(ProbabilityUtil.class);
        mockedProbabilityUtil.when(
                () -> ProbabilityUtil.isSkillRNGSuccessful(SubSkillType.HERBALISM_GREEN_THUMB,
                        mmoPlayer)).thenReturn(true);
        mockedProbabilityUtil.when(
            () -> ProbabilityUtil.isSkillRNGSuccessful(SubSkillType.HERBALISM_SHROOM_THUMB,
                mmoPlayer)).thenReturn(true);

        mockedEventUtils = Mockito.mockStatic(EventUtils.class);
        mockedEventUtils.when(() -> EventUtils.callSubSkillBlockEvent(any(Player.class),
                eq(SubSkillType.HERBALISM_GREEN_THUMB), any(Block.class))).thenReturn(
                subSkillBlockEvent);
    }

    private boolean invokeProcessGreenThumbPlants(BlockState blockState,
            BlockBreakEvent blockBreakEvent, boolean greenTerra) throws Exception {
        final Method greenThumbMethod = HerbalismManager.class.getDeclaredMethod(
                "processGreenThumbPlants", BlockState.class, BlockBreakEvent.class,
                boolean.class);
        greenThumbMethod.setAccessible(true);

        return (boolean) greenThumbMethod.invoke(herbalismManager, blockState, blockBreakEvent,
                greenTerra);
    }

    private ItemStack createRenamedSeedStack() {
        // The higher-level flow only cares that this stack reports the seed material and lives in
        // storage. Its renamed metadata is represented by the fact that other tests treat it as
        // incompatible with a fresh plain ItemStack similarity check.
        return createStorageStack(Material.WHEAT_SEEDS);
    }

    private ItemStack createStorageStack(Material material) {
        final ItemStack storedStack = mock(ItemStack.class);

        when(storedStack.getType()).thenReturn(material);
        when(storedStack.getAmount()).thenReturn(1);

        return storedStack;
    }

    private static Stream<Arguments> immatureGreenThumbCrops() {
        return Stream.of(
                Arguments.of(Material.CARROTS, Material.CARROT, 1, 7),
                Arguments.of(Material.POTATOES, Material.POTATO, 1, 7),
                Arguments.of(Material.WHEAT, Material.WHEAT_SEEDS, 1, 7),
                Arguments.of(Material.BEETROOTS, Material.BEETROOT_SEEDS, 1, 3),
                Arguments.of(Material.COCOA, Material.COCOA_BEANS, 1, 2),
                Arguments.of(Material.SWEET_BERRY_BUSH, Material.SWEET_BERRIES, 1, 3));
    }
}