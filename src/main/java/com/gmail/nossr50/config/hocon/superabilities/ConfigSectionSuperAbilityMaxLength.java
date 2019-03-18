package com.gmail.nossr50.config.hocon.superabilities;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionSuperAbilityMaxLength {
    @Setting(value = "Berserk")
    private int berserk = 60;

    @Setting(value = "Giga-Drill-Breaker")
    private int gigaDrillBreaker = 60;

    @Setting(value = "Green-Terra")
    private int greenTerra = 60;

    @Setting(value = "Serrated-Strikes")
    private int serratedStrikes = 60;

    @Setting(value = "Skull-Splitter")
    private int skullSplitter = 60;

    @Setting(value = "Super-Breaker")
    private int superBreaker = 60;

    @Setting(value = "Tree-Feller")
    private int treeFeller = 60;

    public int getBerserk() {
        return berserk;
    }

    public int getGigaDrillBreaker() {
        return gigaDrillBreaker;
    }

    public int getGreenTerra() {
        return greenTerra;
    }

    public int getSerratedStrikes() {
        return serratedStrikes;
    }

    public int getSkullSplitter() {
        return skullSplitter;
    }

    public int getSuperBreaker() {
        return superBreaker;
    }

    public int getTreeFeller() {
        return treeFeller;
    }
}
