package com.gmail.nossr50.config.hocon.serializers;

import com.gmail.nossr50.datatypes.items.ItemMatch;
import com.gmail.nossr50.skills.repair.RepairCost;
import com.gmail.nossr50.skills.repair.SimpleRepairCost;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class RepairCostSerializer implements TypeSerializer<RepairCost<?>> {

    private static final String TARGET_ITEM = "Target-Item";

    @Nullable
    @Override
    public RepairCost<?> deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        ItemMatch itemMatch = value.getNode(TARGET_ITEM).getValue(TypeToken.of(ItemMatch.class));
        return new SimpleRepairCost(itemMatch);
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable RepairCost<?> obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        value.getNode(TARGET_ITEM).setValue(obj.getRepairCosts());
    }
}
