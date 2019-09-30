package com.gmail.nossr50.config.hocon.skills.salvage;

import com.gmail.nossr50.config.hocon.skills.salvage.general.ConfigSalvageGeneral;
import com.gmail.nossr50.skills.salvage.salvageables.Salvageable;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.Material;

import java.util.ArrayList;

@ConfigSerializable
public class ConfigSalvage {

    private final static ArrayList<Salvageable> DEFAULT_SALVAGEABLES_LIST;

    static {
        DEFAULT_SALVAGEABLES_LIST = new ArrayList<>();

        //Minimum Level
        //Maximum Quantity

        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.WOODEN_SWORD.getKey().toString(), Material.STICK.getKey().toString(), 0, 2));
        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.WOODEN_SHOVEL.getKey().toString(), Material.STICK.getKey().toString(), 0, 1));
        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.WOODEN_PICKAXE.getKey().toString(), Material.STICK.getKey().toString(), 0, 3));
        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.WOODEN_AXE.getKey().toString(), Material.STICK.getKey().toString(), 0, 2));
        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.WOODEN_HOE.getKey().toString(), Material.STICK.getKey().toString(), 0, 2));

        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.STONE_SWORD.getKey().toString(), Material.COBBLESTONE.getKey().toString(), 0, 2));
        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.STONE_SHOVEL.getKey().toString(), Material.COBBLESTONE.getKey().toString(), 0, 1));
        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.STONE_PICKAXE.getKey().toString(), Material.COBBLESTONE.getKey().toString(), 0, 3));
        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.STONE_AXE.getKey().toString(), Material.COBBLESTONE.getKey().toString(), 0, 2));
        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.STONE_HOE.getKey().toString(), Material.COBBLESTONE.getKey().toString(), 0, 2));

        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.IRON_SWORD.getKey().toString(), Material.IRON_INGOT.getKey().toString(), 0, 2));
        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.IRON_SHOVEL.getKey().toString(), Material.IRON_INGOT.getKey().toString(), 0, 1));
        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.IRON_PICKAXE.getKey().toString(), Material.IRON_INGOT.getKey().toString(), 0, 3));
        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.IRON_AXE.getKey().toString(), Material.IRON_INGOT.getKey().toString(), 0, 2));
        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.IRON_HOE.getKey().toString(), Material.IRON_INGOT.getKey().toString(), 0, 2));

        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.GOLDEN_SWORD.getKey().toString(), Material.GOLD_INGOT.getKey().toString(), 0, 2));
        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.GOLDEN_SHOVEL.getKey().toString(), Material.GOLD_INGOT.getKey().toString(), 0, 1));
        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.GOLDEN_PICKAXE.getKey().toString(), Material.GOLD_INGOT.getKey().toString(), 0, 3));
        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.GOLDEN_AXE.getKey().toString(), Material.GOLD_INGOT.getKey().toString(), 0, 2));
        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.GOLDEN_HOE.getKey().toString(), Material.GOLD_INGOT.getKey().toString(), 0, 2));

        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.DIAMOND_SWORD.getKey().toString(), Material.DIAMOND.getKey().toString(), 0, 2));
        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.DIAMOND_SHOVEL.getKey().toString(), Material.DIAMOND.getKey().toString(), 0, 1));
        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.DIAMOND_PICKAXE.getKey().toString(), Material.DIAMOND.getKey().toString(), 0, 3));
        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.DIAMOND_AXE.getKey().toString(), Material.DIAMOND.getKey().toString(), 0, 2));
        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.DIAMOND_HOE.getKey().toString(), Material.DIAMOND.getKey().toString(), 0, 2));

        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.LEATHER_HELMET.getKey().toString(), Material.LEATHER.getKey().toString(), 0, 5));
        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.LEATHER_CHESTPLATE.getKey().toString(), Material.LEATHER.getKey().toString(), 0, 8));
        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.LEATHER_LEGGINGS.getKey().toString(), Material.LEATHER.getKey().toString(), 0, 7));
        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.LEATHER_BOOTS.getKey().toString(), Material.LEATHER.getKey().toString(), 0, 4));

        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.IRON_HELMET.getKey().toString(), Material.IRON_INGOT.getKey().toString(), 0, 5));
        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.IRON_CHESTPLATE.getKey().toString(), Material.IRON_INGOT.getKey().toString(), 0, 8));
        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.IRON_LEGGINGS.getKey().toString(), Material.IRON_INGOT.getKey().toString(), 0, 7));
        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.IRON_BOOTS.getKey().toString(), Material.IRON_INGOT.getKey().toString(), 0, 4));

        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.GOLDEN_HELMET.getKey().toString(), Material.GOLD_INGOT.getKey().toString(), 0, 5));
        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.GOLDEN_CHESTPLATE.getKey().toString(), Material.GOLD_INGOT.getKey().toString(), 0, 8));
        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.GOLDEN_LEGGINGS.getKey().toString(), Material.GOLD_INGOT.getKey().toString(), 0, 7));
        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.GOLDEN_BOOTS.getKey().toString(), Material.GOLD_INGOT.getKey().toString(), 0, 4));

        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.DIAMOND_HELMET.getKey().toString(), Material.GOLD_INGOT.getKey().toString(), 0, 5));
        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.DIAMOND_CHESTPLATE.getKey().toString(), Material.GOLD_INGOT.getKey().toString(), 0, 8));
        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.DIAMOND_LEGGINGS.getKey().toString(), Material.GOLD_INGOT.getKey().toString(), 0, 7));
        DEFAULT_SALVAGEABLES_LIST.add(new Salvageable(Material.DIAMOND_BOOTS.getKey().toString(), Material.GOLD_INGOT.getKey().toString(), 0, 4));
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