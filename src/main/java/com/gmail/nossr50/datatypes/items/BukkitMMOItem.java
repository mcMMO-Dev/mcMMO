package com.gmail.nossr50.datatypes.items;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.nbt.RawNBT;
import net.minecraft.server.v1_13_R2.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;

public class BukkitMMOItem implements MMOItem {

    private ItemStack itemImplementation;

    public BukkitMMOItem(String namespaceKey, int amount) throws NullPointerException {
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

        this.itemImplementation = itemStack;
    }

    public BukkitMMOItem(ItemStack itemStack) {
        NBTTagCompound nbtTagCompound = mcMMO.getNbtManager().getNBT(itemStack);
        this.itemImplementation = itemStack;
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
        NBTTagCompound nbtTagCompound = mcMMO.getNbtManager().getNBT(itemImplementation);
        return new RawNBT(nbtTagCompound.toString(), nbtTagCompound);
    }

}
