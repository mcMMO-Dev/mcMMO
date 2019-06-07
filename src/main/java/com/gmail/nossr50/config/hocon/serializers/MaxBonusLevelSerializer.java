package com.gmail.nossr50.config.hocon.serializers;

import com.gmail.nossr50.datatypes.skills.properties.AbstractMaxBonusLevel;
import com.gmail.nossr50.datatypes.skills.properties.MaxBonusLevel;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MaxBonusLevelSerializer implements TypeSerializer<MaxBonusLevel> {

    public static final String STANDARD_NODE = "Standard-Max-Bonus-Level";
    public static final String RETRO_NODE = "Retro-Max-Bonus-Level";

    @Nullable
    @Override
    public MaxBonusLevel deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        int standard = value.getNode(STANDARD_NODE).getValue(TypeToken.of(Integer.class));
        int retro = value.getNode(RETRO_NODE).getValue(TypeToken.of(Integer.class));

        AbstractMaxBonusLevel maxBonusLevel = new AbstractMaxBonusLevel(standard, retro);
        return maxBonusLevel;
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable MaxBonusLevel obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        value.getNode(STANDARD_NODE).setValue(obj.getStandardScaleValue());
        value.getNode(RETRO_NODE).setValue(obj.getRetroScaleValue());
    }

}
