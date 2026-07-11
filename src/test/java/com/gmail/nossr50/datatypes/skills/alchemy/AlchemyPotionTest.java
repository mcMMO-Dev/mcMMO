package com.gmail.nossr50.datatypes.skills.alchemy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.config.skills.alchemy.PotionConfig;
import com.gmail.nossr50.mcMMO;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Covers the potion matching rules that decide whether an in-world potion corresponds to a
 * configured Alchemy potion. Matching drives both brew recognition and child potion lookups,
 * so false negatives here silently break brewing for the affected potion.
 */
class AlchemyPotionTest {
    private mcMMO previousPluginInstance;
    private PotionMeta potionMeta;
    private ItemStack potionItemStack;

    @BeforeEach
    void setUp() {
        previousPluginInstance = mcMMO.p;
        potionMeta = mock(PotionMeta.class);
        when(potionMeta.getCustomEffects()).thenReturn(List.of());
        potionItemStack = mockPotionStack(Material.POTION, potionMeta);
    }

    @AfterEach
    void tearDown() {
        mcMMO.p = previousPluginInstance;
    }

    private ItemStack mockPotionStack(Material material, PotionMeta meta) {
        final ItemStack itemStack = mock(ItemStack.class);
        when(itemStack.getType()).thenReturn(material);
        when(itemStack.getItemMeta()).thenReturn(meta);
        return itemStack;
    }

    /**
     * PotionEffectType cannot be touched off-server (its static initializer needs the
     * registry), so effect mocks carry a null type and are told apart by amplifier and
     * duration alone. The comparison under test must therefore be null-safe on the type.
     */
    private PotionMeta mockMetaWithEffects(List<PotionEffect> effects) {
        final PotionMeta meta = mock(PotionMeta.class);
        when(meta.getCustomEffects()).thenReturn(effects);
        if (!effects.isEmpty()) {
            when(meta.hasCustomEffect(null)).thenReturn(true);
        }
        return meta;
    }

    private PotionEffect mockEffect(int amplifier, int duration) {
        final PotionEffect effect = mock(PotionEffect.class);
        when(effect.getAmplifier()).thenReturn(amplifier);
        when(effect.getDuration()).thenReturn(duration);
        return effect;
    }

    @Test
    void constructorShouldRejectPotionsWithoutItemMeta() {
        // Given - an item stack that has no item meta at all
        final ItemStack metalessStack = mock(ItemStack.class);

        // When - an alchemy potion is built from it
        // Then - construction fails fast instead of deferring the NPE to first use
        assertThatThrownBy(() -> new AlchemyPotion("BROKEN", metalessStack, Map.of()))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("potionItemMeta");
    }

    @Nested
    class ToItemStack {
        @Test
        void shouldCloneTheConfiguredStackAtTheRequestedAmount() {
            // Given - a configured potion
            final ItemStack clone = mock(ItemStack.class);
            when(potionItemStack.clone()).thenReturn(clone);
            final AlchemyPotion potion = new AlchemyPotion("TEST", potionItemStack, Map.of());

            // When - a three-potion stack is requested
            final ItemStack result = potion.toItemStack(3);

            // Then - the clone carries the requested amount
            assertThat(result).isSameAs(clone);
            verify(clone).setAmount(3);
        }

        @Test
        void shouldClampNonPositiveAmountsToOne() {
            // Given - a configured potion
            final ItemStack clone = mock(ItemStack.class);
            when(potionItemStack.clone()).thenReturn(clone);
            final AlchemyPotion potion = new AlchemyPotion("TEST", potionItemStack, Map.of());

            // When - a zero-sized stack is requested
            potion.toItemStack(0);

            // Then - the amount is clamped to a single potion
            verify(clone).setAmount(1);
        }
    }

    @Nested
    class ChildLookup {
        private PotionConfig potionConfig;

        @BeforeEach
        void wirePotionConfig() {
            mcMMO.p = mock(mcMMO.class);
            potionConfig = mock(PotionConfig.class);
            when(mcMMO.p.getPotionConfig()).thenReturn(potionConfig);
        }

        @Test
        void shouldResolveTheChildMatchingTheIngredient() {
            // Given - a potion with a child recipe keyed by an ingredient
            final ItemStack recipeIngredient = mock(ItemStack.class);
            final AlchemyPotion child = mock(AlchemyPotion.class);
            when(potionConfig.getPotion("CHILD")).thenReturn(child);
            final AlchemyPotion potion = new AlchemyPotion("PARENT", potionItemStack,
                    Map.of(recipeIngredient, "CHILD"));

            // And - an ingredient similar to the recipe's ingredient
            final ItemStack usedIngredient = mock(ItemStack.class);
            when(usedIngredient.isSimilar(recipeIngredient)).thenReturn(true);

            // When - the child is looked up
            // Then - the configured child potion is returned
            assertThat(potion.getChild(usedIngredient)).isSameAs(child);
        }

