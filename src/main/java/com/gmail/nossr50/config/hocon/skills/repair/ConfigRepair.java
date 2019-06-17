package com.gmail.nossr50.config.hocon.skills.repair;

import com.gmail.nossr50.config.ConfigConstants;
import com.gmail.nossr50.config.hocon.skills.repair.general.ConfigRepairGeneral;
import com.gmail.nossr50.config.hocon.skills.repair.repairmastery.ConfigRepairRepairMastery;
import com.gmail.nossr50.config.hocon.skills.repair.subskills.ConfigRepairSubSkills;
import com.gmail.nossr50.skills.repair.repairables.Repairable;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static org.bukkit.Material.*;

@ConfigSerializable
public class ConfigRepair {

    public static final ArrayList<Repairable> CONFIG_REPAIRABLES_DEFAULTS;
    public static final HashSet<RepairWildcard> REPAIR_WILDCARDS_DEFAULTS;
//    public static final Material[] PLANKS = new Material[]{OAK_PLANKS, BIRCH_PLANKS, DARK_OAK_PLANKS, ACACIA_PLANKS, JUNGLE_PLANKS, SPRUCE_PLANKS};

    static {
        REPAIR_WILDCARDS_DEFAULTS = new HashSet<>();

        List<ItemStack> planksList = Arrays.asList(new ItemStack[]{new ItemStack(OAK_PLANKS, 1),
                new ItemStack(BIRCH_PLANKS, 1), new ItemStack(DARK_OAK_PLANKS, 1),
                new ItemStack(ACACIA_PLANKS, 1), new ItemStack(JUNGLE_PLANKS, 1),
                new ItemStack(SPRUCE_PLANKS, 1)});
        RepairWildcard planksWildCard = new RepairWildcard("Planks", new HashSet<>(planksList));
        REPAIR_WILDCARDS_DEFAULTS.add(planksWildCard);

        CONFIG_REPAIRABLES_DEFAULTS = new ArrayList<>();
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(WOODEN_SWORD, planksWildCard, 1, 0, .25D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(WOODEN_SHOVEL, planksWildCard, 1, 0, .15D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(WOODEN_PICKAXE, Arrays.asList(PLANKS), 1, 0, .5D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(WOODEN_AXE, Arrays.asList(PLANKS), 1, 0, .5D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(WOODEN_HOE, Arrays.asList(PLANKS), 1, 0, .25D));

        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(STONE_SWORD, COBBLESTONE, 1, 0, .25D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(STONE_SHOVEL, COBBLESTONE, 1, 0, .15D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(STONE_PICKAXE, COBBLESTONE, 1, 0, .5D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(STONE_AXE, COBBLESTONE, 1, 0, .5D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(STONE_HOE, COBBLESTONE, 1, 0, .25D));

        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(IRON_SWORD, IRON_INGOT, 1, 0, .5D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(IRON_SHOVEL, IRON_INGOT, 1, 0, .3D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(IRON_PICKAXE, IRON_INGOT, 1, 0, 1D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(IRON_AXE, IRON_INGOT, 1, 0, 1D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(IRON_HOE, IRON_INGOT, 1, 0, .5D));

        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(IRON_HELMET, IRON_INGOT, 1, 0, 2D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(IRON_CHESTPLATE, IRON_INGOT, 1, 0, 2D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(IRON_LEGGINGS, IRON_INGOT, 1, 0, 2D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(IRON_BOOTS, IRON_INGOT, 1, 0, 2D));

        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(SHEARS, IRON_INGOT, 1, 0, .5D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(FLINT_AND_STEEL, IRON_INGOT, 1, 0, .3D));

        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(GOLDEN_SWORD, GOLD_INGOT, 1, 0, 4D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(GOLDEN_SHOVEL, GOLD_INGOT, 1, 0, 2.6D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(GOLDEN_PICKAXE, GOLD_INGOT, 1, 0, 8D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(GOLDEN_AXE, GOLD_INGOT, 1, 0, 8D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(GOLDEN_HOE, GOLD_INGOT, 1, 0, 4D));

        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(GOLDEN_HELMET, GOLD_INGOT, 1, 0, 4D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(GOLDEN_CHESTPLATE, GOLD_INGOT, 1, 0, 4D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(GOLDEN_LEGGINGS, GOLD_INGOT, 1, 0, 4D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(GOLDEN_BOOTS, GOLD_INGOT, 1, 0, 4D));

        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(DIAMOND_SWORD, DIAMOND, 1, 0, .5D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(DIAMOND_SHOVEL, DIAMOND, 1, 0, .3D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(DIAMOND_PICKAXE, DIAMOND, 1, 0, 1D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(DIAMOND_AXE, DIAMOND, 1, 0, 1D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(DIAMOND_HOE, DIAMOND, 1, 0, .5D));

        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(DIAMOND_HELMET, DIAMOND, 1, 0, 2D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(DIAMOND_CHESTPLATE, DIAMOND, 1, 0, 2D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(DIAMOND_LEGGINGS, DIAMOND, 1, 0, 2D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(DIAMOND_BOOTS, DIAMOND, 1, 0, 2D));


    }

    @Setting(value = "General")
    private ConfigRepairGeneral repairGeneral = new ConfigRepairGeneral();

    @Setting(value = ConfigConstants.SUB_SKILL_NODE, comment = "Settings for subskills stemming from Repair")
    private ConfigRepairSubSkills repairSubSkills = new ConfigRepairSubSkills();

    @Setting(value = "Z-Repairables", comment = "This is the list of what can be repaired in mcMMO by Anvils and their properties." +
            "\nThe \"Z\" in this config keys name is literally just to place this at the bottom of the config since the serializer uses alphabetical sorting." +
            "\n\n -- Explanation for Parameters --" +
            "\nItem: The name of the item, this has to be equivalent to the internal registry key (Name ID) Minecraft uses for this item" +
            "\nItems-Used-To-Repair: The name of the item consumed as part of repairing, this has to be equivalent to the internal registry key (Name ID) Minecraft uses for this item" +
            "\nMinimum-Quantity-Used-To-Repair: The amount of this item that is required to repair this item at a minimum." +
            "\nOverride-Level-Requirement: If you would like to specify a specific skill level required to repair an item, do it here. It should be noted that a lot of items will be given automatic level requirements if you leave this at zero." +
            "\nXP-Multiplier: When calculating how much XP to give the player for the repair, the end result will be multiplied by this value." +
            "\n\nName ID List: https://minecraft.gamepedia.com/Java_Edition_data_values" +
            "\nTIP: You can omit \"minecraft:\" from the Name ID if you want to, for example you can write \"red_wool\" instead of \"minecraft:red_wool\"")
    private ArrayList<Repairable> configRepairablesList = CONFIG_REPAIRABLES_DEFAULTS;

    @Setting(value = "Z-Repairables-Wildcards", comment = "Used to define an alias that can be matched to several materials.")
    private HashSet<RepairWildcard> repairWildcards = new HashSet<>();

    public ConfigRepairGeneral getRepairGeneral() {
        return repairGeneral;
    }

    public ConfigRepairSubSkills getRepairSubSkills() {
        return repairSubSkills;
    }

    public ConfigRepairRepairMastery getRepairMastery() {
        return repairSubSkills.getRepairMastery();
    }

    public ConfigRepairSuperRepair getSuperRepair() {
        return repairSubSkills.getSuperRepair();
    }

    public ConfigRepairArcaneForging getArcaneForging() {
        return repairSubSkills.getArcaneForging();
    }

    public ArrayList<Repairable> getConfigRepairablesList() {
        return configRepairablesList;
    }

    public HashSet<RepairWildcard> getRepairWildcards() {
        return repairWildcards;
    }
}