package com.gmail.nossr50.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.gmail.nossr50.datatypes.skills.ItemType;
import com.gmail.nossr50.datatypes.skills.MaterialType;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.permissions.Permissible;
import org.junit.jupiter.api.Test;

/**
 * Pins the exact permission node strings queried by the parameterized permission checks, so the
 * node construction can move to precomputed lookups without changing any node names.
 */
class PermissionsNodeFormatTest {

    @Test
    void repairAndSalvageChecksShouldQueryTheTypeSuffixedNodes() {
        // Given - a permissible
        final Permissible permissible = mock(Permissible.class);

        // When - repair and salvage permissions are checked for item and material types
        Permissions.repairItemType(permissible, ItemType.ARMOR);
        Permissions.repairMaterialType(permissible, MaterialType.WOOD);
        Permissions.salvageItemType(permissible, ItemType.TOOL);
        Permissions.salvageMaterialType(permissible, MaterialType.DIAMOND);

        // Then - the type-suffixed nodes are queried
        verify(permissible).hasPermission("mcmmo.ability.repair.armorrepair");
        verify(permissible).hasPermission("mcmmo.ability.repair.woodrepair");
        verify(permissible).hasPermission("mcmmo.ability.salvage.toolsalvage");
        verify(permissible).hasPermission("mcmmo.ability.salvage.diamondsalvage");
    }

    @Test
    void vanillaXpBoostShouldQueryThePerSkillNode() {
        // Given - a permissible
        final Permissible permissible = mock(Permissible.class);

        // When - the vanilla XP boost permission is checked for Mining
        Permissions.vanillaXpBoost(permissible, PrimarySkillType.MINING);

        // Then - the per-skill node is queried
        verify(permissible).hasPermission("mcmmo.ability.mining.vanillaxpboost");
    }

    @Test
    void greenThumbBlockShouldQueryTheUnderscoreFreeMaterialNode() {
        // Given - a permissible
        final Permissible permissible = mock(Permissible.class);

        // When - the Green Thumb block permission is checked for a material with underscores
        Permissions.greenThumbBlock(permissible, Material.MOSSY_COBBLESTONE);

        // Then - underscores are stripped from the material name in the node
        verify(permissible).hasPermission(
                "mcmmo.ability.herbalism.greenthumb.blocks.mossycobblestone");
    }

    @Test
    void greenThumbPlantShouldQueryTheUnderscoreFreeMaterialNode() {
        // Given - a permissible
        final Permissible permissible = mock(Permissible.class);

        // When - the Green Thumb plant permission is checked
        Permissions.greenThumbPlant(permissible, Material.SWEET_BERRY_BUSH);

        // Then - underscores are stripped from the material name in the node
        verify(permissible).hasPermission(
                "mcmmo.ability.herbalism.greenthumb.plants.sweetberrybush");
    }

    @Test
    void callOfTheWildShouldQueryThePerEntityNode() {
        // Given - a permissible
        final Permissible permissible = mock(Permissible.class);

        // When - the Call of the Wild permission is checked for wolves
        Permissions.callOfTheWild(permissible, EntityType.WOLF);

        // Then - the per-entity node is queried
        verify(permissible).hasPermission("mcmmo.ability.taming.callofthewild.wolf");
    }
}
