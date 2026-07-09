package com.gmail.nossr50.skills.herbalism;

import static com.gmail.nossr50.util.ItemUtils.hasItemIncludingOffHand;
import static com.gmail.nossr50.util.ItemUtils.removeItemIncludingOffHand;
import static com.gmail.nossr50.util.Misc.TICK_CONVERSION_FACTOR;
import static com.gmail.nossr50.util.Misc.getBlockCenter;
import static com.gmail.nossr50.util.Permissions.isSubSkillEnabled;
import static com.gmail.nossr50.util.skills.RankUtils.hasUnlockedSubskill;
import static com.gmail.nossr50.util.text.ConfigStringUtils.getMaterialConfigString;
import static java.util.Objects.requireNonNull;

import com.gmail.nossr50.api.FakeBlockBreakEventType;
import com.gmail.nossr50.api.ItemSpawnReason;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.config.treasure.TreasureConfig;
import com.gmail.nossr50.datatypes.BlockSnapshot;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.meta.RecentlyReplantedCropMeta;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.datatypes.treasure.HylianTreasure;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.skills.DelayedCropReplant;
import com.gmail.nossr50.runnables.skills.PlantCollapseXpTask;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.CancellableRunnable;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import com.gmail.nossr50.util.text.StringUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public class HerbalismManager extends SkillManager {
    private final static HashMap<String, Integer> plantBreakLimits;

    private static final String CACTUS_ID = "cactus";
    private static final String CACTUS_FLOWER_STR = "cactus_flower";
    private static final String BAMBOO_ID = "bamboo";
    private static final String SUGAR_CANE_ID = "sugar_cane";
    private static final String KELP_ID = "kelp";
    private static final String KELP_PLANT_ID = "kelp_plant";
    private static final String CHORUS_PLANT_ID = "chorus_plant";
    private static final String SWEET_BERRY_BUSH_ID = "sweet_berry_bush";

    static {
        plantBreakLimits = new HashMap<>();
        plantBreakLimits.put(CACTUS_ID, 3);
        plantBreakLimits.put(BAMBOO_ID, 20);
        plantBreakLimits.put(SUGAR_CANE_ID, 3);
        plantBreakLimits.put(KELP_ID, 26);
        plantBreakLimits.put(KELP_PLANT_ID, 26);
        plantBreakLimits.put(CHORUS_PLANT_ID, 22);
    }

    public HerbalismManager(McMMOPlayer mmoPlayer) {
        super(mmoPlayer, PrimarySkillType.HERBALISM);
    }

    public boolean canGreenThumbBlock(BlockState blockState) {
        if (!hasUnlockedSubskill(getPlayer(), SubSkillType.HERBALISM_GREEN_THUMB)) {
            return false;
        }

        final Player player = getPlayer();
        final ItemStack item = player.getInventory().getItemInMainHand();

        return item.getAmount() > 0
                && item.getType() == Material.WHEAT_SEEDS
                && BlockUtils.canMakeMossy(blockState.getBlock())
                && Permissions.greenThumbBlock(player, blockState.getType());
    }

    public boolean canUseShroomThumb(BlockState blockState) {
        if (!BlockUtils.canMakeShroomy(blockState)) {
            return false;
        }

        if (!hasUnlockedSubskill(getPlayer(), SubSkillType.HERBALISM_SHROOM_THUMB)) {
            return false;
        }

        Player player = getPlayer();
        PlayerInventory inventory = player.getInventory();
        Material itemType = inventory.getItemInMainHand().getType();

        return (itemType == Material.BROWN_MUSHROOM
                || itemType == Material.RED_MUSHROOM)
                && inventory.contains(Material.BROWN_MUSHROOM, 1)
                && inventory.contains(Material.RED_MUSHROOM, 1)
                && isSubSkillEnabled(player, SubSkillType.HERBALISM_SHROOM_THUMB);
    }

    public void processBerryBushHarvesting(@NotNull BlockState blockState) {
        /* Check if the player is harvesting a berry bush */
        if (blockState.getType().toString().equalsIgnoreCase(SWEET_BERRY_BUSH_ID)) {
            if (mmoPlayer.isDebugMode()) {
                mmoPlayer.getPlayer().sendMessage("Processing sweet berry bush rewards");
            }
            //Check the age
            if (blockState.getBlockData() instanceof Ageable ageable) {
                int rewardByAge = 0;

                if (ageable.getAge() == 2) {
                    rewardByAge = 1; //Normal XP
                } else if (ageable.getAge() == 3) {
                    rewardByAge = 2; //Double XP
                } else {
                    return; //Not old enough, back out of processing
                }

                if (mmoPlayer.isDebugMode()) {
                    mmoPlayer.getPlayer().sendMessage("Bush Reward Multiplier: " + rewardByAge);
                }

                int xpReward = ExperienceConfig.getInstance()
                        .getXp(PrimarySkillType.HERBALISM, blockState.getType())
                        * rewardByAge;

                if (mmoPlayer.isDebugMode()) {
                    mmoPlayer.getPlayer().sendMessage("Bush XP: " + xpReward);
                }

                CheckBushAge checkBushAge = new CheckBushAge(blockState.getBlock(), mmoPlayer,
                        xpReward);
                mcMMO.p.getFoliaLib().getScheduler()
                        .runAtLocationLater(blockState.getLocation(), checkBushAge, 1);
            }
        }
    }

    private class CheckBushAge extends CancellableRunnable {

        @NotNull Block block;
        @NotNull McMMOPlayer mmoPlayer;
        int xpReward;

        public CheckBushAge(@NotNull Block block, @NotNull McMMOPlayer mmoPlayer, int xpReward) {
            this.block = block;
            this.mmoPlayer = mmoPlayer;
            this.xpReward = xpReward;
        }

        @Override
        public void run() {
            BlockState blockState = block.getState();

            if (blockState.getType().toString().equalsIgnoreCase(SWEET_BERRY_BUSH_ID)) {
                if (blockState.getBlockData() instanceof Ageable ageable) {

                    if (ageable.getAge() <= 1) {
                        applyXpGain(xpReward, XPGainReason.PVE, XPGainSource.SELF);
                    }
                }
            }
        }
    }


    public boolean canUseHylianLuck() {
        if (!hasUnlockedSubskill(getPlayer(), SubSkillType.HERBALISM_HYLIAN_LUCK)) {
            return false;
        }

        return isSubSkillEnabled(getPlayer(), SubSkillType.HERBALISM_HYLIAN_LUCK);
    }

    public boolean canActivateAbility() {
        return mmoPlayer.getToolPreparationMode(ToolType.HOE)
                && Permissions.greenTerra(getPlayer());
    }

    public boolean isGreenTerraActive() {
        return mmoPlayer.getAbilityMode(SuperAbilityType.GREEN_TERRA);
    }

    /**
     * Handle the Farmer's Diet ability
     *
     * @param eventFoodLevel The initial change in hunger from the event
     * @return the modified change in hunger for the event
     */
    public int farmersDiet(int eventFoodLevel) {
        return SkillUtils.handleFoodSkills(getPlayer(), eventFoodLevel,
                SubSkillType.HERBALISM_FARMERS_DIET);
    }

    public void processGreenTerraBlockConversion(BlockState blockState) {
        final Player player = getPlayer();

        if (!Permissions.greenThumbBlock(player, blockState.getType())) {
            return;
        }

        PlayerInventory playerInventory = player.getInventory();
        ItemStack seed = new ItemStack(Material.WHEAT_SEEDS);

        if (!playerInventory.containsAtLeast(seed, 1)) {
            NotificationManager.sendPlayerInformation(player,
                    NotificationType.REQUIREMENTS_NOT_MET, "Herbalism.Ability.GTe.NeedMore");
            return;
        }

        playerInventory.removeItem(seed);
        // player.updateInventory();

        Herbalism.convertGreenTerraBlocks(blockState);
        blockState.update(true);
    }

    /**
     * Process the Green Terra ability.
     *
     * @param block The {@link Block} to check ability activation for
     */
    public void processGreenTerraBlockConversion(Block block) {
        processGreenTerraBlockConversion(block.getState());
    }

    /**
     * Handles herbalism abilities and XP rewards from a BlockBreakEvent
     *
     * @param blockBreakEvent The Block Break Event to process
     */
    public void processHerbalismBlockBreakEvent(BlockBreakEvent blockBreakEvent) {
        final Player player = getPlayer();

        final Block block = blockBreakEvent.getBlock();

        if (mcMMO.p.getGeneralConfig().getHerbalismPreventAFK() && player.isInsideVehicle()) {
            if (block.hasMetadata(MetadataConstants.METADATA_KEY_REPLANT)) {
                block.removeMetadata(MetadataConstants.METADATA_KEY_REPLANT, mcMMO.p);
            }
            return;
        }

        //Check if the plant was recently replanted
        if (block.getBlockData() instanceof Ageable ageableCrop) {
            if (!block.getMetadata(MetadataConstants.METADATA_KEY_REPLANT).isEmpty()) {
                if (block.getMetadata(MetadataConstants.METADATA_KEY_REPLANT).get(0).asBoolean()) {
                    if (isAgeableMature(ageableCrop)) {
                        block.removeMetadata(MetadataConstants.METADATA_KEY_REPLANT, mcMMO.p);
                    } else {
                        //Crop is recently replanted to back out of destroying it
                        blockBreakEvent.setCancelled(true);
                        return;
                    }
                }
            }
        }

        /*
         * There are single-block plants and multi-block plants in Minecraft
         * In order to give out proper rewards, we need to collect all blocks that would be broken from this event
         */

        //Grab all broken blocks
        final HashSet<Block> brokenBlocks = getBrokenHerbalismBlocks(blockBreakEvent);

        if (brokenBlocks.isEmpty()) {
            return;
        }

        //Handle rewards, xp, ability interactions, etc
        processHerbalismOnBlocksBroken(blockBreakEvent, brokenBlocks);
    }

    /**
     * Process rewards for a set of plant blocks for Herbalism
     *
     * @param blockBreakEvent the block break event
     * @param brokenPlants plant blocks to process
     */
    private void processHerbalismOnBlocksBroken(BlockBreakEvent blockBreakEvent,
            HashSet<Block> brokenPlants) {
        if (blockBreakEvent.isCancelled()) {
            return;
        }

        final BlockState originalBreak = blockBreakEvent.getBlock().getState();

        //TODO: The design of Green Terra needs to change, this is a mess
        if (Permissions.greenThumbPlant(getPlayer(), originalBreak.getType())) {
            if (mcMMO.p.getGeneralConfig().isGreenThumbReplantableCrop(originalBreak.getType())) {
                if (!getPlayer().isSneaking()) {
                    processGreenThumbPlants(originalBreak, blockBreakEvent, isGreenTerraActive());
                }
            }
        }

        final Block originBlock = originalBreak.getBlock();

        // This break event is now the authoritative source of rewards for this position; any
        // older collapse verification still watching it must not also pay for it
        PlantCollapseXpTask.revokeClaim(originBlock);

        // Only the origin block is marked for bonus drops. The connected blocks vanilla pops
        // on later ticks never fire BlockDropItemEvent, so marking them never doubled anything
        // and only left stale metadata behind on blocks that survived the break
        checkDoubleDropsOnBrokenPlants(blockBreakEvent.getPlayer(), List.of(originBlock));

        // The tall-plant XP limit spans the whole plant, so the budget for the connected
        // blocks depends on what the origin pays out right now
        final int collapseXpBudget = calculateCollapseXpBudget(originalBreak);

        // The origin block is guaranteed broken by this event and pays out immediately. The
        // rest of the plant is only destroyed by scheduled block ticks on later ticks - if it
        // is destroyed at all - so its XP is paid once the blocks have verifiably broken
        final HashSet<Block> originOnly = new HashSet<>();
        originOnly.add(originBlock);
        awardXPForPlantBlocks(originOnly);

        scheduleCollapseVerification(originalBreak, brokenPlants, collapseXpBudget);
    }

    /**
     * Claims every connected plant block for deferred XP verification and starts the collapse
     * verification task if any claims were made. Blocks already claimed by an earlier break are
     * skipped, so rapid re-breaks cannot collect XP for the same blocks twice.
     *
     * @param originalBreak the block broken by the event
     * @param brokenPlants all plant blocks expected to break because of the event
     * @param collapseXpBudget the most XP the verification task may pay out
     */
    private void scheduleCollapseVerification(BlockState originalBreak,
            HashSet<Block> brokenPlants, int collapseXpBudget) {
        if (brokenPlants.size() <= 1) {
            // Single-block plants have nothing left to verify
            return;
        }

        final PlantCollapseXpTask collapseXpTask = new PlantCollapseXpTask(mmoPlayer,
                collapseXpBudget);
        final Location originLocation = originalBreak.getLocation();

        for (Block brokenPlant : brokenPlants) {
            if (brokenPlant.getLocation().equals(originLocation)) {
                continue;
            }

            final BlockState plantState = brokenPlant.getState();
            collapseXpTask.claimBlock(brokenPlant, calculatePlantXp(plantState),
                    mcMMO.getUserBlockTracker().isIneligible(plantState));
        }

        if (collapseXpTask.hasPendingBlocks()) {
            collapseXpTask.schedule(originLocation);
        }
    }

    /**
     * Calculates how much XP the collapse verification task may still pay out after the origin
     * block's immediate reward, honoring the tall-plant XP limit.
     *
     * @param originalBreak the block broken by the event
     * @return the remaining XP budget for the rest of the plant
     */
    private int calculateCollapseXpBudget(BlockState originalBreak) {
        if (!ExperienceConfig.getInstance().limitXPOnTallPlants()) {
            return Integer.MAX_VALUE;
        }

        final Integer plantLimit = plantBreakLimits.get(
                originalBreak.getType().getKey().getKey());
        if (plantLimit == null) {
            return Integer.MAX_VALUE;
        }

        final int xpPerBlock = ExperienceConfig.getInstance()
                .getXp(PrimarySkillType.HERBALISM, originalBreak.getType());
        return Math.max(0, plantLimit * xpPerBlock - calculatePlantXp(originalBreak));
    }

    /**
     * Check for double drops on a collection of broken blocks If a double drop has occurred, it
     * will be marked here for bonus drops
     *
     * @param player player who broke the blocks
     * @param brokenPlants the collection of broken plants
     */
    public void checkDoubleDropsOnBrokenPlants(Player player, Collection<Block> brokenPlants) {

        //Only proceed if skill unlocked and permission enabled
        if (!hasUnlockedSubskill(player, SubSkillType.HERBALISM_DOUBLE_DROPS)
                || !isSubSkillEnabled(player, SubSkillType.HERBALISM_DOUBLE_DROPS)) {
            return;
        }

        for (Block brokenPlant : brokenPlants) {
            BlockState brokenPlantState = brokenPlant.getState();
            BlockData plantData = brokenPlantState.getBlockData();

            //Check for double drops
            if (!mcMMO.getUserBlockTracker().isIneligible(brokenPlant)) {
                /*
                 * Natural Blocks
                 */
                //Not all things that are natural should give double drops, make sure its fully mature as well
                if (plantData instanceof Ageable ageable) {

                    if (isAgeableMature(ageable) || isBizarreAgeable(plantData)) {
                        if (checkDoubleDrop(brokenPlant)) {
                            markForBonusDrops(brokenPlant);
                        }
                    }
                } else if (checkDoubleDrop(brokenPlant)) {
                    //Add metadata to mark this block for double or triple drops
                    markForBonusDrops(brokenPlant);
                }
            } else {
                /*
                 * Unnatural Blocks
                 */
                //If it's a crop, we need to reward XP when its fully grown
                if (isAgeableAndFullyMature(plantData) && !isBizarreAgeable(plantData)) {
                    //Add metadata to mark this block for double or triple drops
                    markForBonusDrops(brokenPlant);
                }
            }
        }
    }

    /**
     * Checks if BlockData is bizarre ageable, and we cannot trust that age for Herbalism rewards/XP
     * reasons
     *
     * @param blockData target BlockData
     * @return returns true if the BlockData is a bizarre ageable for Herbalism XP / Rewards
     */
    public boolean isBizarreAgeable(BlockData blockData) {
        if (blockData instanceof Ageable) {
            // Cactus and Sugar Canes cannot be trusted
            return switch (blockData.getMaterial()) {
                case CACTUS, KELP, SUGAR_CANE, BAMBOO -> true;
                default -> false;
            };
        }

        return false;
    }

    public boolean canUseVerdantBounty() {
        return Permissions.canUseSubSkill(getPlayer(), SubSkillType.HERBALISM_VERDANT_BOUNTY);
    }

    /**
     * Mark a block for bonus drops.
     * <p>
     * Triple drops are awarded when Green Terra is active, or when the Verdant Bounty RNG check
     * succeeds. Otherwise, double drops are awarded.
     *
     * @param block the block to mark
     */
    public void markForBonusDrops(Block block) {
        final boolean triple = mmoPlayer.getAbilityMode(SuperAbilityType.GREEN_TERRA)
                || (canUseVerdantBounty() && ProbabilityUtil.isSkillRNGSuccessful(
                        SubSkillType.HERBALISM_VERDANT_BOUNTY, mmoPlayer));
        BlockUtils.markDropsAsBonus(block, triple);
    }

    /**
     * Checks if a block is an ageable and if that ageable is fully mature
     *
     * @param plantData target plant
     * @return returns true if the block is both an ageable and fully mature
     */
    public boolean isAgeableAndFullyMature(BlockData plantData) {
        return plantData instanceof Ageable && isAgeableMature((Ageable) plantData);
    }

    public void awardXPForPlantBlocks(HashSet<Block> brokenPlants) {
        int xpToReward = 0;
        int firstXpReward = -1;

        for (Block brokenPlantBlock : brokenPlants) {
            final BlockState brokenBlockNewState = brokenPlantBlock.getState();
            final int plantXp = calculatePlantXp(brokenBlockNewState);

            if (plantXp > 0) {
                xpToReward += plantXp;
                if (firstXpReward == -1) {
                    firstXpReward = plantXp;
                }
            }

            //Mark it as natural again as it is being broken
            if (mcMMO.getUserBlockTracker().isIneligible(brokenBlockNewState)) {
                mcMMO.getUserBlockTracker().setEligible(brokenBlockNewState);
            }
        }

        if (mmoPlayer.isDebugMode()) {
            mmoPlayer.getPlayer().sendMessage("Plants processed: " + brokenPlants.size());
        }

        //Reward XP
        if (xpToReward > 0) {
            // get first block from hash set using stream API
            final Block firstBlock = brokenPlants.stream().findFirst().orElse(null);
            if (firstBlock != null
                    && ExperienceConfig.getInstance().limitXPOnTallPlants()
                    && plantBreakLimits.containsKey(firstBlock.getType().getKey().getKey())) {
                int limit = plantBreakLimits.get(firstBlock.getType().getKey().getKey())
                        * firstXpReward;
                // Plant may be unnaturally tall, limit XP
                applyXpGain(Math.min(xpToReward, limit), XPGainReason.PVE, XPGainSource.SELF);
            } else {
                applyXpGain(xpToReward, XPGainReason.PVE, XPGainSource.SELF);
            }
        }
    }

    /**
     * Calculates the Herbalism XP a single plant block is worth under the natural/unnatural and
     * maturity rules. Player-placed blocks only reward XP for fully grown crops whose age can be
     * trusted; natural ageable blocks must be mature unless their age is untrustworthy.
     *
     * @param plantState the state of the plant block
     * @return the XP the block is worth, or 0 when it rewards nothing
     */
    private int calculatePlantXp(BlockState plantState) {
        final BlockData plantData = plantState.getBlockData();

        if (mcMMO.getUserBlockTracker().isIneligible(plantState)) {
            if (isAgeableAndFullyMature(plantData) && !isBizarreAgeable(plantData)) {
                return ExperienceConfig.getInstance()
                        .getXp(PrimarySkillType.HERBALISM, plantState.getType());
            }
            return 0;
        }

        if (plantData instanceof Ageable plantAgeable && !isAgeableMature(plantAgeable)
                && !isBizarreAgeable(plantData)) {
            return 0;
        }

        return ExperienceConfig.getInstance()
                .getXp(PrimarySkillType.HERBALISM, plantState.getType());
    }

    public boolean isAgeableMature(Ageable ageable) {
        // Sweet berry bush is harvestable at age 2 and 3 (max is 3)
        if (ageable.getMaterial() == Material.SWEET_BERRY_BUSH) {
            return ageable.getAge() >= 2;
        }
        return ageable.getAge() == ageable.getMaximumAge()
                && ageable.getAge() != 0;
    }

    /**
     * Award XP for any blocks that used to be something else but are now AIR
     *
     * @param brokenPlants snapshot of broken blocks
     * @deprecated XP for multi-block plants, chorus trees included, is verified and awarded by
     *         {@link com.gmail.nossr50.runnables.skills.PlantCollapseXpTask}
     */
    @Deprecated(forRemoval = true, since = "2.3.000")
    public void awardXPForBlockSnapshots(ArrayList<BlockSnapshot> brokenPlants) {
        /*
         * This handles XP for blocks that we need to check are broken after the fact
         * This only applies to chorus trees right now
         */
        int xpToReward = 0;
        int blocksGivingXP = 0;

        for (BlockSnapshot blockSnapshot : brokenPlants) {
            final BlockState brokenBlockNewState = blockSnapshot.getBlockRef().getState();

            //Remove metadata from the snapshot of blocks
            if (brokenBlockNewState.hasMetadata(MetadataConstants.METADATA_KEY_BONUS_DROPS)) {
                brokenBlockNewState.removeMetadata(MetadataConstants.METADATA_KEY_BONUS_DROPS,
                        mcMMO.p);
            }

            //If the block is not AIR that means it wasn't broken
            if (brokenBlockNewState.getType() != Material.AIR) {
                continue;
            }

            if (mcMMO.getUserBlockTracker().isIneligible(brokenBlockNewState)) {
                //Mark it as natural again as it is being broken
                mcMMO.getUserBlockTracker().setEligible(brokenBlockNewState);
            } else {
                //TODO: Do we care about chorus flower age?
                //Calculate XP for the old type
                xpToReward += ExperienceConfig.getInstance()
                        .getXp(PrimarySkillType.HERBALISM, blockSnapshot.getOldType());
                blocksGivingXP++;
            }
        }

        if (mmoPlayer.isDebugMode()) {
            mmoPlayer.getPlayer()
                    .sendMessage("Chorus Plants checked for XP: " + brokenPlants.size());
            mmoPlayer.getPlayer().sendMessage("Valid Chorus Plant XP Gains: " + blocksGivingXP);
        }

        //Reward XP
        if (xpToReward > 0) {
            applyXpGain(xpToReward, XPGainReason.PVE, XPGainSource.SELF);
        }
    }

    /**
     * Process and return plant blocks from a BlockBreakEvent
     *
     * @param blockBreakEvent target event
     * @return a set of plant-blocks that were broken as a result of this event
     */
    private HashSet<Block> getBrokenHerbalismBlocks(@NotNull BlockBreakEvent blockBreakEvent) {
        //Get an updated capture of this block
        final BlockState originBlockState = blockBreakEvent.getBlock().getState();
        final Material originBlockMaterial = originBlockState.getType();
        final HashSet<Block> blocksBroken = new HashSet<>(); //Blocks broken

        //Add the initial block
        blocksBroken.add(originBlockState.getBlock());

        if (!isOneBlockPlant(originBlockMaterial)) {
            //If the block is a multi-block structure, capture a set of all blocks broken and return that
            addBrokenBlocksMultiBlockPlants(originBlockState, blocksBroken);
        }

        //Return all broken plant-blocks
        return blocksBroken;
    }

    private void addChorusTreeBrokenBlocks(Block currentBlock, Set<Block> traversed) {
        if (!isChorusTree(currentBlock.getType())) {
            return;
        }

        // Prevent any infinite loops, who needs more than 256 chorus anyways
        if (traversed.size() > 256) {
            return;
        }

        if (!traversed.add(currentBlock)) {
            return;
        }

        //Grab all Blocks in the Tree
        for (BlockFace blockFace : new BlockFace[]{BlockFace.UP, BlockFace.NORTH, BlockFace.SOUTH,
                BlockFace.EAST, BlockFace.WEST}) {
            addChorusTreeBrokenBlocks(currentBlock.getRelative(blockFace, 1), traversed);
        }
    }


    protected void addBrokenBlocksMultiBlockPlants(BlockState brokenBlock,
            Set<Block> brokenBlocks) {
        if (isChorusBranch(brokenBlock.getType())) {
            // Traverse with a fresh set: the origin block is already in brokenBlocks, and the
            // traversal's visited-check would otherwise stop before collecting anything
            final Set<Block> traversed = new HashSet<>();
            addChorusTreeBrokenBlocks(brokenBlock.getBlock(), traversed);
            brokenBlocks.addAll(traversed);
        } else if (isCactus(brokenBlock.getType())) {
            final Set<Block> traversed = new HashSet<>();
            addCactusBlocks(brokenBlock.getBlock(), traversed);
            brokenBlocks.addAll(traversed);
        } else {
            addBlocksBrokenAboveOrBelow(brokenBlock.getBlock(), brokenBlocks,
                    mcMMO.getMaterialMapStore().isMultiBlockHangingPlant(brokenBlock.getType()));
        }
    }

    private void addCactusBlocks(Block currentBlock, Set<Block> traversed) {
        if (!isCactus(currentBlock.getType())) {
            return;
        }

        if (traversed.size() > 4) // Max size 3 cactus + flower
        {
            return;
        }

        if (!traversed.add(currentBlock)) {
            return;
        }

        addCactusBlocks(currentBlock.getRelative(BlockFace.UP), traversed);
        addCactusBlocks(currentBlock.getRelative(BlockFace.DOWN), traversed);
    }

    private boolean isCactus(Material material) {
        return material.getKey().getKey().equalsIgnoreCase(CACTUS_ID)
                || material.getKey().getKey().equalsIgnoreCase(CACTUS_FLOWER_STR);
    }

    private boolean isChorusBranch(Material blockType) {
        return blockType == Material.CHORUS_PLANT;
    }

    private boolean isChorusTree(Material blockType) {
        return blockType == Material.CHORUS_PLANT || blockType == Material.CHORUS_FLOWER;
    }

    private void addBlocksBrokenAboveOrBelow(Block originBlock, Set<Block> brokenBlocks,
            boolean below) {
        //Limit our search
        int maxHeight = 512;

        final BlockFace relativeFace = below ? BlockFace.DOWN : BlockFace.UP;

        // Search vertically for multi-block plants, exit early if any non-multi block plants
        for (int y = 0; y < maxHeight; y++) {
            final Block relativeBlock = originBlock.getRelative(relativeFace, y);

            //Abandon our search if the block isn't multi
            if (isOneBlockPlant(relativeBlock.getType())) {
                break;
            }

            brokenBlocks.add(relativeBlock);
        }
    }

    /**
     * If the plant is considered a one block plant This is determined by seeing if it exists in a
     * hard-coded collection of Multi-Block plants
     *
     * @param material target plant material
     * @return true if the block is not contained in the collection of multi-block plants
     */
    private boolean isOneBlockPlant(Material material) {
        return !mcMMO.getMaterialMapStore().isMultiBlockPlant(material)
                && !mcMMO.getMaterialMapStore().isMultiBlockHangingPlant(material);
    }

    /**
     * Check for success on herbalism double drops
     *
     * @param block target block state
     * @return true if the double drop succeeds
     */
    private boolean checkDoubleDrop(@NotNull Block block) {
        requireNonNull(block, "BlockState cannot be null");
        return BlockUtils.checkDoubleDrops(mmoPlayer, block, SubSkillType.HERBALISM_DOUBLE_DROPS);
    }

    /**
     * Process the Green Thumb ability for blocks.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @return true if the ability was successful, false otherwise
     */
    public boolean processGreenThumbBlocks(BlockState blockState) {
        if (!ProbabilityUtil.isSkillRNGSuccessful(SubSkillType.HERBALISM_GREEN_THUMB, mmoPlayer)) {
            NotificationManager.sendPlayerInformation(getPlayer(),
                    NotificationType.SUBSKILL_MESSAGE_FAILED, "Herbalism.Ability.GTh.Fail");
            return false;
        }

        return Herbalism.convertGreenTerraBlocks(blockState);
    }

    /**
     * Process the Hylian Luck ability.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @return true if the ability was successful, false otherwise
     */
    public boolean processHylianLuck(BlockState blockState) {
        if (!ProbabilityUtil.isSkillRNGSuccessful(SubSkillType.HERBALISM_HYLIAN_LUCK, mmoPlayer)) {
            return false;
        }

        final String materialConfigString = getMaterialConfigString(
                blockState.getBlockData().getMaterial());
        if (!TreasureConfig.getInstance().hylianMap.containsKey(materialConfigString)) {
            return false;
        }
        List<HylianTreasure> treasures = TreasureConfig.getInstance().hylianMap.get(
                materialConfigString);

        if (treasures.isEmpty()) {
            return false;
        }
        int skillLevel = getSkillLevel();
        final Location centerOfBlock = getBlockCenter(blockState);

        for (HylianTreasure treasure : treasures) {
            if (skillLevel >= treasure.getDropLevel()
                    && ProbabilityUtil.isStaticSkillRNGSuccessful(PrimarySkillType.HERBALISM,
                    mmoPlayer, treasure.getDropChance())) {
                if (!EventUtils.simulateBlockBreak(blockState.getBlock(), mmoPlayer.getPlayer(),
                        FakeBlockBreakEventType.FAKE)) {
                    return false;
                }
                blockState.setType(Material.AIR);
                ItemUtils.spawnItem(getPlayer(), centerOfBlock, treasure.getDrop(),
                        ItemSpawnReason.HYLIAN_LUCK_TREASURE);
                NotificationManager.sendPlayerInformation(mmoPlayer.getPlayer(),
                        NotificationType.SUBSKILL_MESSAGE, "Herbalism.HylianLuck");
                return true;
            }
        }
        return false;
    }

    /**
     * Process the Shroom Thumb ability.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @return true if the ability was successful, false otherwise
     */
    public boolean processShroomThumb(BlockState blockState) {
        PlayerInventory playerInventory = getPlayer().getInventory();

        if (!playerInventory.contains(Material.BROWN_MUSHROOM, 1)) {
            NotificationManager.sendPlayerInformation(getPlayer(),
                    NotificationType.REQUIREMENTS_NOT_MET, "Skills.NeedMore",
                    StringUtils.getPrettyMaterialString(Material.BROWN_MUSHROOM));
            return false;
        }

        if (!playerInventory.contains(Material.RED_MUSHROOM, 1)) {
            NotificationManager.sendPlayerInformation(getPlayer(),
                    NotificationType.REQUIREMENTS_NOT_MET, "Skills.NeedMore",
                    StringUtils.getPrettyMaterialString(Material.RED_MUSHROOM));
            return false;
        }

        // Consume mushrooms by material so renamed/custom-meta variants are not skipped by
        // CraftBukkit's stricter ItemStack similarity matcher.
        removeItemIncludingOffHand(getPlayer(), Material.BROWN_MUSHROOM, 1);
        removeItemIncludingOffHand(getPlayer(), Material.RED_MUSHROOM, 1);

        if (!ProbabilityUtil.isSkillRNGSuccessful(SubSkillType.HERBALISM_SHROOM_THUMB, mmoPlayer)) {
            NotificationManager.sendPlayerInformation(getPlayer(),
                    NotificationType.SUBSKILL_MESSAGE_FAILED, "Herbalism.Ability.ShroomThumb.Fail");
            return false;
        }

        return Herbalism.convertShroomThumb(blockState);
    }

    /**
     * Starts the delayed replant task and turns
     *
     * @param desiredCropAge the desired age of the crop
     * @param blockBreakEvent the {@link BlockBreakEvent} this crop was involved in
     * @param cropState the {@link BlockState} of the crop
     */
    private void startReplantTask(int desiredCropAge, BlockBreakEvent blockBreakEvent,
            BlockState cropState, boolean isImmature) {
        //Mark the plant as recently replanted to avoid accidental breakage
        mcMMO.p.getFoliaLib().getScheduler()
                .runAtLocationLater(blockBreakEvent.getBlock().getLocation(),
                        new DelayedCropReplant(blockBreakEvent, cropState, desiredCropAge,
                                isImmature), TICK_CONVERSION_FACTOR);
        blockBreakEvent.getBlock().setMetadata(MetadataConstants.METADATA_KEY_REPLANT,
                new RecentlyReplantedCropMeta(mcMMO.p, true));
    }

    /**
     * Process the Green Thumb ability for plants.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @param greenTerra boolean to determine if greenTerra is active or not
     */
    private boolean processGreenThumbPlants(@NotNull BlockState blockState,
            @NotNull BlockBreakEvent blockBreakEvent,
            boolean greenTerra) {
        if (!ItemUtils.isHoe(blockBreakEvent.getPlayer().getInventory().getItemInMainHand())
                && !ItemUtils.isAxe(
                blockBreakEvent.getPlayer().getInventory().getItemInMainHand())) {
            return false;
        }

        final BlockData blockData = blockState.getBlockData();

        if (!(blockData instanceof Ageable ageable)) {
            return false;
        }

        //If the ageable is NOT mature and the player is NOT using a hoe, abort

        final Player player = getPlayer();
        final Material replantMaterial;

        switch (blockState.getType().getKey().getKey().toLowerCase(Locale.ENGLISH)) {
            case "carrots" -> replantMaterial = Material.matchMaterial("CARROT");
            case "wheat" -> replantMaterial = Material.matchMaterial("WHEAT_SEEDS");
            case "nether_wart" -> replantMaterial = Material.getMaterial("NETHER_WART");
            case "potatoes" -> replantMaterial = Material.matchMaterial("POTATO");
            case "beetroots" -> replantMaterial = Material.matchMaterial("BEETROOT_SEEDS");
            case "cocoa" -> replantMaterial = Material.matchMaterial("COCOA_BEANS");
            case "torchflower" -> replantMaterial = Material.matchMaterial("TORCHFLOWER_SEEDS");
            case "sweet_berry_bush" -> replantMaterial = Material.matchMaterial("SWEET_BERRIES");
            default -> {
                return false;
            }
        }

        if (replantMaterial == null) {
            return false;
        }

        if (ItemUtils.isAxe(blockBreakEvent.getPlayer().getInventory().getItemInMainHand())
                && blockState.getType() != Material.COCOA) {
            return false;
        }

        if (!greenTerra && !ProbabilityUtil.isSkillRNGSuccessful(SubSkillType.HERBALISM_GREEN_THUMB,
                mmoPlayer)) {
            return false;
        }

        if (!hasItemIncludingOffHand(player, replantMaterial)) {
            return false;
        }

        if (EventUtils.callSubSkillBlockEvent(player, SubSkillType.HERBALISM_GREEN_THUMB,
                        blockState.getBlock())
                .isCancelled()) {
            return false;
        } else {
            if (!processGrowingPlants(blockState, ageable, blockBreakEvent, greenTerra)) {
                return false;
            }
            // remove the item from the player's inventory
            removeItemIncludingOffHand(player, replantMaterial, 1);
            // player.updateInventory(); // Needed until replacement available

            //Play sound
            SoundManager.sendSound(player, player.getLocation(), SoundType.ITEM_CONSUMED);
            return true;
        }
    }

    private boolean processGrowingPlants(BlockState blockState, Ageable ageable,
            BlockBreakEvent blockBreakEvent, boolean greenTerra) {
        //This check is needed
        if (isBizarreAgeable(ageable)) {
            return false;
        }

        int finalAge;
        int greenThumbStage = getGreenThumbStage(greenTerra);

        //Immature plants will start over at 0
        if (!isAgeableMature(ageable)) {
            startReplantTask(0, blockBreakEvent, blockState, true);
            return true;
        }

        switch (blockState.getType().getKey().getKey()) {

            case "potatoes":
            case "carrots":
            case "wheat":

                finalAge = getGreenThumbStage(greenTerra);
                break;

            case "beetroots":
            case "nether_wart":

                if (greenTerra || greenThumbStage > 2) {
                    finalAge = 2;
                } else if (greenThumbStage == 2) {
                    finalAge = 1;
                } else {
                    finalAge = 0;
                }
                break;

            case "cocoa":

                if (getGreenThumbStage(greenTerra) >= 2) {
                    finalAge = 1;
                } else {
                    finalAge = 0;
                }
                break;

            case "sweet_berry_bush":

                // Sweet berry bush has ages 0-3, where 2+ has berries
                // Cap at age 1 to prevent instant re-harvest exploit with enough herbalism levels
                if (greenTerra || greenThumbStage >= 2) {
                    finalAge = 1;
                } else {
                    finalAge = 0;
                }
                break;

            default:
                return false;
        }

        //Start the delayed replant
        startReplantTask(finalAge, blockBreakEvent, blockState, false);
        return true;
    }

    private int getGreenThumbStage(boolean greenTerraActive) {
        if (greenTerraActive) {
            return Math.min(RankUtils.getHighestRank(SubSkillType.HERBALISM_GREEN_THUMB),
                    RankUtils.getRank(getPlayer(), SubSkillType.HERBALISM_GREEN_THUMB) + 1);
        }

        return RankUtils.getRank(getPlayer(), SubSkillType.HERBALISM_GREEN_THUMB);
    }
}
