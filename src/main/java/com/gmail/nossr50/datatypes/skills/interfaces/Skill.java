package com.gmail.nossr50.datatypes.skills.interfaces;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.text.StringUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Skill {
    /**
     * The primary skill
     * @return this primary skill
     */
    PrimarySkillType getPrimarySkill();

    /**
     * Returns the key name used for this skill in conjunction with config files
     * @return config file key name
     */
    String getPrimaryKeyName();

    Class<? extends SkillManager> getManagerClass();

    SuperAbilityType getAbility();

    /**
     * Get the max level of this skill.
     *
     * @return the max level of this skill
     */
    int getMaxLevel();

    boolean isSuperAbilityUnlocked(Player player);

    boolean getPVPEnabled();

    boolean getPVEEnabled();

    boolean getDoubleDropsDisabled();

    boolean getHardcoreStatLossEnabled();

    void setHardcoreStatLossEnabled(boolean enable);

    boolean getHardcoreVampirismEnabled();

    void setHardcoreVampirismEnabled(boolean enable);

    ToolType getTool();

    List<SubSkillType> getSkillAbilities();

    double getXpModifier();

    // TODO: This is a little "hacky", we probably need to add something to distinguish child skills in the enum, or to use another enum for them
    boolean isChildSkill();

    static PrimarySkillType bySecondaryAbility(SubSkillType subSkillType) {
        for (PrimarySkillType type : PrimarySkillType.values()) {
            if (type.getSkillAbilities().contains(subSkillType)) {
                return type;
            }
        }

        return null;
    }

    static PrimarySkillType byAbility(SuperAbilityType ability) {
            for (PrimarySkillType type : PrimarySkillType.values()) {
                if (type.getAbility() == ability) {
                    return type;
                }
            }

            return null;
    }

    static PrimarySkillType getSkill(@NotNull String skillName) {
        if (!mcMMO.p.getGeneralConfig().getLocale().equalsIgnoreCase("en_US")) {
            for (PrimarySkillType type : PrimarySkillType.values()) {
                if (skillName.equalsIgnoreCase(LocaleLoader.getString(StringUtils.getCapitalized(type.name()) + ".SkillName"))) {
                    return type;
                }
            }
        }

        for (PrimarySkillType type : PrimarySkillType.values()) {
            if (type.name().equalsIgnoreCase(skillName)) {
                return type;
            }
        }

        if (!skillName.equalsIgnoreCase("all")) {
            mcMMO.p.getLogger().warning("Invalid mcMMO skill (" + skillName + ")"); //TODO: Localize
        }

        return null;
    }

    String getName();

    boolean getPermissions(Player player);

    boolean shouldProcess(Entity target);
}
