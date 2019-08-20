package com.gmail.nossr50.skills.taming;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.core.MetadataConstants;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.behaviours.TamingBehaviour;
import com.gmail.nossr50.datatypes.skills.subskills.taming.CallOfTheWildType;
import com.gmail.nossr50.datatypes.skills.subskills.taming.TamingSummon;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.random.RandomChanceSkillStatic;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import com.gmail.nossr50.util.skills.SkillActivationType;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TamingManager extends SkillManager {
    //TODO: Temporary cache, will be changed in 2.2
    private long lastSummonTimeStamp;
    private TamingBehaviour tamingBehaviour;

    private HashMap<CallOfTheWildType, List<TrackedTamingEntity>> playerSummonedEntities;

    public TamingManager(mcMMO pluginRef, McMMOPlayer mcMMOPlayer) {
        super(pluginRef, mcMMOPlayer, PrimarySkillType.TAMING);
        init();
    }

    //TODO: Hacky stuff for 2.1, will be cleaned up in 2.2
    private void init() {
        //Init Behaviour
        tamingBehaviour = new TamingBehaviour(pluginRef);

        //prevents accidentally summoning too many things when holding down left click
        lastSummonTimeStamp = 0L;

        //Init per-player tracking of summoned entities
        initPerPlayerSummonTracking();
    }

    private void initPerPlayerSummonTracking() {
        playerSummonedEntities = new HashMap<>();

        for(CallOfTheWildType callOfTheWildType : CallOfTheWildType.values()) {
            playerSummonedEntities.put(callOfTheWildType, new ArrayList<TrackedTamingEntity>());
        }
    }

    public boolean canUseThickFur() {
        return pluginRef.getRankTools().hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_THICK_FUR)
                && pluginRef.getPermissionTools().isSubSkillEnabled(getPlayer(), SubSkillType.TAMING_THICK_FUR);
    }

    public boolean canUseEnvironmentallyAware() {
        return pluginRef.getRankTools().hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_ENVIRONMENTALLY_AWARE)
                && pluginRef.getPermissionTools().isSubSkillEnabled(getPlayer(), SubSkillType.TAMING_ENVIRONMENTALLY_AWARE);
    }

    public boolean canUseShockProof() {
        return pluginRef.getRankTools().hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_SHOCK_PROOF)
                && pluginRef.getPermissionTools().isSubSkillEnabled(getPlayer(), SubSkillType.TAMING_SHOCK_PROOF);
    }

    public boolean canUseHolyHound() {
        return pluginRef.getRankTools().hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_ENVIRONMENTALLY_AWARE)
                && pluginRef.getPermissionTools().isSubSkillEnabled(getPlayer(), SubSkillType.TAMING_HOLY_HOUND);
    }

    public boolean canUseFastFoodService() {
        return pluginRef.getRankTools().hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_FAST_FOOD_SERVICE)
                && pluginRef.getPermissionTools().isSubSkillEnabled(getPlayer(), SubSkillType.TAMING_FAST_FOOD_SERVICE);
    }

    public boolean canUseSharpenedClaws() {
        return pluginRef.getRankTools().hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_SHARPENED_CLAWS)
                && pluginRef.getPermissionTools().isSubSkillEnabled(getPlayer(), SubSkillType.TAMING_SHARPENED_CLAWS);
    }

    public boolean canUseGore() {
        if(!pluginRef.getRankTools().hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_GORE))
            return false;

        return pluginRef.getPermissionTools().isSubSkillEnabled(getPlayer(), SubSkillType.TAMING_GORE);
    }

    public boolean canUseBeastLore() {
        if(!pluginRef.getRankTools().hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_BEAST_LORE))
            return false;

        return pluginRef.getPermissionTools().isSubSkillEnabled(getPlayer(), SubSkillType.TAMING_BEAST_LORE);
    }

    /**
     * Award XP for taming.
     *
     * @param entity The LivingEntity to award XP for
     */
    public void awardTamingXP(LivingEntity entity) {
        applyXpGain(ExperienceConfig.getInstance().getTamingXP(entity.getType()), XPGainReason.PVE);
    }

    /**
     * Apply the Fast Food Service ability.
     *
     * @param wolf The wolf using the ability
     * @param damage The damage being absorbed by the wolf
     */
    public void fastFoodService(Wolf wolf, double damage) {
        if (!pluginRef.getRandomChanceTools().isActivationSuccessful(SkillActivationType.RANDOM_STATIC_CHANCE, SubSkillType.TAMING_FAST_FOOD_SERVICE, getPlayer())) {
            return;
        }

        double health = wolf.getHealth();
        double maxHealth = wolf.getMaxHealth();

        if (health < maxHealth) {
            double newHealth = health + damage;
            wolf.setHealth(Math.min(newHealth, maxHealth));
        }
    }

    /**
     * Apply the Gore ability.
     *
     * @param target The LivingEntity to apply Gore on
     * @param damage The initial damage
     */
    public double gore(LivingEntity target, double damage) {
        if (!pluginRef.getRandomChanceTools().isActivationSuccessful(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, SubSkillType.TAMING_GORE, getPlayer())) {
            return 0;
        }

        pluginRef.getBleedTimerTask().add(target, getPlayer(), pluginRef.getConfigManager().getConfigTaming().getSubSkills().getGore().getGoreBleedTicks(), 1, 2);

        if (target instanceof Player) {
            pluginRef.getNotificationManager().sendPlayerInformation((Player)target, NotificationType.SUBSKILL_MESSAGE, "Combat.StruckByGore");
        }

        pluginRef.getNotificationManager().sendPlayerInformation(getPlayer(), NotificationType.SUBSKILL_MESSAGE, "Combat.Gore");

        damage = (damage * pluginRef.getConfigManager().getConfigTaming().getSubSkills().getGore().getGoreMofifier()) - damage;
        return damage;
    }

    //TODO: Add tooltips to /taming for this
    public double sharpenedClaws(boolean PVE) {
        if(PVE)
            return pluginRef.getConfigManager().getConfigTaming().getSubSkills().getSharpenedClaws().getBonusDamage().getPVEModifier();
        else
            return pluginRef.getConfigManager().getConfigTaming().getSubSkills().getSharpenedClaws().getBonusDamage().getPVPModifier();
    }

    /**
     * Summon an ocelot to your side.
     */
    public void summonOcelot() {
        if(!pluginRef.getRankTools().hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_CALL_OF_THE_WILD))
            return;

        if (!pluginRef.getPermissionTools().callOfTheWild(getPlayer(), EntityType.OCELOT)) {
            return;
        }

        processCallOfTheWild();
    }

    /**
     * Summon a wolf to your side.
     */
    public void summonWolf() {
        if(!pluginRef.getRankTools().hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_CALL_OF_THE_WILD))
            return;

        if (!pluginRef.getPermissionTools().callOfTheWild(getPlayer(), EntityType.WOLF)) {
            return;
        }

        processCallOfTheWild();
    }

    /**
     * Summon a horse to your side.
     */
    public void summonHorse() {
        if(!pluginRef.getRankTools().hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_CALL_OF_THE_WILD))
            return;

        if (!pluginRef.getPermissionTools().callOfTheWild(getPlayer(), EntityType.HORSE)) {
            return;
        }

        processCallOfTheWild();
    }

    /**
     * Handle the Beast Lore ability.
     *
     * @param target The entity to examine
     */
    public void beastLore(LivingEntity target) {
        Player player = getPlayer();
        Tameable beast = (Tameable) target;

        String message = pluginRef.getLocaleManager().getString("Combat.BeastLore") + " ";

        if (beast.isTamed() && beast.getOwner() != null) {
            message = message.concat(pluginRef.getLocaleManager().getString("Combat.BeastLoreOwner", beast.getOwner().getName()) + " ");
        }

        message = message.concat(pluginRef.getLocaleManager().getString("Combat.BeastLoreHealth", target.getHealth(), target.getMaxHealth()));
        player.sendMessage(message);
    }

    public void processEnvironmentallyAware(Wolf wolf, double damage) {
        if (damage > wolf.getHealth()) {
            return;
        }

        Player owner = getPlayer();

        wolf.teleport(owner);
        pluginRef.getNotificationManager().sendPlayerInformation(owner, NotificationType.SUBSKILL_MESSAGE, "Taming.Listener.Wolf");
    }

    public void pummel(LivingEntity target, Wolf wolf) {
        if(!pluginRef.getRankTools().hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_PUMMEL))
            return;

        if(!pluginRef.getRandomChanceTools().checkRandomChanceExecutionSuccess(new RandomChanceSkillStatic(pluginRef, pluginRef.getDynamicSettingsManager().getSkillPropertiesManager().getStaticChance(SubSkillType.TAMING_PUMMEL), getPlayer(), SubSkillType.TAMING_PUMMEL)))
            return;

        ParticleEffectUtils.playGreaterImpactEffect(target);
        target.setVelocity(wolf.getLocation().getDirection().normalize().multiply(1.5D));

        if (target instanceof Player) {
            Player defender = (Player) target;

            if (pluginRef.getNotificationManager().doesPlayerUseNotifications(defender)) {
                pluginRef.getNotificationManager().sendPlayerInformation(defender, NotificationType.SUBSKILL_MESSAGE, "Taming.SubSkill.Pummel.TargetMessage");
            }
        }
    }

    public void attackTarget(LivingEntity target) {
        if(target instanceof Tameable)
        {
            Tameable tameable = (Tameable) target;
            if(tameable.getOwner() == getPlayer())
            {
                return;
            }
        }
        double range = 5;
        Player player = getPlayer();

        for (Entity entity : player.getNearbyEntities(range, range, range)) {
            if (entity.getType() != EntityType.WOLF) {
                continue;
            }

            Wolf wolf = (Wolf) entity;

            if (!wolf.isTamed() || (wolf.getOwner() != player) || wolf.isSitting()) {
                continue;
            }

            wolf.setTarget(target);
        }
    }


    private void processCallOfTheWild() {
        //Prevent summoning too many things accidentally if a player holds down the button
        if(lastSummonTimeStamp + 150 > System.currentTimeMillis()) {
            return;
        } else {
            lastSummonTimeStamp = System.currentTimeMillis();
        }

        Player player = getPlayer();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        //Check if the item the player is currently holding is a COTW item
        if(isCOTWItem(itemInMainHand)) {
            //Get the summoning type
            CallOfTheWildType callOfTheWildType = pluginRef.getDynamicSettingsManager().getTamingItemManager().getCallType(itemInMainHand.getType());
            TamingSummon tamingSummon = tamingBehaviour.getSummon(callOfTheWildType);

            //Players will pay for the cost if at least one thing was summoned
            int amountSummoned = 0;

            //Check to see if players have the correct amount of the item required to summon
            if(itemInMainHand.getAmount() >= tamingSummon.getItemAmountRequired()) {
                //Initial Spawn location
                Location spawnLocation = Misc.getLocationOffset(player.getLocation(), 1);

                //COTW can summon multiple entities per usage
                for (int i = 0; i < tamingSummon.getEntitiesSummoned(); i++) {

                    if (getAmountCurrentlySummoned(callOfTheWildType) >= tamingSummon.getSummonCap()) {
                        pluginRef.getNotificationManager().sendPlayerInformationChatOnly(player, "Taming.Summon.COTW.Limit",
                                String.valueOf(tamingSummon.getSummonCap()),
                                StringUtils.getCapitalized(callOfTheWildType.toString()));
                        break;
                    }

                    spawnLocation = Misc.getLocationOffset(spawnLocation, 1);
                    spawnCOTWEntity(callOfTheWildType, spawnLocation, tamingSummon.getEntityType());

                    //Inform the player about what they have just done
                    if (tamingSummon.getSummonLifespan() > 0) {
                        pluginRef.getNotificationManager().sendPlayerInformationChatOnly(player, "Taming.Summon.COTW.Success.WithLifespan",
                                StringUtils.getCapitalized(callOfTheWildType.toString()), String.valueOf(tamingSummon.getSummonLifespan()));
                    } else {
                        pluginRef.getNotificationManager().sendPlayerInformationChatOnly(player, "Taming.Summon.COTW.Success.WithoutLifespan", StringUtils.getCapitalized(callOfTheWildType.toString()));
                    }

                    //Send Sound
                    SoundManager.sendSound(player, player.getLocation(), SoundType.ABILITY_ACTIVATED_GENERIC);

                    amountSummoned++;
                }

                //Remove items from the player if they had at least one entity summoned successfully
                if(amountSummoned >= 1) {
                    //Remove the items used to summon
                    int itemAmountAfterPayingCost = itemInMainHand.getAmount() - tamingSummon.getItemAmountRequired();
                    itemInMainHand.setAmount(itemAmountAfterPayingCost);
                    player.updateInventory();
                }

            } else {
                //Player did not have enough of the item in their main hand
                int difference = tamingSummon.getItemAmountRequired() - itemInMainHand.getAmount();
                pluginRef.getNotificationManager().sendPlayerInformationChatOnly(player, "Taming.Summon.COTW.NeedMoreItems", String.valueOf(difference), StringUtils.getPrettyItemString(itemInMainHand.getType()));
            }
        }
    }

    private void spawnCOTWEntity(CallOfTheWildType callOfTheWildType, Location spawnLocation, EntityType entityType) {
        switch(callOfTheWildType) {
            case CAT:
                //Entity type is needed for cats because in 1.13 and below we spawn ocelots, in 1.14 and above we spawn cats
                spawnCat(spawnLocation, entityType);
                break;
            case HORSE:
                spawnHorse(spawnLocation);
                break;
            case WOLF:
                spawnWolf(spawnLocation);
                break;
        }
    }

    private void spawnWolf(Location spawnLocation) {
        LivingEntity callOfWildEntity = (LivingEntity) getPlayer().getWorld().spawnEntity(spawnLocation, EntityType.WOLF);

        //This is used to prevent XP gains for damaging this entity
        applyMetaDataToCOTWEntity(callOfWildEntity);

        setBaseCOTWEntityProperties(callOfWildEntity);

        ((Wolf) callOfWildEntity).setAdult();
        addToTracker(callOfWildEntity, CallOfTheWildType.WOLF);

        //Setup wolf stats
        callOfWildEntity.setMaxHealth(20.0);
        callOfWildEntity.setHealth(callOfWildEntity.getMaxHealth());

        callOfWildEntity.setCustomName(pluginRef.getLocaleManager().getString("Taming.Summon.Name.Format", getPlayer().getName(), StringUtils.getPrettyEntityTypeString(EntityType.WOLF)));
    }

    private void spawnCat(Location spawnLocation, EntityType entityType) {
        LivingEntity callOfWildEntity = (LivingEntity) getPlayer().getWorld().spawnEntity(spawnLocation, entityType);

        //This is used to prevent XP gains for damaging this entity
        applyMetaDataToCOTWEntity(callOfWildEntity);

        setBaseCOTWEntityProperties(callOfWildEntity);

        addToTracker(callOfWildEntity, CallOfTheWildType.CAT);

        //Randomize the cat
        if(callOfWildEntity instanceof Ocelot) {
            int numberOfTypes = Ocelot.Type.values().length;
            ((Ocelot) callOfWildEntity).setCatType(Ocelot.Type.values()[Misc.getRandom().nextInt(numberOfTypes)]);
            ((Ocelot) callOfWildEntity).setAdult();
        } else if(callOfWildEntity instanceof Cat) {
            int numberOfTypes = Cat.Type.values().length;
            ((Cat) callOfWildEntity).setCatType(Cat.Type.values()[Misc.getRandom().nextInt(numberOfTypes)]);
            ((Cat) callOfWildEntity).setAdult();
        }

        callOfWildEntity.setCustomName(pluginRef.getLocaleManager().getString("Taming.Summon.Name.Format", getPlayer().getName(), StringUtils.getPrettyEntityTypeString(entityType)));

        //Particle effect
        ParticleEffectUtils.playCallOfTheWildEffect(callOfWildEntity);
    }

    private void spawnHorse(Location spawnLocation) {
        LivingEntity callOfWildEntity = (LivingEntity) getPlayer().getWorld().spawnEntity(spawnLocation, EntityType.HORSE);
        applyMetaDataToCOTWEntity(callOfWildEntity);

        setBaseCOTWEntityProperties(callOfWildEntity);

        addToTracker(callOfWildEntity, CallOfTheWildType.HORSE);

        //Randomize Horse
        Horse horse = (Horse) callOfWildEntity;

        callOfWildEntity.setMaxHealth(15.0 + (Misc.getRandom().nextDouble() * 15));
        callOfWildEntity.setHealth(callOfWildEntity.getMaxHealth());
        horse.setColor(Horse.Color.values()[Misc.getRandom().nextInt(Horse.Color.values().length)]);
        horse.setStyle(Horse.Style.values()[Misc.getRandom().nextInt(Horse.Style.values().length)]);
        horse.setJumpStrength(Math.max(pluginRef.getConfigManager().getConfigTaming().getMinHorseJumpStrength(),
                Math.min(Math.min(Misc.getRandom().nextDouble(), Misc.getRandom().nextDouble()) * 2, pluginRef.getConfigManager().getConfigTaming().getMaxHorseJumpStrength())));
        horse.setAdult();

        //TODO: setSpeed, once available

        callOfWildEntity.setCustomName(pluginRef.getLocaleManager().getString("Taming.Summon.Name.Format", getPlayer().getName(), StringUtils.getPrettyEntityTypeString(EntityType.HORSE)));

        //Particle effect
        ParticleEffectUtils.playCallOfTheWildEffect(callOfWildEntity);
    }

    private void setBaseCOTWEntityProperties(LivingEntity callOfWildEntity) {
        ((Tameable) callOfWildEntity).setOwner(getPlayer());
        callOfWildEntity.setRemoveWhenFarAway(false);
    }

    private void applyMetaDataToCOTWEntity(LivingEntity callOfWildEntity) {
        //This is used to prevent XP gains for damaging this entity
        callOfWildEntity.setMetadata(MetadataConstants.UNNATURAL_MOB_METAKEY, MetadataConstants.metadataValue);

        //This helps identify the entity as being summoned by COTW
        callOfWildEntity.setMetadata(MetadataConstants.COTW_TEMPORARY_SUMMON, MetadataConstants.metadataValue);
    }

    /**
     * Whether or not the itemstack is used for COTW
     * @param itemStack target ItemStack
     * @return true if it is used for any COTW
     */
    public boolean isCOTWItem(ItemStack itemStack) {
        return pluginRef.getDynamicSettingsManager().getTamingItemManager().isCOTWItem(itemStack.getType());
    }

    //TODO: The way this tracker was written is garbo, I should just rewrite it, I'll save that for a future update
    private int getAmountCurrentlySummoned(CallOfTheWildType callOfTheWildType) {
        //The tracker is unreliable so validate its contents first
        recalibrateTracker();

        return playerSummonedEntities.get(callOfTheWildType).size();
    }

    //TODO: The way this tracker was written is garbo, I should just rewrite it, I'll save that for a future update
    private void addToTracker(LivingEntity livingEntity, CallOfTheWildType callOfTheWildType) {
        TrackedTamingEntity trackedEntity = new TrackedTamingEntity(pluginRef, livingEntity, callOfTheWildType, this);

        playerSummonedEntities.get(callOfTheWildType).add(trackedEntity);
    }

    //TODO: The way this tracker was written is garbo, I should just rewrite it, I'll save that for a future update
    public List<TrackedTamingEntity> getTrackedEntities(CallOfTheWildType callOfTheWildType) {
        return playerSummonedEntities.get(callOfTheWildType);
    }

    //TODO: The way this tracker was written is garbo, I should just rewrite it, I'll save that for a future update
    public void removeFromTracker(TrackedTamingEntity trackedEntity) {
        if(playerSummonedEntities.get(trackedEntity.getCallOfTheWildType()).contains(trackedEntity))
            playerSummonedEntities.get(trackedEntity.getCallOfTheWildType()).remove(trackedEntity);

        pluginRef.getNotificationManager().sendPlayerInformationChatOnly(getPlayer(), "Taming.Summon.COTW.TimeExpired", StringUtils.getPrettyEntityTypeString(trackedEntity.getLivingEntity().getType()));
    }

    /**
     * Builds a new tracked list by determining which tracked things are still valid
     */
    //TODO: The way this tracker was written is garbo, I should just rewrite it, I'll save that for a future update
    private void recalibrateTracker() {
        for(CallOfTheWildType callOfTheWildType : CallOfTheWildType.values()) {
            ArrayList<TrackedTamingEntity> validEntities = getValidTrackedEntities(callOfTheWildType);
            playerSummonedEntities.put(callOfTheWildType, validEntities); //Replace the old list with the new list
        }
    }

    //TODO: The way this tracker was written is garbo, I should just rewrite it, I'll save that for a future update
    private ArrayList<TrackedTamingEntity> getValidTrackedEntities(CallOfTheWildType callOfTheWildType) {
        ArrayList<TrackedTamingEntity> validTrackedEntities = new ArrayList<>();

        for(TrackedTamingEntity trackedTamingEntity : getTrackedEntities(callOfTheWildType)) {
            LivingEntity livingEntity = trackedTamingEntity.getLivingEntity();

            //Remove from existence
            if(livingEntity != null && livingEntity.isValid()) {
                validTrackedEntities.add(trackedTamingEntity);
            }
        }

        return validTrackedEntities;
    }

    /**
     * Remove all tracked entities from existence if they currently exist
     * Clear the tracked entity lists afterwards
     */
    //TODO: The way this tracker was written is garbo, I should just rewrite it, I'll save that for a future update
    public void cleanupAllSummons() {
        for(List<TrackedTamingEntity> trackedTamingEntities : playerSummonedEntities.values()) {
            for(TrackedTamingEntity trackedTamingEntity : trackedTamingEntities) {
                LivingEntity livingEntity = trackedTamingEntity.getLivingEntity();

                //Remove from existence
                if(livingEntity != null && livingEntity.isValid()) {
                    livingEntity.setHealth(0);
                    livingEntity.remove();
                }
            }

            //Clear the list
            trackedTamingEntities.clear();
        }
    }
}
