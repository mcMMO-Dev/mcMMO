package com.gmail.nossr50.config.hocon.serializers;

import com.gmail.nossr50.skills.repair.RepairTransaction;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class RepairTransactionSerializer implements TypeSerializer<RepairTransaction> {
    @Nullable
    @Override
    public RepairTransaction deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        return null;
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable RepairTransaction obj, @NonNull ConfigurationNode value) throws ObjectMappingException {

    }
}
