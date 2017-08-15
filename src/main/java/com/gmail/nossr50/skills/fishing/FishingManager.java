package com.gmail.nossr50.skills.fishing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Wool;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.config.treasure.TreasureConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.skills.XPGainReason;
import com.gmail.nossr50.datatypes.treasure.EnchantmentTreasure;
import com.gmail.nossr50.datatypes.treasure.FishingTreasure;
import com.gmail.nossr50.datatypes.treasure.Rarity;
import com.gmail.nossr50.datatypes.treasure.ShakeTreasure;
import com.gmail.nossr50.events.skills.fishing.McMMOPlayerFishingTreasureEvent;
import com.gmail.nossr50.events.skills.fishing.McMMOPlayerShakeEvent;
import com.gmail.nossr50.events.skills.secondaryabilities.SecondaryAbilityWeightedActivationCheckEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.runnables.skills.KrakenAttackTask;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.fishing.Fishing.Tier;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.adapter.SoundAdapter;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.SkillUtils;

public class FishingManager extends SkillManager {
    private final long FISHING_COOLDOWN_SECONDS = 1000L;

    private int fishingTries = 0;
    private long fishingTimestamp = 0L;
    private Location fishingTarget;
    private Item fishingCatch;
    private Location hookLocation;

    public FishingManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, SkillType.FISHING);
    }

    public boolean canShake(Entity target) {
        return target instanceof LivingEntity && getSkillLevel() >= Tier.ONE.getLevel() && Permissions.secondaryAbilityEnabled(getPlayer(), SecondaryAbility.SHAKE);
    }

    public boolean canMasterAngler() {
        return getSkillLevel() >= AdvancedConfig.getInstance().getMasterAnglerUnlockLevel() && Permissions.secondaryAbilityEnabled(getPlayer(), SecondaryAbility.MASTER_ANGLER);
    }

    public boolean unleashTheKraken() {
        return unleashTheKraken(true);
    }

    private boolean unleashTheKraken(boolean forceSpawn) {
        if (!forceSpawn && (fishingTries < AdvancedConfig.getInstance().getKrakenTriesBeforeRelease() || fishingTries <= Misc.getRandom().nextInt(200))) {
            return false;
        }

        Player player = getPlayer();
        World world = player.getWorld();

        player.setPlayerWeather(WeatherType.DOWNFALL);

        Entity vehicle = player.getVehicle();

        if (vehicle != null && vehicle.getType() == EntityType.BOAT) {
            vehicle.eject();
            vehicle.remove();
        }

        player.teleport(player.getTargetBlock((HashSet<Material>) null, 100).getLocation(), TeleportCause.PLUGIN);

        String unleashMessage = AdvancedConfig.getInstance().getPlayerUnleashMessage();

        if (!unleashMessage.isEmpty()) {
            player.sendMessage(unleashMessage);
        }

        Location location = player.getLocation();
        boolean globalEffectsEnabled = AdvancedConfig.getInstance().getKrakenGlobalEffectsEnabled();

        if (globalEffectsEnabled) {
            world.strikeLightningEffect(location);
            world.strikeLightningEffect(location);
            world.strikeLightningEffect(location);

            world.playSound(location, SoundAdapter.GHAST_SCREAM, Misc.GHAST_VOLUME, Misc.getGhastPitch());
            mcMMO.p.getServer().broadcastMessage(ChatColor.RED + AdvancedConfig.getInstance().getServerUnleashMessage().replace("(PLAYER)", player.getDisplayName()));
        }
        else {
            world.createExplosion(location.getX(), location.getY(), location.getZ(), 0F, false, false);
            world.createExplosion(location.getX(), location.getY(), location.getZ(), 0F, false, false);
            world.createExplosion(location.getX(), location.getY(), location.getZ(), 0F, false, false);

            player.playSound(location, SoundAdapter.GHAST_SCREAM, Misc.GHAST_VOLUME, Misc.getGhastPitch());
        }

        if (player.getInventory().getItemInMainHand().getType() == Material.FISHING_ROD) {
            player.getInventory().setItemInMainHand(null);
        }

        LivingEntity kraken = (LivingEntity) world.spawnEntity(player.getEyeLocation(), (Misc.getRandom().nextInt(100) == 0 ? EntityType.CHICKEN : EntityType.SQUID));
        kraken.setCustomName(AdvancedConfig.getInstance().getKrakenName());

        if (!kraken.isValid()) {
            int attackInterval = AdvancedConfig.getInstance().getKrakenAttackInterval() * Misc.TICK_CONVERSION_FACTOR;
            new KrakenAttackTask(kraken, player, player.getLocation()).runTaskTimer(mcMMO.p, attackInterval, attackInterval);

            if (!forceSpawn) {
                fishingTries = 0;
            }

            return true;
        }

        kraken.setMaxHealth(AdvancedConfig.getInstance().getKrakenHealth());
        kraken.setHealth(kraken.getMaxHealth());

        int attackInterval = AdvancedConfig.getInstance().getKrakenAttackInterval() * Misc.TICK_CONVERSION_FACTOR;
        new KrakenAttackTask(kraken, player).runTaskTimer(mcMMO.p, attackInterval, attackInterval);

        if (!forceSpawn) {
            fishingTries = 0;
        }

        return true;
    }

    public boolean exploitPrevention() {
        if (!AdvancedConfig.getInstance().getKrakenEnabled()) {
            return false;
        }

        Block targetBlock = getPlayer().getTargetBlock((HashSet<Material>) BlockUtils.getTransparentBlocks(), 100);

        if (!targetBlock.isLiquid()) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        boolean hasFished = (currentTime < fishingTimestamp + FISHING_COOLDOWN_SECONDS);

        fishingTries = hasFished ? fishingTries + 1 : Math.max(fishingTries - 1, 0);
        fishingTimestamp = currentTime;

        Location targetLocation = targetBlock.getLocation();
        boolean sameTarget = (fishingTarget != null && fishingTarget.equals(targetLocation));

        fishingTries = sameTarget ? fishingTries + 1 : Math.max(fishingTries - 1, 0);
        fishingTarget = targetLocation;

        return unleashTheKraken(false);
    }

    public boolean canIceFish(Block block) {
        if (getSkillLevel() < AdvancedConfig.getInstance().getIceFishingUnlockLevel()) {
            return false;
        }

        if (block.getType() != Material.ICE) {
            return false;
        }

        // Make sure this is a body of water, not just a block of ice.
        if (!Fishing.iceFishingBiomes.contains(block.getBiome()) && (block.getRelative(BlockFace.DOWN, 3).getType() != Material.STATIONARY_WATER)) {
            return false;
        }

        Player player = getPlayer();

        if (!Permissions.secondaryAbilityEnabled(getPlayer(), SecondaryAbility.ICE_FISHING)) {
            return false;
        }

        return EventUtils.simulateBlockBreak(block, player, false);
    }

    /**
     * Gets the loot tier
     *
     * @return the loot tier
     */
    public int getLootTier() {
        int skillLevel = getSkillLevel();

        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.toNumerical();
            }
        }

        return 0;
    }

    /**
     * Gets the Shake Mob probability
     *
     * @return Shake Mob probability
     */
    public double getShakeProbability() {
        int skillLevel = getSkillLevel();

        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.getShakeChance();
            }
        }

        return 0.0;
    }

    /**
     * Handle the Fisherman's Diet ability
     *
     * @param rankChange     The # of levels to change rank for the food
     * @param eventFoodLevel The initial change in hunger from the event
     *
     * @return the modified change in hunger for the event
     */
    public int handleFishermanDiet(int rankChange, int eventFoodLevel) {
        return SkillUtils.handleFoodSkills(getPlayer(), skill, eventFoodLevel, Fishing.fishermansDietRankLevel1, Fishing.fishermansDietMaxLevel, rankChange);
    }

    public void iceFishing(Fish hook, Block block) {
        // Make a hole
        block.setType(Material.STATIONARY_WATER);

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Block relative = block.getRelative(x, 0, z);

                if (relative.getType() == Material.ICE) {
                    relative.setType(Material.STATIONARY_WATER);
                }
            }
        }

        // Recast in the new spot
        EventUtils.callFakeFishEvent(getPlayer(), hook);
    }

    public void masterAngler(Fish hook) {
        Player player = getPlayer();
        Location location = hook.getLocation();
        double biteChance = hook.getBiteChance();

        hookLocation = location;

        if (Fishing.masterAnglerBiomes.contains(location.getBlock().getBiome())) {
            biteChance = biteChance * AdvancedConfig.getInstance().getMasterAnglerBiomeModifier();
        }

        if (player.isInsideVehicle() && player.getVehicle().getType() == EntityType.BOAT) {
            biteChance = biteChance * AdvancedConfig.getInstance().getMasterAnglerBoatModifier();
        }

        hook.setBiteChance(Math.min(biteChance, 1.0));
    }

    /**
     * Process the results from a successful fishing trip
     *
     * @param fishingCatch The {@link Item} initially caught
     */
    public void handleFishing(Item fishingCatch) {
        this.fishingCatch = fishingCatch;
        int fishXp = ExperienceConfig.getInstance().getXp(SkillType.FISHING, fishingCatch.getItemStack().getData());
        int treasureXp = 0;
        Player player = getPlayer();
        FishingTreasure treasure = null;

        if (Config.getInstance().getFishingDropsEnabled() && Permissions.secondaryAbilityEnabled(player, SecondaryAbility.FISHING_TREASURE_HUNTER)) {
            treasure = getFishingTreasure();
            this.fishingCatch = null;
        }

        if (treasure != null) {
            ItemStack treasureDrop = treasure.getDrop().clone(); // Not cloning is bad, m'kay?
            Map<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();

            if (Permissions.secondaryAbilityEnabled(player, SecondaryAbility.MAGIC_HUNTER) && ItemUtils.isEnchantable(treasureDrop)) {
                enchants = handleMagicHunter(treasureDrop);
            }

            McMMOPlayerFishingTreasureEvent event = EventUtils.callFishingTreasureEvent(player, treasureDrop, treasure.getXp(), enchants);

            if (!event.isCancelled()) {
                treasureDrop = event.getTreasure();
                treasureXp = event.getXp();
            }
            else {
                treasureDrop = null;
                treasureXp = 0;
            }

            // Drop the original catch at the feet of the player and set the treasure as the real catch
            if (treasureDrop != null) {
                boolean enchanted = false;

                if (!enchants.isEmpty()) {
                    treasureDrop.addUnsafeEnchantments(enchants);
                    enchanted = true;
                }

                if (enchanted) {
                    player.sendMessage(LocaleLoader.getString("Fishing.Ability.TH.MagicFound"));
                }

                if (Config.getInstance().getFishingExtraFish()) {
                    Misc.dropItem(player.getEyeLocation(), fishingCatch.getItemStack());
                }

                fishingCatch.setItemStack(treasureDrop);
            }
        }

        applyXpGain(fishXp + treasureXp, XPGainReason.PVE);
    }

    /**
     * Handle the vanilla XP boost for Fishing
     *
     * @param experience The amount of experience initially awarded by the event
     *
     * @return the modified event damage
     */
    public int handleVanillaXpBoost(int experience) {
        return experience * getVanillaXpMultiplier();
    }

    public Location getHookLocation() {
        return hookLocation;
    }

    /**
     * Handle the Shake ability
     *
     * @param target The {@link LivingEntity} affected by the ability
     */
    public void shakeCheck(LivingEntity target) {
        fishingTries--; // Because autoclicking to shake is OK.

        SecondaryAbilityWeightedActivationCheckEvent activationEvent = new SecondaryAbilityWeightedActivationCheckEvent(getPlayer(), SecondaryAbility.SHAKE, getShakeProbability() / activationChance);
        mcMMO.p.getServer().getPluginManager().callEvent(activationEvent);
        if ((activationEvent.getChance() * activationChance) > Misc.getRandom().nextInt(activationChance)) {
            List<ShakeTreasure> possibleDrops = Fishing.findPossibleDrops(target);

            if (possibleDrops == null || possibleDrops.isEmpty()) {
                return;
            }

            ItemStack drop = Fishing.chooseDrop(possibleDrops);

            // It's possible that chooseDrop returns null if the sum of probability in possibleDrops is inferior than 100
            if (drop == null) {
                return;
            }

            // Extra processing depending on the mob and drop type
            switch (target.getType()) {
                case PLAYER:
                    Player targetPlayer = (Player) target;

                    switch (drop.getType()) {
                        case SKULL_ITEM:
                            drop.setDurability((short) 3);
                            SkullMeta skullMeta = (SkullMeta) drop.getItemMeta();
                            skullMeta.setOwner(targetPlayer.getName());
                            drop.setItemMeta(skullMeta);
                            break;

                        case BED_BLOCK:
                            if (TreasureConfig.getInstance().getInventoryStealEnabled()) {
                                PlayerInventory inventory = targetPlayer.getInventory();
                                int length = inventory.getContents().length;
                                int slot = Misc.getRandom().nextInt(length);
                                drop = inventory.getItem(slot);

                                if (drop == null) {
                                    break;
                                }

                                if (TreasureConfig.getInstance().getInventoryStealStacks()) {
                                    inventory.setItem(slot, null);
                                }
                                else {
                                    inventory.setItem(slot, (drop.getAmount() > 1) ? new ItemStack(drop.getType(), drop.getAmount() - 1) : null);
                                    drop.setAmount(1);
                                }

                                targetPlayer.updateInventory();
                            }
                            break;

                        default:
                            break;
                    }
                    break;

                case SHEEP:
                    Sheep sheep = (Sheep) target;

                    if (drop.getType() == Material.WOOL) {
                        if (sheep.isSheared()) {
                            return;
                        }

                        drop = new Wool(sheep.getColor()).toItemStack(drop.getAmount());
                        sheep.setSheared(true);
                    }
                    break;
                case WITHER_SKELETON:
                    if(drop.getType() == Material.SKULL_ITEM){
                    	drop.setDurability((short) 1);
                    }
                    break;
                default:
                	break;
            }

            McMMOPlayerShakeEvent shakeEvent = new McMMOPlayerShakeEvent(getPlayer(), drop);

            drop = shakeEvent.getDrop();

            if (shakeEvent.isCancelled() || drop == null) {
                return;
            }

            Misc.dropItem(target.getLocation(), drop);
            CombatUtils.dealDamage(target, Math.max(target.getMaxHealth() / 4, 1), getPlayer()); // Make it so you can shake a mob no more than 4 times.
            applyXpGain(ExperienceConfig.getInstance().getFishingShakeXP(), XPGainReason.PVE);
        }
    }

    /**
     * Process the Treasure Hunter ability for Fishing
     *
     * @return The {@link FishingTreasure} found, or null if no treasure was found.
     */
    private FishingTreasure getFishingTreasure() {
        double diceRoll = Misc.getRandom().nextDouble() * 100;
        diceRoll -= getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LUCK);

        FishingTreasure treasure = null;

        for (Rarity rarity : Rarity.values()) {
            double dropRate = TreasureConfig.getInstance().getItemDropRate(getLootTier(), rarity);

            if (diceRoll <= dropRate) {
                if (rarity == Rarity.TRAP) {
                    handleTraps();
                    break;
                }

                List<FishingTreasure> fishingTreasures = TreasureConfig.getInstance().fishingRewards.get(rarity);

                if (fishingTreasures.isEmpty()) {
                    return null;
                }

                treasure = fishingTreasures.get(Misc.getRandom().nextInt(fishingTreasures.size()));
                break;
            }

            diceRoll -= dropRate;
        }

        if (treasure == null) {
            return null;
        }

        ItemStack treasureDrop = treasure.getDrop().clone();
        short maxDurability = treasureDrop.getType().getMaxDurability();

        if (maxDurability > 0) {
            treasureDrop.setDurability((short) (Misc.getRandom().nextInt(maxDurability)));
        }

        if (treasureDrop.getAmount() > 1) {
            treasureDrop.setAmount(Misc.getRandom().nextInt(treasureDrop.getAmount()) + 1);
        }

        treasure.setDrop(treasureDrop);

        return treasure;
    }

    private void handleTraps() {
        Player player = getPlayer();

        if (Permissions.trapsBypass(player)) {
            return;
        }

        if (Misc.getRandom().nextBoolean()) {
            player.sendMessage(LocaleLoader.getString("Fishing.Ability.TH.Boom"));

            TNTPrimed tnt = (TNTPrimed) player.getWorld().spawnEntity(fishingCatch.getLocation(), EntityType.PRIMED_TNT);
            fishingCatch.setPassenger(tnt);

            Vector velocity = fishingCatch.getVelocity();
            double magnitude = velocity.length();
            fishingCatch.setVelocity(velocity.multiply((magnitude + 1) / magnitude));

            tnt.setMetadata(mcMMO.tntsafeMetadataKey, mcMMO.metadataValue);
            tnt.setFuseTicks(3 * Misc.TICK_CONVERSION_FACTOR);
        }
        else {
            player.sendMessage(LocaleLoader.getString("Fishing.Ability.TH.Poison"));

            ThrownPotion thrownPotion = player.getWorld().spawn(fishingCatch.getLocation(), ThrownPotion.class);
            thrownPotion.setItem(new Potion(PotionType.POISON).splash().toItemStack(1));

            fishingCatch.setPassenger(thrownPotion);
        }
    }

    /**
     * Process the Magic Hunter ability
     *
     * @param treasureDrop The {@link ItemStack} to enchant
     *
     * @return true if the item has been enchanted
     */
    private Map<Enchantment, Integer> handleMagicHunter(ItemStack treasureDrop) {
        Map<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
        List<EnchantmentTreasure> fishingEnchantments = null;

        double diceRoll = Misc.getRandom().nextDouble() * 100;

        for (Rarity rarity : Rarity.values()) {
            if (rarity == Rarity.TRAP || rarity == Rarity.RECORD) {
                continue;
            }

            double dropRate = TreasureConfig.getInstance().getEnchantmentDropRate(getLootTier(), rarity);

            if (diceRoll <= dropRate) {
                fishingEnchantments = TreasureConfig.getInstance().fishingEnchantments.get(rarity);
                break;
            }

            diceRoll -= dropRate;
        }

        if (fishingEnchantments == null) {
            return enchants;
        }

        List<Enchantment> validEnchantments = getPossibleEnchantments(treasureDrop);
        List<EnchantmentTreasure> possibleEnchants = new ArrayList<EnchantmentTreasure>();

        for (EnchantmentTreasure enchantmentTreasure : fishingEnchantments) {
            if (validEnchantments.contains(enchantmentTreasure.getEnchantment())) {
                possibleEnchants.add(enchantmentTreasure);
            }
        }

        if (possibleEnchants.isEmpty()) {
            return enchants;
        }

        // This make sure that the order isn't always the same, for example previously Unbreaking had a lot more chance to be used than any other enchant
        Collections.shuffle(possibleEnchants, Misc.getRandom());

        int specificChance = 1;

        for (EnchantmentTreasure enchantmentTreasure : possibleEnchants) {
            Enchantment possibleEnchantment = enchantmentTreasure.getEnchantment();

            if (treasureDrop.getItemMeta().hasConflictingEnchant(possibleEnchantment) || Misc.getRandom().nextInt(specificChance) != 0) {
                continue;
            }

            enchants.put(possibleEnchantment, enchantmentTreasure.getLevel());

            specificChance *= 2;
        }

        return enchants;
    }

    private List<Enchantment> getPossibleEnchantments(ItemStack treasureDrop) {
        Material dropType = treasureDrop.getType();

        if (Fishing.ENCHANTABLE_CACHE.containsKey(dropType)) {
            return Fishing.ENCHANTABLE_CACHE.get(dropType);
        }

        List<Enchantment> possibleEnchantments = new ArrayList<Enchantment>();

        for (Enchantment enchantment : Enchantment.values()) {
            if (enchantment.canEnchantItem(treasureDrop)) {
                possibleEnchantments.add(enchantment);
            }
        }

        Fishing.ENCHANTABLE_CACHE.put(dropType, possibleEnchantments);
        return possibleEnchantments;
    }

    /**
     * Gets the vanilla XP multiplier
     *
     * @return the vanilla XP multiplier
     */
    private int getVanillaXpMultiplier() {
        int skillLevel = getSkillLevel();

        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.getVanillaXPBoostModifier();
            }
        }

        return 1;
    }
}
