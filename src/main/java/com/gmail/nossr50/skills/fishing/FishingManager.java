package com.gmail.nossr50.skills.fishing;

import com.gmail.nossr50.api.ItemSpawnReason;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.config.treasure.FishingTreasureConfig;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.treasure.EnchantmentTreasure;
import com.gmail.nossr50.datatypes.treasure.FishingTreasure;
import com.gmail.nossr50.datatypes.treasure.FishingTreasureBook;
import com.gmail.nossr50.datatypes.treasure.Rarity;
import com.gmail.nossr50.datatypes.treasure.ShakeTreasure;
import com.gmail.nossr50.events.skills.fishing.McMMOPlayerFishingTreasureEvent;
import com.gmail.nossr50.events.skills.fishing.McMMOPlayerMasterAnglerEvent;
import com.gmail.nossr50.events.skills.fishing.McMMOPlayerShakeEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.skills.MasterAnglerTask;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.adapter.BiomeAdapter;
import com.gmail.nossr50.util.compat.layers.skills.MasterAnglerCompatibilityLayer;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FishingManager extends SkillManager {
    public static final int FISHING_ROD_CAST_CD_MILLISECONDS = 100;
    private final long FISHING_COOLDOWN_SECONDS = 1000L;

    private long fishingRodCastTimestamp = 0L;
    private long fishHookSpawnTimestamp = 0L;
    private long lastWarned = 0L;
    private final long lastWarnedExhaust = 0L;
    private FishHook fishHookReference;
    private BoundingBox lastFishingBoundingBox;
    private boolean sameTarget;
    private Item fishingCatch;
    private Location hookLocation;
    private int fishCaughtCounter = 1;
    private final int masterAnglerMinWaitLowerBound;
    private final int masterAnglerMaxWaitLowerBound;

    public FishingManager(McMMOPlayer mmoPlayer) {
        super(mmoPlayer, PrimarySkillType.FISHING);
        //Ticks for minWait and maxWait never go below this value
        int bonusCapMin = mcMMO.p.getAdvancedConfig().getFishingReductionMinWaitCap();
        int bonusCapMax = mcMMO.p.getAdvancedConfig().getFishingReductionMaxWaitCap();

        this.masterAnglerMinWaitLowerBound = Math.max(bonusCapMin, 0);
        this.masterAnglerMaxWaitLowerBound = Math.max(bonusCapMax,
                masterAnglerMinWaitLowerBound + 40);
    }

    public boolean canShake(Entity target) {
        return target instanceof LivingEntity && RankUtils.hasUnlockedSubskill(getPlayer(),
                SubSkillType.FISHING_SHAKE)
                && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.FISHING_SHAKE);
    }

    public boolean canMasterAngler() {
        return mcMMO.getCompatibilityManager().getMasterAnglerCompatibilityLayer() != null
                && getSkillLevel() >= RankUtils.getUnlockLevel(SubSkillType.FISHING_MASTER_ANGLER)
                && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.FISHING_MASTER_ANGLER);
    }

