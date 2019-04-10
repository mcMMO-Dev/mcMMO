package com.gmail.nossr50.config.hocon.skills.exampleconfigs;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.Material;

import java.util.ArrayList;

/**
 * This class is used to generate a table that can be referenced to compare Bukkit Material names
 *      to full qualified names used in internal registers for Minecraft
 */
@ConfigSerializable
public class ConfigNameRegisterDefaults {

    private static final ArrayList<MinecraftMaterialWrapper> BUKKIT_MATERIAL_NAME_LOOKUP_EXAMPLE;

    static {
        BUKKIT_MATERIAL_NAME_LOOKUP_EXAMPLE = new ArrayList<>();
        for(Material m : Material.values())
        {
            BUKKIT_MATERIAL_NAME_LOOKUP_EXAMPLE.add(new MinecraftMaterialWrapper((m)));
        }
    }

    @Setting(value = "Default-Name-Registers", comment = "These are the names mcMMO will recognize for Items " +
            "used internally by Minecraft.")
    private ArrayList<MinecraftMaterialWrapper> defaultMaterialNameRegisters = BUKKIT_MATERIAL_NAME_LOOKUP_EXAMPLE;

    public ArrayList<MinecraftMaterialWrapper> getDefaultMaterialNameRegisters() {
        return defaultMaterialNameRegisters;
    }
}