package com.gmail.nossr50.skills.taming;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.skills.SkillManager;
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
    public void awardTamingXP(EntityTameEvent event) {
        if (mcMMO.placeStore.isSpawnedMob(event.getEntity())) {
            return;
        }

        switch (event.getEntityType()) {
        case WOLF:
            mcMMOPlayer.addXp(SkillType.TAMING, Taming.wolfXp);
            break;

        case OCELOT:
            mcMMOPlayer.addXp(SkillType.TAMING, Taming.ocelotXp);
            break;

        default:
            break;
        }
    }

    /**
     * Apply the Fast Food Service ability.
     *
     * @param wolf The wolf using the ability
     * @param damage The damage being absorbed by the wolf
     */
    public void fastFoodService(Wolf wolf, int damage) {
        if (Misc.getRandom().nextInt(activationChance) < Taming.fastFoodServiceActivationChance) {
            FastFoodServiceEventHandler eventHandler = new FastFoodServiceEventHandler(wolf);

            eventHandler.modifyHealth(damage);
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

        float chance = (float) ((Taming.goreMaxChance / Taming.goreMaxBonusLevel) * skillLevel);
        if (chance > Taming.goreMaxChance) chance = (float) Taming.goreMaxChance;

        if (chance > Misc.getRandom().nextInt(activationChance)) {
            eventHandler.modifyEventDamage();
            eventHandler.applyBleed();
            eventHandler.sendAbilityMessage();
        }
    }

    /**
     * Prevent damage to wolves based on various skills.
     *
     * @param event The event to modify
     */
    public void preventDamage(EntityDamageEvent event) {
        DamageCause cause = event.getCause();

        switch (cause) {
        case CONTACT:
        case LAVA:
        case FIRE:
        case FALL:
            environmentallyAware(event, cause);
            break;

        case ENTITY_ATTACK:
        case FIRE_TICK:
        case PROJECTILE:
            thickFur(event, cause);
            break;

        case BLOCK_EXPLOSION:
        case ENTITY_EXPLOSION:
        case LIGHTNING:
            shockProof(event);
            break;

        default:
            break;
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

    /**
     * Handle the Environmentally Aware ability.
     *
     * @param event The event to modify
     * @param cause The damage cause of the event
     */
    private void environmentallyAware(EntityDamageEvent event, DamageCause cause) {
        if (skillLevel >= Taming.environmentallyAwareUnlockLevel && Permissions.environmentallyAware(mcMMOPlayer.getPlayer())) {
            EnvironmentallyAwareEventHandler eventHandler = new EnvironmentallyAwareEventHandler(this, event);

            switch (cause) {
            case CONTACT:
            case FIRE:
            case LAVA:
                eventHandler.teleportWolf();
                eventHandler.sendAbilityMessage();
                break;

            case FALL:
                eventHandler.cancelEvent();
                break;

            default:
                break;
            }
        }
    }

    /**
     * Handle the Thick Fur ability.
     *
     * @param event The event to modify
     * @param cause The damage cause of the event
     */
    private void thickFur(EntityDamageEvent event, DamageCause cause) {
        if (skillLevel >= Taming.thickFurUnlockLevel && Permissions.thickFur(mcMMOPlayer.getPlayer())) {
            ThickFurEventHandler eventHandler = new ThickFurEventHandler(event, cause);
            eventHandler.modifyEventDamage();
        }
    }

    /**
     * Handle the Shock Proof ability.
     *
     * @param event The event to modify
     */
    private void shockProof(EntityDamageEvent event) {
        if (skillLevel >= Taming.shockProofUnlockLevel && Permissions.shockProof(mcMMOPlayer.getPlayer())) {
            ShockProofEventHandler eventHandler = new ShockProofEventHandler(event);
            eventHandler.modifyEventDamage();
        }
    }
}
