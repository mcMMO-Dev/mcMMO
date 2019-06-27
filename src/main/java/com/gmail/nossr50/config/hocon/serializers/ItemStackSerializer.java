package com.gmail.nossr50.config.hocon.serializers;

import com.gmail.nossr50.datatypes.items.BukkitMMOItem;
import com.gmail.nossr50.datatypes.items.MMOItem;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ValueType;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.bukkit.Material;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ItemStackSerializer implements TypeSerializer<MMOItem<?>> {

    private static final String ITEM_MINECRAFT_NAME = "Item-Name";
    private static final String AMOUNT = "Amount";

    @Nullable
    @Override
    public MMOItem<?> deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        String itemIdentifier = value.getNode(ITEM_MINECRAFT_NAME).getValue(TypeToken.of(String.class));

        Material itemMatch = Material.matchMaterial(itemIdentifier);

        if(itemMatch == null) {
            pluginRef.getLogger().info("Could not find a match for "+itemIdentifier);
            return null;
        }

        ConfigurationNode itemNode = value.getNode(ITEM_MINECRAFT_NAME);

        Integer amount;
        //Get the amount of items in the stack
        if(itemNode.getNode(AMOUNT).getValueType() != ValueType.NULL) {
            amount = itemNode.getNode(AMOUNT).getValue(TypeToken.of(Integer.class));
        } else {
            amount = 1;
        }

        return new BukkitMMOItem(itemIdentifier, amount);
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable MMOItem<?> obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        ConfigurationNode itemNode = value.getNode(ITEM_MINECRAFT_NAME);
        value.getNode(ITEM_MINECRAFT_NAME).setValue(obj.getNamespaceKey());
        itemNode.getNode(AMOUNT).setValue(obj.getItemAmount());
    }

}
