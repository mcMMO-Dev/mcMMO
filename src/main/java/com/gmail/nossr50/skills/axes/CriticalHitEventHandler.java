package com.gmail.nossr50.skills.axes;

import org.bukkit.Effect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.util.Users;

public class CriticalHitEventHandler {
    private AxeManager manager;
    private EntityDamageByEntityEvent event;
    private int damage;

    protected LivingEntity defender;
    protected int skillModifier;

    public CriticalHitEventHandler(AxeManager manager, EntityDamageByEntityEvent event, LivingEntity defender) {
        this.manager = manager;
        this.event = event;
        this.defender = defender;
        this.damage = event.getDamage();

        calculateSkillModifier();
    }

    protected void modifyEventDamage() {
        if (defender instanceof Player) {
            event.setDamage((int) (damage * Axes.criticalHitPVPModifier));
        }
        else {
            event.setDamage((int) (damage * Axes.criticalHitPVEModifier));
        }
    }

    protected void sendAbilityMessages() {
        McMMOPlayer mcMMOPlayer = manager.getMcMMOPlayer();
        Player attacker = mcMMOPlayer.getPlayer();

        attacker.playEffect(defender.getEyeLocation(), Effect.MOBSPAWNER_FLAMES, 0);

        if (mcMMOPlayer.getProfile().useChatNotifications()) {
            attacker.sendMessage(LocaleLoader.getString("Axes.Combat.CriticalHit"));
        }

        if (defender instanceof Player) {
            Player defendingPlayer = (Player) defender;

            if (Users.getPlayer(defendingPlayer).getProfile().useChatNotifications()) {
                defendingPlayer.sendMessage(LocaleLoader.getString("Axes.Combat.CritStruck"));
            }
        }
    }

    private void calculateSkillModifier() {
        this.skillModifier = SkillTools.skillCheck(manager.getSkillLevel(), Axes.criticalHitMaxBonusLevel);
    }
}
