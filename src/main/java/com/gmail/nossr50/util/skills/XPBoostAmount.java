package com.gmail.nossr50.util.skills;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.permissions.Permissible;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class XPBoostAmount {

    public static final double NONE = 1.0;

    public static final XPBoostAmount QUADRUPLE = new XPBoostAmount(4.0, "mcmmo.perks.xp.quadruple.%s");
    public static final XPBoostAmount TRIPLE = new XPBoostAmount(3.0, "mcmmo.perks.xp.triple.%s");
    public static final XPBoostAmount DOUBLE_AND_ONE_HALF = new XPBoostAmount(2.5, "mcmmo.perks.xp.150percentboost.%s");
    public static final XPBoostAmount DOUBLE = new XPBoostAmount(2.0, "mcmmo.perks.xp.double.%s");
    public static final XPBoostAmount ONE_AND_ONE_HALF = new XPBoostAmount(1.5, "mcmmo.perks.xp.50percentboost.%s");
    public static final XPBoostAmount ONE_AND_ONE_QUARTER = new XPBoostAmount(1.25, "mcmmo.perks.xp.25percentboost.%s");
    public static final XPBoostAmount ONE_AND_ONE_TENTH = new XPBoostAmount(1.1, "mcmmo.perks.xp.10percentboost.%s");
    public static final XPBoostAmount CUSTOM = new XPBoostAmount(ExperienceConfig.getInstance().getCustomXpPerkBoost(), "mcmmo.perks.xp.customboost.%s");


    private String FIELD_NAME;
    private final double multiplier;
    private final String permissionNode;

    public XPBoostAmount(double multiplier, String permissionNode) {
        this.multiplier = multiplier;
        this.permissionNode = permissionNode;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public String getPermissionNode() {
        return permissionNode;
    }



    public boolean hasBoostPermission(Permissible permissible, PrimarySkillType skill) {
        return permissible.hasPermission(String.format(permissionNode, "all")) ||
                permissible.hasPermission(String.format(permissionNode, "*")) ||
                permissible.hasPermission(String.format(permissionNode, skill.toString().toLowerCase(Locale.ENGLISH)));
    }


    // Make this class work like an enum

    @Override
    public String toString() {
        return FIELD_NAME;
    }

    public String name() {
        return FIELD_NAME;
    }

    public static final Map<String, XPBoostAmount> VALUES = new HashMap<>();
    public static final List<XPBoostAmount> VALUES_SORTED_BY_MULTIPLIER = new ArrayList<>(); // Faster to just sort once since mcMMO doesn't have any reloading anyway

    static {
        for (Field field : XPBoostAmount.class.getDeclaredFields()) {
            if (field.getType() == XPBoostAmount.class) {
                try {
                    XPBoostAmount itemType = (XPBoostAmount) field.get(null);
                    itemType.FIELD_NAME = field.getName();
                    VALUES.put(field.getName(), itemType);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        VALUES_SORTED_BY_MULTIPLIER.addAll(VALUES.values().stream().sorted((a, b) -> Double.compare(b.getMultiplier(), a.getMultiplier())).toList());
    }

    public static XPBoostAmount valueOf(String name) {
        return VALUES.get(name);
    }

    public static List<XPBoostAmount> values() {
        return VALUES.values().stream().toList();
    }

    public static List<XPBoostAmount> getByHighestMultiplier() {
        return VALUES_SORTED_BY_MULTIPLIER;
    }
}
