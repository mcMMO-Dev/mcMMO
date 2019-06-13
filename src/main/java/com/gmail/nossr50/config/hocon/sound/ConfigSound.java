package com.gmail.nossr50.config.hocon.sound;

import com.gmail.nossr50.util.sounds.SoundType;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public class ConfigSound {

    private static final HashMap<SoundType, SoundSetting> SOUND_SETTINGS_MAP_DEFAULT;
    private static final double MASTER_VOLUME_DEFAULT = 1.0;

    static {
        SOUND_SETTINGS_MAP_DEFAULT = new HashMap<>();

        SOUND_SETTINGS_MAP_DEFAULT.put(SoundType.ANVIL, new SoundSetting(1.0, 0.3));
        SOUND_SETTINGS_MAP_DEFAULT.put(SoundType.FIZZ, new SoundSetting(0.5));
        SOUND_SETTINGS_MAP_DEFAULT.put(SoundType.LEVEL_UP, new SoundSetting(.75, 0.5));
        SOUND_SETTINGS_MAP_DEFAULT.put(SoundType.ITEM_BREAK, new SoundSetting(1.0, 1.0));
        SOUND_SETTINGS_MAP_DEFAULT.put(SoundType.POP, new SoundSetting(1.0));
        SOUND_SETTINGS_MAP_DEFAULT.put(SoundType.CHIMAERA_WING, new SoundSetting(1.0, 0.6));
        SOUND_SETTINGS_MAP_DEFAULT.put(SoundType.ROLL_ACTIVATED, new SoundSetting(1.0, 0.7));
        SOUND_SETTINGS_MAP_DEFAULT.put(SoundType.SKILL_UNLOCKED, new SoundSetting(1.0, 1.4));
        SOUND_SETTINGS_MAP_DEFAULT.put(SoundType.DEFLECT_ARROWS, new SoundSetting(1.0, 2.0));
        SOUND_SETTINGS_MAP_DEFAULT.put(SoundType.TOOL_READY, new SoundSetting(1.0, 0.4));
        SOUND_SETTINGS_MAP_DEFAULT.put(SoundType.ABILITY_ACTIVATED_GENERIC, new SoundSetting(1.0, 0.1));
        SOUND_SETTINGS_MAP_DEFAULT.put(SoundType.ABILITY_ACTIVATED_BERSERK, new SoundSetting(0.5, 1.7));
        SOUND_SETTINGS_MAP_DEFAULT.put(SoundType.TIRED, new SoundSetting(1.0, 1.7));
        SOUND_SETTINGS_MAP_DEFAULT.put(SoundType.BLEED, new SoundSetting(2.0, 2.0));
    }

    @Setting(value = "Sound-Settings", comment = "Adjust sound settings for various mcMMO sounds here." +
            "\nSome sound settings such as pitch are ignored because mcMMO randomizes the pitch with certain sounds.")
    private HashMap<SoundType, SoundSetting> soundSettingsHashMap = SOUND_SETTINGS_MAP_DEFAULT;

    @Setting(value = "Master-Volume", comment = "All sound values are multiplied by this value to determine their final volume." +
            "\nUse a range from 0.0 -> 1.0" +
            "\nDefault Value: "+MASTER_VOLUME_DEFAULT)
    private double masterVolume = MASTER_VOLUME_DEFAULT;

    public double getMasterVolume() {
        return masterVolume;
    }

    public SoundSetting getSoundSetting(SoundType soundType) {
        return soundSettingsHashMap.get(soundType);
    }
}
