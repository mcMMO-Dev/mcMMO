package com.gmail.nossr50.config.hocon.items;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigItems {

    @Setting(value = "Consumables", comment = "Settings for items that get consumed after one use.")
    private ConfigItemsConsumables consumables = new ConfigItemsConsumables();

    public ConfigItemsConsumables getConsumables() {
        return consumables;
    }

    public ConfigItemsChimaeraWing getChimaeraWing() {
        return consumables.getChimaeraWing();
    }

    public int getUseCost() {
        return getChimaeraWing().getUseCost();
    }

    public int getRecipeCost() {
        return getChimaeraWing().getRecipeCost();
    }

    public String getRecipeMats() {
        return getChimaeraWing().getRecipeMats();
    }

    public int getWarmup() {
        return getChimaeraWing().getWarmup();
    }

    public int getCooldown() {
        return getChimaeraWing().getCooldown();
    }

    public boolean isEnabled() {
        return getChimaeraWing().isEnabled();
    }

    public int getRecentlyHurtCooldown() {
        return getChimaeraWing().getRecentlyHurtCooldown();
    }

    public boolean isPreventUndergroundUse() {
        return getChimaeraWing().isPreventUndergroundUse();
    }

    public boolean isUseBedSpawn() {
        return getChimaeraWing().isUseBedSpawn();
    }

    public boolean isSoundEnabled() {
        return getChimaeraWing().isSoundEnabled();
    }
}