//    public void setFishingRodCastTimestamp()
//    {
//        long currentTime = System.currentTimeMillis();
//        //Only track spam casting if the fishing hook is fresh
//        if (currentTime > fishHookSpawnTimestamp + 1000)
//            return;
//
//        if (currentTime < fishingRodCastTimestamp + FISHING_ROD_CAST_CD_MILLISECONDS)
//        {
//            ItemStack fishingRod = getPlayer().getInventory().getItemInMainHand();
//
//            //Ensure correct hand item is damaged
//            if (fishingRod.getType() != Material.FISHING_ROD) {
//                fishingRod = getPlayer().getInventory().getItemInOffHand();
//            }
//
//            getPlayer().setFoodLevel(Math.max(getPlayer().getFoodLevel() - 1, 0));
//            fishingRod.setDurability((short) (fishingRod.getDurability() + 5));
//            getPlayer().updateInventory();
//
//            if (lastWarnedExhaust + (1000) < currentTime)
//            {
//                getPlayer().sendMessage(LocaleLoader.getString("Fishing.Exhausting"));
//                lastWarnedExhaust = currentTime;
//                SoundManager.sendSound(getPlayer(), getPlayer().getLocation(), SoundType.TIRED);
//            }
//        }
//
//        fishingRodCastTimestamp = System.currentTimeMillis();
//    }

    public void setFishHookReference(FishHook fishHook) {
        if (fishHook.getMetadata(MetadataConstants.METADATA_KEY_FISH_HOOK_REF).size() > 0) {
            return;
        }

        fishHook.setMetadata(MetadataConstants.METADATA_KEY_FISH_HOOK_REF,
                MetadataConstants.MCMMO_METADATA_VALUE);
        this.fishHookReference = fishHook;
        fishHookSpawnTimestamp = System.currentTimeMillis();
        fishingRodCastTimestamp = System.currentTimeMillis();

    }

    public boolean isFishingTooOften() {
        long currentTime = System.currentTimeMillis();
        long fishHookSpawnCD = fishHookSpawnTimestamp + 1000;
        boolean hasFished = (currentTime < fishHookSpawnCD);

        if (hasFished && (lastWarned + (1000) < currentTime)) {
            getPlayer().sendMessage(LocaleLoader.getString("Fishing.Scared"));
            lastWarned = System.currentTimeMillis();
        }

        return hasFished;
    }

    public void processExploiting(Vector centerOfCastVector) {
        BoundingBox newCastBoundingBox = makeBoundingBox(centerOfCastVector);
        this.sameTarget = lastFishingBoundingBox != null && lastFishingBoundingBox.overlaps(
                newCastBoundingBox);

        if (this.sameTarget) {
            fishCaughtCounter++;
        } else {
            fishCaughtCounter = 1;
        }

        //If the new bounding box does not intersect with the old one, then update our bounding box reference
        if (!this.sameTarget) {
            lastFishingBoundingBox = newCastBoundingBox;
        }

        if (fishCaughtCounter + 1 == ExperienceConfig.getInstance()
                .getFishingExploitingOptionOverFishLimit()) {
            getPlayer().sendMessage(LocaleLoader.getString("Fishing.LowResourcesTip",
                    ExperienceConfig.getInstance().getFishingExploitingOptionMoveRange()));
        }
    }

    public boolean isExploitingFishing(Vector centerOfCastVector) {

        /*Block targetBlock = getPlayer().getTargetBlock(BlockUtils.getTransparentBlocks(), 100);

        if (!targetBlock.isLiquid()) {
            return false;
        }*/

        return this.sameTarget && fishCaughtCounter >= ExperienceConfig.getInstance()
                .getFishingExploitingOptionOverFishLimit();
    }

    public static BoundingBox makeBoundingBox(Vector centerOfCastVector) {
        int exploitingRange = ExperienceConfig.getInstance().getFishingExploitingOptionMoveRange();
        return BoundingBox.of(centerOfCastVector, exploitingRange / 2, 1, exploitingRange / 2);
    }

    public void setFishingTarget() {
        getPlayer().getTargetBlock(BlockUtils.getTransparentBlocks(), 100);
    }

    public boolean canIceFish(Block block) {
        if (getSkillLevel() < RankUtils.getUnlockLevel(SubSkillType.FISHING_ICE_FISHING)) {
            return false;
        }

        if (block.getType() != Material.ICE) {
            return false;
        }

        // Make sure this is a body of water, not just a block of ice.
        if (!BiomeAdapter.ICE_BIOMES.contains(block.getBiome())
                && (block.getRelative(BlockFace.DOWN, 3).getType() != Material.WATER)) {
            return false;
        }

        Player player = getPlayer();

        if (!Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.FISHING_ICE_FISHING)) {
            return false;
        }

        return EventUtils.simulateBlockBreak(block, player);
    }

    /**
     * Gets the loot tier
     *
     * @return the loot tier
     */
    public int getLootTier() {
        return RankUtils.getRank(getPlayer(), SubSkillType.FISHING_TREASURE_HUNTER);
    }

    public double getShakeChance() {
        return mcMMO.p.getAdvancedConfig().getShakeChance(
                RankUtils.getRank(mmoPlayer.getPlayer(), SubSkillType.FISHING_SHAKE));
    }

    protected int getVanillaXPBoostModifier() {
        return mcMMO.p.getAdvancedConfig().getFishingVanillaXPModifier(getLootTier());
    }

    /**
     * Gets the Shake Mob probability
     *
     * @return Shake Mob probability
     */
    public double getShakeProbability() {
        return getShakeChance();
    }

    /**
     * Handle the Fisherman's Diet ability
     *
     * @param eventFoodLevel The initial change in hunger from the event
     * @return the modified change in hunger for the event
     */
    public int handleFishermanDiet(int eventFoodLevel) {
        return SkillUtils.handleFoodSkills(getPlayer(), eventFoodLevel,
                SubSkillType.FISHING_FISHERMANS_DIET);
    }

    public void iceFishing(FishHook hook, Block block) {
        // Make a hole
        block.setType(Material.WATER);

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Block relative = block.getRelative(x, 0, z);

                if (relative.getType() == Material.ICE) {
                    relative.setType(Material.WATER);
                }
            }
        }

        // Recast in the new spot
        EventUtils.callFakeFishEvent(getPlayer(), hook);
    }

    public void masterAngler(@NotNull FishHook hook, int lureLevel) {
        mcMMO.p.getFoliaLib().getScheduler()
                .runAtEntityLater(hook, new MasterAnglerTask(hook, this, lureLevel),
                        1); //We run later to get the lure bonus applied
    }

    /**
     * Processes master angler Reduced tick time on fish hook, etc
     *
     * @param fishHook target fish hook
     */
    public void processMasterAngler(@NotNull FishHook fishHook, int lureLevel) {
        MasterAnglerCompatibilityLayer masterAnglerCompatibilityLayer = (MasterAnglerCompatibilityLayer) mcMMO.getCompatibilityManager()
                .getMasterAnglerCompatibilityLayer();

        if (masterAnglerCompatibilityLayer != null) {
            int maxWaitTicks = masterAnglerCompatibilityLayer.getMaxWaitTime(fishHook);
            int minWaitTicks = masterAnglerCompatibilityLayer.getMinWaitTime(fishHook);

            int masterAnglerRank = RankUtils.getRank(mmoPlayer, SubSkillType.FISHING_MASTER_ANGLER);
            int convertedLureBonus = 0;

            //This avoids a Minecraft bug where lure levels above 3 break fishing
            if (lureLevel > 0) {
                masterAnglerCompatibilityLayer.setApplyLure(fishHook, false);
                convertedLureBonus = lureLevel * 100;
            }

            boolean boatBonus = isInBoat();
            int minWaitReduction = getMasterAnglerTickMinWaitReduction(masterAnglerRank, boatBonus);
            int maxWaitReduction = getMasterAnglerTickMaxWaitReduction(masterAnglerRank, boatBonus,
                    convertedLureBonus);

            int reducedMinWaitTime = getReducedTicks(minWaitTicks, minWaitReduction,
                    masterAnglerMinWaitLowerBound);
            int reducedMaxWaitTime = getReducedTicks(maxWaitTicks, maxWaitReduction,
                    masterAnglerMaxWaitLowerBound);

            boolean badValuesFix = false;

            //If we find bad values correct it
            if (reducedMaxWaitTime < reducedMinWaitTime) {
                reducedMaxWaitTime = reducedMinWaitTime + 100;
                badValuesFix = true;
            }

            final McMMOPlayerMasterAnglerEvent event =
                    new McMMOPlayerMasterAnglerEvent(mmoPlayer, reducedMinWaitTime,
                            reducedMaxWaitTime, this);
            mcMMO.p.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return;
            }

            reducedMaxWaitTime = event.getReducedMaxWaitTime();
            reducedMinWaitTime = event.getReducedMinWaitTime();

            if (mmoPlayer.isDebugMode()) {
                mmoPlayer.getPlayer().sendMessage(ChatColor.GOLD + "Master Angler Debug");

                if (badValuesFix) {
                    mmoPlayer.getPlayer()
                            .sendMessage(ChatColor.RED + "Bad values were applied and corrected," +
                                    " check your configs, minWaitLowerBound wait should never be lower than min wait.");
                }

                mmoPlayer.getPlayer().sendMessage(
                        "ALLOW STACK WITH LURE: " + masterAnglerCompatibilityLayer.getApplyLure(
                                fishHook));
                mmoPlayer.getPlayer().sendMessage("MIN TICK REDUCTION: " + minWaitReduction);
                mmoPlayer.getPlayer().sendMessage("MAX TICK REDUCTION: " + maxWaitReduction);
                mmoPlayer.getPlayer().sendMessage("BOAT BONUS: " + boatBonus);

                if (boatBonus) {
                    mmoPlayer.getPlayer()
                            .sendMessage("BOAT MAX TICK REDUCTION: " + maxWaitReduction);
                    mmoPlayer.getPlayer()
                            .sendMessage("BOAT MIN TICK REDUCTION: " + maxWaitReduction);
                }

                mmoPlayer.getPlayer().sendMessage("");

                mmoPlayer.getPlayer()
                        .sendMessage(ChatColor.DARK_AQUA + "BEFORE MASTER ANGLER WAS APPLIED");
                mmoPlayer.getPlayer().sendMessage("Original Max Wait Ticks: " + maxWaitTicks);
                mmoPlayer.getPlayer().sendMessage("Original Min Wait Ticks: " + minWaitTicks);
                mmoPlayer.getPlayer().sendMessage("");

                mmoPlayer.getPlayer()
                        .sendMessage(ChatColor.DARK_AQUA + "AFTER MASTER ANGLER WAS APPLIED");
                mmoPlayer.getPlayer().sendMessage("Current Max Wait Ticks: " + reducedMaxWaitTime);
                mmoPlayer.getPlayer().sendMessage("Current Min Wait Ticks: " + reducedMinWaitTime);

                mmoPlayer.getPlayer().sendMessage("");

                mmoPlayer.getPlayer()
                        .sendMessage(ChatColor.DARK_AQUA + "Caps / Limits (edit in advanced.yml)");
                mmoPlayer.getPlayer().sendMessage("Lowest possible minWaitLowerBound wait ticks "
                        + masterAnglerMinWaitLowerBound);
                mmoPlayer.getPlayer().sendMessage(
                        "Lowest possible min wait ticks " + masterAnglerMaxWaitLowerBound);
            }

            masterAnglerCompatibilityLayer.setMaxWaitTime(fishHook, reducedMaxWaitTime);
            masterAnglerCompatibilityLayer.setMinWaitTime(fishHook, reducedMinWaitTime);
        }

    }

    public int getReducedTicks(int ticks, int totalBonus, int tickBounds) {
        return Math.max(tickBounds, ticks - totalBonus);
    }

    public boolean isInBoat() {
        return mmoPlayer.getPlayer().isInsideVehicle() && mmoPlayer.getPlayer()
                .getVehicle() instanceof Boat;
    }

    public int getMasterAnglerTickMaxWaitReduction(int masterAnglerRank, boolean boatBonus,
            int emulatedLureBonus) {
        int totalBonus =
                mcMMO.p.getAdvancedConfig().getFishingReductionMaxWaitTicks() * masterAnglerRank;

        if (boatBonus) {
            totalBonus += getFishingBoatMaxWaitReduction();
        }

        totalBonus += emulatedLureBonus;

        return totalBonus;
    }

    public int getMasterAnglerTickMinWaitReduction(int masterAnglerRank, boolean boatBonus) {
        int totalBonus =
                mcMMO.p.getAdvancedConfig().getFishingReductionMinWaitTicks() * masterAnglerRank;

        if (boatBonus) {
            totalBonus += getFishingBoatMinWaitReduction();
        }

        return totalBonus;
    }

    public int getFishingBoatMinWaitReduction() {
        return mcMMO.p.getAdvancedConfig().getFishingBoatReductionMinWaitTicks();
    }

    public int getFishingBoatMaxWaitReduction() {
        return mcMMO.p.getAdvancedConfig().getFishingBoatReductionMaxWaitTicks();
    }

    public boolean isMagicHunterEnabled() {
        return RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.FISHING_MAGIC_HUNTER)
                && RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.FISHING_TREASURE_HUNTER)
                && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.FISHING_MAGIC_HUNTER)
                && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.FISHING_TREASURE_HUNTER);
    }

    /**
     * Process the results from a successful fishing trip
     *
     * @param fishingCatch The {@link Item} initially caught
     */
    public void processFishing(@NotNull Item fishingCatch) {
        this.fishingCatch = fishingCatch;
        int fishXp = ExperienceConfig.getInstance()
                .getXp(PrimarySkillType.FISHING, fishingCatch.getItemStack().getType());
        int treasureXp = 0;
        ItemStack treasureDrop = null;
        Player player = getPlayer();
        FishingTreasure treasure = null;
        boolean fishingSucceeds = false;

        if (mcMMO.p.getGeneralConfig().getFishingDropsEnabled() && Permissions.isSubSkillEnabled(
                player, SubSkillType.FISHING_TREASURE_HUNTER)) {
            treasure = getFishingTreasure();
            this.fishingCatch = null;
        }

        if (treasure != null) {
            if (treasure instanceof FishingTreasureBook) {
                treasureDrop = ItemUtils.createEnchantBook((FishingTreasureBook) treasure);
            } else {
                treasureDrop = treasure.getDrop().clone(); // Not cloning is bad, m'kay?

            }
            Map<Enchantment, Integer> enchants = new HashMap<>();
            McMMOPlayerFishingTreasureEvent event;

            /*
             * Books get some special treatment
             */
            if (treasure instanceof FishingTreasureBook) {
                //Skip the magic hunter stuff
                if (treasureDrop.getItemMeta() != null) {
                    enchants.putAll(treasureDrop.getItemMeta().getEnchants());
                }

                event = EventUtils.callFishingTreasureEvent(mmoPlayer, treasureDrop,
                        treasure.getXp(), enchants);
            } else {
                if (isMagicHunterEnabled() && ItemUtils.isEnchantable(treasureDrop)) {
                    enchants = processMagicHunter(treasureDrop);
                }

                event = EventUtils.callFishingTreasureEvent(mmoPlayer, treasureDrop,
                        treasure.getXp(), enchants);
            }

            if (!event.isCancelled()) {
                treasureDrop = event.getTreasure();
                treasureXp = event.getXp();

                // Drop the original catch at the feet of the player and set the treasure as the real catch
                if (treasureDrop != null) {
                    fishingSucceeds = true;
                    boolean enchanted = false;

                    if (treasure instanceof FishingTreasureBook) {
                        enchanted = true;
                    } else if (!enchants.isEmpty()) {
                        treasureDrop.addUnsafeEnchantments(enchants);
                        enchanted = true;
                    }

                    if (enchanted) {
                        NotificationManager.sendPlayerInformation(player,
                                NotificationType.SUBSKILL_MESSAGE, "Fishing.Ability.TH.MagicFound");
                    }

                }
            } else {
                treasureDrop = null;
                treasureXp = 0;
            }
        }

        if (fishingSucceeds) {
            if (mcMMO.p.getGeneralConfig().getFishingExtraFish()) {
                ItemUtils.spawnItem(getPlayer(), player.getEyeLocation(),
                        fishingCatch.getItemStack(), ItemSpawnReason.FISHING_EXTRA_FISH);
            }

            fishingCatch.setItemStack(treasureDrop);
        }

        applyXpGain(fishXp + treasureXp, XPGainReason.PVE, XPGainSource.SELF);
    }

    /**
     * Handle the vanilla XP boost for Fishing
     *
     * @param experience The amount of experience initially awarded by the event
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
    public void shakeCheck(@NotNull LivingEntity target) {
        if (ProbabilityUtil.isStaticSkillRNGSuccessful(PrimarySkillType.FISHING, mmoPlayer,
                getShakeChance())) {
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
                        case PLAYER_HEAD:
                            drop.setDurability((short) 3);
                            SkullMeta skullMeta = (SkullMeta) drop.getItemMeta();
                            skullMeta.setOwningPlayer(targetPlayer);
                            drop.setItemMeta(skullMeta);
                            break;

                        case BEDROCK:
                            if (FishingTreasureConfig.getInstance().getInventoryStealEnabled()) {
                                PlayerInventory inventory = targetPlayer.getInventory();
                                int length = inventory.getContents().length;
                                int slot = Misc.getRandom().nextInt(length);
                                drop = inventory.getItem(slot);

                                if (drop == null) {
                                    break;
                                }

                                if (FishingTreasureConfig.getInstance().getInventoryStealStacks()) {
                                    inventory.setItem(slot, null);
                                } else {
                                    inventory.setItem(slot,
                                            (drop.getAmount() > 1) ? new ItemStack(drop.getType(),
                                                    drop.getAmount() - 1) : null);
                                    drop.setAmount(1);
                                }
                            }
                            break;

                        default:
                            break;
                    }
                    break;

                case SHEEP:
                    Sheep sheep = (Sheep) target;

                    if (drop.getType().name().endsWith("WOOL")) {
                        if (sheep.isSheared()) {
                            return;
                        }
                        sheep.setSheared(true);
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

            ItemUtils.spawnItem(getPlayer(), target.getLocation(), drop,
                    ItemSpawnReason.FISHING_SHAKE_TREASURE);
            // Make it so you can shake a mob no more than 4 times.
            double dmg = Math.min(Math.max(target.getMaxHealth() / 4, 1), 10);
            CombatUtils.safeDealDamage(target, dmg, getPlayer());
            applyXpGain(ExperienceConfig.getInstance().getFishingShakeXP(), XPGainReason.PVE,
                    XPGainSource.SELF);
        }
    }

    /**
     * Process the Treasure Hunter ability for Fishing
     *
     * @return The {@link FishingTreasure} found, or null if no treasure was found.
     */
    private @Nullable FishingTreasure getFishingTreasure() {
        double diceRoll = Misc.getRandom().nextDouble() * 100;
        int luck;

        if (getPlayer().getInventory().getItemInMainHand().getType() == Material.FISHING_ROD) {
            luck = getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(
                    mcMMO.p.getEnchantmentMapper().getLuckOfTheSea());
        } else {
            // We know something was caught, so if the rod wasn't in the main hand it must be in the offhand
            luck = getPlayer().getInventory().getItemInOffHand().getEnchantmentLevel(
                    mcMMO.p.getEnchantmentMapper().getLuckOfTheSea());
        }

        // Rather than subtracting luck (and causing a minimum 3% chance for every drop), scale by luck.
        diceRoll *= (1.0 - luck * mcMMO.p.getGeneralConfig().getFishingLureModifier() / 100);

        FishingTreasure treasure = null;

        for (Rarity rarity : Rarity.values()) {
            double dropRate = FishingTreasureConfig.getInstance()
                    .getItemDropRate(getLootTier(), rarity);

            if (diceRoll <= dropRate) {

                List<FishingTreasure> fishingTreasures = FishingTreasureConfig.getInstance().fishingRewards.get(
                        rarity);

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

        //TODO: Add option to randomize the amount rewarded
        /*if (treasureDrop.getAmount() > 1) {
            treasureDrop.setAmount(Misc.getRandom().nextInt(treasureDrop.getAmount()) + 1);
        }*/

        treasure.setDrop(treasureDrop);

        return treasure;
    }

    /**
     * Process the Magic Hunter ability
     *
     * @param treasureDrop The {@link ItemStack} to enchant
     */
    private Map<Enchantment, Integer> processMagicHunter(@NotNull ItemStack treasureDrop) {
        Map<Enchantment, Integer> enchants = new HashMap<>();
        List<EnchantmentTreasure> fishingEnchantments = null;

        double diceRoll = Misc.getRandom().nextDouble() * 100;

        for (Rarity rarity : Rarity.values()) {

            double dropRate = FishingTreasureConfig.getInstance()
                    .getEnchantmentDropRate(getLootTier(), rarity);

            if (diceRoll <= dropRate) {
                // Make sure enchanted books always get some kind of enchantment.  --hoorigan
                if (treasureDrop.getType() == Material.ENCHANTED_BOOK) {
                    diceRoll = dropRate + 1;
                    continue;
                }

                fishingEnchantments = FishingTreasureConfig.getInstance().fishingEnchantments.get(
                        rarity);
                break;
            }

            diceRoll -= dropRate;
        }

        if (fishingEnchantments == null) {
            return enchants;
        }

        List<Enchantment> validEnchantments = getPossibleEnchantments(treasureDrop);
        List<EnchantmentTreasure> possibleEnchants = new ArrayList<>();

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

            if (treasureDrop.getItemMeta().hasConflictingEnchant(possibleEnchantment)
                    || Misc.getRandom().nextInt(specificChance) != 0) {
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

        List<Enchantment> possibleEnchantments = new ArrayList<>();

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
        return getVanillaXPBoostModifier();
    }

    public int getMasterAnglerMinWaitLowerBound() {
        return masterAnglerMinWaitLowerBound;
    }

    public int getMasterAnglerMaxWaitLowerBound() {
        return masterAnglerMaxWaitLowerBound;
    }
}