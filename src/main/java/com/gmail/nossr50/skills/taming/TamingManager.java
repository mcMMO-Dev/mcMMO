package com.gmail.nossr50.skills.taming;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.taming.CallOfTheWildType;
import com.gmail.nossr50.datatypes.skills.subskills.taming.TamingSummon;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.skills.BleedTimerTask;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.random.RandomChanceSkillStatic;
import com.gmail.nossr50.util.random.RandomChanceUtil;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillActivationType;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TamingManager extends SkillManager {
    //TODO: Temporary static cache, will be changed in 2.2
    private static HashMap<Material, CallOfTheWildType> summoningItems;
    private static HashMap<CallOfTheWildType, TamingSummon> cotwSummonDataProperties;
    private long lastSummonTimeStamp;

    private HashMap<CallOfTheWildType, List<TrackedTamingEntity>> playerSummonedEntities;

    public TamingManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, PrimarySkillType.TAMING);
        init();
    }

    //TODO: Hacky stuff for 2.1, will be cleaned up in 2.2
    private void init() {
        //prevents accidentally summoning too many things when holding down left click
        lastSummonTimeStamp = 0L;

        //Init per-player tracking of summoned entities
        initPerPlayerSummonTracking();

        //Hacky stuff used as a band-aid
        initStaticCaches();
    }

    private void initPerPlayerSummonTracking() {
        playerSummonedEntities = new HashMap<>();

        for(CallOfTheWildType callOfTheWildType : CallOfTheWildType.values()) {
            playerSummonedEntities.put(callOfTheWildType, new ArrayList<TrackedTamingEntity>());
        }
    }

    private void initStaticCaches() {
        //TODO: Temporary static cache, will be changed in 2.2
        //This is shared between instances of TamingManager
        if(summoningItems == null) {
            summoningItems = new HashMap<>();

            summoningItems.put(Config.getInstance().getTamingCOTWMaterial(CallOfTheWildType.CAT.getConfigEntityTypeEntry()), CallOfTheWildType.CAT);
            summoningItems.put(Config.getInstance().getTamingCOTWMaterial(CallOfTheWildType.WOLF.getConfigEntityTypeEntry()), CallOfTheWildType.WOLF);
            summoningItems.put(Config.getInstance().getTamingCOTWMaterial(CallOfTheWildType.HORSE.getConfigEntityTypeEntry()), CallOfTheWildType.HORSE);
        }

        //TODO: Temporary static cache, will be changed in 2.2
        //This is shared between instances of TamingManager
        if(cotwSummonDataProperties == null) {
            cotwSummonDataProperties = new HashMap<>();

            for(CallOfTheWildType callOfTheWildType : CallOfTheWildType.values()) {
                Material itemSummonMaterial = Config.getInstance().getTamingCOTWMaterial(callOfTheWildType.getConfigEntityTypeEntry());
                int itemAmountRequired = Config.getInstance().getTamingCOTWCost(callOfTheWildType.getConfigEntityTypeEntry());
                int entitiesSummonedPerCOTW = Config.getInstance().getTamingCOTWAmount(callOfTheWildType.getConfigEntityTypeEntry());
                int summonLifespanSeconds = Config.getInstance().getTamingCOTWLength(callOfTheWildType.getConfigEntityTypeEntry());
                int perPlayerMaxAmount = Config.getInstance().getTamingCOTWMaxAmount(callOfTheWildType.getConfigEntityTypeEntry());

                TamingSummon tamingSummon = new TamingSummon(callOfTheWildType, itemSummonMaterial, itemAmountRequired, entitiesSummonedPerCOTW, summonLifespanSeconds, perPlayerMaxAmount);
                cotwSummonDataProperties.put(callOfTheWildType, tamingSummon);
            }
        }
    }

    public boolean canUseThickFur() {
        return RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_THICK_FUR)
                && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.TAMING_THICK_FUR);
    }

    public boolean canUseEnvironmentallyAware() {
        return RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_ENVIRONMENTALLY_AWARE)
                && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.TAMING_ENVIRONMENTALLY_AWARE);
    }

    public boolean canUseShockProof() {
        return RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_SHOCK_PROOF)
                && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.TAMING_SHOCK_PROOF);
    }

    public boolean canUseHolyHound() {
        return RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_ENVIRONMENTALLY_AWARE)
                && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.TAMING_HOLY_HOUND);
    }

    public boolean canUseFastFoodService() {
        return RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_FAST_FOOD_SERVICE)
                && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.TAMING_FAST_FOOD_SERVICE);
    }

    public boolean canUseSharpenedClaws() {
        return RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_SHARPENED_CLAWS)
                && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.TAMING_SHARPENED_CLAWS);
    }

    public boolean canUseGore() {
        if(!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_GORE))
            return false;

        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.TAMING_GORE);
    }

    public boolean canUseBeastLore() {
        if(!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_BEAST_LORE))
            return false;

        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.TAMING_BEAST_LORE);
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
        if (!RandomChanceUtil.isActivationSuccessful(SkillActivationType.RANDOM_STATIC_CHANCE, SubSkillType.TAMING_FAST_FOOD_SERVICE, getPlayer())) {
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
        if (!RandomChanceUtil.isActivationSuccessful(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, SubSkillType.TAMING_GORE, getPlayer())) {
            return 0;
        }

        BleedTimerTask.add(target, getPlayer(), Taming.goreBleedTicks, 1, 2);

        if (target instanceof Player) {
            NotificationManager.sendPlayerInformation((Player)target, NotificationType.SUBSKILL_MESSAGE, "Combat.StruckByGore");
        }

        NotificationManager.sendPlayerInformation(getPlayer(), NotificationType.SUBSKILL_MESSAGE, "Combat.Gore");

        damage = (damage * Taming.goreModifier) - damage;
        return damage;
    }

    public double sharpenedClaws() {
        return Taming.sharpenedClawsBonusDamage;
    }

    /**
     * Summon an ocelot to your side.
     */
    public void summonOcelot() {
        if(!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_CALL_OF_THE_WILD))
            return;

        if (!Permissions.callOfTheWild(getPlayer(), EntityType.OCELOT)) {
            return;
        }

        processCallOfTheWild();
    }

    /**
     * Summon a wolf to your side.
     */
    public void summonWolf() {
        if(!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_CALL_OF_THE_WILD))
            return;

        if (!Permissions.callOfTheWild(getPlayer(), EntityType.WOLF)) {
            return;
        }

        processCallOfTheWild();
    }

    /**
     * Summon a horse to your side.
     */
    public void summonHorse() {
        if(!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_CALL_OF_THE_WILD))
            return;

        if (!Permissions.callOfTheWild(getPlayer(), EntityType.HORSE)) {
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

        String message = LocaleLoader.getString("Combat.BeastLore") + " ";

        if (beast.isTamed() && beast.getOwner() != null) {
            message = message.concat(LocaleLoader.getString("Combat.BeastLoreOwner", beast.getOwner().getName()) + " ");
        }

        message = message.concat(LocaleLoader.getString("Combat.BeastLoreHealth", target.getHealth(), target.getMaxHealth()));

        if (beast instanceof Horse) {
            Horse horse = (Horse) beast;
            double jumpStrength = horse.getAttribute(Attribute.HORSE_JUMP_STRENGTH).getValue();
            // Taken from https://minecraft.gamepedia.com/Horse#Jump_strength
            jumpStrength = -0.1817584952 * Math.pow(jumpStrength, 3) + 3.689713992 * Math.pow(jumpStrength, 2) + 2.128599134 * jumpStrength - 0.343930367;
            message = message.concat("\n" + LocaleLoader.getString("Combat.BeastLoreHorseSpeed", horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue() * 43))
                    .concat("\n" + LocaleLoader.getString("Combat.BeastLoreHorseJumpStrength", jumpStrength));
        }

        player.sendMessage(message);
    }

    public void processEnvironmentallyAware(Wolf wolf, double damage) {
        if (damage > wolf.getHealth()) {
            return;
        }

        Player owner = getPlayer();

        wolf.teleport(owner);
        NotificationManager.sendPlayerInformation(owner, NotificationType.SUBSKILL_MESSAGE, "Taming.Listener.Wolf");
    }

    public void pummel(LivingEntity target, Wolf wolf) {
        if(!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_PUMMEL))
            return;

        if(!RandomChanceUtil.checkRandomChanceExecutionSuccess(new RandomChanceSkillStatic(AdvancedConfig.getInstance().getPummelChance(), getPlayer(), SubSkillType.TAMING_PUMMEL)))
            return;

        ParticleEffectUtils.playGreaterImpactEffect(target);
        target.setVelocity(wolf.getLocation().getDirection().normalize().multiply(1.5D));

        if (target instanceof Player) {
            Player defender = (Player) target;

            if (NotificationManager.doesPlayerUseNotifications(defender)) {
                NotificationManager.sendPlayerInformation(defender, NotificationType.SUBSKILL_MESSAGE, "Taming.SubSkill.Pummel.TargetMessage");
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
            CallOfTheWildType callOfTheWildType = summoningItems.get(itemInMainHand.getType());
            TamingSummon tamingSummon = cotwSummonDataProperties.get(callOfTheWildType);

            //Players will pay for the cost if at least one thing was summoned
            int amountSummoned = 0;

            //Check to see if players have the correct amount of the item required to summon
            if(itemInMainHand.getAmount() >= tamingSummon.getItemAmountRequired()) {
                //Initial Spawn location
                Location spawnLocation = Misc.getLocationOffset(player.getLocation(), 1);

                //COTW can summon multiple entities per usage
                for (int i = 0; i < tamingSummon.getEntitiesSummoned(); i++) {

                    if (getAmountCurrentlySummoned(callOfTheWildType) >= tamingSummon.getSummonCap()) {
                        NotificationManager.sendPlayerInformationChatOnly(player, "Taming.Summon.COTW.Limit",
                                String.valueOf(tamingSummon.getSummonCap()),
                                StringUtils.getCapitalized(callOfTheWildType.toString()));
                        break;
                    }

                    spawnLocation = Misc.getLocationOffset(spawnLocation, 1);
                    spawnCOTWEntity(callOfTheWildType, spawnLocation, tamingSummon.getEntityType());

                    //Inform the player about what they have just done
                    if (tamingSummon.getSummonLifespan() > 0) {
                        NotificationManager.sendPlayerInformationChatOnly(player, "Taming.Summon.COTW.Success.WithLifespan",
                                StringUtils.getCapitalized(callOfTheWildType.toString()), String.valueOf(tamingSummon.getSummonLifespan()));
                    } else {
                        NotificationManager.sendPlayerInformationChatOnly(player, "Taming.Summon.COTW.Success.WithoutLifespan", StringUtils.getCapitalized(callOfTheWildType.toString()));
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
                NotificationManager.sendPlayerInformationChatOnly(player, "Taming.Summon.COTW.NeedMoreItems", String.valueOf(difference), StringUtils.getPrettyItemString(itemInMainHand.getType()));
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

        callOfWildEntity.setCustomName(LocaleLoader.getString("Taming.Summon.Name.Format", getPlayer().getName(), StringUtils.getPrettyEntityTypeString(EntityType.WOLF)));
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

        callOfWildEntity.setCustomName(LocaleLoader.getString("Taming.Summon.Name.Format", getPlayer().getName(), StringUtils.getPrettyEntityTypeString(entityType)));

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
        horse.setJumpStrength(Math.max(AdvancedConfig.getInstance().getMinHorseJumpStrength(), Math.min(Math.min(Misc.getRandom().nextDouble(), Misc.getRandom().nextDouble()) * 2, AdvancedConfig.getInstance().getMaxHorseJumpStrength())));
        horse.setAdult();

        //TODO: setSpeed, once available

        callOfWildEntity.setCustomName(LocaleLoader.getString("Taming.Summon.Name.Format", getPlayer().getName(), StringUtils.getPrettyEntityTypeString(EntityType.HORSE)));

        //Particle effect
        ParticleEffectUtils.playCallOfTheWildEffect(callOfWildEntity);
    }

    private void setBaseCOTWEntityProperties(LivingEntity callOfWildEntity) {
        ((Tameable) callOfWildEntity).setOwner(getPlayer());
        callOfWildEntity.setRemoveWhenFarAway(false);
    }

    private void applyMetaDataToCOTWEntity(LivingEntity callOfWildEntity) {
        //This is used to prevent XP gains for damaging this entity
        callOfWildEntity.setMetadata(mcMMO.entityMetadataKey, mcMMO.metadataValue);

        //This helps identify the entity as being summoned by COTW
        callOfWildEntity.setMetadata(mcMMO.COTW_TEMPORARY_SUMMON, mcMMO.metadataValue);
    }

    /**
     * Whether or not the itemstack is used for COTW
     * @param itemStack target ItemStack
     * @return true if it is used for any COTW
     */
    public boolean isCOTWItem(ItemStack itemStack) {
        return summoningItems.containsKey(itemStack.getType());
    }

    //TODO: The way this tracker was written is garbo, I should just rewrite it, I'll save that for a future update
    private int getAmountCurrentlySummoned(CallOfTheWildType callOfTheWildType) {
        //The tracker is unreliable so validate its contents first
        recalibrateTracker();

        return playerSummonedEntities.get(callOfTheWildType).size();
    }

    //TODO: The way this tracker was written is garbo, I should just rewrite it, I'll save that for a future update
    private void addToTracker(LivingEntity livingEntity, CallOfTheWildType callOfTheWildType) {
        TrackedTamingEntity trackedEntity = new TrackedTamingEntity(livingEntity, callOfTheWildType, this);

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

        NotificationManager.sendPlayerInformationChatOnly(getPlayer(), "Taming.Summon.COTW.TimeExpired", StringUtils.getPrettyEntityTypeString(trackedEntity.getLivingEntity().getType()));
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
