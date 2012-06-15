package com.gmail.nossr50.skills.unarmed;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.util.Misc;

public class IronGripEventHandler {
    private UnarmedManager manager;
    private Player defender;
    protected int skillModifier;

    protected IronGripEventHandler(UnarmedManager manager, Player defender) {
        this.manager = manager;
        this.defender = defender;

        calculateSkillModifier();
    }

    protected void calculateSkillModifier() {
        this.skillModifier = Misc.skillCheck(manager.getSkillLevel(), Unarmed.DISARM_MAX_BONUS_LEVEL);
    }

    protected void sendAbilityMessages() {
        defender.sendMessage(ChatColor.GREEN + "Your iron grip kept you from being disarmed!"); //TODO: Use locale
        manager.getPlayer().sendMessage(ChatColor.RED + "Your opponent has an iron grip!"); //TODO: Use locale
    }
}
