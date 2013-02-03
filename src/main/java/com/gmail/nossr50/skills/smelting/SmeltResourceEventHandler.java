package com.gmail.nossr50.skills.smelting;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Misc;
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
        this.skillModifier = Misc.skillCheck(manager.getSkillLevel(), Smelting.secondSmeltMaxLevel);
    }

    protected void handleXPGain() {
        Material sourceType = event.getSource().getType();
        int xp = 0;

        switch (sourceType) {
        case COAL_ORE:
            xp = Config.getInstance().getSmeltingXPCoal();
            break;

        case GLOWING_REDSTONE_ORE:
        case REDSTONE_ORE:
            xp = Config.getInstance().getSmeltingXPRedstone();
            break;

        case IRON_ORE:
            xp = Config.getInstance().getSmeltingXPIron();
            break;

        case GOLD_ORE:
            xp = Config.getInstance().getSmeltingXPGold();
            break;

        case DIAMOND_ORE:
            xp = Config.getInstance().getSmeltingXPDiamond();
            break;

        case LAPIS_ORE:
            xp = Config.getInstance().getSmeltingXPLapis();
            break;

        case EMERALD_ORE:
            xp = Config.getInstance().getSmeltingXPEmerald();
            break;

        default:
            break;
        }

        McMMOPlayer mcMMOPlayer = manager.getMcMMOPlayer();
        Player player = mcMMOPlayer.getPlayer();

        if (Permissions.mining(player)) {
            mcMMOPlayer.beginXpGain(SkillType.MINING, xp / 2);
        }

        if (Permissions.repair(player)) {
            mcMMOPlayer.beginXpGain(SkillType.REPAIR, xp / 2);
        }
    }

    protected void handleBonusSmelts() {
        ItemStack result = event.getResult();

        result.setAmount(result.getAmount() + 1);
        event.setResult(result);
    }
}
