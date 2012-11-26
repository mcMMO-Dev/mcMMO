package com.gmail.nossr50.skills.swords;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.nossr50.config.AdvancedConfig;
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
        if(player == null)
            return;

        if(permissionsInstance == null)
            return;

        if (!permissionsInstance.swordsBleed(player)) {
            return;
        }

        if (Combat.shouldBeAffected(player, defender)) {
            BleedEventHandler eventHandler = new BleedEventHandler(this, defender);

            int bleedChanceMax = AdvancedConfig.getInstance().getBleedChanceMax();
            int bleedMaxLevel = AdvancedConfig.getInstance().getBleedMaxBonusLevel();
            int randomChance = 100;

            if (player.hasPermission("mcmmo.perks.lucky.swords")) {
                randomChance = (int) (randomChance * 0.75);
            }

            final float chance = (float) (((double) bleedChanceMax / (double) bleedMaxLevel) * (double) skillLevel);
            if (chance > Swords.getRandom().nextInt(randomChance)) {
                eventHandler.addBleedTicks();
                eventHandler.sendAbilityMessages();
            }
        }
    }

    public void counterAttackChecks(LivingEntity attacker, int damage) {
        if(player == null)
            return;

        if(permissionsInstance == null)
            return;

        if (!permissionsInstance.counterAttack(player)) {
            return;
        }

        CounterAttackEventHandler eventHandler = new CounterAttackEventHandler(this, attacker, damage);

        if (eventHandler.isHoldingSword()) {
            eventHandler.calculateSkillModifier();
            int counterChanceMax = AdvancedConfig.getInstance().getCounterChanceMax();
            int counterMaxLevel = AdvancedConfig.getInstance().getCounterMaxBonusLevel();
            int randomChance = 100;

            if (player.hasPermission("mcmmo.perks.lucky.swords")) {
                randomChance = (int) (randomChance * 0.75);
            }

            final float chance = (float) (((double) counterChanceMax / (double) counterMaxLevel) * (double) skillLevel);
            if (chance > Swords.getRandom().nextInt(randomChance)) {
                eventHandler.dealDamage();
                eventHandler.sendAbilityMessages();
            }
        }
    }

    public void serratedStrikes(LivingEntity target, int damage) {
        if(player == null)
            return;

        if(permissionsInstance == null)
            return;

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
