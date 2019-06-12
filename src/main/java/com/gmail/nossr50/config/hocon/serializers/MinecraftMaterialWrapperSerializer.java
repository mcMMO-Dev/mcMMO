package com.gmail.nossr50.config.hocon.serializers;

import com.gmail.nossr50.config.hocon.skills.exampleconfigs.MinecraftMaterialWrapper;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.bukkit.Material;

public class MinecraftMaterialWrapperSerializer implements TypeSerializer<MinecraftMaterialWrapper> {

    private static final String FULLY_QUALIFIED_NAME = "Fully-Qualified-Name";
    private static final String BUKKIT_MATERIAL_NAME = "Bukkit-Material-Name";

    @Override
    public MinecraftMaterialWrapper deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        Material material = Material.matchMaterial(value.getNode(FULLY_QUALIFIED_NAME).getValue(TypeToken.of(String.class)));
        return new MinecraftMaterialWrapper(material);
    }

    @Override
    public void serialize(TypeToken<?> type, MinecraftMaterialWrapper obj, ConfigurationNode value) {
        value.setValue(obj.getName()); //Name
        value.getNode(FULLY_QUALIFIED_NAME).setValue(obj.getFullyQualifiedName());
        value.getNode(BUKKIT_MATERIAL_NAME).setValue(obj.getBukkitMaterialName());
    }
}