        @Test
        void shouldReturnNullForUnknownIngredients() {
            // Given - a potion with a child recipe the used ingredient does not match
            final ItemStack recipeIngredient = mock(ItemStack.class);
            final AlchemyPotion potion = new AlchemyPotion("PARENT", potionItemStack,
                    Map.of(recipeIngredient, "CHILD"));

            // When - the child is looked up with an unrelated ingredient
            // Then - there is no child brew
            assertThat(potion.getChild(mock(ItemStack.class))).isNull();
        }

        @Test
        void shouldReturnNullWhenThePotionHasNoChildren() {
            // Given - a terminal potion with no child recipes
            final AlchemyPotion potion = new AlchemyPotion("TERMINAL", potionItemStack, Map.of());

            // When - any child is looked up
            // Then - there is no child brew and the config is never consulted
            assertThat(potion.getChild(mock(ItemStack.class))).isNull();
            verify(potionConfig, never()).getPotion(anyString());
        }
    }

    @Nested
    class SimilarityMatching {
        @Test
        void differentMaterialShouldNotMatch() {
            // Given - a regular potion and a splash potion
            final AlchemyPotion potion = new AlchemyPotion("TEST", potionItemStack, Map.of());
            final ItemStack other = mockPotionStack(Material.SPLASH_POTION,
                    mock(PotionMeta.class));

            // When - similarity is checked
            // Then - the differing material rules out a match
            assertThat(potion.isSimilarPotion(other)).isFalse();
        }

        @Test
        void missingMetaShouldNotMatch() {
            // Given - an other potion without item meta
            final AlchemyPotion potion = new AlchemyPotion("TEST", potionItemStack, Map.of());
            final ItemStack other = mock(ItemStack.class);
            when(other.getType()).thenReturn(Material.POTION);

            // When - similarity is checked
            // Then - the meta-less item cannot match
            assertThat(potion.isSimilarPotion(other)).isFalse();
        }

        @Test
        void matchingTypeEffectsAndLoreShouldMatch() {
            // Given - two plain potions with the same base type, no custom effects and no lore
            final AlchemyPotion potion = new AlchemyPotion("TEST", potionItemStack, Map.of());
            final PotionMeta otherMeta = mock(PotionMeta.class);
            when(otherMeta.getCustomEffects()).thenReturn(List.of());
            final ItemStack other = mockPotionStack(Material.POTION, otherMeta);

            // When - similarity is checked
            // Then - they match
            assertThat(potion.isSimilarPotion(other)).isTrue();
        }

        @Test
        void differingBasePotionTypeShouldNotMatch() {
            // Given - a water bottle and a mundane potion
            when(potionMeta.getBasePotionType()).thenReturn(PotionType.WATER);
            final AlchemyPotion potion = new AlchemyPotion("WATER", potionItemStack, Map.of());
            final PotionMeta otherMeta = mock(PotionMeta.class);
            when(otherMeta.getCustomEffects()).thenReturn(List.of());
            when(otherMeta.getBasePotionType()).thenReturn(PotionType.MUNDANE);
            final ItemStack other = mockPotionStack(Material.POTION, otherMeta);

            // When - similarity is checked
            // Then - the base potion types rule out a match
            assertThat(potion.isSimilarPotion(other)).isFalse();
        }

        @Test
        void missingCustomEffectShouldNotMatch() {
            // Given - a configured potion with a custom effect the other potion lacks
            final PotionEffect configuredEffect = mockEffect(0, 100);
            final PotionMeta configuredMeta = mockMetaWithEffects(List.of(configuredEffect));
            final ItemStack configuredStack = mockPotionStack(Material.POTION, configuredMeta);
            final AlchemyPotion potion = new AlchemyPotion("TEST", configuredStack, Map.of());

            final PotionMeta otherMeta = mock(PotionMeta.class);
            when(otherMeta.getCustomEffects()).thenReturn(List.of());
            final ItemStack other = mockPotionStack(Material.POTION, otherMeta);

            // When - similarity is checked
            // Then - the missing effect rules out a match
            assertThat(potion.isSimilarPotion(other)).isFalse();
        }

        @Test
        void differingAmplifierShouldNotMatch() {
            // Given - the same custom effect at different amplifiers
            final PotionEffect configuredEffect = mockEffect(0, 100);
            final PotionMeta configuredMeta = mockMetaWithEffects(List.of(configuredEffect));
            final ItemStack configuredStack = mockPotionStack(Material.POTION, configuredMeta);
            final AlchemyPotion potion = new AlchemyPotion("TEST", configuredStack, Map.of());

            final PotionEffect amplifiedEffect = mockEffect(1, 100);
            final PotionMeta otherMeta = mockMetaWithEffects(List.of(amplifiedEffect));
            final ItemStack other = mockPotionStack(Material.POTION, otherMeta);

            // When - similarity is checked
            // Then - the amplifier difference rules out a match
            assertThat(potion.isSimilarPotion(other)).isFalse();
        }

