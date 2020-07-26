package com.gmail.nossr50.skills.smelting;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.RankUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class Smelting {

    protected static int getResourceXp(ItemStack smelting) {
        return mcMMO.getModManager().isCustomOre(smelting.getType()) ? mcMMO.getModManager().getBlock(smelting.getType()).getSmeltingXpGain() : ExperienceConfig.getInstance().getXp(PrimarySkillType.SMELTING, smelting.getType());
    }
}
