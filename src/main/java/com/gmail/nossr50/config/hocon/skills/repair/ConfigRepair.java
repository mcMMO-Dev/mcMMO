package com.gmail.nossr50.config.hocon.skills.repair;

import com.gmail.nossr50.config.hocon.skills.repair.general.ConfigRepairGeneral;
import com.gmail.nossr50.config.hocon.skills.repair.repairmastery.ConfigRepairMastery;
import com.gmail.nossr50.skills.repair.repairables.SimpleRepairable;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;

import static org.bukkit.Material.*;

@ConfigSerializable
public class ConfigRepair {

     public static final ArrayList<SimpleRepairable> CONFIG_REPAIRABLES_DEFAULTS;

    static {
        CONFIG_REPAIRABLES_DEFAULTS = new ArrayList<>();
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(WOODEN_SWORD, OAK_PLANKS, 1, 0, .25D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(WOODEN_SHOVEL, OAK_PLANKS, 1, 0, .15D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(WOODEN_PICKAXE, OAK_PLANKS, 1, 0, .5D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(WOODEN_AXE, OAK_PLANKS, 1, 0, .5D));
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(WOODEN_HOE, OAK_PLANKS, 1, 0, .25D));

        /*
        Repairables:
            #
            # Wooden repairables
            ###
            # Tools
            WOODEN_SWORD:
                MinimumLevel: 0
                XpMultiplier: .25
            WOODEN_SHOVEL:
                MinimumLevel: 0
                XpMultiplier: .16
            WOODEN_PICKAXE:
                MinimumLevel: 0
                XpMultiplier: .5
            WOODEN_AXE:
                MinimumLevel: 0
                XpMultiplier: .5
            WOODEN_HOE:
                MinimumLevel: 0
                XpMultiplier: .25
            #
            # Stone repairables
            ###
            # Tools
            STONE_SWORD:
                MinimumLevel: 0
                XpMultiplier: .25
            STONE_SHOVEL:
                MinimumLevel: 0
                XpMultiplier: .16
            STONE_PICKAXE:
                MinimumLevel: 0
                XpMultiplier: .5
            STONE_AXE:
                MinimumLevel: 0
                XpMultiplier: .5
            STONE_HOE:
                MinimumLevel: 0
                XpMultiplier: .25
            #
            # Iron repairables
            ###
            # Tools
            IRON_SWORD:
                MinimumLevel: 0
                XpMultiplier: .5
            IRON_SHOVEL:
                MinimumLevel: 0
                XpMultiplier: .3
            IRON_PICKAXE:
                MinimumLevel: 0
                XpMultiplier: 1
            IRON_AXE:
                MinimumLevel: 0
                XpMultiplier: 1
            IRON_HOE:
                MinimumLevel: 0
                XpMultiplier: .5
            SHEARS:
                MinimumLevel: 0
                XpMultiplier: .5
            FLINT_AND_STEEL:
                MinimumLevel: 0
                XpMultiplier: .3
            # Armor
            IRON_HELMET:
                MinimumLevel: 0
                XpMultiplier: 2
            IRON_CHESTPLATE:
                MinimumLevel: 0
                XpMultiplier: 2
            IRON_LEGGINGS:
                MinimumLevel: 0
                XpMultiplier: 2
            IRON_BOOTS:
                MinimumLevel: 0
                XpMultiplier: 2
            #
            # Gold repairables
            ###
            # Tools
            GOLDEN_SWORD:
                MinimumLevel: 0
                XpMultiplier: 4
            GOLDEN_SHOVEL:
                MinimumLevel: 0
                XpMultiplier: 2.6
            GOLDEN_PICKAXE:
                MinimumLevel: 0
                XpMultiplier: 8
            GOLDEN_AXE:
                MinimumLevel: 0
                XpMultiplier: 8
            GOLDEN_HOE:
                MinimumLevel: 0
                XpMultiplier: 4
            # Armor
            GOLDEN_HELMET:
                MinimumLevel: 0
                XpMultiplier: 4
            GOLDEN_CHESTPLATE:
                MinimumLevel: 0
                XpMultiplier: 4
            GOLDEN_LEGGINGS:
                MinimumLevel: 0
                XpMultiplier: 4
            GOLDEN_BOOTS:
                MinimumLevel: 0
                XpMultiplier: 4
            #
            # Diamond repairables
            ###
            # Tools
            DIAMOND_SWORD:
                MinimumLevel: 50
                XpMultiplier: .5
            DIAMOND_SHOVEL:
                MinimumLevel: 50
                XpMultiplier: .3
            DIAMOND_PICKAXE:
                MinimumLevel: 50
                XpMultiplier: 1
            DIAMOND_AXE:
                MinimumLevel: 50
                XpMultiplier: 1
            DIAMOND_HOE:
                MinimumLevel: 50
                XpMultiplier: .5
            # Armor
            DIAMOND_HELMET:
                MinimumLevel: 50
                XpMultiplier: 6
            DIAMOND_CHESTPLATE:
                MinimumLevel: 50
                XpMultiplier: 6
            DIAMOND_LEGGINGS:
                MinimumLevel: 50
                XpMultiplier: 6
            DIAMOND_BOOTS:
                MinimumLevel: 50
                XpMultiplier: 6
            #
            # Leather repairables
            ###
            # Armor
            LEATHER_HELMET:
                MinimumLevel: 0
                XpMultiplier: 1
            LEATHER_CHESTPLATE:
                MinimumLevel: 0
                XpMultiplier: 1
            LEATHER_LEGGINGS:
                MinimumLevel: 0
                XpMultiplier: 1
            LEATHER_BOOTS:
                MinimumLevel: 0
                XpMultiplier: 1
            #
            # String repairables
            ###
            # Tools
            FISHING_ROD:
                MinimumLevel: 0
                XpMultiplier: .5
            BOW:
                MinimumLevel: 0
                XpMultiplier: .5
            CARROT_ON_A_STICK:
                MinimumLevel: 0
                XpMultiplier: .5
         */
    }



    @Setting(value = "Repair-Mastery", comment = "Settings related to the repair mastery subskill")
    private ConfigRepairMastery repairMastery = new ConfigRepairMastery();

    @Setting(value = "Super-Repair", comment = "Settings related to the super repair subskill")
    private ConfigRepairSuperRepair superRepair = new ConfigRepairSuperRepair();

    @Setting(value = "Arcane-Forging", comment = "Settings related to the arcane forging subskill")
    private ConfigRepairArcaneForging arcaneForging = new ConfigRepairArcaneForging();

    @Setting(value = "General")
    private ConfigRepairGeneral repairGeneral = new ConfigRepairGeneral();

    @Setting(value = "Repairables")
    private ArrayList<SimpleRepairable> configRepairablesList = CONFIG_REPAIRABLES_DEFAULTS;

    public ConfigRepairGeneral getRepairGeneral() {
        return repairGeneral;
    }

    public ConfigRepairMastery getRepairMastery() {
        return repairMastery;
    }

    public ConfigRepairSuperRepair getSuperRepair() {
        return superRepair;
    }

    public ConfigRepairArcaneForging getArcaneForging() {
        return arcaneForging;
    }

    public ArrayList<SimpleRepairable> getConfigRepairablesList() {
        return configRepairablesList;
    }
}