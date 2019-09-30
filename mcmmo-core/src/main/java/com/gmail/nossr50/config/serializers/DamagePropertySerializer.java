package com.gmail.nossr50.config.serializers;

import com.gmail.nossr50.datatypes.skills.properties.AbstractDamageProperty;
import com.gmail.nossr50.datatypes.skills.properties.DamageProperty;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class DamagePropertySerializer implements TypeSerializer<DamageProperty> {

    public static final String PVP_NODE = "PVP";
    public static final String PVE_NODE = "PVE";

    @Nullable
    @Override
    public DamageProperty deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        Double pvp = value.getNode(PVP_NODE).getValue(TypeToken.of(Double.class));
        Double pve = value.getNode(PVE_NODE).getValue(TypeToken.of(Double.class));
        DamageProperty damageProperty = new AbstractDamageProperty(pve, pvp);
        return damageProperty;
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable DamageProperty obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        value.getNode(PVP_NODE).setValue(obj.getPVPModifier());
        value.getNode(PVE_NODE).setValue(obj.getPVEModifier());
    }
}
