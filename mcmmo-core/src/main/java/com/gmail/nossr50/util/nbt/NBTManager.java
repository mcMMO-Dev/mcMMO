package com.gmail.nossr50.util.nbt;


import net.minecraft.server.v1_14_R1.NBTBase;
import net.minecraft.server.v1_14_R1.NBTList;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_14_R1.util.CraftNBTTagConfigSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NBTManager {

    private final String CRAFT_META_ITEM_CLASS_PATH = "org.bukkit.craftbukkit.inventory.CraftMetaItem";
    private Class<?> craftMetaItemClass;

    public NBTManager() {
        init(); //Setup method references etc
    }

    private void init() {
//        try {
//            Class<?> craftMetaItemClass = Class.forName(CRAFT_META_ITEM_CLASS_PATH); //for type comparisons
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    public void debugNBTInMainHandItem(Player player) {
        player.sendMessage("Starting NBT Debug Dump...");

        ItemStack itemStack = player.getInventory().getItemInMainHand();
        player.sendMessage("Checking NBT for "+itemStack.toString());

        player.sendMessage("Total NBT Entries: "+getNBTCopy(player.getInventory().getItemInMainHand()).getKeys().size());
        printNBT(player.getInventory().getItemInMainHand(), player);
        player.sendMessage("-- END OF NBT REPORT --");

        player.sendMessage("Attempting to add NBT key named - Herp");
        addFloatNBT(player.getInventory().getItemInMainHand(), "herp", 13.37F);
        player.updateInventory();

        player.sendMessage("(After HERP) Total NBT Entries: "+getNBTCopy(player.getInventory().getItemInMainHand()).getKeys().size());
        printNBT(player.getInventory().getItemInMainHand(), player);
        player.sendMessage("-- END OF NBT REPORT --");

        player.sendMessage("Attempting to save NBT data...");
        player.updateInventory();
    }

    public net.minecraft.server.v1_14_R1.ItemStack getNMSItemStack(ItemStack itemStack) {
        return CraftItemStack.asNMSCopy(itemStack);
    }

    @NonNull
    public NBTTagCompound getNBTCopy(ItemStack itemStack) {
        net.minecraft.server.v1_14_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound freshNBTCopy = nmsItemStack.save(new NBTTagCompound());

        if(!freshNBTCopy.hasKeyOfType("tag", 10)) {
            freshNBTCopy.set("tag", new NBTTagCompound());
        }

        return freshNBTCopy;
    }

    public void addFloatNBT(ItemStack itemStack, String key, float value) {
        //NBT Copied off Item
        net.minecraft.server.v1_14_R1.ItemStack nmsIS = getNMSItemStack(itemStack);
        NBTTagCompound freshNBTCopy = getNBTCopy(itemStack);

        //New Float NBT Value
        NBTTagCompound updatedNBT = new NBTTagCompound();
        updatedNBT.setFloat(key, value);

        //Merge
        mergeToTagCompound(freshNBTCopy, updatedNBT);

        //Invoke load() time
        applyNBT(nmsIS, freshNBTCopy);

        //Apply Item Meta (Not sure if needed)
        CraftItemStack craftItemStack = CraftItemStack.asCraftMirror(nmsIS);
        itemStack.setItemMeta(craftItemStack.getItemMeta());
    }

    public void mergeToTagCompound(NBTTagCompound targetCompound, NBTTagCompound modificationCompound) {
        NBTTagCompound tagCompound = (NBTTagCompound) targetCompound.get("tag");
        tagCompound.a(modificationCompound);
    }



    public void applyNBT(net.minecraft.server.v1_14_R1.ItemStack nmsItemStack, NBTTagCompound nbtTagCompound) {

        try {
            Class clazz = Class.forName("net.minecraft.server.v1_14_R1.ItemStack");
            Class[] methodParameters = new Class[]{ NBTTagCompound.class };
            Method loadMethod = clazz.getDeclaredMethod("load", methodParameters);
            loadMethod.setAccessible(true);
            loadMethod.invoke(nmsItemStack, nbtTagCompound);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public NBTBase constructNBT(String nbtString) {
        try {
            return CraftNBTTagConfigSerializer.deserialize(nbtString);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(("[mcMMO NBT Debug] was unable parse the NBT string from your config! Double check that it is proper NBT!"));
            return null;
        }
    }

    public void printNBT(ItemStack itemStack, Player player) {
        NBTTagCompound tagCompoundCopy = getNBTCopy(itemStack);
        for(String key : tagCompoundCopy.getKeys()) {
            player.sendMessage("");
            player.sendMessage("NBT Key: "+key);
            player.sendMessage("NBT Value: " + tagCompoundCopy.get(key).asString());
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
