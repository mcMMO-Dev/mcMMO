package com.gmail.nossr50.skills.taming;

import static com.gmail.nossr50.util.AttributeMapper.MAPPED_JUMP_STRENGTH;
import static com.gmail.nossr50.util.AttributeMapper.MAPPED_MOVEMENT_SPEED;
import static com.gmail.nossr50.util.MobMetadataUtils.flagMetadata;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.taming.CallOfTheWildType;
import com.gmail.nossr50.datatypes.skills.subskills.taming.TamingSummon;
import com.gmail.nossr50.events.skills.taming.McMMOPlayerTameEntityEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.metadata.MobMetaFlagType;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import com.gmail.nossr50.util.text.StringUtils;
import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TamingManager extends SkillManager {
    //TODO: Temporary static cache, will be changed in 2.2
    private static HashMap<Material, CallOfTheWildType> summoningItems;
    private static HashMap<CallOfTheWildType, TamingSummon> cotwSummonDataProperties;
    private long lastSummonTimeStamp;

    public TamingManager(@NotNull McMMOPlayer mmoPlayer) {
        super(mmoPlayer, PrimarySkillType.TAMING);
        init();
    }

    //TODO: Hacky stuff for 2.1, will be cleaned up in 2.2
    private void init() {
        //prevents accidentally summoning too many things when holding down left click
        lastSummonTimeStamp = 0L;

        //Init per-player tracking of summoned entities
        mcMMO.getTransientEntityTracker().initPlayer(mmoPlayer.getPlayer());

        //Hacky stuff used as a band-aid
        initStaticCaches();
    }

    private void initStaticCaches() {
        //TODO: Temporary static cache, will be changed in 2.2
        //This is shared between instances of TamingManager
        if (summoningItems == null) {
            summoningItems = new HashMap<>();

            summoningItems.put(mcMMO.p.getGeneralConfig()
                            .getTamingCOTWMaterial(CallOfTheWildType.CAT.getConfigEntityTypeEntry()),
                    CallOfTheWildType.CAT);
            summoningItems.put(mcMMO.p.getGeneralConfig()
                            .getTamingCOTWMaterial(CallOfTheWildType.WOLF.getConfigEntityTypeEntry()),
                    CallOfTheWildType.WOLF);
            summoningItems.put(mcMMO.p.getGeneralConfig()
                            .getTamingCOTWMaterial(CallOfTheWildType.HORSE.getConfigEntityTypeEntry()),
                    CallOfTheWildType.HORSE);
        }

        //TODO: Temporary static cache, will be changed in 2.2
        //This is shared between instances of TamingManager
        if (cotwSummonDataProperties == null) {
            cotwSummonDataProperties = new HashMap<>();

            for (CallOfTheWildType callOfTheWildType : CallOfTheWildType.values()) {
                Material itemSummonMaterial = mcMMO.p.getGeneralConfig()
                        .getTamingCOTWMaterial(callOfTheWildType.getConfigEntityTypeEntry());
                int itemAmountRequired = mcMMO.p.getGeneralConfig()
                        .getTamingCOTWCost(callOfTheWildType.getConfigEntityTypeEntry());
                int entitiesSummonedPerCOTW = mcMMO.p.getGeneralConfig()
                        .getTamingCOTWAmount(callOfTheWildType.getConfigEntityTypeEntry());
                int summonLifespanSeconds = mcMMO.p.getGeneralConfig()
                        .getTamingCOTWLength(callOfTheWildType.getConfigEntityTypeEntry());
                int perPlayerMaxAmount = mcMMO.p.getGeneralConfig()
                        .getTamingCOTWMaxAmount(callOfTheWildType.getConfigEntityTypeEntry());

                TamingSummon tamingSummon = new TamingSummon(callOfTheWildType, itemSummonMaterial,
                        itemAmountRequired, entitiesSummonedPerCOTW, summonLifespanSeconds,
                        perPlayerMaxAmount);
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
                && Permissions.isSubSkillEnabled(getPlayer(),
                SubSkillType.TAMING_ENVIRONMENTALLY_AWARE);
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
                && Permissions.isSubSkillEnabled(getPlayer(),
                SubSkillType.TAMING_FAST_FOOD_SERVICE);
    }

    public boolean canUseSharpenedClaws() {
        return RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_SHARPENED_CLAWS)
                && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.TAMING_SHARPENED_CLAWS);
    }

    public boolean canUseGore() {
        if (!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_GORE)) {
            return false;
        }

        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.TAMING_GORE);
    }

    public boolean canUseBeastLore() {
        if (!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_BEAST_LORE)) {
            return false;
        }

        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.TAMING_BEAST_LORE);
    }

    /**
     * Award XP for taming.
     *
     * @param entity The LivingEntity to award XP for
     */
    public void awardTamingXP(@NotNull LivingEntity entity) {
        int xp = ExperienceConfig.getInstance().getTamingXP(entity.getType());

        final McMMOPlayerTameEntityEvent event = new McMMOPlayerTameEntityEvent(mmoPlayer, xp,
                entity);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            applyXpGain(event.getXpGained(), XPGainReason.PVE, XPGainSource.SELF);
        }
    }

    /**
     * Apply the Fast Food Service ability.
     *
     * @param wolf The wolf using the ability
     * @param damage The damage being absorbed by the wolf
     */
    public void fastFoodService(@NotNull Wolf wolf, double damage) {
        if (!ProbabilityUtil.isSkillRNGSuccessful(SubSkillType.TAMING_FAST_FOOD_SERVICE,
                mmoPlayer)) {
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
    public double gore(@NotNull LivingEntity target, double damage) {
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
        if (!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_CALL_OF_THE_WILD)) {
            return;
        }

        if (!Permissions.callOfTheWild(getPlayer(), EntityType.OCELOT)) {
            return;
        }

        processCallOfTheWild();
    }

    /**
     * Summon a wolf to your side.
     */
    public void summonWolf() {
        if (!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_CALL_OF_THE_WILD)) {
            return;
        }

        if (!Permissions.callOfTheWild(getPlayer(), EntityType.WOLF)) {
            return;
        }

        processCallOfTheWild();
    }

    /**
     * Summon a horse to your side.
     */
    public void summonHorse() {
        if (!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_CALL_OF_THE_WILD)) {
            return;
        }

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
            message = message.concat(
                    LocaleLoader.getString("Combat.BeastLoreOwner", beast.getOwner().getName())
                            + " ");
        }

        message = message.concat(
                LocaleLoader.getString("Combat.BeastLoreHealth", target.getHealth(),
                        target.getMaxHealth()));

        // Bred mules & donkeys can actually have horse-like stats, but llamas cannot.
        if (beast instanceof AbstractHorse horseLikeCreature && !(beast instanceof Llama)) {
            AttributeInstance jumpAttribute = horseLikeCreature.getAttribute(MAPPED_JUMP_STRENGTH);

            if (jumpAttribute != null) {
                double jumpStrength = jumpAttribute.getValue();
                // Taken from https://minecraft.wiki/w/Horse#Jump_strength
                jumpStrength = -0.1817584952 * Math.pow(jumpStrength, 3) + 3.689713992 * Math.pow(
                        jumpStrength, 2) + 2.128599134 * jumpStrength - 0.343930367;
                message = message.concat("\n" + LocaleLoader.getString("Combat.BeastLoreHorseSpeed",
                                horseLikeCreature.getAttribute(MAPPED_MOVEMENT_SPEED).getValue() * 43))
                        .concat("\n" + LocaleLoader.getString("Combat.BeastLoreHorseJumpStrength",
                                jumpStrength));
            }
        }

        player.sendMessage(message);
    }

    public void processEnvironmentallyAware(@NotNull Wolf wolf, double damage) {
        if (damage > wolf.getHealth()) {
            return;
        }

        Player owner = getPlayer();

        wolf.teleport(owner);
        NotificationManager.sendPlayerInformation(owner, NotificationType.SUBSKILL_MESSAGE,
                "Taming.Listener.Wolf");
    }

    public void pummel(LivingEntity target, Wolf wolf) {
        if (!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_PUMMEL)) {
            return;
        }

        if (!ProbabilityUtil.isStaticSkillRNGSuccessful(PrimarySkillType.TAMING, mmoPlayer,
                mcMMO.p.getAdvancedConfig().getPummelChance())) {
            return;
        }

        ParticleEffectUtils.playGreaterImpactEffect(target);
        target.setVelocity(wolf.getLocation().getDirection().normalize().multiply(1.5D));

        if (target instanceof Player defender) {

            if (NotificationManager.doesPlayerUseNotifications(defender)) {
                NotificationManager.sendPlayerInformation(defender,
                        NotificationType.SUBSKILL_MESSAGE, "Taming.SubSkill.Pummel.TargetMessage");
            }
        }
    }

    public void attackTarget(LivingEntity target) {
        if (target instanceof Tameable tameable) {
            if (tameable.getOwner() == getPlayer()) {
                return;
            }
        }
        double range = 5;
        Player player = getPlayer();

        if (!target.getWorld().equals(player.getWorld())) {
            return;
        }

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
        if (lastSummonTimeStamp + 150 > System.currentTimeMillis()) {
            return;
        } else {
            lastSummonTimeStamp = System.currentTimeMillis();
        }

        Player player = getPlayer();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        //Check if the item the player is currently holding is a COTW item
        if (isCOTWItem(itemInMainHand)) {
            //Get the summoning type
            CallOfTheWildType callOfTheWildType = summoningItems.get(itemInMainHand.getType());
            TamingSummon tamingSummon = cotwSummonDataProperties.get(callOfTheWildType);

            //Players will pay for the cost if at least one thing was summoned
            int amountSummoned = 0;

            //Check to see if players have the correct amount of the item required to summon
            if (itemInMainHand.getAmount() >= tamingSummon.getItemAmountRequired()) {
                //Initial Spawn location
                Location spawnLocation = Misc.getLocationOffset(player.getLocation(), 1);

                //COTW can summon multiple entities per usage
                for (int i = 0; i < tamingSummon.getEntitiesSummoned(); i++) {

                    if (getAmountCurrentlySummoned(callOfTheWildType)
                            >= tamingSummon.getSummonCap()) {
                        NotificationManager.sendPlayerInformationChatOnly(player,
                                "Taming.Summon.COTW.Limit",
                                String.valueOf(tamingSummon.getSummonCap()),
                                StringUtils.getCapitalized(callOfTheWildType.toString()));
                        break;
                    }

                    spawnLocation = Misc.getLocationOffset(spawnLocation, 1);
                    spawnCOTWEntity(callOfTheWildType, spawnLocation, tamingSummon.getEntityType());

                    //Inform the player about what they have just done
                    if (tamingSummon.getSummonLifespan() > 0) {
                        NotificationManager.sendPlayerInformationChatOnly(player,
                                "Taming.Summon.COTW.Success.WithLifespan",
                                StringUtils.getCapitalized(callOfTheWildType.toString()),
                                String.valueOf(tamingSummon.getSummonLifespan()));
                    } else {
                        NotificationManager.sendPlayerInformationChatOnly(player,
                                "Taming.Summon.COTW.Success.WithoutLifespan",
                                StringUtils.getCapitalized(callOfTheWildType.toString()));
                    }

                    //Send Sound
                    SoundManager.sendSound(player, player.getLocation(),
                            SoundType.ABILITY_ACTIVATED_GENERIC);

                    amountSummoned++;
                }

                //Remove items from the player if they had at least one entity summoned successfully
                if (amountSummoned >= 1) {
                    //Remove the items used to summon
                    int itemAmountAfterPayingCost =
                            itemInMainHand.getAmount() - tamingSummon.getItemAmountRequired();
                    itemInMainHand.setAmount(itemAmountAfterPayingCost);
                    player.updateInventory();
                }

            } else {
                //Player did not have enough of the item in their main hand
                int difference = tamingSummon.getItemAmountRequired() - itemInMainHand.getAmount();
                NotificationManager.sendPlayerInformationChatOnly(player,
                        "Taming.Summon.COTW.NeedMoreItems", String.valueOf(difference),
                        StringUtils.getPrettyMaterialString(itemInMainHand.getType()));
            }
        }
    }

    private void spawnCOTWEntity(CallOfTheWildType callOfTheWildType, Location spawnLocation,
            EntityType entityType) {
        switch (callOfTheWildType) {
            case CAT ->
                //Entity type is needed for cats because in 1.13 and below we spawn ocelots, in 1.14 and above we spawn cats
                    spawnCat(spawnLocation, entityType);
            case HORSE -> spawnHorse(spawnLocation);
            case WOLF -> spawnWolf(spawnLocation);
        }
    }

    private void spawnWolf(Location spawnLocation) {
        LivingEntity callOfWildEntity = (LivingEntity) getPlayer().getWorld()
                .spawnEntity(spawnLocation, EntityType.WOLF);

        //This is used to prevent XP gains for damaging this entity
        applyMetaDataToCOTWEntity(callOfWildEntity);

        setBaseCOTWEntityProperties(callOfWildEntity);

        ((Wolf) callOfWildEntity).setAdult();
        addToTracker(callOfWildEntity, CallOfTheWildType.WOLF);

        //Setup wolf stats
        callOfWildEntity.setMaxHealth(20.0);
        callOfWildEntity.setHealth(callOfWildEntity.getMaxHealth());

        callOfWildEntity.setCustomName(
                LocaleLoader.getString("Taming.Summon.Name.Format", getPlayer().getName(),
                        StringUtils.getPrettyEntityTypeString(EntityType.WOLF)));
    }

    @SuppressWarnings("deprecation")
    private void spawnCat(Location spawnLocation, EntityType entityType) {
        LivingEntity callOfWildEntity = (LivingEntity) getPlayer().getWorld()
                .spawnEntity(spawnLocation, entityType);

        //This is used to prevent XP gains for damaging this entity
        applyMetaDataToCOTWEntity(callOfWildEntity);

        setBaseCOTWEntityProperties(callOfWildEntity);

        addToTracker(callOfWildEntity, CallOfTheWildType.CAT);

        //Randomize the cat
        if (callOfWildEntity instanceof Ocelot) {
            // Ocelot.Type is deprecated, but that's fine since this only runs on 1.13
            int numberOfTypes = Ocelot.Type.values().length;
            ((Ocelot) callOfWildEntity).setCatType(
                    Ocelot.Type.values()[Misc.getRandom().nextInt(numberOfTypes)]);
        }

        ((Ageable) callOfWildEntity).setAdult();

        callOfWildEntity.setCustomName(
                LocaleLoader.getString("Taming.Summon.Name.Format", getPlayer().getName(),
                        StringUtils.getPrettyEntityTypeString(entityType)));

        //Particle effect
        ParticleEffectUtils.playCallOfTheWildEffect(callOfWildEntity);
    }

    private void spawnHorse(Location spawnLocation) {
        LivingEntity callOfWildEntity = (LivingEntity) getPlayer().getWorld()
                .spawnEntity(spawnLocation, EntityType.HORSE);
        applyMetaDataToCOTWEntity(callOfWildEntity);

        setBaseCOTWEntityProperties(callOfWildEntity);

        addToTracker(callOfWildEntity, CallOfTheWildType.HORSE);

        //Randomize Horse
        Horse horse = (Horse) callOfWildEntity;

        callOfWildEntity.setMaxHealth(15.0 + (Misc.getRandom().nextDouble() * 15));
        callOfWildEntity.setHealth(callOfWildEntity.getMaxHealth());
        horse.setColor(Horse.Color.values()[Misc.getRandom().nextInt(Horse.Color.values().length)]);
        horse.setStyle(Horse.Style.values()[Misc.getRandom().nextInt(Horse.Style.values().length)]);
        horse.setJumpStrength(Math.max(mcMMO.p.getAdvancedConfig().getMinHorseJumpStrength(),
                Math.min(Math.min(Misc.getRandom().nextDouble(), Misc.getRandom().nextDouble()) * 2,
                        mcMMO.p.getAdvancedConfig().getMaxHorseJumpStrength())));
        horse.setAdult();

        //TODO: setSpeed, once available

        callOfWildEntity.setCustomName(
                LocaleLoader.getString("Taming.Summon.Name.Format", getPlayer().getName(),
                        StringUtils.getPrettyEntityTypeString(EntityType.HORSE)));

        //Particle effect
        ParticleEffectUtils.playCallOfTheWildEffect(callOfWildEntity);
    }

    private void setBaseCOTWEntityProperties(LivingEntity callOfWildEntity) {
        ((Tameable) callOfWildEntity).setOwner(getPlayer());
        callOfWildEntity.setRemoveWhenFarAway(false);
    }

    private void applyMetaDataToCOTWEntity(LivingEntity summonedEntity) {
        //This helps identify the entity as being summoned by COTW
        flagMetadata(MobMetaFlagType.COTW_SUMMONED_MOB, summonedEntity);
    }

    /**
     * Whether the itemstack is used for COTW
     *
     * @param itemStack target ItemStack
     * @return true if it is used for any COTW
     */
    public boolean isCOTWItem(@NotNull ItemStack itemStack) {
        return summoningItems.containsKey(itemStack.getType());
    }

    private int getAmountCurrentlySummoned(@NotNull CallOfTheWildType callOfTheWildType) {
        return mcMMO.getTransientEntityTracker()
                .getActiveSummonsForPlayerOfType(getPlayer().getUniqueId(), callOfTheWildType);
    }

    private void addToTracker(@NotNull LivingEntity livingEntity,
            @NotNull CallOfTheWildType callOfTheWildType) {
        mcMMO.getTransientEntityTracker().addSummon(getPlayer().getUniqueId(),
                new TrackedTamingEntity(livingEntity, callOfTheWildType, getPlayer()));
    }

    /**
     * Remove all tracked entities from existence if they currently exist Clear the tracked entity
     * lists afterwards
     */
    //TODO: The way this tracker was written is garbo, I should just rewrite it, I'll save that for a future update
    public void cleanupAllSummons() {
        mcMMO.getTransientEntityTracker().cleanupPlayer(getPlayer());
    }
}
