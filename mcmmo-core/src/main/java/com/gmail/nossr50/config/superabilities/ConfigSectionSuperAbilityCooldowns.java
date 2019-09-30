package com.gmail.nossr50.config.superabilities;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionSuperAbilityCooldowns {

    @Setting(value = "Berserk")
    private int berserk = 240;

    @Setting(value = "Giga-Drill-Breaker")
    private int gigaDrillBreaker = 240;

    @Setting(value = "Green-Terra")
    private int greenTerra = 240;

    @Setting(value = "Serrated-Strikes")
    private int serratedStrikes = 240;

    @Setting(value = "Skull-Splitter")
    private int skullSplitter = 240;

    @Setting(value = "Super-Breaker")
    private int superBreaker = 240;

    @Setting(value = "Tree-Feller")
    private int treeFeller = 240;

    @Setting(value = "Blast-Mining")
    private int blastMining = 120;

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

    public int getBlastMining() {
        return blastMining;
    }
}