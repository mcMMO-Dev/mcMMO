package com.gmail.nossr50.skills.unarmed;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.events.skills.McMMOPlayerDisarmEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.utilities.SkillTools;
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
    }

    protected boolean isHoldingItem() {
        return (inHand.getType() != Material.AIR);
    }

    protected void calculateSkillModifier() {
        this.skillModifier = SkillTools.skillCheck(manager.getSkillLevel(), Unarmed.disarmMaxBonusLevel);
    }

    private void sendAbilityMessage() {
        defender.sendMessage(LocaleLoader.getString("Skills.Disarmed"));
    }

    protected void handleDisarm() {
        McMMOPlayerDisarmEvent event = new McMMOPlayerDisarmEvent(defender);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        if(!event.isCancelled()) {
            Misc.dropItem(defender.getLocation(), inHand);
            defender.setItemInHand(new ItemStack(Material.AIR));

            sendAbilityMessage();
        }
    }
}
