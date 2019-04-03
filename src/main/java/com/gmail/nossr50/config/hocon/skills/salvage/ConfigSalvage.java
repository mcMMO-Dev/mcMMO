package com.gmail.nossr50.config.hocon.skills.salvage;

import com.gmail.nossr50.config.hocon.skills.salvage.general.ConfigSalvageGeneral;
import com.gmail.nossr50.skills.salvage.salvageables.Salvageable;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;

@ConfigSerializable
public class ConfigSalvage {

    private final static ArrayList<Salvageable> DEFAULT_SALVAGEABLES_LIST;

    static {
        DEFAULT_SALVAGEABLES_LIST = new ArrayList<>();



        /*
        Salvageables:
        #
        # Wooden salvageables
        ###
        # Tools
        WOODEN_SWORD:
            MinimumLevel: 0
            XpMultiplier: .25
            MaximumQuantity: 2
        WOODEN_SHOVEL:
            MinimumLevel: 0
            XpMultiplier: .16
            MaximumQuantity: 1
        WOODEN_PICKAXE:
            MinimumLevel: 0
            XpMultiplier: .5
            MaximumQuantity: 3
        WOODEN_AXE:
            MinimumLevel: 0
            XpMultiplier: .5
            MaximumQuantity: 2
        WOODEN_HOE:
            MinimumLevel: 0
            XpMultiplier: .25
            MaximumQuantity: 2
        #
        # Stone salvageables
        ###
        # Tools
        STONE_SWORD:
            MinimumLevel: 0
            XpMultiplier: .25
            MaximumQuantity: 2
        STONE_SHOVEL:
            MinimumLevel: 0
            XpMultiplier: .16
            MaximumQuantity: 1
        STONE_PICKAXE:
            MinimumLevel: 0
            XpMultiplier: .5
            MaximumQuantity: 3
        STONE_AXE:
            MinimumLevel: 0
            XpMultiplier: .5
            MaximumQuantity: 2
        STONE_HOE:
            MinimumLevel: 0
            XpMultiplier: .25
            MaximumQuantity: 2
        #
        # Iron salvageables
        ###
        # Tools
        IRON_SWORD:
            MinimumLevel: 0
            XpMultiplier: .5
            MaximumQuantity: 2
        IRON_SHOVEL:
            MinimumLevel: 0
            XpMultiplier: .3
            MaximumQuantity: 1
        IRON_PICKAXE:
            MinimumLevel: 0
            XpMultiplier: 1
            MaximumQuantity: 3
        IRON_AXE:
            MinimumLevel: 0
            XpMultiplier: 1
            MaximumQuantity: 2
        IRON_HOE:
            MinimumLevel: 0
            XpMultiplier: .5
            MaximumQuantity: 2
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
            MaximumQuantity: 5
        IRON_CHESTPLATE:
            MinimumLevel: 0
            XpMultiplier: 2
            MaximumQuantity: 8
        IRON_LEGGINGS:
            MinimumLevel: 0
            XpMultiplier: 2
            MaximumQuantity: 7
        IRON_BOOTS:
            MinimumLevel: 0
            XpMultiplier: 2
            MaximumQuantity: 4
        #
        # Gold salvageables
        ###
        # Tools
        GOLDEN_SWORD:
            MinimumLevel: 0
            XpMultiplier: 4
            MaximumQuantity: 2
        GOLDEN_SHOVEL:
            MinimumLevel: 0
            XpMultiplier: 2.6
            MaximumQuantity: 1
        GOLDEN_PICKAXE:
            MinimumLevel: 0
            XpMultiplier: 8
            MaximumQuantity: 3
        GOLDEN_AXE:
            MinimumLevel: 0
            XpMultiplier: 8
            MaximumQuantity: 2
        GOLDEN_HOE:
            MinimumLevel: 0
            XpMultiplier: 4
            MaximumQuantity: 2
        # Armor
        GOLDEN_HELMET:
            MinimumLevel: 0
            XpMultiplier: 4
            MaximumQuantity: 5
        GOLDEN_CHESTPLATE:
            MinimumLevel: 0
            XpMultiplier: 4
            MaximumQuantity: 8
        GOLDEN_LEGGINGS:
            MinimumLevel: 0
            XpMultiplier: 4
            MaximumQuantity: 7
        GOLDEN_BOOTS:
            MinimumLevel: 0
            XpMultiplier: 4
            MaximumQuantity: 4
        #
        # Diamond salvageables
        ###
        # Tools
        DIAMOND_SWORD:
            MinimumLevel: 50
            XpMultiplier: .5
            MaximumQuantity: 2
        DIAMOND_SHOVEL:
            MinimumLevel: 50
            XpMultiplier: .3
            MaximumQuantity: 1
        DIAMOND_PICKAXE:
            MinimumLevel: 50
            XpMultiplier: 1
            MaximumQuantity: 3
        DIAMOND_AXE:
            MinimumLevel: 50
            XpMultiplier: 1
            MaximumQuantity: 2
        DIAMOND_HOE:
            MinimumLevel: 50
            XpMultiplier: .5
            MaximumQuantity: 2
        # Armor
        DIAMOND_HELMET:
            MinimumLevel: 50
            XpMultiplier: 6
            MaximumQuantity: 5
        DIAMOND_CHESTPLATE:
            MinimumLevel: 50
            XpMultiplier: 6
            MaximumQuantity: 8
        DIAMOND_LEGGINGS:
            MinimumLevel: 50
            XpMultiplier: 6
            MaximumQuantity: 7
        DIAMOND_BOOTS:
            MinimumLevel: 50
            XpMultiplier: 6
            MaximumQuantity: 4
        #
        # Leather salvageables
        ###
        # Armor
        LEATHER_HELMET:
            MinimumLevel: 0
            XpMultiplier: 1
            MaximumQuantity: 5
        LEATHER_CHESTPLATE:
            MinimumLevel: 0
            XpMultiplier: 1
            MaximumQuantity: 8
        LEATHER_LEGGINGS:
            MinimumLevel: 0
            XpMultiplier: 1
            MaximumQuantity: 7
        LEATHER_BOOTS:
            MinimumLevel: 0
            XpMultiplier: 1
            MaximumQuantity: 4
        #
        # String salvageables
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

    @Setting(value = "Z-Salvageables", comment = "Salvage rewards and misc parameters")
    ArrayList<Salvageable> configSalvageablesList = DEFAULT_SALVAGEABLES_LIST;


    @Setting(value = "Arcane-Salvage", comment = "Settings related to the Arcane Salvage Sub-Skill")
    ConfigArcaneSalvage configArcaneSalvage = new ConfigArcaneSalvage();

    @Setting(value = "General")
    ConfigSalvageGeneral general = new ConfigSalvageGeneral();

    public ConfigArcaneSalvage getConfigArcaneSalvage() {
        return configArcaneSalvage;
    }

    public ConfigSalvageGeneral getGeneral() {
        return general;
    }

    public ArrayList<Salvageable> getConfigSalvageablesList() {
        return configSalvageablesList;
    }
}