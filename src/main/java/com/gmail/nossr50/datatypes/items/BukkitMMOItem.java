package com.gmail.nossr50.datatypes.items;

import com.gmail.nossr50.util.nbt.RawNBT;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BukkitMMOItem implements MMOItem<ItemStack> {

    private ItemStack itemImplementation;
    private RawNBT rawNBT;

    public BukkitMMOItem(String namespaceKey, int amount, RawNBT rawNBT) throws NullPointerException {
        ItemStack itemStack;
        Material material = Material.matchMaterial(namespaceKey);

        if(material == null) {
            throw new NullPointerException("Material for user defined item could not be found in the server software.");
        }

        itemStack = new ItemStack(material);

        //Get default item meta
        ItemMeta itemMeta = Bukkit.getItemFactory().getItemMeta(itemStack.getType());

        //Set default item meta
        itemStack.setItemMeta(itemMeta);

        //Set amount
        itemStack.setAmount(amount);

        //Set item implementation
        this.itemImplementation = itemStack;

        //Set raw NBT
        if(rawNBT != null)
            this.rawNBT = rawNBT;
    }

    @Override
    public ItemStack getItemImplementation() {
        return itemImplementation;
    }

    @Override
    public String getNamespaceKey() {
        return itemImplementation.getType().getKey().toString();
    }

    @Override
    public int getItemAmount() {
        return itemImplementation.getAmount();
    }

    @Override
    public RawNBT getRawNBT() {
        return rawNBT;
    }

}
