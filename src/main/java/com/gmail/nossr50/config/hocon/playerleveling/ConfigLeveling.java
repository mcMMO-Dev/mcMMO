package com.gmail.nossr50.config.hocon.playerleveling;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigLeveling {

    /* DEFAULT VALUES */
    private static final int STARTING_LEVEL_DEFAULT = 1;

    /*
     * CONFIG NODES
     */

    @Setting(value = "Player-Starting-Level",
            comment = "\nPlayers will start at this level in all skills if they aren't already saved in the database." +
                    "\nHistorically this number has been 0, but this was changed in 2.1.X to 1 as I felt it was better to start from 1 than 0." +
                    "\nDefault value: "+STARTING_LEVEL_DEFAULT)
    private int startingLevel = STARTING_LEVEL_DEFAULT;

    @Setting(value = "Player-Level-Caps",
            comment = "Restrict players from going above certain skill levels" +
                    "\nPlayers that have skills above the limit will have their skill levels truncated down to the limit.")
    private ConfigSectionLevelCaps configSectionLevelCaps = new ConfigSectionLevelCaps();

    /*
     * GETTER BOILERPLATE
     */

    public int getStartingLevel() {
        return startingLevel;
    }

    public ConfigSectionLevelCaps getConfigSectionLevelCaps() {
        return configSectionLevelCaps;
    }

    /*
     * HELPER METHODS
     */

    public int getLevelCap(PrimarySkillType primarySkillType)
    {
        switch(primarySkillType)
        {
            case ACROBATICS:
                return configSectionLevelCaps.getConfigSectionSkills().getAcrobatics().getLevelCap();
            case ALCHEMY:
                return configSectionLevelCaps.getConfigSectionSkills().getAlchemy().getLevelCap();
            case ARCHERY:
                return configSectionLevelCaps.getConfigSectionSkills().getArchery().getLevelCap();
            case AXES:
                return configSectionLevelCaps.getConfigSectionSkills().getAxes().getLevelCap();
            case EXCAVATION:
                return configSectionLevelCaps.getConfigSectionSkills().getExcavation().getLevelCap();
            case FISHING:
                return configSectionLevelCaps.getConfigSectionSkills().getFishing().getLevelCap();
            case HERBALISM:
                return configSectionLevelCaps.getConfigSectionSkills().getHerbalism().getLevelCap();
            case MINING:
                return configSectionLevelCaps.getConfigSectionSkills().getMining().getLevelCap();
            case REPAIR:
                return configSectionLevelCaps.getConfigSectionSkills().getRepair().getLevelCap();
            case SWORDS:
                return configSectionLevelCaps.getConfigSectionSkills().getSwords().getLevelCap();
            case TAMING:
                return configSectionLevelCaps.getConfigSectionSkills().getTaming().getLevelCap();
            case UNARMED:
                return configSectionLevelCaps.getConfigSectionSkills().getUnarmed().getLevelCap();
            case WOODCUTTING:
                return configSectionLevelCaps.getConfigSectionSkills().getWoodcutting().getLevelCap();
            case SMELTING:
                return configSectionLevelCaps.getConfigSectionSkills().getWoodcutting().getLevelCap();
            case SALVAGE:
                return configSectionLevelCaps.getConfigSectionSkills().getSalvage().getLevelCap();
            default:
                return Integer.MAX_VALUE;
        }
    }

    public boolean isLevelCapEnabled(PrimarySkillType primarySkillType)
    {
        switch(primarySkillType)
        {
            case ACROBATICS:
                return configSectionLevelCaps.getConfigSectionSkills().getAcrobatics().isLevelCapEnabled();
            case ALCHEMY:
                return configSectionLevelCaps.getConfigSectionSkills().getAlchemy().isLevelCapEnabled();
            case ARCHERY:
                return configSectionLevelCaps.getConfigSectionSkills().getArchery().isLevelCapEnabled();
            case AXES:
                return configSectionLevelCaps.getConfigSectionSkills().getAxes().isLevelCapEnabled();
            case EXCAVATION:
                return configSectionLevelCaps.getConfigSectionSkills().getExcavation().isLevelCapEnabled();
            case FISHING:
                return configSectionLevelCaps.getConfigSectionSkills().getFishing().isLevelCapEnabled();
            case HERBALISM:
                return configSectionLevelCaps.getConfigSectionSkills().getHerbalism().isLevelCapEnabled();
            case MINING:
                return configSectionLevelCaps.getConfigSectionSkills().getMining().isLevelCapEnabled();
            case REPAIR:
                return configSectionLevelCaps.getConfigSectionSkills().getRepair().isLevelCapEnabled();
            case SWORDS:
                return configSectionLevelCaps.getConfigSectionSkills().getSwords().isLevelCapEnabled();
            case TAMING:
                return configSectionLevelCaps.getConfigSectionSkills().getTaming().isLevelCapEnabled();
            case UNARMED:
                return configSectionLevelCaps.getConfigSectionSkills().getUnarmed().isLevelCapEnabled();
            case WOODCUTTING:
                return configSectionLevelCaps.getConfigSectionSkills().getWoodcutting().isLevelCapEnabled();
            case SMELTING:
                return configSectionLevelCaps.getConfigSectionSkills().getWoodcutting().isLevelCapEnabled();
            case SALVAGE:
                return configSectionLevelCaps.getConfigSectionSkills().getSalvage().isLevelCapEnabled();
            default:
                return false;
        }
    }
}
