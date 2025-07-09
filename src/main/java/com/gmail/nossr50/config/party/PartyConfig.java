package com.gmail.nossr50.config.party;

import com.gmail.nossr50.config.BukkitConfig;
import java.io.File;

public class PartyConfig extends BukkitConfig {
    public PartyConfig(File dataFolder) {
        super("party.yml", dataFolder);
        validate();
    }

    @Override
    protected void loadKeys() {

    }

    public boolean isPartyEnabled() {
        return config.getBoolean("Party.Enabled", true);
    }
}
