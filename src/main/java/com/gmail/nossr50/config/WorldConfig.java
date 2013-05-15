package com.gmail.nossr50.config;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.util.StringUtils;

public class WorldConfig extends ConfigLoader {

    private static WorldConfig instance;

    public WorldConfig() {
        super("worlds.yml");
    }

    @Override
    protected void loadKeys() { }

    public static WorldConfig getInstance() {
        if (instance == null) {
            instance = new WorldConfig();
        }

        return instance;
    }
    
    
    // World settings
    public boolean isSkillEnabled(SkillType skillType, String world) {
        String skill = StringUtils.getCapitalized(skillType.toString());
        return config.getBoolean("Worlds." + StringUtils.getCapitalized(world) + ".Skills." + skill + ".Enabled", isSkillEnabled(skill));
    }

    public boolean isAbilityEnabled(SkillType skillType, String world) {
        String skill = StringUtils.getCapitalized(skillType.toString());
        return config.getBoolean("Worlds." + StringUtils.getCapitalized(world) + ".Skills." + skill + ".AbilityEnabled", isAbilityEnabled(skill));
    }

    public boolean isBlockStoreEnabled(String world) { return config.getBoolean("Worlds." + StringUtils.getCapitalized(world) + ".BlockStore", isBlockStoreEnabled()); }

    public boolean isMobHealthEnabled(String world) { return config.getBoolean("Worlds." + StringUtils.getCapitalized(world) + ".MobHealthBar", isMobHealthEnabled()); }

    // Default settings
    private boolean isSkillEnabled(String skill) { return config.getBoolean("Worlds.default.Skills." + skill + ".Enabled", true); }

    private boolean isAbilityEnabled(String skill) { return config.getBoolean("Worlds.default.Skills." + skill + ".AbilityEnabled", true); }

    private boolean isBlockStoreEnabled() { return config.getBoolean("Worlds.default.BlockStore", true); }

    private boolean isMobHealthEnabled() { return config.getBoolean("Worlds.default.MobHealthBar", true); }
}
