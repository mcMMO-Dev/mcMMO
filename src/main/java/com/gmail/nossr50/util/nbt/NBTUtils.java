package com.gmail.nossr50.util.nbt;


import com.gmail.nossr50.mcMMO;
import net.minecraft.server.v1_13_R2.NBTBase;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftItem;
import org.bukkit.craftbukkit.v1_13_R2.util.CraftNBTTagConfigSerializer;
import org.bukkit.inventory.ItemStack;

public class NBTUtils {

    public RawNBT<?> constructNBT(String nbtString) {
        try {
            return new RawNBT<NBTBase>(nbtString, CraftNBTTagConfigSerializer.deserialize(nbtString));
        } catch (Exception e) {
            e.printStackTrace();
            mcMMO.p.getLogger().severe("mcMMO was unable parse the NBT string from your config! Double check that it is proper NBT!");
            return null;
        }
    }

    public boolean hasNBT(ItemStack itemStack) {
        if(CraftItem)
    }

}
