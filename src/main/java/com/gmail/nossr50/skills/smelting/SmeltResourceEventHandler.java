package com.gmail.nossr50.skills.smelting;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Permissions;

public class SmeltResourceEventHandler {
    private SmeltingManager manager;
    private FurnaceSmeltEvent event;

    protected int skillModifier;

    protected SmeltResourceEventHandler(SmeltingManager manager, FurnaceSmeltEvent event) {
        this.manager = manager;
        this.event = event;
    }

    protected void calculateSkillModifier() {
        this.skillModifier = SkillTools.skillCheck(manager.getSkillLevel(), Smelting.secondSmeltMaxLevel);
    }

    protected void handleXPGain() {
        Material sourceType = event.getSource().getType();
        int xp = Config.getInstance().getXp(SkillType.SMELTING, sourceType);

        if (sourceType == Material.GLOWING_REDSTONE_ORE) {
            xp = Config.getInstance().getXp(SkillType.SMELTING, Material.REDSTONE_ORE);
        }

        McMMOPlayer mcMMOPlayer = manager.getMcMMOPlayer();
        Player player = mcMMOPlayer.getPlayer();

        if (Permissions.skillEnabled(player, SkillType.MINING)) {
            mcMMOPlayer.beginXpGain(SkillType.MINING, xp / 2);
        }

        if (Permissions.skillEnabled(player, SkillType.REPAIR)) {
            mcMMOPlayer.beginXpGain(SkillType.REPAIR, xp / 2);
        }
    }

    protected void handleBonusSmelts() {
        ItemStack result = event.getResult();

        result.setAmount(result.getAmount() + 1);
        event.setResult(result);
    }
}
