package com.gmail.nossr50.skills.unarmed;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Users;

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
        this.skillModifier = Misc.skillCheck(Users.getProfile(defender).getSkillLevel(SkillType.UNARMED), Unarmed.IRON_GRIP_MAX_BONUS_LEVEL);
    }

    protected void sendAbilityMessages() {
        defender.sendMessage(ChatColor.GREEN + "Your iron grip kept you from being disarmed!"); //TODO: Use locale
        manager.getPlayer().sendMessage(ChatColor.RED + "Your opponent has an iron grip!"); //TODO: Use locale
    }
}
