package com.gmail.nossr50.config.hocon.skills.mining;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigMiningSubskills {

    @Setting(value = "Blast-Mining", comment = "Settings for Blast Mining")
    private ConfigMiningBlastMining blastMining = new ConfigMiningBlastMining();

    @Setting(value = "Double-Drops")
    private ConfigMiningDoubleDrops doubleDrops = new ConfigMiningDoubleDrops();

    public ConfigMiningBlastMining getBlastMining() {
        return blastMining;
    }

    public ConfigMiningDoubleDrops getDoubleDrops() {
        return doubleDrops;
    }
}