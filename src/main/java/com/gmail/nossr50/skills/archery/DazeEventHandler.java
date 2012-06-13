package com.gmail.nossr50.skills.archery;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;

public class DazeEventHandler {
    private ArcheryManager manager;
    private EntityDamageEvent event;
    private Player defender;

    protected int skillModifier;

    protected DazeEventHandler (ArcheryManager manager, EntityDamageEvent event, Player defender) {
        this.manager = manager;
        this.event = event;
        this.defender = defender;

        calculateSkillModifier();
    }

    protected void calculateSkillModifier() {
        this.skillModifier = Misc.skillCheck(manager.getSkillLevel(), Archery.DAZE_MAX_BONUS_LEVEL);
    }

    protected void handleDazeEffect() {
        Location location = defender.getLocation();

        if (Archery.getRandom().nextInt(10) > 5) {
            location.setPitch(90);
        }
        else {
            location.setPitch(-90);
        }

        defender.teleport(location);
        event.setDamage(event.getDamage() + Archery.DAZE_MODIFIER);
    }

    protected void sendAbilityMessages() {
        defender.sendMessage(LocaleLoader.getString("Combat.TouchedFuzzy"));
        manager.getPlayer().sendMessage(LocaleLoader.getString("Combat.TargetDazed"));
    }
}
