package com.gmail.nossr50.config.hocon;

import com.gmail.nossr50.config.hocon.skills.exampleconfigs.MinecraftMaterialWrapper;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.bukkit.Material;

public class MinecraftMaterialWrapperSerializer implements TypeSerializer<MinecraftMaterialWrapper> {

    @Override
    public MinecraftMaterialWrapper deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        Material material = Material.matchMaterial(value.getValue(new TypeToken<String>() {}));

        return new MinecraftMaterialWrapper(material);
    }

    @Override
    public void serialize(TypeToken<?> type, MinecraftMaterialWrapper obj, ConfigurationNode value) {

        value.setValue(obj.getName()); //Name
        value.getNode("Fully-Qualified-Name").setValue(obj.getFullyQualifiedName());
        value.getNode("Bukkit-Material-Name").setValue(obj.getBukkitMaterialName());
    }
}