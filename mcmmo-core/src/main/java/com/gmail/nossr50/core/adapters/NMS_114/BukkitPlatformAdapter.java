package com.gmail.nossr50.core.adapters.NMS_114;

import com.gmail.nossr50.core.adapters.NBTAdapter;
import com.gmail.nossr50.core.adapters.PlatformAdapter;
import com.gmail.nossr50.core.nbt.NBTBase;
import com.gmail.nossr50.core.nbt.NBTByte;
import net.minecraft.server.v1_14_R1.NBTTagByte;

public class BukkitPlatformAdapter extends PlatformAdapter {

    public BukkitPlatformAdapter() {
        super(new BukkitNBTAdapter());
    }



}
