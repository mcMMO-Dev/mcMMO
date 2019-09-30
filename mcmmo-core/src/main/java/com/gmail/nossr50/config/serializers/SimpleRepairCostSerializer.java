package com.gmail.nossr50.config.serializers;

import com.gmail.nossr50.datatypes.items.ItemMatch;
import com.gmail.nossr50.skills.repair.SimpleRepairCost;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class SimpleRepairCostSerializer implements TypeSerializer<SimpleRepairCost> {

    private static final String ITEM_MATCH = "Item-Match";

    @Nullable
    @Override
    public SimpleRepairCost deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        ItemMatch<?> itemMatch = value.getNode(ITEM_MATCH).getValue(new TypeToken<ItemMatch<?>>() {});
        SimpleRepairCost<?> simpleRepairCost = new SimpleRepairCost<>(itemMatch);
        return simpleRepairCost;
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable SimpleRepairCost obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        value.getNode(ITEM_MATCH).setValue(obj.getItemMatch());
    }
}
