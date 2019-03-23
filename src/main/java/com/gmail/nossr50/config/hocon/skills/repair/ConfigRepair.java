package com.gmail.nossr50.config.hocon.skills.repair;

import com.gmail.nossr50.config.hocon.skills.repair.general.ConfigRepairGeneral;
import com.gmail.nossr50.config.hocon.skills.repair.repairmastery.ConfigRepairMastery;
import com.gmail.nossr50.config.hocon.skills.repair.subskills.ConfigRepairSubSkills;
import com.gmail.nossr50.skills.repair.repairables.SimpleRepairable;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;

import static org.bukkit.Material.*;

@ConfigSerializable
public class ConfigRepair {

     public static final ArrayList<SimpleRepairable> CONFIG_REPAIRABLES_DEFAULTS;
     public static final Material[] PLANKS = new Material[] { OAK_PLANKS, BIRCH_PLANKS, DARK_OAK_PLANKS, ACACIA_PLANKS, JUNGLE_PLANKS, SPRUCE_PLANKS};

    static {
        CONFIG_REPAIRABLES_DEFAULTS = new ArrayList<>();
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(WOODEN_SWORD, Arrays.asList(PLANKS), 1, 0, .25D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(WOODEN_SHOVEL, Arrays.asList(PLANKS), 1, 0, .15D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(WOODEN_PICKAXE, Arrays.asList(PLANKS), 1, 0, .5D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(WOODEN_AXE, Arrays.asList(PLANKS), 1, 0, .5D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(WOODEN_HOE, Arrays.asList(PLANKS), 1, 0, .25D));

        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(STONE_SWORD, COBBLESTONE, 1, 0, .25D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(STONE_SHOVEL, COBBLESTONE, 1, 0, .15D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(STONE_PICKAXE, COBBLESTONE, 1, 0, .5D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(STONE_AXE, COBBLESTONE, 1, 0, .5D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(STONE_HOE, COBBLESTONE, 1, 0, .25D));

        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(IRON_SWORD, IRON_INGOT, 1, 0, .5D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(IRON_SHOVEL, IRON_INGOT, 1, 0, .3D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(IRON_PICKAXE, IRON_INGOT, 1, 0, 1D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(IRON_AXE, IRON_INGOT, 1, 0, 1D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(IRON_HOE, IRON_INGOT, 1, 0, .5D));

        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(IRON_HELMET, IRON_INGOT, 1, 0, 2D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(IRON_CHESTPLATE, IRON_INGOT, 1, 0, 2D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(IRON_LEGGINGS, IRON_INGOT, 1, 0, 2D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(IRON_BOOTS, IRON_INGOT, 1, 0, 2D));

        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(SHEARS, IRON_INGOT, 1, 0, .5D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(FLINT_AND_STEEL, IRON_INGOT, 1, 0, .3D));

        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(GOLDEN_SWORD, GOLD_INGOT, 1, 0, 4D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(GOLDEN_SHOVEL, GOLD_INGOT, 1, 0, 2.6D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(GOLDEN_PICKAXE, GOLD_INGOT, 1, 0, 8D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(GOLDEN_AXE, GOLD_INGOT, 1, 0, 8D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(GOLDEN_HOE, GOLD_INGOT, 1, 0, 4D));

        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(GOLDEN_HELMET, GOLD_INGOT, 1, 0, 4D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(GOLDEN_CHESTPLATE, GOLD_INGOT, 1, 0, 4D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(GOLDEN_LEGGINGS, GOLD_INGOT, 1, 0, 4D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(GOLDEN_BOOTS, GOLD_INGOT, 1, 0, 4D));

        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(DIAMOND_SWORD, DIAMOND, 1, 0, .5D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(DIAMOND_SHOVEL, DIAMOND, 1, 0, .3D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(DIAMOND_PICKAXE, DIAMOND, 1, 0, 1D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(DIAMOND_AXE, DIAMOND, 1, 0, 1D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(DIAMOND_HOE, DIAMOND, 1, 0, .5D));

        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(DIAMOND_HELMET, DIAMOND, 1, 0, 2D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(DIAMOND_CHESTPLATE, DIAMOND, 1, 0, 2D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(DIAMOND_LEGGINGS, DIAMOND, 1, 0, 2D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(DIAMOND_BOOTS, DIAMOND, 1, 0, 2D));

    }

    @Setting(value = "General")
    private ConfigRepairGeneral repairGeneral = new ConfigRepairGeneral();

    @Setting(value = "SubSkills", comment = "Settings for subskills stemming from Repair")
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
    private ArrayList<SimpleRepairable> configRepairablesList = CONFIG_REPAIRABLES_DEFAULTS;

    public ConfigRepairGeneral getRepairGeneral() {
        return repairGeneral;
    }

    public ConfigRepairSubSkills getRepairSubSkills() {
        return repairSubSkills;
    }

    public ConfigRepairMastery getRepairMastery() {
        return repairSubSkills.getRepairMastery();
    }

    public ConfigRepairSuperRepair getSuperRepair() {
        return repairSubSkills.getSuperRepair();
    }

    public ConfigRepairArcaneForging getArcaneForging() {
        return repairSubSkills.getArcaneForging();
    }

    public ArrayList<SimpleRepairable> getConfigRepairablesList() {
        return configRepairablesList;
    }
}