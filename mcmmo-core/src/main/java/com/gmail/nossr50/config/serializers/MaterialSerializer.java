package com.gmail.nossr50.config.serializers;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.bukkit.Material;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MaterialSerializer implements TypeSerializer<Material> {

    private static final String FULLY_QUALIFIED_NAME = "Fully-Qualified-Name";

    @Nullable
    @Override
    public Material deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        Material material = null;

        try {
            material = Material.matchMaterial(value.getNode(FULLY_QUALIFIED_NAME).getValue(TypeToken.of(String.class)));
        } catch (ObjectMappingException | NullPointerException e) {
            e.printStackTrace();
        }

        return material;
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable Material obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        value.getNode(FULLY_QUALIFIED_NAME).setValue(obj.getKey().toString());
    }
}
