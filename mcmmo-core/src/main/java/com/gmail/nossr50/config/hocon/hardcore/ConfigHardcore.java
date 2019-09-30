package com.gmail.nossr50.config.hocon.hardcore;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigHardcore {

    @Setting(value = "Hardcore-Death-Penalty", comment = "Hardcore penalizes players for dying by removing mcMMO levels from them." +
            "\nThis mechanic was inspired by a popular Action-RPG, it was one of the last things I added when I left the mcMMO project in 2013." +
            "\nFeels good to be back :D")
    private ConfigHardcoreDeathPenalty deathPenalty = new ConfigHardcoreDeathPenalty();

    @Setting(value = "Vampirism", comment = "Vampirism allows players to steal levels from another in PVP." +
            "\nVampirism requires hardcore mode to be enabled to function.")
    private ConfigVampirism vampirism = new ConfigVampirism();

    public ConfigHardcoreDeathPenalty getDeathPenalty() {
        return deathPenalty;
    }

    public ConfigVampirism getVampirism() {
        return vampirism;
    }

}
