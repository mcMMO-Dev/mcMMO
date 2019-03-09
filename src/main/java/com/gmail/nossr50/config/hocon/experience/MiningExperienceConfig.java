package com.gmail.nossr50.config.hocon.experience;

import com.gmail.nossr50.config.ConfigConstants;
import com.gmail.nossr50.config.ConfigValidated;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class MiningExperienceConfig extends ConfigValidated {

    public MiningExperienceConfig()
    {
        super("xp_mining", ConfigConstants.getDataFolder(), ConfigConstants.RELATIVE_PATH_XP_DIR, true, true, true, false);
    }

    @Override
    public void unload() {

    }

    @Override
    public List<String> validateKeys() {
        return null;
    }

    /**
     * The version of this config
     *
     * @return
     */
    @Override
    public double getConfigVersion() {
        return 1;
    }
}
