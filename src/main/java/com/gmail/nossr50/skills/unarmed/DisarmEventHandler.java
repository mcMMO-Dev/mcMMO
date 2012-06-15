package com.gmail.nossr50.skills.unarmed;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;

public class DisarmEventHandler {
    private UnarmedManager manager;
    private Player defender;
    private ItemStack inHand;
    protected int skillModifier;

    protected DisarmEventHandler(UnarmedManager manager, Player defender) {
        this.manager = manager;
        this.defender = defender;
        this.inHand = defender.getItemInHand();

        calculateSkillModifier();
    }

    protected boolean isHoldingItem() {
        return (inHand.getType() != Material.AIR);
    }

    protected void calculateSkillModifier() {
        this.skillModifier = Misc.skillCheck(manager.getSkillLevel(), Unarmed.DISARM_MAX_BONUS_LEVEL);
    }

    protected void sendAbilityMessage() {
        defender.sendMessage(LocaleLoader.getString("Skills.Disarmed"));
    }

    protected void handleDisarm() {
        Misc.dropItem(defender.getLocation(), inHand);
        defender.setItemInHand(new ItemStack(Material.AIR));
    }
}
