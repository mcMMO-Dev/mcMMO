package com.gmail.nossr50.config;

import com.gmail.nossr50.util.sounds.SoundType;

public class SoundConfig extends AutoUpdateConfigLoader {
    private static SoundConfig instance;

    public SoundConfig()
    {
        super("sounds.yml");
        validate();
        this.instance = this;
    }

    @Override
    protected void loadKeys() {

    }

    public static SoundConfig getInstance()
    {
        if(instance == null)
            return new SoundConfig();

        return instance;
    }

    @Override
    protected boolean validateKeys() {
        for(SoundType soundType : SoundType.values())
        {
            if(config.getDouble("Sounds."+soundType.toString()+".Volume") < 0)
            {
                plugin.getLogger().info("[mcMMO] Sound volume cannot be below 0 for "+soundType.toString());
                return false;
            }

            //Sounds with custom pitching don't use pitch values
            if(!soundType.usesCustomPitch())
            {
                if(config.getDouble("Sounds."+soundType.toString()+".Pitch") < 0)
                {
                    plugin.getLogger().info("[mcMMO] Sound pitch cannot be below 0 for "+soundType.toString());
                    return false;
                }
            }
        }
        return true;
    }

    public float getMasterVolume() { return (float) config.getDouble("Sounds.MasterVolume", 1.0); }

    public float getVolume(SoundType soundType)
    {
        String key = "Sounds."+soundType.toString()+".Volume";
        return (float) config.getDouble(key);
    }

    public float getPitch(SoundType soundType)
    {
        String key = "Sounds."+soundType.toString()+".Pitch";
        return (float) config.getDouble(key);
    }

    public boolean getIsEnabled(SoundType soundType)
    {
        String key = "Sounds."+soundType.toString()+".Enabled";
        return config.getBoolean(key, true);
    }
}
