package com.gmail.nossr50.skills.smelting;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Smelting {

    public static int getSmeltXP(@NotNull ItemStack smelting) {
        return ExperienceConfig.getInstance().getXp(PrimarySkillType.SMELTING, smelting.getType());
    }

}