        @Test
        void differingDurationShouldNotMatch() {
            // Given - the same custom effect at different durations
            final PotionEffect configuredEffect = mockEffect(0, 100);
            final PotionMeta configuredMeta = mockMetaWithEffects(List.of(configuredEffect));
            final ItemStack configuredStack = mockPotionStack(Material.POTION, configuredMeta);
            final AlchemyPotion potion = new AlchemyPotion("TEST", configuredStack, Map.of());

            final PotionEffect extendedEffect = mockEffect(0, 200);
            final PotionMeta otherMeta = mockMetaWithEffects(List.of(extendedEffect));
            final ItemStack other = mockPotionStack(Material.POTION, otherMeta);

            // When - similarity is checked
            // Then - the duration difference rules out a match
            assertThat(potion.isSimilarPotion(other)).isFalse();
        }

        /**
         * The order in which custom effects appear on a potion is not part of its identity:
         * Bukkit gives no ordering guarantee, so two potions carrying the same effects in a
         * different order must still be recognized as the same configured potion. A false
         * negative here makes the brew silently fail for that potion.
         */
        @Test
        void customEffectsInDifferentOrderShouldStillMatch() {
            // Given - two potions with identical custom effects listed in opposite order
            final PotionEffect speedEffect = mockEffect(1, 100);
            final PotionEffect strengthEffect = mockEffect(2, 200);
            final PotionEffect speedCopy = mockEffect(1, 100);
            final PotionEffect strengthCopy = mockEffect(2, 200);

            final PotionMeta configuredMeta =
                    mockMetaWithEffects(List.of(speedEffect, strengthEffect));
            final ItemStack configuredStack = mockPotionStack(Material.POTION, configuredMeta);
            final AlchemyPotion potion = new AlchemyPotion("TEST", configuredStack, Map.of());

            final PotionMeta otherMeta =
                    mockMetaWithEffects(List.of(strengthCopy, speedCopy));
            final ItemStack other = mockPotionStack(Material.POTION, otherMeta);

            // When - similarity is checked
            // Then - the order difference does not rule out a match
            assertThat(potion.isSimilarPotion(other)).isTrue();
        }

        @Test
        void loreOnOnlyOneSideShouldNotMatch() {
            // Given - a configured potion without lore and an otherwise identical one with lore
            final AlchemyPotion potion = new AlchemyPotion("TEST", potionItemStack, Map.of());
            final PotionMeta otherMeta = mock(PotionMeta.class);
            when(otherMeta.getCustomEffects()).thenReturn(List.of());
            when(otherMeta.hasLore()).thenReturn(true);
            final ItemStack other = mockPotionStack(Material.POTION, otherMeta);

            // When - similarity is checked
            // Then - the lore difference rules out a match
            assertThat(potion.isSimilarPotion(other)).isFalse();
        }

        @Test
        void matchingLoreShouldMatch() {
            // Given - two potions with the same lore
            when(potionMeta.hasLore()).thenReturn(true);
            when(potionMeta.getLore()).thenReturn(List.of("A test brew"));
            final AlchemyPotion potion = new AlchemyPotion("TEST", potionItemStack, Map.of());

            final PotionMeta otherMeta = mock(PotionMeta.class);
            when(otherMeta.getCustomEffects()).thenReturn(List.of());
            when(otherMeta.hasLore()).thenReturn(true);
            when(otherMeta.getLore()).thenReturn(List.of("A test brew"));
            final ItemStack other = mockPotionStack(Material.POTION, otherMeta);

            // When - similarity is checked
            // Then - they match
            assertThat(potion.isSimilarPotion(other)).isTrue();
        }

        @Test
        void differingLoreShouldNotMatch() {
            // Given - two potions with different lore
            when(potionMeta.hasLore()).thenReturn(true);
            when(potionMeta.getLore()).thenReturn(List.of("A test brew"));
            final AlchemyPotion potion = new AlchemyPotion("TEST", potionItemStack, Map.of());

            final PotionMeta otherMeta = mock(PotionMeta.class);
            when(otherMeta.getCustomEffects()).thenReturn(List.of());
            when(otherMeta.hasLore()).thenReturn(true);
            when(otherMeta.getLore()).thenReturn(List.of("A different brew"));
            final ItemStack other = mockPotionStack(Material.POTION, otherMeta);

            // When - similarity is checked
            // Then - the lore difference rules out a match
            assertThat(potion.isSimilarPotion(other)).isFalse();
        }
    }
}
