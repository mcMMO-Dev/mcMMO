package com.gmail.nossr50.core.adapters.NMS_114;

import com.gmail.nossr50.core.adapters.PlatformAdapter;

public class BukkitPlatformAdapter extends PlatformAdapter {

    public BukkitPlatformAdapter() {
        super(new BukkitNBTAdapter());
    }

}
