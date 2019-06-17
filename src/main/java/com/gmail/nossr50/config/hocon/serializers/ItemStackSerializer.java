package com.gmail.nossr50.config.hocon.serializers;

import com.gmail.nossr50.mcMMO;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ValueType;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

public class ItemStackSerializer implements TypeSerializer<ItemStack> {

    private static final String ITEM_MINECRAFT_NAME = "Item-Minecraft-Name";
    private static final String AMOUNT = "Amount";
    private static final String ITEM_LORE = "Item-Lore";

    @Nullable
    @Override
    public ItemStack deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        ItemStack itemStack;

        String itemIdentifier = value.getNode(ITEM_MINECRAFT_NAME).getValue(TypeToken.of(String.class));

        Material itemMatch = Material.matchMaterial(itemIdentifier);

        if(itemMatch == null) {
            mcMMO.p.getLogger().info("Could not find a match for "+itemIdentifier);
            return null;
        }

        ConfigurationNode itemNode = value.getNode(ITEM_MINECRAFT_NAME);

        //Get the amount of items in the stack
        if(itemNode.getNode(AMOUNT).getValueType() != ValueType.NULL) {
            Integer amount = itemNode.getNode(AMOUNT).getValue(TypeToken.of(Integer.class));
            itemStack = new ItemStack(itemMatch, amount);
        } else {
            itemStack = new ItemStack(itemMatch, 1);
        }

        //Init default item meta
        itemStack.setItemMeta(Bukkit.getItemFactory().getItemMeta(itemMatch));

        //Set Lore if it exists
        if(itemNode.getNode(ITEM_LORE).getValueType() != ValueType.NULL) {
            List<String> lore = itemNode.getNode(ITEM_LORE).getValue(new TypeToken<List<String>>() {});
            itemStack.getItemMeta().setLore(lore);
        }


        return null;
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable ItemStack obj, @NonNull ConfigurationNode value) throws ObjectMappingException {

    }

}
