package com.gmail.nossr50.skills.taming;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.runnables.skills.BleedTimerTask;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.skills.SkillUtils;

public class TamingManager extends SkillManager {
    public TamingManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, SkillType.TAMING);
    }

    public boolean canUseThickFur() {
        return getSkillLevel() >= Taming.thickFurUnlockLevel && Permissions.thickFur(getPlayer());
    }

    public boolean canUseEnvironmentallyAware() {
        return getSkillLevel() >= Taming.environmentallyAwareUnlockLevel && Permissions.environmentallyAware(getPlayer());
    }

    public boolean canUseShockProof() {
        return getSkillLevel() >= Taming.shockProofUnlockLevel && Permissions.shockProof(getPlayer());
    }

    public boolean canUseHolyHound() {
        return getSkillLevel() >= Taming.holyHoundUnlockLevel && Permissions.holyHound(getPlayer());
    }

    public boolean canUseFastFoodService() {
        return getSkillLevel() >= Taming.fastFoodServiceUnlockLevel && Permissions.fastFoodService(getPlayer());
    }

    public boolean canUseSharpenedClaws() {
        return getSkillLevel() >= Taming.sharpenedClawsUnlockLevel && Permissions.sharpenedClaws(getPlayer());
    }

    public boolean canUseGore() {
        return Permissions.gore(getPlayer());
    }

    public boolean canUseBeastLore() {
        return Permissions.beastLore(getPlayer());
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
        if (Taming.fastFoodServiceActivationChance > Misc.getRandom().nextInt(getActivationChance())) {

            int health = wolf.getHealth();
            int maxHealth = wolf.getMaxHealth();

            if (health < maxHealth) {
                int newHealth = health + damage;
                wolf.setHealth(Math.min(newHealth, maxHealth));
            }
        }
    }

    /**
     * Apply the Gore ability.
     *
     * @param event The event to modify
     */
    public int gore(LivingEntity target, int damage) {
        if (SkillUtils.activationSuccessful(getSkillLevel(), getActivationChance(), Taming.goreMaxChance, Taming.goreMaxBonusLevel)) {
            BleedTimerTask.add(target, Taming.goreBleedTicks);

            if (target instanceof Player) {
                ((Player) target).sendMessage(LocaleLoader.getString("Combat.StruckByGore"));
            }

            getPlayer().sendMessage(LocaleLoader.getString("Combat.Gore"));
            return damage * Taming.goreModifier;
        }

        return damage;
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
     * @param target The entity to examine
     */
    public void beastLore(LivingEntity target) {
        Player player = getPlayer();
        Tameable beast = (Tameable) target;

        String message = LocaleLoader.getString("Combat.BeastLore") + " ";

        if (beast.isTamed()) {
            message = message.concat(LocaleLoader.getString("Combat.BeastLoreOwner", beast.getOwner().getName()) + " ");
        }

        message = message.concat(LocaleLoader.getString("Combat.BeastLoreHealth", target.getHealth(), target.getMaxHealth()));
        player.sendMessage(message);
    }

    public void processEnvironmentallyAware(Wolf wolf, int damage) {
        if (damage > wolf.getHealth()) {
            return;
        }

        Player owner = getPlayer();

        wolf.teleport(owner);
        owner.sendMessage(LocaleLoader.getString("Taming.Listener.Wolf"));
    }

    /**
     * Handle the Call of the Wild ability.
     *
     * @param type The type of entity to summon.
     * @param summonAmount The amount of material needed to summon the entity
     */
    private void callOfTheWild(EntityType type, int summonAmount) {
        Player player = getPlayer();

        ItemStack heldItem = player.getItemInHand();
        int heldItemAmount = heldItem.getAmount();

        if (heldItemAmount < summonAmount) {
            player.sendMessage(LocaleLoader.getString("Skills.NeedMore", StringUtils.getPrettyItemString(heldItem.getTypeId())));
            return;
        }

        for (Entity entity : player.getNearbyEntities(40, 40, 40)) {
            if (entity.getType() == type) {
                player.sendMessage(Taming.getCallOfTheWildFailureMessage(type));
                return;
            }
        }

        LivingEntity entity = (LivingEntity) player.getWorld().spawnEntity(player.getLocation(), type);

        entity.setMetadata(mcMMO.entityMetadataKey, mcMMO.metadataValue);
        ((Tameable) entity).setOwner(player);

        if (type == EntityType.OCELOT) {
            ((Ocelot) entity).setCatType(Ocelot.Type.getType(1 + Misc.getRandom().nextInt(3)));
        }
        else {
            entity.setHealth(entity.getMaxHealth());
        }

        player.setItemInHand(new ItemStack(heldItem.getType(), heldItemAmount - summonAmount));
        player.sendMessage(LocaleLoader.getString("Taming.Summon.Complete"));
    }
}
