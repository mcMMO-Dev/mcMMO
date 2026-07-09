package com.gmail.nossr50.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

class MaterialMapStoreTest {

    /**
     * Bedrock Edition style block and item identifiers registered on purpose so mcMMO keeps
     * working on Bukkit-API servers backed by Bedrock naming (some users run such hybrids).
     * These never resolve against the Java Material registry and must stay.
     */
    private static final Set<String> BEDROCK_EDITION_ALIASES = Set.of(
            "block_of_amethyst",
            "block_of_coal",
            "block_of_diamond",
            "block_of_emerald",
            "block_of_gold",
            "block_of_iron",
            "block_of_netherite",
            "block_of_quartz",
            "block_of_redstone",
            "colored_terracotta",
            "compound_creator",
            "concrete",
            "element_constructor",
            "enchantment_table",
            "glazed_terracotta",
            "glowing_obsidian",
            "heat_block",
            "lapis_lazuli_block",
            "lapis_lazuli_ore",
            "material_reducer",
            "quartz_ore",
            "raw_beef",
            "raw_chicken",
            "raw_cod",
            "raw_mutton",
            "raw_porkchop",
            "raw_rabbit",
            "raw_salmon",
            "red_netherbrick_slab",
            "red_netherbrick_stairs",
            "turtle_shell",
            "weighted_pressure_plates");

    /**
     * Java identifiers added ahead of the Minecraft version this build compiles against; they
     * resolve at runtime on newer servers but not against the test classpath's Material enum.
     */
    private static final Set<String> NEWER_THAN_TEST_API = Set.of(
            "acacia_shelf",
            "bamboo_shelf",
            "birch_shelf",
            "cherry_shelf",
            "crimson_shelf",
            "dark_oak_shelf",
            "jungle_shelf",
            "mangrove_shelf",
            "oak_shelf",
            "pale_oak_shelf",
            "spruce_shelf",
            "warped_shelf",
            "cactus_flower",
            "pale_hanging_moss",
            "pale_oak_button",
            "pale_oak_door",
            "pale_oak_fence",
            "pale_oak_fence_gate",
            "pale_oak_hanging_sign",
            "pale_oak_leaves",
            "pale_oak_log",
            "pale_oak_pressure_plate",
            "pale_oak_sign",
            "pale_oak_trapdoor",
            "pale_oak_wood",
            "stripped_pale_oak_log",
            "stripped_pale_oak_wood",
            "copper_chest",
            "exposed_copper_chest",
            "weathered_copper_chest",
            "oxidized_copper_chest",
            "waxed_copper_chest",
            "copper_golem_statue",
            "exposed_copper_golem_statue",
            "weathered_copper_golem_statue",
            "oxidized_copper_golem_statue",
            "waxed_copper_golem_statue",
            "mannequin",
            "copper_axe",
            "copper_boots",
            "copper_chestplate",
            "copper_helmet",
            "copper_hoe",
            "copper_leggings",
            "copper_pickaxe",
            "copper_shovel",
            "copper_sword",
            "copper_spear",
            "diamond_spear",
            "golden_spear",
            "iron_spear",
            "netherite_spear",
            "stone_spear",
            "wooden_spear",
            "cinnabar",
            "cinnabar_slab",
            "cinnabar_stairs",
            "cinnabar_wall",
            "cinnabar_bricks",
            "cinnabar_brick_slab",
            "cinnabar_brick_stairs",
            "cinnabar_brick_wall",
            "chiseled_cinnabar",
            "polished_cinnabar",
            "polished_cinnabar_slab",
            "polished_cinnabar_stairs",
            "polished_cinnabar_wall",
            "sulfur",
            "sulfur_slab",
            "sulfur_stairs",
            "sulfur_wall",
            "sulfur_bricks",
            "sulfur_brick_slab",
            "sulfur_brick_stairs",
            "sulfur_brick_wall",
            "sulfur_spike",
            "chiseled_sulfur",
            "polished_sulfur",
            "polished_sulfur_slab",
            "polished_sulfur_stairs",
            "polished_sulfur_wall",
            "potent_sulfur");

    /**
     * Every name registered in a MaterialMapStore register must either resolve to a real
     * Material or be documented above; anything else is a dead entry that can never match.
     */
    @Test
    void everyRegisteredNameShouldResolveOrBeDocumented() throws IllegalAccessException {
        // Given - a fully populated material map store
        final MaterialMapStore store = new MaterialMapStore();

        // When - every name in every register is resolved against the Material registry
        final Set<String> unresolved = new TreeSet<>();
        for (final Field field : MaterialMapStore.class.getDeclaredFields()) {
            field.setAccessible(true);
            final Object value = field.get(store);

            if (value instanceof Set<?> register) {
                for (final Object entry : register) {
                    collectUnresolved((String) entry, unresolved);
                }
            } else if (value instanceof Map<?, ?> register) {
                for (final Object entry : register.keySet()) {
                    collectUnresolved((String) entry, unresolved);
                }
            }
        }

        // Then - every name resolves, or is a documented Bedrock alias or future-version name
        assertThat(unresolved).isEmpty();
    }

    private void collectUnresolved(String name, Set<String> unresolved) {
        if (Material.matchMaterial(name) != null
                || BEDROCK_EDITION_ALIASES.contains(name)
                || NEWER_THAN_TEST_API.contains(name)) {
            return;
        }

        unresolved.add(name);
    }
}
