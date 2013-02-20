package com.gmail.nossr50.skills.archery;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Users;

public class DazeEventHandler {
    private ArcheryManager manager;
    private McMMOPlayer mcMMOPlayer;
    private EntityDamageEvent event;
    private Player defender;

    protected int skillModifier;

    protected DazeEventHandler (ArcheryManager manager, EntityDamageEvent event, Player defender) {
        this.manager = manager;
        this.mcMMOPlayer = manager.getMcMMOPlayer();
        this.event = event;
        this.defender = defender;

        calculateSkillModifier();
    }

    protected void calculateSkillModifier() {
        this.skillModifier = SkillTools.skillCheck(manager.getSkillLevel(), Archery.dazeMaxBonusLevel);
    }

    protected void handleDazeEffect() {
        Location location = defender.getLocation();
        location.setPitch(90 - Misc.getRandom().nextInt(181));

        defender.teleport(location);
        defender.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 10, 10));

        event.setDamage(event.getDamage() + Archery.dazeModifier);
    }

    protected void sendAbilityMessages() {
        if (Users.getPlayer(defender).getProfile().useChatNotifications()) {
            defender.sendMessage(LocaleLoader.getString("Combat.TouchedFuzzy"));
        }

        if (mcMMOPlayer.getProfile().useChatNotifications()) {
            mcMMOPlayer.getPlayer().sendMessage(LocaleLoader.getString("Combat.TargetDazed"));
        }
    }
}
