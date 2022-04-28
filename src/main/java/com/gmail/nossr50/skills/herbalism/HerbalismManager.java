package com.gmail.nossr50.skills.herbalism;

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
import com.gmail.nossr50.runnables.skills.DelayedHerbalismXPCheckTask;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.*;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.random.RandomChanceSkillStatic;
import com.gmail.nossr50.util.random.RandomChanceUtil;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillActivationType;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import com.gmail.nossr50.util.text.StringUtils;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class HerbalismManager extends SkillManager {
    public HerbalismManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, PrimarySkillType.HERBALISM);
    }

    public boolean canGreenThumbBlock(BlockState blockState) {
        if(!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.HERBALISM_GREEN_THUMB))
            return false;

        Player player = getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        return item.getAmount() > 0 && item.getType() == Material.WHEAT_SEEDS && BlockUtils.canMakeMossy(blockState) && Permissions.greenThumbBlock(player, blockState.getType());
    }

    public boolean canUseShroomThumb(BlockState blockState) {
        if(!BlockUtils.canMakeShroomy(blockState)) {
            return false;
        }

        if(!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.HERBALISM_SHROOM_THUMB))
            return false;

        Player player = getPlayer();
        PlayerInventory inventory = player.getInventory();
        Material itemType = inventory.getItemInMainHand().getType();

        return (itemType == Material.BROWN_MUSHROOM
                || itemType == Material.RED_MUSHROOM)
                && inventory.contains(Material.BROWN_MUSHROOM, 1)
                && inventory.contains(Material.RED_MUSHROOM, 1)
                && Permissions.isSubSkillEnabled(player, SubSkillType.HERBALISM_SHROOM_THUMB);
    }

    public void processBerryBushHarvesting(@NotNull BlockState blockState) {
        /* Check if the player is harvesting a berry bush */
        if(blockState.getType().toString().equalsIgnoreCase("sweet_berry_bush")) {
            if(mmoPlayer.isDebugMode()) {
                mmoPlayer.getPlayer().sendMessage("Processing sweet berry bush rewards");
            }
            //Check the age
            if(blockState.getBlockData() instanceof Ageable ageable) {
                int rewardByAge = 0;

                if(ageable.getAge() == 2) {
                    rewardByAge = 1; //Normal XP
                } else if(ageable.getAge() == 3) {
                    rewardByAge = 2; //Double XP
                } else {
                    return; //Not old enough, back out of processing
                }

                if(mmoPlayer.isDebugMode()) {
                    mmoPlayer.getPlayer().sendMessage("Bush Reward Multiplier: " + rewardByAge);
                }

                int xpReward = ExperienceConfig.getInstance().getXp(PrimarySkillType.HERBALISM, blockState) * rewardByAge;

                if(mmoPlayer.isDebugMode()) {
                    mmoPlayer.getPlayer().sendMessage("Bush XP: " + xpReward);
                }

                CheckBushAge checkBushAge = new CheckBushAge(blockState.getBlock(), mmoPlayer, xpReward);
                checkBushAge.runTaskLater(mcMMO.p, 1);
            }
        }
    }

    private class CheckBushAge extends BukkitRunnable {

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

            if(blockState.getType().toString().equalsIgnoreCase("sweet_berry_bush")) {
                if(blockState.getBlockData() instanceof Ageable ageable) {

                    if(ageable.getAge() <= 1) {
                        applyXpGain(xpReward, XPGainReason.PVE, XPGainSource.SELF);
                    }
                }
            }
        }
    }


    public boolean canUseHylianLuck() {
        if(!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.HERBALISM_HYLIAN_LUCK))
            return false;

        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.HERBALISM_HYLIAN_LUCK);
    }

    public boolean canGreenTerraBlock(BlockState blockState) {
        return mmoPlayer.getAbilityMode(SuperAbilityType.GREEN_TERRA) && BlockUtils.canMakeMossy(blockState);
    }

    public boolean canActivateAbility() {
        return mmoPlayer.getToolPreparationMode(ToolType.HOE) && Permissions.greenTerra(getPlayer());
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
        return SkillUtils.handleFoodSkills(getPlayer(), eventFoodLevel, SubSkillType.HERBALISM_FARMERS_DIET);
    }

    /**
     * Process the Green Terra ability.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @return true if the ability was successful, false otherwise
     */
    public boolean processGreenTerraBlockConversion(BlockState blockState) {
        Player player = getPlayer();

        if (!Permissions.greenThumbBlock(player, blockState.getType())) {
            return false;
        }

        PlayerInventory playerInventory = player.getInventory();
        ItemStack seed = new ItemStack(Material.WHEAT_SEEDS);

        if (!playerInventory.containsAtLeast(seed, 1)) {
            NotificationManager.sendPlayerInformation(player, NotificationType.REQUIREMENTS_NOT_MET, "Herbalism.Ability.GTe.NeedMore");
            return false;
        }

        playerInventory.removeItem(seed);
        player.updateInventory(); // Needed until replacement available

        return Herbalism.convertGreenTerraBlocks(blockState);
    }

    /**
     * Handles herbalism abilities and XP rewards from a BlockBreakEvent
     * @param blockBreakEvent The Block Break Event to process
     */
    public void processHerbalismBlockBreakEvent(BlockBreakEvent blockBreakEvent) {
        Player player = getPlayer();

        Block block = blockBreakEvent.getBlock();

        if (mcMMO.p.getGeneralConfig().getHerbalismPreventAFK() && player.isInsideVehicle()) {
            if(block.hasMetadata(MetadataConstants.METADATA_KEY_REPLANT)) {
                block.removeMetadata(MetadataConstants.METADATA_KEY_REPLANT, mcMMO.p);
            }
            return;
        }

        //Check if the plant was recently replanted
        if(block.getBlockData() instanceof Ageable ageableCrop) {
            if(block.getMetadata(MetadataConstants.METADATA_KEY_REPLANT).size() >= 1) {
                if(block.getMetadata(MetadataConstants.METADATA_KEY_REPLANT).get(0).asBoolean()) {
                    if(isAgeableMature(ageableCrop)) {
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
        HashSet<Block> brokenBlocks = getBrokenHerbalismBlocks(blockBreakEvent);

        if(brokenBlocks.size() == 0)
            return;

        //Handle rewards, xp, ability interactions, etc
        processHerbalismOnBlocksBroken(blockBreakEvent, brokenBlocks);
    }

    /**
     * Process rewards for a set of plant blocks for Herbalism
     * @param blockBreakEvent the block break event
     * @param brokenPlants plant blocks to process
     */
    private void processHerbalismOnBlocksBroken(BlockBreakEvent blockBreakEvent, HashSet<Block> brokenPlants) {
        if(blockBreakEvent.isCancelled())
            return;

        BlockState originalBreak = blockBreakEvent.getBlock().getState();
        boolean greenThumbActivated = false;

        //TODO: The design of Green Terra needs to change, this is a mess
        if(Permissions.greenThumbPlant(getPlayer(), originalBreak.getType())) {
            if(mcMMO.p.getGeneralConfig().isGreenThumbReplantableCrop(originalBreak.getType())) {
                if(!getPlayer().isSneaking()) {
                    greenThumbActivated = processGreenThumbPlants(originalBreak, blockBreakEvent, isGreenTerraActive());
                }
            }
        }

        /*
         * Mark blocks for double drops
         * Be aware of the hacky interactions we are doing with Chorus Plants
         */
        checkDoubleDropsOnBrokenPlants(blockBreakEvent.getPlayer(), brokenPlants);

        //It would take an expensive algorithm to predict which parts of a Chorus Tree will break as a result of root break
        //So this hacky method is used instead
        ArrayList<BlockSnapshot> delayedChorusBlocks = new ArrayList<>(); //Blocks that will be checked in future ticks
        HashSet<Block> noDelayPlantBlocks = new HashSet<>(); //Blocks that will be checked immediately

        for(Block brokenPlant : brokenPlants) {
            /*
             * This check is to make XP bars appear to work properly with Chorus Trees by giving XP for the originalBreak immediately instead of later
             */
            if(brokenPlant.getLocation().equals(originalBreak.getBlock().getLocation())) {
                //If its the same block as the original, we are going to directly check it for being a valid XP gain and add it to the nonChorusBlocks list even if its a chorus block
                //This stops a delay from happening when bringing up the XP bar for chorus trees
                if(!mcMMO.getPlaceStore().isTrue(originalBreak)) {
                    //Even if its a chorus block, the original break will be moved to nonChorusBlocks for immediate XP rewards
                    noDelayPlantBlocks.add(brokenPlant);
                } else {
                    if(isChorusTree(brokenPlant.getType())) {
                        //If its a chorus tree AND it was marked as true in the placestore then we add this block to the list of chorus blocks
                        delayedChorusBlocks.add(new BlockSnapshot(brokenPlant.getType(), brokenPlant));
                    } else {
                        noDelayPlantBlocks.add(brokenPlant); //If its not a chorus plant that was marked as unnatural but it was marked unnatural, put it in the nodelay list to be handled
                    }
                }
            } else if(isChorusTree(brokenPlant.getType())) {
                //Chorus Blocks get checked for XP several ticks later to avoid expensive calculations
                delayedChorusBlocks.add(new BlockSnapshot(brokenPlant.getType(), brokenPlant));
            } else {
                noDelayPlantBlocks.add(brokenPlant);
            }
        }

        //Give out XP to the non-chorus blocks
        if(noDelayPlantBlocks.size() > 0) {
            //Note: Will contain 1 chorus block if the original block was a chorus block, this is to prevent delays for the XP bar
            awardXPForPlantBlocks(noDelayPlantBlocks);
        }

        if(delayedChorusBlocks.size() > 0) {
            //Check XP for chorus blocks
            DelayedHerbalismXPCheckTask delayedHerbalismXPCheckTask = new DelayedHerbalismXPCheckTask(mmoPlayer, delayedChorusBlocks);

            //Large delay because the tree takes a while to break
            delayedHerbalismXPCheckTask.runTaskLater(mcMMO.p, 0); //Calculate Chorus XP + Bonus Drops 1 tick later
        }
    }

    /**
     * Check for double drops on a collection of broken blocks
     * If a double drop has occurred, it will be marked here for bonus drops
     * @param player player who broke the blocks
     * @param brokenPlants the collection of broken plants
     */
    public void checkDoubleDropsOnBrokenPlants(Player player, Collection<Block> brokenPlants) {

        //Only proceed if skill unlocked and permission enabled
        if (!RankUtils.hasUnlockedSubskill(player, SubSkillType.HERBALISM_DOUBLE_DROPS)
                || !Permissions.isSubSkillEnabled(player, SubSkillType.HERBALISM_DOUBLE_DROPS)) {
            return;
        }

        for(Block brokenPlant : brokenPlants) {
            BlockState brokenPlantState = brokenPlant.getState();
            BlockData plantData = brokenPlantState.getBlockData();

            //Check for double drops
            if(!mcMMO.getPlaceStore().isTrue(brokenPlant)) {

                /*
                 *
                 * Natural Blocks
                 *
                 *
                 *
                 */

                //Not all things that are natural should give double drops, make sure its fully mature as well
                if(plantData instanceof Ageable ageable) {

                    if(isAgeableMature(ageable) || isBizarreAgeable(plantData)) {
                        if(checkDoubleDrop(brokenPlantState)) {
                            markForBonusDrops(brokenPlantState);
                        }
                    }
                } else if(checkDoubleDrop(brokenPlantState)) {
                    //Add metadata to mark this block for double or triple drops
                    markForBonusDrops(brokenPlantState);
                }
            } else {

                /*
                 *
                 * Unnatural Blocks
                 *
                 */

                //If its a Crop we need to reward XP when its fully grown
                if(isAgeableAndFullyMature(plantData) && !isBizarreAgeable(plantData)) {
                    //Add metadata to mark this block for double or triple drops
                    markForBonusDrops(brokenPlantState);
                }
            }
        }
    }

    /**
     * Checks if BlockData is ageable and we can trust that age for Herbalism rewards/XP reasons
     * @param blockData target BlockData
     * @return returns true if the ageable is trustworthy for Herbalism XP / Rewards
     */
    public boolean isBizarreAgeable(BlockData blockData) {
        if(blockData instanceof Ageable) {
            //Catcus and Sugar Canes cannot be trusted
            switch(blockData.getMaterial()) {
                case CACTUS:
                case KELP:
                case SUGAR_CANE:
                    return true;
                default:
                    return false;
            }
        }

        return false;
    }

    public void markForBonusDrops(BlockState brokenPlantState) {
        //Add metadata to mark this block for double or triple drops
        boolean awardTriple = mmoPlayer.getAbilityMode(SuperAbilityType.GREEN_TERRA);
        BlockUtils.markDropsAsBonus(brokenPlantState, awardTriple);
    }

    /**
     * Checks if a block is an ageable and if that ageable is fully mature
     * @param plantData target plant
     * @return returns true if the block is both an ageable and fully mature
     */
    public boolean isAgeableAndFullyMature(BlockData plantData) {
        return plantData instanceof Ageable && isAgeableMature((Ageable) plantData);
    }

    public void awardXPForPlantBlocks(HashSet<Block> brokenPlants) {
        int xpToReward = 0;

        for(Block brokenPlantBlock : brokenPlants) {
            BlockState brokenBlockNewState = brokenPlantBlock.getState();
            BlockData plantData = brokenBlockNewState.getBlockData();

            if(mcMMO.getPlaceStore().isTrue(brokenBlockNewState)) {
                /*
                 *
                 * Unnatural Blocks
                 *
                 *
                 */

                //If its a Crop we need to reward XP when its fully grown
                if(isAgeableAndFullyMature(plantData) && !isBizarreAgeable(plantData)) {
                    xpToReward += ExperienceConfig.getInstance().getXp(PrimarySkillType.HERBALISM, brokenBlockNewState.getType());
                }

                //Mark it as natural again as it is being broken
                mcMMO.getPlaceStore().setFalse(brokenBlockNewState);
            } else {
                /*
                 *
                 * Natural Blocks
                 *
                 *
                 */

                //Calculate XP
                if(plantData instanceof Ageable plantAgeable) {

                    if(isAgeableMature(plantAgeable) || isBizarreAgeable(plantData)) {
                        xpToReward += ExperienceConfig.getInstance().getXp(PrimarySkillType.HERBALISM, brokenBlockNewState.getType());
                    }

                } else {
                    xpToReward += ExperienceConfig.getInstance().getXp(PrimarySkillType.HERBALISM, brokenPlantBlock.getType());
                }
            }
        }

        if(mmoPlayer.isDebugMode()) {
            mmoPlayer.getPlayer().sendMessage("Plants processed: "+brokenPlants.size());
        }

        //Reward XP
        if(xpToReward > 0) {
            applyXpGain(xpToReward, XPGainReason.PVE, XPGainSource.SELF);
        }
    }

    public boolean isAgeableMature(Ageable ageable) {
        return ageable.getAge() == ageable.getMaximumAge()
                && ageable.getAge() != 0;
    }

    /**
     * Award XP for any blocks that used to be something else but are now AIR
     * @param brokenPlants snapshot of broken blocks
     */
    public void awardXPForBlockSnapshots(ArrayList<BlockSnapshot> brokenPlants) {
        /*
         * This handles XP for blocks that we need to check are broken after the fact
         * This only applies to chorus trees right now
         */
        int xpToReward = 0;
        int blocksGivingXP = 0;

        for(BlockSnapshot blockSnapshot : brokenPlants) {
            BlockState brokenBlockNewState = blockSnapshot.getBlockRef().getState();

            //Remove metadata from the snapshot of blocks
            if(brokenBlockNewState.hasMetadata(MetadataConstants.METADATA_KEY_BONUS_DROPS)) {
                brokenBlockNewState.removeMetadata(MetadataConstants.METADATA_KEY_BONUS_DROPS, mcMMO.p);
            }

            //If the block is not AIR that means it wasn't broken
            if(brokenBlockNewState.getType() != Material.AIR) {
                continue;
            }

            if(mcMMO.getPlaceStore().isTrue(brokenBlockNewState)) {
                //Mark it as natural again as it is being broken
                mcMMO.getPlaceStore().setFalse(brokenBlockNewState);
            } else {
                //TODO: Do we care about chorus flower age?
                //Calculate XP for the old type
                xpToReward += ExperienceConfig.getInstance().getXp(PrimarySkillType.HERBALISM, blockSnapshot.getOldType());
                blocksGivingXP++;
            }
        }

        if(mmoPlayer.isDebugMode()) {
            mmoPlayer.getPlayer().sendMessage("Chorus Plants checked for XP: "+brokenPlants.size());
            mmoPlayer.getPlayer().sendMessage("Valid Chorus Plant XP Gains: "+blocksGivingXP);
        }

        //Reward XP
        if(xpToReward > 0) {
            applyXpGain(xpToReward, XPGainReason.PVE, XPGainSource.SELF);
        }
    }

    /**
     * Process and return plant blocks from a BlockBreakEvent
     * @param blockBreakEvent target event
     * @return a set of plant-blocks that were broken as a result of this event
     */
    private HashSet<Block> getBrokenHerbalismBlocks(@NotNull BlockBreakEvent blockBreakEvent) {
        //Get an updated capture of this block
        BlockState originBlockState = blockBreakEvent.getBlock().getState();
        Material originBlockMaterial = originBlockState.getType();
        HashSet<Block> blocksBroken = new HashSet<>(); //Blocks broken

        //Add the initial block
        blocksBroken.add(originBlockState.getBlock());

        if(!isOneBlockPlant(originBlockMaterial)) {
            //If the block is a multi-block structure, capture a set of all blocks broken and return that
            blocksBroken = getBrokenBlocksMultiBlockPlants(originBlockState);
        }

        //Return all broken plant-blocks
        return blocksBroken;
    }

    private HashSet<Block> getBrokenChorusBlocks(BlockState originalBreak) {
        return grabChorusTreeBrokenBlocksRecursive(originalBreak.getBlock(), new HashSet<>());
    }

    private HashSet<Block> grabChorusTreeBrokenBlocksRecursive(Block currentBlock, HashSet<Block> traversed) {
        if (!isChorusTree(currentBlock.getType()))
            return traversed;

        // Prevent any infinite loops, who needs more than 256 chorus anyways
        if (traversed.size() > 256)
            return traversed;

        if (!traversed.add(currentBlock))
            return traversed;

        //Grab all Blocks in the Tree
        for (BlockFace blockFace : new BlockFace[] { BlockFace.UP, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST ,BlockFace.WEST})
            grabChorusTreeBrokenBlocksRecursive(currentBlock.getRelative(blockFace, 1), traversed);

        traversed.add(currentBlock);

        return traversed;
    }

    /**
     * Grab a set of all plant blocks that are broken as a result of this event
     * The method to grab these blocks is a bit hacky and does not hook into the API
     * Basically we expect the blocks to be broken if this event is not cancelled and we determine which block are broken on our end rather than any event state captures
     *
     * @return a set of plant-blocks broken from this event
     */
    protected HashSet<Block> getBrokenBlocksMultiBlockPlants(BlockState brokenBlock) {
        //Track the broken blocks
        HashSet<Block> brokenBlocks;

        if (isChorusBranch(brokenBlock.getType())) {
            brokenBlocks = getBrokenChorusBlocks(brokenBlock);
        } else {
            brokenBlocks = getBlocksBrokenAboveOrBelow(brokenBlock, false, mcMMO.getMaterialMapStore().isMultiBlockHangingPlant(brokenBlock.getType()));
        }

        return brokenBlocks;
    }

    private boolean isChorusBranch(Material blockType) {
        return blockType == Material.CHORUS_PLANT;
    }

    private boolean isChorusTree(Material blockType) {
        return blockType == Material.CHORUS_PLANT || blockType == Material.CHORUS_FLOWER;
    }

    /**
     * Grabs blocks upwards from a target block
     * A lot of Plants/Crops in Herbalism only break vertically from a broken block
     * The vertical search returns early if it runs into anything that is not a multi-block plant
     * Multi-block plants are hard-coded and kept in {@link MaterialMapStore}
     *
     * @param originBlock The point of the "break"
     * @param inclusive Whether to include the origin block
     * @param below Whether to search down instead of up.
     * @return A set of blocks above the target block which can be assumed to be broken
     */
    private HashSet<Block> getBlocksBrokenAboveOrBelow(BlockState originBlock, boolean inclusive, boolean below) {
        HashSet<Block> brokenBlocks = new HashSet<>();
        Block block = originBlock.getBlock();

        //Add the initial block to the set
        if(inclusive)
            brokenBlocks.add(block);

        //Limit our search
        int maxHeight = 512;

        final BlockFace relativeFace = below ? BlockFace.DOWN : BlockFace.UP;

        // Search vertically for multi-block plants, exit early if any non-multi block plants
        for (int y = 0; y < maxHeight; y++) {
            //TODO: Should this grab state? It would be more expensive..
            Block relativeBlock = block.getRelative(relativeFace, y);

            //Abandon our search if the block isn't multi
            if (isOneBlockPlant(relativeBlock.getType()))
                break;

            brokenBlocks.add(relativeBlock);
        }

        return brokenBlocks;
    }

    /**
     * If the plant is considered a one block plant
     * This is determined by seeing if it exists in a hard-coded collection of Multi-Block plants
     * @param material target plant material
     * @return true if the block is not contained in the collection of multi-block plants
     */
    private boolean isOneBlockPlant(Material material) {
        return !mcMMO.getMaterialMapStore().isMultiBlockPlant(material) && !mcMMO.getMaterialMapStore().isMultiBlockHangingPlant(material);
    }

    /**
     * Check for success on herbalism double drops
     * @param blockState target block state
     * @return true if double drop succeeds
     */
    private boolean checkDoubleDrop(BlockState blockState)
    {
        return BlockUtils.checkDoubleDrops(getPlayer(), blockState, skill, SubSkillType.HERBALISM_DOUBLE_DROPS);
    }

    /**
     * Process the Green Thumb ability for blocks.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @return true if the ability was successful, false otherwise
     */
    public boolean processGreenThumbBlocks(BlockState blockState) {
        if (!RandomChanceUtil.isActivationSuccessful(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, SubSkillType.HERBALISM_GREEN_THUMB, getPlayer())) {
            NotificationManager.sendPlayerInformation(getPlayer(), NotificationType.SUBSKILL_MESSAGE_FAILED, "Herbalism.Ability.GTh.Fail");
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
        if (!RandomChanceUtil.isActivationSuccessful(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, SubSkillType.HERBALISM_HYLIAN_LUCK, getPlayer())) {
            return false;
        }

        String friendly = StringUtils.getFriendlyConfigBlockDataString(blockState.getBlockData());
        if (!TreasureConfig.getInstance().hylianMap.containsKey(friendly))
            return false;
        List<HylianTreasure> treasures = TreasureConfig.getInstance().hylianMap.get(friendly);

        Player player = getPlayer();

        if (treasures.isEmpty()) {
            return false;
        }
        int skillLevel = getSkillLevel();
        Location location = Misc.getBlockCenter(blockState);

        for (HylianTreasure treasure : treasures) {
            if (skillLevel >= treasure.getDropLevel()
                    && RandomChanceUtil.checkRandomChanceExecutionSuccess(new RandomChanceSkillStatic(treasure.getDropChance(), getPlayer(), SubSkillType.HERBALISM_HYLIAN_LUCK))) {
                if (!EventUtils.simulateBlockBreak(blockState.getBlock(), player, false)) {
                    return false;
                }
                blockState.setType(Material.AIR);
                Misc.spawnItem(getPlayer(), location, treasure.getDrop(), ItemSpawnReason.HYLIAN_LUCK_TREASURE);
                NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE, "Herbalism.HylianLuck");
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
        Player player = getPlayer();
        PlayerInventory playerInventory = player.getInventory();
        
        if (!playerInventory.contains(Material.BROWN_MUSHROOM, 1)) {
            NotificationManager.sendPlayerInformation(player, NotificationType.REQUIREMENTS_NOT_MET, "Skills.NeedMore", StringUtils.getPrettyItemString(Material.BROWN_MUSHROOM));
            return false;
        }

        if (!playerInventory.contains(Material.RED_MUSHROOM, 1)) {
            NotificationManager.sendPlayerInformation(player, NotificationType.REQUIREMENTS_NOT_MET, "Skills.NeedMore", StringUtils.getPrettyItemString(Material.RED_MUSHROOM));
            return false;
        }

        playerInventory.removeItem(new ItemStack(Material.BROWN_MUSHROOM));
        playerInventory.removeItem(new ItemStack(Material.RED_MUSHROOM));
        player.updateInventory();

        if (!RandomChanceUtil.isActivationSuccessful(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, SubSkillType.HERBALISM_SHROOM_THUMB, player)) {
            NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE_FAILED, "Herbalism.Ability.ShroomThumb.Fail");
            return false;
        }

        return Herbalism.convertShroomThumb(blockState);
    }

    /**
     * Starts the delayed replant task and turns
     * @param desiredCropAge the desired age of the crop
     * @param blockBreakEvent the {@link BlockBreakEvent} this crop was involved in
     * @param cropState the {@link BlockState} of the crop
     */
    private void startReplantTask(int desiredCropAge, BlockBreakEvent blockBreakEvent, BlockState cropState, boolean isImmature) {
        //Mark the plant as recently replanted to avoid accidental breakage
        new DelayedCropReplant(blockBreakEvent, cropState, desiredCropAge, isImmature).runTaskLater(mcMMO.p, 20 * 2);
        blockBreakEvent.getBlock().setMetadata(MetadataConstants.METADATA_KEY_REPLANT, new RecentlyReplantedCropMeta(mcMMO.p, true));
    }

    /**
     * Process the Green Thumb ability for plants.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @param greenTerra boolean to determine if greenTerra is active or not
     */
    private boolean processGreenThumbPlants(BlockState blockState, BlockBreakEvent blockBreakEvent, boolean greenTerra) {
        if (!ItemUtils.isHoe(blockBreakEvent.getPlayer().getInventory().getItemInMainHand())
            && !ItemUtils.isAxe(blockBreakEvent.getPlayer().getInventory().getItemInMainHand())) {
            return false;
        }

        BlockData blockData = blockState.getBlockData();

        if (!(blockData instanceof Ageable ageable)) {
            return false;
        }

        //If the ageable is NOT mature and the player is NOT using a hoe, abort

        Player player = getPlayer();
        PlayerInventory playerInventory = player.getInventory();
        Material seed;

        switch (blockState.getType()) {
            case CARROTS:
                seed = Material.CARROT;
                break;

            case WHEAT:
                seed = Material.WHEAT_SEEDS;
                break;

            case NETHER_WART:
                seed = Material.NETHER_WART;
                break;

            case POTATOES:
                seed = Material.POTATO;
                break;

            case BEETROOTS:
                seed = Material.BEETROOT_SEEDS;
                break;

            case COCOA:
                seed = Material.COCOA_BEANS;
                break;

            default:
                return false;
        }

        ItemStack seedStack = new ItemStack(seed);

        if (ItemUtils.isAxe(blockBreakEvent.getPlayer().getInventory().getItemInMainHand())
        && blockState.getType() != Material.COCOA) {
            return false;
        }

        if (!greenTerra && !RandomChanceUtil.checkRandomChanceExecutionSuccess(player, SubSkillType.HERBALISM_GREEN_THUMB, true)) {
            return false;
        }

        if (!playerInventory.containsAtLeast(seedStack, 1)) {
            return false;
        }

        if (!processGrowingPlants(blockState, ageable, blockBreakEvent, greenTerra)) {
            return false;
        }

        if(EventUtils.callSubSkillBlockEvent(player, SubSkillType.HERBALISM_GREEN_THUMB, blockState.getBlock()).isCancelled()) {
            return false;
        } else {
            playerInventory.removeItem(seedStack);
            player.updateInventory(); // Needed until replacement available
            //Play sound
            SoundManager.sendSound(player, player.getLocation(), SoundType.ITEM_CONSUMED);
            return true;
        }

//        new HerbalismBlockUpdaterTask(blockState).runTaskLater(mcMMO.p, 0);
    }

    private boolean processGrowingPlants(BlockState blockState, Ageable ageable, BlockBreakEvent blockBreakEvent, boolean greenTerra) {
        //This check is needed
        if(isBizarreAgeable(ageable)) {
            return false;
        }

        int finalAge;
        int greenThumbStage = getGreenThumbStage(greenTerra);

        //Immature plants will start over at 0
        if(!isAgeableMature(ageable)) {
//            blockBreakEvent.setCancelled(true);
            startReplantTask(0, blockBreakEvent, blockState, true);
//            blockState.setType(Material.AIR);
            blockBreakEvent.setDropItems(false);
            return true;
        }

        switch (blockState.getType()) {

            case POTATOES:
            case CARROTS:
            case WHEAT:

                    finalAge = getGreenThumbStage(greenTerra);
                break;

            case BEETROOTS:
            case NETHER_WART:

                if (greenTerra || greenThumbStage > 2) {
                    finalAge = 2;
                }
                else if (greenThumbStage == 2) {
                    finalAge = 1;
                }
                else {
                    finalAge = 0;
                }
               break;

            case COCOA:

                if (getGreenThumbStage(greenTerra) >= 2) {
                    finalAge = 1;
                }
                else {
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
        if(greenTerraActive)
            return Math.min(RankUtils.getHighestRank(SubSkillType.HERBALISM_GREEN_THUMB),
                    RankUtils.getRank(getPlayer(), SubSkillType.HERBALISM_GREEN_THUMB) + 1);

        return RankUtils.getRank(getPlayer(), SubSkillType.HERBALISM_GREEN_THUMB);
    }
}
