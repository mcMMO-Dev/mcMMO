package com.gmail.nossr50.config.skills.taming;

import com.gmail.nossr50.datatypes.skills.subskills.taming.CallOfTheWildType;
import com.gmail.nossr50.datatypes.skills.subskills.taming.TamingSummon;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.Material;

import java.util.HashMap;


@ConfigSerializable
public class ConfigTamingCallOfTheWild {
    private static final double MIN_HORSE_DEFAULT = 0.7D;
    private static final double MAX_HORSE_DEFAULT = 2.0D;
    private static final HashMap<CallOfTheWildType, TamingSummon> TAMING_SUMMON_DEFAULT_MAP;

    static {
        TAMING_SUMMON_DEFAULT_MAP = new HashMap<>();
        TAMING_SUMMON_DEFAULT_MAP.put(CallOfTheWildType.CAT, new TamingSummon(CallOfTheWildType.CAT, Material.COD, 10, 1, 240, 1));
        TAMING_SUMMON_DEFAULT_MAP.put(CallOfTheWildType.WOLF, new TamingSummon(CallOfTheWildType.WOLF, Material.BONE, 10, 1, 240, 2));
        TAMING_SUMMON_DEFAULT_MAP.put(CallOfTheWildType.HORSE, new TamingSummon(CallOfTheWildType.HORSE, Material.APPLE, 10, 1, 240, 1));
    }

    //TODO: Set this up for custom item stuff after NBT support is done

    @Setting(value = "Taming-Summons", comment = "Taming summon settings.")
    private HashMap<CallOfTheWildType, TamingSummon> tamingSummonHashMap = TAMING_SUMMON_DEFAULT_MAP;

    @Setting(value = "Minimum-Horse-Jump-Strength", comment = "The minimum value of jump strength a summoned COTW horse can have." +
            "\nDefault value: "+MIN_HORSE_DEFAULT)
    private double minHorseJumpStrength = MIN_HORSE_DEFAULT;

    @Setting(value = "Maximum-Horse-Jump-Strength", comment = "The maximum value of jump strength a summoned COTW horse can have." +
            "\nDefault value: "+MAX_HORSE_DEFAULT)
    private double maxHorseJumpStrength = MAX_HORSE_DEFAULT;

    public double getMinHorseJumpStrength() {
        return minHorseJumpStrength;
    }

    public double getMaxHorseJumpStrength() {
        return maxHorseJumpStrength;
    }

    public TamingSummon getCOTWSummon(CallOfTheWildType callOfTheWildType) {
        if(tamingSummonHashMap.get(callOfTheWildType) == null) {
            System.out.println("mcMMO - Could not find summon config entry for CallOfTheWildType, using default instead - "+callOfTheWildType.toString());
            return TAMING_SUMMON_DEFAULT_MAP.get(callOfTheWildType);
        } else {
            return tamingSummonHashMap.get(callOfTheWildType);
        }
    }
}
