package com.gmail.nossr50.config.hocon.skills.mining;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.Material;

import java.util.ArrayList;

@ConfigSerializable
public class ConfigMiningBlastMining {

    public static final ArrayList<String> DETONATORS_DEFAULT;

    static {
        DETONATORS_DEFAULT = new ArrayList<>();
        DETONATORS_DEFAULT.add(Material.FLINT_AND_STEEL.getKey().toString());
        DETONATORS_DEFAULT.add(Material.DIAMOND_PICKAXE.getKey().toString());
        DETONATORS_DEFAULT.add(Material.GOLDEN_PICKAXE.getKey().toString());
        DETONATORS_DEFAULT.add(Material.IRON_PICKAXE.getKey().toString());
        DETONATORS_DEFAULT.add(Material.WOODEN_PICKAXE.getKey().toString());
    }

    @Setting(value = "Detonators", comment = "Items that can be used to activate Blast-Mining")
    private ArrayList<String> detonators = DETONATORS_DEFAULT;

    public ArrayList<String> getDetonators() {
        return detonators;
    }
}