package com.gmail.nossr50.skills.taming;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;

public class TamingManager extends SkillManager {
    private Config configInstance;

    public TamingManager (Player player) {
        super(player, SkillType.TAMING);
        this.configInstance = Config.getInstance();
    }

    /**
     * Apply the Fast Food Service ability.
     *
     * @param wolf The wolf using the ability
     * @param damage The damage being absorbed by the wolf
     */
    public void fastFoodService(Wolf wolf, int damage) {
        if (player == null)
            return;

        if (!Permissions.fastFoodService(player)) {
            return;
        }

        if (skillLevel >= Taming.FAST_FOOD_SERVICE_ACTIVATION_LEVEL) {
            int randomChance = 100;

            if (Permissions.luckyTaming(player)) {
                randomChance = (int) (randomChance * 0.75);
            }

            if (Misc.getRandom().nextInt(randomChance) < Taming.FAST_FOOD_SERVICE_ACTIVATION_CHANCE) {
                FastFoodServiceEventHandler eventHandler = new FastFoodServiceEventHandler(wolf);

                eventHandler.modifyHealth(damage);
            }
        }
    }

    /**
     * Apply the Sharpened Claws ability.
     *
     * @param event The event to modify
     */
    public void sharpenedClaws(EntityDamageEvent event) {
        if (player == null)
            return;

        if (!Permissions.sharpenedClaws(player)) {
            return;
        }

        if (skillLevel >= Taming.SHARPENED_CLAWS_ACTIVATION_LEVEL) {
            SharpenedClawsEventHandler eventHandler = new SharpenedClawsEventHandler(event);

            eventHandler.modifyEventDamage();
        }
    }

    /**
     * Apply the Gore ability.
     *
     * @param event The event to modify
     */
    public void gore(EntityDamageEvent event) {
        if (player == null)
            return;

        if (!Permissions.gore(player)) {
            return;
        }

        GoreEventHandler eventHandler = new GoreEventHandler(this, event);

        int randomChance = 100;

        if (Permissions.luckyTaming(player)) {
            randomChance = (int) (randomChance * 0.75);
        }

        float chance = (float) (((double) Taming.GORE_CHANCE_MAX / (double) Taming.GORE_MAX_BONUS_LEVEL) * skillLevel);
        if (chance > Taming.GORE_CHANCE_MAX) chance = Taming.GORE_CHANCE_MAX;

        if (chance > Misc.getRandom().nextInt(randomChance)) {
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
        callOfTheWild(EntityType.OCELOT, configInstance.getTamingCOTWOcelotCost());
    }

    /**
     * Summon a wolf to your side.
     */
    public void summonWolf() {
        callOfTheWild(EntityType.WOLF, configInstance.getTamingCOTWWolfCost());
    }

    /**
     * Handle the Beast Lore ability.
     *
     * @param livingEntity The entity to examine
     */
    public void beastLore(LivingEntity livingEntity) {
        if (player == null)
            return;

        if (!Permissions.beastLore(player)) {
            return;
        }

        BeastLoreEventHandler eventHandler = new BeastLoreEventHandler(player, livingEntity);

        eventHandler.sendInspectMessage();
    }

    /**
     * Handle the Call of the Wild ability.
     *
     * @param type The type of entity to summon.
     * @param summonAmount The amount of material needed to summon the entity
     */
    private void callOfTheWild(EntityType type, int summonAmount) {
        if (player == null)
            return;

        if (!Permissions.callOfTheWild(player)) {
            return;
        }

        CallOfTheWildEventHandler eventHandler = new CallOfTheWildEventHandler(player, type, summonAmount);

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
        if (player == null)
            return;

        if (!Permissions.environmentallyAware(player)) {
            return;
        }

        if (skillLevel >= Taming.ENVIRONMENTALLY_AWARE_ACTIVATION_LEVEL) {
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
        if (player == null)
            return;

        if (!Permissions.thickFur(player)) {
            return;
        }

        if (skillLevel >= Taming.THICK_FUR_ACTIVATION_LEVEL) {
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
        if (player == null)
            return;

        if (!Permissions.shockProof(player)) {
            return;
        }

        if (skillLevel >= Taming.SHOCK_PROOF_ACTIVATION_LEVEL) {
            ShockProofEventHandler eventHandler = new ShockProofEventHandler(event);

            eventHandler.modifyEventDamage();
        }
    }
}
