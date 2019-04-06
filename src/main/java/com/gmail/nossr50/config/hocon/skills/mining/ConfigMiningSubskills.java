package com.gmail.nossr50.config.hocon.skills.mining;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigMiningSubskills {

    @Setting(value = "Blast-Mining", comment = "Settings for Blast Mining")
    public ConfigMiningBlastMining blastMining = new ConfigMiningBlastMining();

    public ConfigMiningBlastMining getBlastMining() {
        return blastMining;
    }
}