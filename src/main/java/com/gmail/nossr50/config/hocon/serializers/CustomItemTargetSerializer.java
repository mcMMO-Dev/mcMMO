package com.gmail.nossr50.config.hocon.serializers;

import com.gmail.nossr50.datatypes.items.ItemMatch;
import com.gmail.nossr50.datatypes.items.ItemMatchProperty;
import com.gmail.nossr50.datatypes.items.MMOItem;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ValueType;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashSet;

public class CustomItemTargetSerializer implements TypeSerializer<ItemMatch> {

    private static final String ITEM_CONSUMED_FOR_REPAIR = "Item-Consumed-For-Repair";
    private static final String NBT_REQUIREMENTS = "NBT-Requirements";

    @Nullable
    @Override
    public ItemMatch deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        MMOItem<?> mmoItem = value.getNode(ITEM_CONSUMED_FOR_REPAIR).getValue(new TypeToken<MMOItem<?>>() {});
        if(value.getNode(NBT_REQUIREMENTS).getValueType() != ValueType.NULL)
        {
            HashSet<ItemMatchProperty> itemMatchProperties = value.getNode(NBT_REQUIREMENTS).getValue(new TypeToken<HashSet<ItemMatchProperty>>() {});
            return new ItemMatch(mmoItem, itemMatchProperties);
        }

        return new ItemMatch(mmoItem, null);
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable ItemMatch obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        value.getNode(ITEM_CONSUMED_FOR_REPAIR).setValue(obj.getItem());

        if(obj.getItemMatchProperties().size() > 0) {
            value.getNode(NBT_REQUIREMENTS).setValue(obj.getItemMatchProperties());
            SerializerUtil.addCommentIfCompatible(value.getNode(NBT_REQUIREMENTS), "List optional NBT that is required here, you write it the same way you do in vanilla commands.");
        }
    }
}
