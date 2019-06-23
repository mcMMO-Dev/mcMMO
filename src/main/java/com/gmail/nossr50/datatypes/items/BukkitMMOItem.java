package com.gmail.nossr50.datatypes.items;

import com.gmail.nossr50.util.nbt.RawNBT;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BukkitMMOItem<T extends ItemStack> implements MMOItem<T> {

    private T itemImplementation;
    private RawNBT rawNBT;

    //Suppressed because the type is always an ItemStack
    @SuppressWarnings("unchecked")
    public BukkitMMOItem(String namespaceKey, int amount, RawNBT rawNBT) throws NullPointerException {
        T itemStack;
        Material material = Material.matchMaterial(namespaceKey);

        if(material == null) {
            throw new NullPointerException("Material for user defined item could not be found in the server software.");
        }

        itemStack = (T) new ItemStack(material);

        //Get default item meta
        ItemMeta itemMeta = Bukkit.getItemFactory().getItemMeta(itemStack.getType());

        //Set default item meta
        itemStack.setItemMeta(itemMeta);

        //Set amount
        itemStack.setAmount(amount);

        this.itemImplementation = itemStack;
        this.rawNBT = rawNBT;
    }

    public BukkitMMOItem(T itemStack) {
        this.itemImplementation = itemStack;
    }

    @Override
    public T getItemImplementation() {
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
