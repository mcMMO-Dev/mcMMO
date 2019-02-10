package com.gmail.nossr50.core.skills.primary.taming;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.core.config.AdvancedConfig;
import com.gmail.nossr50.core.config.Config;
import com.gmail.nossr50.core.data.UserManager;
import com.gmail.nossr50.core.datatypes.experience.XPGainReason;
import com.gmail.nossr50.core.datatypes.interactions.NotificationType;
import com.gmail.nossr50.core.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.core.locale.LocaleLoader;
import com.gmail.nossr50.core.skills.PrimarySkillType;
import com.gmail.nossr50.core.skills.SkillManager;
import com.gmail.nossr50.core.skills.SubSkillType;
import com.gmail.nossr50.events.fake.FakeEntityTameEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.skills.BleedTimerTask;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.random.RandomChanceSkillStatic;
import com.gmail.nossr50.util.random.RandomChanceUtil;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillActivationType;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TamingManager extends SkillManager {
    private static HashMap<EntityType, List<TrackedTamingEntity>> summonedEntities = new HashMap<EntityType, List<TrackedTamingEntity>>();

    public TamingManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, PrimarySkillType.TAMING);
    }

    protected static void addToTracker(LivingEntity livingEntity) {
        TrackedTamingEntity trackedEntity = new TrackedTamingEntity(livingEntity);

        if (!summonedEntities.containsKey(livingEntity.getType())) {
            summonedEntities.put(livingEntity.getType(), new ArrayList<TrackedTamingEntity>());
        }

        summonedEntities.get(livingEntity.getType()).add(trackedEntity);
    }

    protected static List<TrackedTamingEntity> getTrackedEntities(EntityType entityType) {
        return summonedEntities.get(entityType);
    }

    protected static void removeFromTracker(TrackedTamingEntity trackedEntity) {
        summonedEntities.get(trackedEntity.getLivingEntity().getType()).remove(trackedEntity);
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
        if (!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_GORE))
            return false;

        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.TAMING_GORE);
    }

    public boolean canUseBeastLore() {
        if (!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_BEAST_LORE))
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
     * @param wolf   The wolf using the ability
     * @param damage The damage being absorbed by the wolf
     */
    public void fastFoodService(Wolf wolf, double damage) {
        //static chance (3rd param)
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

        BleedTimerTask.add(target, getPlayer(), Taming.goreBleedTicks, 1);

        if (target instanceof Player) {
            NotificationManager.sendPlayerInformation((Player) target, NotificationType.SUBSKILL_MESSAGE, "Combat.StruckByGore");
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
        if (!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_CALL_OF_THE_WILD))
            return;

        if (!Permissions.callOfTheWild(getPlayer(), EntityType.OCELOT)) {
            return;
        }

        callOfTheWild(EntityType.OCELOT, Config.getInstance().getTamingCOTWCost(EntityType.OCELOT));
    }

    /**
     * Summon a wolf to your side.
     */
    public void summonWolf() {
        if (!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_CALL_OF_THE_WILD))
            return;

        if (!Permissions.callOfTheWild(getPlayer(), EntityType.WOLF)) {
            return;
        }

        callOfTheWild(EntityType.WOLF, Config.getInstance().getTamingCOTWCost(EntityType.WOLF));
    }

    /**
     * Summon a horse to your side.
     */
    public void summonHorse() {
        if (!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_CALL_OF_THE_WILD))
            return;

        if (!Permissions.callOfTheWild(getPlayer(), EntityType.HORSE)) {
            return;
        }

        callOfTheWild(EntityType.HORSE, Config.getInstance().getTamingCOTWCost(EntityType.HORSE));
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
        if (!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.TAMING_PUMMEL))
            return;

        if (!RandomChanceUtil.checkRandomChanceExecutionSuccess(new RandomChanceSkillStatic(AdvancedConfig.getInstance().getPummelChance(), getPlayer(), SubSkillType.TAMING_PUMMEL)))
            return;

        ParticleEffectUtils.playGreaterImpactEffect(target);
        target.setVelocity(wolf.getLocation().getDirection().normalize().multiply(1.5D));

        if (target instanceof Player) {
            Player defender = (Player) target;

            if (UserManager.getPlayer(defender).useChatNotifications()) {
                NotificationManager.sendPlayerInformation(defender, NotificationType.SUBSKILL_MESSAGE, "Taming.SubSkill.Pummel.TargetMessage");
            }
        }
    }

    public void attackTarget(LivingEntity target) {
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

    /**
     * Handle the Call of the Wild ability.
     *
     * @param type         The type of entity to summon.
     * @param summonAmount The amount of material needed to summon the entity
     */
    private void callOfTheWild(EntityType type, int summonAmount) {
        Player player = getPlayer();

        ItemStack heldItem = player.getInventory().getItemInMainHand();
        int heldItemAmount = heldItem.getAmount();
        Location location = player.getLocation();

        if (heldItemAmount < summonAmount) {
            int moreAmount = summonAmount - heldItemAmount;
            NotificationManager.sendPlayerInformation(player, NotificationType.REQUIREMENTS_NOT_MET, "Item.NotEnough", String.valueOf(moreAmount), StringUtils.getPrettyItemString(heldItem.getType()));
            return;
        }

        if (!rangeCheck(type)) {
            return;
        }

        int amount = Config.getInstance().getTamingCOTWAmount(type);
        int tamingCOTWLength = Config.getInstance().getTamingCOTWLength(type);

        for (int i = 0; i < amount; i++) {
            if (!summonAmountCheck(type)) {
                return;
            }

            location = Misc.getLocationOffset(location, 1);
            LivingEntity entity = (LivingEntity) player.getWorld().spawnEntity(location, type);

            FakeEntityTameEvent event = new FakeEntityTameEvent(entity, player);
            mcMMO.p.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                continue;
            }

            entity.setMetadata(mcMMO.entityMetadataKey, mcMMO.metadataValue);
            ((Tameable) entity).setOwner(player);
            entity.setRemoveWhenFarAway(false);

            addToTracker(entity);

            switch (type) {
                case EntityType.OCELOT:
                    ((Ocelot) entity).setCatType(Ocelot.Type.values()[1 + Misc.getRandom().nextInt(3)]);
                    break;

                case EntityType.WOLF:
                    entity.setMaxHealth(20.0);
                    entity.setHealth(entity.getMaxHealth());
                    break;

                case EntityType.HORSE:
                    Horse horse = (Horse) entity;

                    entity.setMaxHealth(15.0 + (Misc.getRandom().nextDouble() * 15));
                    entity.setHealth(entity.getMaxHealth());
                    horse.setColor(Horse.Color.values()[Misc.getRandom().nextInt(Horse.Color.values().length)]);
                    horse.setStyle(Horse.Style.values()[Misc.getRandom().nextInt(Horse.Style.values().length)]);
                    horse.setJumpStrength(Math.max(AdvancedConfig.getInstance().getMinHorseJumpStrength(), Math.min(Math.min(Misc.getRandom().nextDouble(), Misc.getRandom().nextDouble()) * 2, AdvancedConfig.getInstance().getMaxHorseJumpStrength())));
                    //TODO: setSpeed, once available
                    break;

                default:
                    break;
            }

            if (Permissions.renamePets(player)) {
                entity.setCustomName(LocaleLoader.getString("Taming.Summon.Name.Format", player.getName(), StringUtils.getPrettyEntityTypeString(type)));
            }

            ParticleEffectUtils.playCallOfTheWildEffect(entity);
        }

        ItemStack leftovers = new ItemStack(heldItem);
        leftovers.setAmount(heldItemAmount - summonAmount);
        player.getInventory().setItemInMainHand(heldItemAmount == summonAmount ? null : leftovers);

        String lifeSpan = "";
        if (tamingCOTWLength > 0) {
            lifeSpan = LocaleLoader.getString("Taming.Summon.Lifespan", tamingCOTWLength);
        }

        NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE, "Taming.Summon.Complete", lifeSpan);
        player.playSound(location, Sound.ENTITY_FIREWORK_ROCKET_BLAST_FAR, 1F, 0.5F);
    }

    private boolean rangeCheck(EntityType type) {
        double range = Config.getInstance().getTamingCOTWRange();
        Player player = getPlayer();

        if (range == 0) {
            return true;
        }

        for (Entity entity : player.getNearbyEntities(range, range, range)) {
            if (entity.getType() == type) {
                NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE_FAILED, Taming.getCallOfTheWildFailureMessage(type));
                return false;
            }
        }

        return true;
    }

    private boolean summonAmountCheck(EntityType entityType) {
        Player player = getPlayer();

        int maxAmountSummons = Config.getInstance().getTamingCOTWMaxAmount(entityType);

        if (maxAmountSummons <= 0) {
            return true;
        }

        List<TrackedTamingEntity> trackedEntities = getTrackedEntities(entityType);
        int summonAmount = trackedEntities == null ? 0 : trackedEntities.size();

        if (summonAmount >= maxAmountSummons) {
            NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE_FAILED, "Taming.Summon.Fail.TooMany", String.valueOf(maxAmountSummons));
            return false;
        }

        return true;
    }
}
