package com.gmail.nossr50.util.nbt;


import com.gmail.nossr50.mcMMO;
import net.minecraft.server.v1_14_R1.NBTBase;
import net.minecraft.server.v1_14_R1.NBTList;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_14_R1.util.CraftNBTTagConfigSerializer;
import org.bukkit.inventory.ItemStack;

public class NBTManager {

    private static final String CRAFT_META_ITEM_CLASS_PATH = "org.bukkit.craftbukkit.inventory.CraftMetaItem";
    private Class<?> craftMetaItemClass;
    private final mcMMO pluginRef;

    public NBTManager(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
        init(); //Setup method references etc
    }

    private void init() {
        try {
            Class<?> craftMetaItemClass = Class.forName(CRAFT_META_ITEM_CLASS_PATH); //for type comparisons
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public NBTTagCompound getNBT(ItemStack itemStack) {
        Bukkit.broadcastMessage("Checking NBT for "+itemStack.toString());
        net.minecraft.server.v1_14_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound rootTag = nmsItemStack.getTag();
        return rootTag;
    }

    public NBTBase constructNBT(String nbtString) {
        try {
            return CraftNBTTagConfigSerializer.deserialize(nbtString);
        } catch (Exception e) {
            e.printStackTrace();
            pluginRef.getLogger().severe("mcMMO was unable parse the NBT string from your config! Double check that it is proper NBT!");
            return null;
        }
    }

    public void printNBT(ItemStack itemStack) {
        for(String key : getNBT(itemStack).getKeys()) {
            Bukkit.broadcastMessage("NBT Key found: "+key);
        }
    }

    public boolean hasNBT(NBTBase nbt, NBTTagCompound otherNbt) {
        if(nbt instanceof NBTList<?>) {

        } else {

        }

        //TODO: Implement this
        return false;
    }

}
