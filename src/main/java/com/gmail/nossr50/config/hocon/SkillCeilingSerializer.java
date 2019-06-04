package com.gmail.nossr50.config.hocon;

import com.gmail.nossr50.datatypes.skills.properties.AbstractSkillCeiling;
import com.gmail.nossr50.datatypes.skills.properties.SkillCeiling;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class SkillCeilingSerializer implements TypeSerializer<SkillCeiling> {

    private static final String STANDARD_MAX_LEVEL = "Standard-Max-Level";
    private static final String RETRO_MAX_LEVEL = "Retro-Max-Level";

    @Nullable
    @Override
    public SkillCeiling deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        Integer standardCeiling = value.getNode(STANDARD_MAX_LEVEL).getValue(TypeToken.of(Integer.class));
        Integer retroCeiling = value.getNode(RETRO_MAX_LEVEL).getValue(TypeToken.of(Integer.class));
        AbstractSkillCeiling skillCeiling = new AbstractSkillCeiling(standardCeiling, retroCeiling);
        return skillCeiling;
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable SkillCeiling obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        value.getNode(STANDARD_MAX_LEVEL).setValue(obj.getStandardMaxLevel());
        value.getNode(RETRO_MAX_LEVEL).setValue(obj.getRetroMaxLevel());
    }
}
