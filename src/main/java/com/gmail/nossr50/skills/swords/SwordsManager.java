package com.gmail.nossr50.skills.swords;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.util.Combat;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class SwordsManager {
    private Player player;
    private PlayerProfile profile;
    private int skillLevel;
    private Permissions permissionsInstance;

    public SwordsManager (Player player) {
        this.player = player;
        this.profile = Users.getProfile(player);
        this.skillLevel = profile.getSkillLevel(SkillType.SWORDS);
        this.permissionsInstance =  Permissions.getInstance();
    }

    /**
     * Check for Bleed effect.
     *
     * @param defender The defending entity
     */
    public void bleedCheck(LivingEntity defender) {
        if (!permissionsInstance.swordsBleed(player)) {
            return;
        }

        if (Combat.shouldBeAffected(player, defender)) {
            BleedEventHandler eventHandler = new BleedEventHandler(this, defender);

            int randomChance = 1000;

            if (player.hasPermission("mcmmo.perks.lucky.swords")) {
                randomChance = (int) (randomChance * 0.75);
            }

            if (Swords.getRandom().nextInt(randomChance) < eventHandler.skillModifier) {
                eventHandler.addBleedTicks();
                eventHandler.sendAbilityMessages();
            }
        }
    }

    public void counterAttackChecks(LivingEntity attacker, int damage) {
        if (!permissionsInstance.counterAttack(player)) {
            return;
        }

        CounterAttackEventHandler eventHandler = new CounterAttackEventHandler(this, attacker, damage);

        if (eventHandler.isHoldingSword()) {
            eventHandler.calculateSkillModifier();

            int randomChance = 2000;

            if (player.hasPermission("mcmmo.perks.lucky.swords")) {
                randomChance = (int) (randomChance * 0.75);
            }

            if (Swords.getRandom().nextInt(randomChance) < eventHandler.skillModifier) {
                eventHandler.dealDamage();
                eventHandler.sendAbilityMessages();
            }
        }
    }

    public void serratedStrikes(LivingEntity target, int damage) {
        if (!permissionsInstance.serratedStrikes(player)) {
            return;
        }

        SerratedStrikesEventHandler eventHandler = new SerratedStrikesEventHandler(this, target, damage);

        eventHandler.applyAbilityEffects();
    }

    protected int getSkillLevel() {
        return skillLevel;
    }

    protected Player getPlayer() {
        return player;
    }
}
