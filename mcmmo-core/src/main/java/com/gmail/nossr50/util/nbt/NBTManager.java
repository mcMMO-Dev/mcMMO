package com.gmail.nossr50.util.nbt;


import net.minecraft.server.v1_14_R1.Item;
import net.minecraft.server.v1_14_R1.NBTBase;
import net.minecraft.server.v1_14_R1.NBTList;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_14_R1.util.CraftNBTTagConfigSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NBTManager {

    private static final String CRAFT_META_ITEM_CLASS_PATH = "org.bukkit.craftbukkit.inventory.CraftMetaItem";
    private Class<?> craftMetaItemClass;

    public NBTManager() {
        init(); //Setup method references etc
    }

    private void init() {
        try {
            Class<?> craftMetaItemClass = Class.forName(CRAFT_META_ITEM_CLASS_PATH); //for type comparisons
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void debugNBTInMainHandItem(Player player) {
        player.sendMessage("Starting NBT Debug Dump...");
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        player.sendMessage("Checking NBT for "+itemStack.toString());
        NBTTagCompound nbtTagCompound = getNBT(itemStack);

        if(nbtTagCompound == null) {
            player.sendMessage("No NBT data found for main hand item.");
            return;
        }

        player.sendMessage("Total NBT Entries: "+nbtTagCompound.getKeys().size());
        printNBT(nbtTagCompound, player);
        player.sendMessage("-- END OF NBT REPORT --");

        player.sendMessage("Attempting to add NBT key named - Herp");
        addFloatNBT(nbtTagCompound, "herp", 13.37F);

        player.sendMessage("(After HERP) Total NBT Entries: "+nbtTagCompound.getKeys().size());
        printNBT(nbtTagCompound, player);
        player.sendMessage("-- END OF NBT REPORT --");

        player.sendMessage("Attempting to save NBT data...");
        player.getInventory().setItemInMainHand(saveNBT(itemStack, nbtTagCompound));
        player.updateInventory();
    }

    public static ItemStack saveNBT(ItemStack itemStack, NBTTagCompound nbtTagCompound) {
        net.minecraft.server.v1_14_R1.ItemStack nmsItemStack = getNMSItemStack(itemStack);

        try {
            Class clazz = Class.forName("net.minecraft.server.v1_14_R1.ItemStack");
            Class[] methodParameters = new Class[]{ NBTTagCompound.class };
            Method loadMethod = clazz.getDeclaredMethod("load", methodParameters);
            loadMethod.setAccessible(true);
            loadMethod.invoke(nmsItemStack, nbtTagCompound);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

//        nmsItemStack.save(nbtTagCompound);
//        itemStack.setItemMeta(nmsItemStack..getItemMeta());
        return itemStack;
    }

    public static net.minecraft.server.v1_14_R1.ItemStack getNMSItemStack(ItemStack itemStack) {
        return CraftItemStack.asNMSCopy(itemStack);
    }

    @Nullable
    public static NBTTagCompound getNBT(ItemStack itemStack) {
        net.minecraft.server.v1_14_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return nmsItemStack.getTag();
    }

    public static void addFloatNBT(NBTTagCompound nbtTagCompound, String key, float value) {
        nbtTagCompound.setFloat(key, value);
    }

    public static NBTBase constructNBT(String nbtString) {
        try {
            return CraftNBTTagConfigSerializer.deserialize(nbtString);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(("[mcMMO NBT Debug] was unable parse the NBT string from your config! Double check that it is proper NBT!"));
            return null;
        }
    }

    public static void printNBT(NBTTagCompound nbtTagCompound, Player player) {
        for(String key : nbtTagCompound.getKeys()) {
            player.sendMessage("");
            player.sendMessage("NBT Key: "+key);
            player.sendMessage("NBT Value: " + nbtTagCompound.get(key).asString());
        }
    }

    public static boolean hasNBT(NBTBase nbt, NBTTagCompound otherNbt) {
        if(nbt instanceof NBTList<?>) {

        } else {

        }

        //TODO: Implement this
        return false;
    }

}
