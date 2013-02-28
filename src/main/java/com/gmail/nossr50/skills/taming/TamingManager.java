package com.gmail.nossr50.skills.taming;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;

public class TamingManager extends SkillManager {
    public TamingManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, SkillType.TAMING);
    }

    /**
     * Award XP for taming.
     *
     * @param event The event to award XP for
     */
    public void awardTamingXP(LivingEntity entity) {
        switch (entity.getType()) {
        case WOLF:
            applyXpGain(Taming.wolfXp);
            return;

        case OCELOT:
            applyXpGain(Taming.ocelotXp);
            return;

        default:
            return;
        }
    }

    /**
     * Apply the Fast Food Service ability.
     *
     * @param wolf The wolf using the ability
     * @param damage The damage being absorbed by the wolf
     */
    public void fastFoodService(Wolf wolf, int damage) {
        if (SkillTools.activationSuccessful(getPlayer(), skill, Taming.fastFoodServiceActivationChance)) {

            int health = wolf.getHealth();
            int maxHealth = wolf.getMaxHealth();

            if (health < maxHealth) {
                int newHealth = health + damage;
                wolf.setHealth(Math.min(newHealth, maxHealth));
            }
        }
    }

    /**
     * Apply the Sharpened Claws ability.
     *
     * @param event The event to modify
     */
    public void sharpenedClaws(EntityDamageEvent event) {
        SharpenedClawsEventHandler eventHandler = new SharpenedClawsEventHandler(event);
        eventHandler.modifyEventDamage();
    }

    /**
     * Apply the Gore ability.
     *
     * @param event The event to modify
     */
    public void gore(EntityDamageEvent event) {
        GoreEventHandler eventHandler = new GoreEventHandler(this, event);

        float chance = (float) ((Taming.goreMaxChance / Taming.goreMaxBonusLevel) * getSkillLevel());
        if (chance > Taming.goreMaxChance) chance = (float) Taming.goreMaxChance;

        if (chance > Misc.getRandom().nextInt(activationChance)) {
            eventHandler.modifyEventDamage();
            eventHandler.applyBleed();
            eventHandler.sendAbilityMessage();
        }
    }

    /**
     * Summon an ocelot to your side.
     */
    public void summonOcelot() {
        callOfTheWild(EntityType.OCELOT, Config.getInstance().getTamingCOTWOcelotCost());
    }

    /**
     * Summon a wolf to your side.
     */
    public void summonWolf() {
        callOfTheWild(EntityType.WOLF, Config.getInstance().getTamingCOTWWolfCost());
    }

    /**
     * Handle the Beast Lore ability.
     *
     * @param livingEntity The entity to examine
     */
    public void beastLore(LivingEntity livingEntity) {
        BeastLoreEventHandler eventHandler = new BeastLoreEventHandler(mcMMOPlayer.getPlayer(), livingEntity);
        eventHandler.sendInspectMessage();
    }

    /**
     * Handle the Call of the Wild ability.
     *
     * @param type The type of entity to summon.
     * @param summonAmount The amount of material needed to summon the entity
     */
    private void callOfTheWild(EntityType type, int summonAmount) {
        if (!Permissions.callOfTheWild(mcMMOPlayer.getPlayer())) {
            return;
        }

        CallOfTheWildEventHandler eventHandler = new CallOfTheWildEventHandler(mcMMOPlayer.getPlayer(), type, summonAmount);

        ItemStack inHand = eventHandler.inHand;
        int inHandAmount = inHand.getAmount();

        if (inHandAmount < summonAmount) {
            eventHandler.sendInsufficientAmountMessage();
            return;
        }

        if (eventHandler.nearbyEntityExists()) {
            eventHandler.sendFailureMessage();
        }
        else {
            eventHandler.spawnCreature();
            eventHandler.processResourceCost();
            eventHandler.sendSuccessMessage();
        }
    }
}
