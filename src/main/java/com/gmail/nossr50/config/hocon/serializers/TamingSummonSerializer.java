package com.gmail.nossr50.config.hocon.serializers;

import com.gmail.nossr50.datatypes.skills.subskills.taming.TamingSummon;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.bukkit.Material;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class TamingSummonSerializer implements TypeSerializer<TamingSummon> {
    @Nullable
    @Override
    public TamingSummon deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        /*
            private Material itemType;
            private int itemAmountRequired;
            private int entitiesSummoned;
            private int summonLifespan;
            private int summonCap;
            private CallOfTheWildType callOfTheWildType;
            private EntityType entityType;
         */

        Material itemType = value.getNode("Item-Material").getValue(TypeToken.of(Material.class));
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable TamingSummon obj, @NonNull ConfigurationNode value) throws ObjectMappingException {

    }
}
