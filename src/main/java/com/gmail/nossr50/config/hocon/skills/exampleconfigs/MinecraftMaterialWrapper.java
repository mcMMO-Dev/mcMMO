package com.gmail.nossr50.config.hocon.skills.exampleconfigs;

import org.bukkit.Material;

public class MinecraftMaterialWrapper {
    private String name;
    private String fullyQualifiedName;
    private String bukkitMaterialName;

    public MinecraftMaterialWrapper(Material material)
    {
        this.name = material.getKey().getKey();
        this.fullyQualifiedName = material.getKey().toString();
        this.bukkitMaterialName = material.toString();
    }

    public String getName() {
        return name;
    }

    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public String getBukkitMaterialName() {
        return bukkitMaterialName;
    }
}
