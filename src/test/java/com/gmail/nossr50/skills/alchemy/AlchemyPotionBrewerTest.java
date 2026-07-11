package com.gmail.nossr50.skills.alchemy;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.config.skills.alchemy.PotionConfig;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.alchemy.AlchemyPotion;
import com.gmail.nossr50.datatypes.skills.alchemy.PotionStage;
import com.gmail.nossr50.events.fake.FakeBrewEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;

/**
 * Covers the static brewing pipeline: ingredient and brew validation against the Concoctions
 * tier, and {@code finishBrewing} - the step that swaps brewed potions into the stand,
 * consumes the ingredient, fires the fake brew event other plugins can cancel, and pays XP.
 */
class AlchemyPotionBrewerTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(AlchemyPotionBrewerTest.class.getName());

    private static final int CONCOCTIONS_TIER = 2;

    private PotionConfig potionConfig;
    private AlchemyManager alchemyManager;
    private ItemStack configIngredient;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(logger);

        potionConfig = mock(PotionConfig.class);
        when(mcMMO.p.getPotionConfig()).thenReturn(potionConfig);

        alchemyManager = mock(AlchemyManager.class);
        when(alchemyManager.getTier()).thenReturn(CONCOCTIONS_TIER);
        doReturn(alchemyManager).when(mmoPlayer).getAlchemyManager();
        when(Permissions.isSubSkillEnabled(mmoPlayer, SubSkillType.ALCHEMY_CONCOCTIONS))
                .thenReturn(true);

        configIngredient = mock(ItemStack.class);
        when(potionConfig.getIngredients(CONCOCTIONS_TIER))
                .thenReturn(List.of(configIngredient));
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    private ItemStack mockItem(Material material, int amount) {
        final ItemStack item = mock(ItemStack.class);
        when(item.getType()).thenReturn(material);
        when(item.getAmount()).thenReturn(amount);
        when(item.clone()).thenReturn(item);
        return item;
    }

    private ItemStack mockValidIngredient(int amount) {
        final ItemStack ingredient = mockItem(Material.NETHER_WART, amount);
        when(ingredient.isSimilar(configIngredient)).thenReturn(true);
        return ingredient;
    }

    @Nested
    class EmptyChecks {
        @Test
        void nullItemShouldBeEmpty() {
            assertThat(AlchemyPotionBrewer.isEmpty(null)).isTrue();
        }

        @Test
        void airShouldBeEmpty() {
            assertThat(AlchemyPotionBrewer.isEmpty(mockItem(Material.AIR, 1))).isTrue();
        }

        @Test
        void zeroAmountStackShouldBeEmpty() {
            assertThat(AlchemyPotionBrewer.isEmpty(mockItem(Material.NETHER_WART, 0))).isTrue();
        }

        @Test
        void regularStackShouldNotBeEmpty() {
            assertThat(AlchemyPotionBrewer.isEmpty(mockItem(Material.NETHER_WART, 1))).isFalse();
        }
    }

    @Nested
    class IngredientValidation {
        @Test
        void configuredIngredientShouldBeValidForItsLevel() {
            // Given - an item similar to a configured tier ingredient
            final ItemStack ingredient = mockValidIngredient(1);

            // When - the ingredient is validated by level
            // Then - it is accepted
            assertThat(AlchemyPotionBrewer.isValidIngredientByLevel(CONCOCTIONS_TIER, ingredient))
                    .isTrue();
        }

        @Test
        void unknownItemShouldNotBeValid() {
            // Given - an item that matches no configured ingredient
            final ItemStack stone = mockItem(Material.STONE, 1);

            // When - the ingredient is validated by level
            // Then - it is rejected
            assertThat(AlchemyPotionBrewer.isValidIngredientByLevel(CONCOCTIONS_TIER, stone))
                    .isFalse();
        }

        @Test
        void emptyItemShouldNotBeValid() {
            // Given - an empty slot
            // When - the ingredient is validated by level
            // Then - it is rejected before any config lookup
            assertThat(AlchemyPotionBrewer.isValidIngredientByLevel(CONCOCTIONS_TIER, null))
                    .isFalse();
        }

        @Test
        void playerValidationShouldUseTheConcoctionsTier() {
            // Given - a player whose Concoctions tier has the ingredient configured
            final ItemStack ingredient = mockValidIngredient(1);

            // When - the ingredient is validated by player
            // Then - the player's tier ingredient list accepts it
            assertThat(AlchemyPotionBrewer.isValidIngredientByPlayer(player, ingredient))
                    .isTrue();
            verify(potionConfig).getIngredients(CONCOCTIONS_TIER);
        }

        @Test
        void playerWithoutConcoctionsShouldFallBackToTierOne() {
            // Given - a player without the Concoctions subskill
            when(Permissions.isSubSkillEnabled(mmoPlayer, SubSkillType.ALCHEMY_CONCOCTIONS))
                    .thenReturn(false);
            final ItemStack ingredient = mockItem(Material.NETHER_WART, 1);
            when(potionConfig.getIngredients(1)).thenReturn(List.of());

            // When - the ingredient is validated by player
            AlchemyPotionBrewer.isValidIngredientByPlayer(player, ingredient);

            // Then - the tier one ingredient list is used
            verify(potionConfig).getIngredients(1);
        }

        @Test
        void unknownPlayerShouldFallBackToTierOne() {
            // Given - a player mcMMO has no data for
            when(UserManager.getPlayer(player)).thenReturn(null);
            final ItemStack ingredient = mockItem(Material.NETHER_WART, 1);
            when(potionConfig.getIngredients(1)).thenReturn(List.of());

            // When - the ingredient is validated by player
            AlchemyPotionBrewer.isValidIngredientByPlayer(player, ingredient);

            // Then - the tier one ingredient list is used
            verify(potionConfig).getIngredients(1);
        }
    }

    @Nested
    class BrewValidation {
        private ItemStack[] contentsWith(ItemStack potionSlot, ItemStack ingredient) {
            return new ItemStack[]{potionSlot, null, null, ingredient};
        }

        @ParameterizedTest
        @EnumSource(names = {"POTION", "SPLASH_POTION", "LINGERING_POTION"})
        void potionWithAMatchingChildShouldBeBrewable(Material potionMaterial) {
            // Given - a stand holding a potion whose recipe accepts the ingredient
            final ItemStack ingredient = mockValidIngredient(1);
            final ItemStack potionStack = mockItem(potionMaterial, 1);
            final AlchemyPotion inputPotion = mock(AlchemyPotion.class);
            when(potionConfig.getPotion(potionStack)).thenReturn(inputPotion);
            when(inputPotion.getChild(ingredient)).thenReturn(mock(AlchemyPotion.class));

            // When - the brew is validated
            // Then - it is brewable
            assertThat(AlchemyPotionBrewer.isValidBrew(CONCOCTIONS_TIER,
                    contentsWith(potionStack, ingredient))).isTrue();
        }

        @Test
        void invalidIngredientShouldNotBeBrewable() {
            // Given - a stand whose ingredient matches no configured ingredient
            final ItemStack stone = mockItem(Material.STONE, 1);
            final ItemStack potionStack = mockItem(Material.POTION, 1);

            // When - the brew is validated
            // Then - it is rejected before the potions are examined
            assertThat(AlchemyPotionBrewer.isValidBrew(CONCOCTIONS_TIER,
                    contentsWith(potionStack, stone))).isFalse();
        }

        @Test
        void potionWithoutAMatchingChildShouldNotBeBrewable() {
            // Given - a valid ingredient that no potion in the stand reacts to
            final ItemStack ingredient = mockValidIngredient(1);
            final ItemStack potionStack = mockItem(Material.POTION, 1);
            final AlchemyPotion inputPotion = mock(AlchemyPotion.class);
            when(potionConfig.getPotion(potionStack)).thenReturn(inputPotion);
            when(inputPotion.getChild(ingredient)).thenReturn(null);

            // When - the brew is validated
            // Then - it is not brewable
            assertThat(AlchemyPotionBrewer.isValidBrew(CONCOCTIONS_TIER,
                    contentsWith(potionStack, ingredient))).isFalse();
        }

        @Test
        void nonPotionContentsShouldBeSkipped() {
            // Given - a valid ingredient but only non-potion items in the potion slots
            final ItemStack ingredient = mockValidIngredient(1);
            final ItemStack stone = mockItem(Material.STONE, 1);

            // When - the brew is validated
            // Then - the non-potion slot cannot make it brewable
            assertThat(AlchemyPotionBrewer.isValidBrew(CONCOCTIONS_TIER,
                    contentsWith(stone, ingredient))).isFalse();
        }

        @Test
        void emptyPotionSlotsShouldNotBeBrewable() {
            // Given - a valid ingredient over three empty potion slots
            final ItemStack ingredient = mockValidIngredient(1);

            // When - the brew is validated
            // Then - there is nothing to brew
            assertThat(AlchemyPotionBrewer.isValidBrew(CONCOCTIONS_TIER,
                    contentsWith(null, ingredient))).isFalse();
        }
    }

    @Nested
    class FinishBrewing {
        private BrewingStand brewingStand;
        private BrewerInventory brewerInventory;

        @BeforeEach
        void setUpStand() {
            brewerInventory = mock(BrewerInventory.class);
            brewingStand = mock(BrewingStand.class);
            when(brewingStand.getInventory()).thenReturn(brewerInventory);
            when(brewingStand.getFuelLevel()).thenReturn(5);
            when(brewingStand.getBlock()).thenReturn(mock(Block.class));
        }

        /**
         * Builds the input potion in slot 0: a configured potion whose recipe turns the
         * ingredient into the given output potion.
         */
        private ItemStack wireBrewablePotion(ItemStack ingredient, AlchemyPotion outputPotion) {
            final ItemStack potionStack = mockItem(Material.POTION, 1);
            final AlchemyPotion inputPotion = mock(AlchemyPotion.class);
            final PotionMeta inputMeta = mock(PotionMeta.class);
            when(inputMeta.getCustomEffects()).thenReturn(List.of());
            when(inputPotion.getAlchemyPotionMeta()).thenReturn(inputMeta);
            when(potionConfig.isValidPotion(potionStack)).thenReturn(true);
            when(potionConfig.getPotion(potionStack)).thenReturn(inputPotion);
            when(inputPotion.getChild(ingredient)).thenReturn(outputPotion);
            when(brewerInventory.getItem(0)).thenReturn(potionStack);
            return potionStack;
        }

        private AlchemyPotion mockOutputPotion(ItemStack resultStack) {
            final AlchemyPotion outputPotion = mock(AlchemyPotion.class);
            final PotionMeta outputMeta = mock(PotionMeta.class);
            when(outputMeta.getCustomEffects()).thenReturn(List.of());
            when(outputPotion.getAlchemyPotionMeta()).thenReturn(outputMeta);
            when(outputPotion.toItemStack(1)).thenReturn(resultStack);
            return outputPotion;
        }

        @Test
        void nonBrewingStandStateShouldDoNothing() {
            // Given - a block state that is not a brewing stand
            final BlockState notAStand = mock(BlockState.class);

            // When - brewing finishes
            AlchemyPotionBrewer.finishBrewing(notAStand, mmoPlayer);

            // Then - no brew event is fired
            verify(pluginManager, never()).callEvent(any());
        }

        @Test
        void missingIngredientShouldDoNothing() {
            // Given - a stand with an empty ingredient slot
            when(brewerInventory.getIngredient()).thenReturn(null);

            // When - brewing finishes
            AlchemyPotionBrewer.finishBrewing(brewingStand, mmoPlayer);

            // Then - no brew event is fired
            verify(pluginManager, never()).callEvent(any());
        }

        @Test
        void missingPlayerShouldDoNothing() {
            // Given - a stand with a valid ingredient but no owning player
            final ItemStack ingredient = mockValidIngredient(2);
            when(brewerInventory.getIngredient()).thenReturn(ingredient);

            // When - brewing finishes without a player
            AlchemyPotionBrewer.finishBrewing(brewingStand, null);

            // Then - no brew event is fired
            verify(pluginManager, never()).callEvent(any());
        }

        @Test
        void successfulBrewShouldSwapPotionsConsumeIngredientAndPayXp() {
            // Given - a stand with a two-item ingredient stack and a brewable potion in slot 0
            final ItemStack ingredient = mockValidIngredient(2);
            when(brewerInventory.getIngredient()).thenReturn(ingredient);
            final ItemStack resultStack = mockItem(Material.POTION, 1);
            final AlchemyPotion outputPotion = mockOutputPotion(resultStack);
            wireBrewablePotion(ingredient, outputPotion);

            // When - brewing finishes
            AlchemyPotionBrewer.finishBrewing(brewingStand, mmoPlayer);

            // Then - the fake brew event is fired for other plugins, carrying the brewed
            // result and the stand's fuel level
            final ArgumentCaptor<FakeBrewEvent> eventCaptor =
                    ArgumentCaptor.forClass(FakeBrewEvent.class);
            verify(pluginManager).callEvent(eventCaptor.capture());
            assertThat(eventCaptor.getValue().getResults()).containsExactly(resultStack, null,
                    null);
            assertThat(eventCaptor.getValue().getFuelLevel()).isEqualTo(5);

            // And - the brewed potion replaces the input potion
            verify(brewerInventory).setItem(0, resultStack);

            // And - one ingredient is consumed from the stack
            verify(ingredient).setAmount(1);
            verify(brewerInventory).setIngredient(ingredient);

            // And - the brew pays XP for the potion stage of the upgrade
            verify(alchemyManager).handlePotionBrewSuccesses(PotionStage.FIVE, 1);
        }

        @Test
        void lastIngredientShouldClearTheIngredientSlot() {
            // Given - a stand holding a single-item ingredient stack
            final ItemStack ingredient = mockValidIngredient(1);
            when(brewerInventory.getIngredient()).thenReturn(ingredient);
            final AlchemyPotion outputPotion =
                    mockOutputPotion(mockItem(Material.POTION, 1));
            wireBrewablePotion(ingredient, outputPotion);

            // When - brewing finishes
            AlchemyPotionBrewer.finishBrewing(brewingStand, mmoPlayer);

            // Then - the ingredient slot is emptied
            verify(brewerInventory).setIngredient(null);
        }

        @Test
        void cancelledBrewEventShouldSuppressTheBrew() {
            // Given - a brewable stand and a plugin that cancels the fake brew event
            final ItemStack ingredient = mockValidIngredient(2);
            when(brewerInventory.getIngredient()).thenReturn(ingredient);
            final ItemStack resultStack = mockItem(Material.POTION, 1);
            final AlchemyPotion outputPotion = mockOutputPotion(resultStack);
            wireBrewablePotion(ingredient, outputPotion);
            doAnswer(invocation -> {
                ((Cancellable) invocation.getArgument(0)).setCancelled(true);
                return null;
            }).when(pluginManager).callEvent(any(FakeBrewEvent.class));

            // When - brewing finishes
            AlchemyPotionBrewer.finishBrewing(brewingStand, mmoPlayer);

            // Then - no potion is swapped in, no ingredient is consumed, and no XP is paid
            verify(brewerInventory, never()).setItem(anyInt(), any());
            verify(brewerInventory, never()).setIngredient(any());
            verify(alchemyManager, never()).handlePotionBrewSuccesses(any(), anyInt());
        }

        @Test
        void invalidPotionSlotsShouldBeSkipped() {
            // Given - a valid ingredient, a glass bottle in slot 1 and an unknown potion in
            // slot 2
            final ItemStack ingredient = mockValidIngredient(2);
            when(brewerInventory.getIngredient()).thenReturn(ingredient);
            final AlchemyPotion outputPotion =
                    mockOutputPotion(mockItem(Material.POTION, 1));
            wireBrewablePotion(ingredient, outputPotion);

            final ItemStack glassBottle = mockItem(Material.GLASS_BOTTLE, 1);
            when(brewerInventory.getItem(1)).thenReturn(glassBottle);
            final ItemStack unknownPotion = mockItem(Material.POTION, 1);
            when(potionConfig.isValidPotion(unknownPotion)).thenReturn(false);
            when(brewerInventory.getItem(2)).thenReturn(unknownPotion);

            // When - brewing finishes
            AlchemyPotionBrewer.finishBrewing(brewingStand, mmoPlayer);

            // Then - only the brewable slot is swapped and only one success pays XP
            verify(brewerInventory, never()).setItem(eq(1), any());
            verify(brewerInventory, never()).setItem(eq(2), any());
            verify(alchemyManager).handlePotionBrewSuccesses(PotionStage.FIVE, 1);
        }
    }
}
