package com.gmail.nossr50.worldguard;

import com.sk89q.worldguard.protection.flags.StateFlag;

public class WorldGuardFlags {
    // StateFlag with the name "my-custom-flag", which defaults to "allow"
    public static final StateFlag MCMMO_ENABLE_WG_FLAG = new StateFlag("mcmmo", true);
    public static final StateFlag MCMMO_XP_WG_FLAG = new StateFlag("mcmmo-xp", true);
    public static final StateFlag MCMMO_HARDCORE_WG_FLAG = new StateFlag("mcmmo-hardcore", true);
}
