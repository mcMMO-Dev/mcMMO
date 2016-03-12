package com.gmail.nossr50.skills.herbalism;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.SmoothBrick;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.util.skills.SkillUtils;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class Herbalism {
    public static int farmersDietRankLevel1 = AdvancedConfig.getInstance().getFarmerDietRankChange();
    public static int farmersDietRankLevel2 = farmersDietRankLevel1 * 2;
    public static int farmersDietMaxLevel   = farmersDietRankLevel1 * 5;

    public static int greenThumbStageChangeLevel = AdvancedConfig.getInstance().getGreenThumbStageChange();
    public static int greenThumbStageMaxLevel    = greenThumbStageChangeLevel * 4;

    /**
     * Convert blocks affected by the Green Thumb & Green Terra abilities.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @return true if the ability was successful, false otherwise
     */
    protected static boolean convertGreenTerraBlocks(BlockState blockState) {
        switch (blockState.getType()) {
            case COBBLE_WALL:
                blockState.setRawData((byte) 0x1);
                return true;

            case SMOOTH_BRICK:
                ((SmoothBrick) blockState.getData()).setMaterial(Material.MOSSY_COBBLESTONE);
                return true;

            case DIRT:
            case GRASS_PATH:
                blockState.setType(Material.GRASS);
                return true;

            case COBBLESTONE:
                blockState.setType(Material.MOSSY_COBBLESTONE);
                return true;

            default:
                return false;
        }
    }
    
    public static List<Block> findChorusPlant(Block target) {
        return findChorusPlant(target, true);
    }
    
    private static List<Block> findChorusPlant(Block target, boolean origin) {
        List<Block> blocks = new ArrayList<Block>();
        
        if(target.getType() != Material.CHORUS_PLANT) {
            return blocks;
        }
        
        blocks.add(target);
        
        Block relative = target.getRelative(BlockFace.UP, 1);
        if(relative.getType() == Material.CHORUS_PLANT) {
            blocks.addAll(findChorusPlant(relative, false));
        }
        
        if(origin || target.getRelative(BlockFace.DOWN, 1).getType() == Material.CHORUS_PLANT) {
            relative = target.getRelative(BlockFace.NORTH, 1);
            if(relative.getType() == Material.CHORUS_PLANT) {
                blocks.addAll(findChorusPlant(relative, false));
            }
            
            relative = target.getRelative(BlockFace.SOUTH, 1);
            if(relative.getType() == Material.CHORUS_PLANT) {
                blocks.addAll(findChorusPlant(relative, false));
            }

            relative = target.getRelative(BlockFace.EAST, 1);
            if(relative.getType() == Material.CHORUS_PLANT) {
                blocks.addAll(findChorusPlant(relative, false));
            }

            relative = target.getRelative(BlockFace.WEST, 1);
            if(relative.getType() == Material.CHORUS_PLANT) {
                blocks.addAll(findChorusPlant(relative, false));
            }
        }
        
        return new ArrayList<Block>(new LinkedHashSet<Block>(blocks));
    }

    /**
     * Calculate the drop amounts for multi block plants based on the blocks relative to them.
     *
     * @param blockState The {@link BlockState} of the bottom block of the plant
     * @return the number of bonus drops to award from the blocks in this plant
     */
    protected static int calculateMultiBlockPlantDrops(BlockState blockState) {
        Block block = blockState.getBlock();
        Material blockType = blockState.getType();
        int dropAmount = mcMMO.getPlaceStore().isTrue(block) ? 0 : 1;

        if(blockType == Material.CHORUS_PLANT) {
            dropAmount = 1;
            
            if(block.getRelative(BlockFace.DOWN, 1).getType() == Material.ENDER_STONE) {
                List<Block> blocks = findChorusPlant(block);
                
                dropAmount = blocks.size();
				
                /*for(Block b : blocks) {
                    b.breakNaturally();
                }*/
            }
        }
        else {
            // Handle the two blocks above it - cacti & sugar cane can only grow 3 high naturally
            for (int y = 1; y < 3; y++) {
                Block relativeBlock = block.getRelative(BlockFace.UP, y);

                if (relativeBlock.getType() != blockType) {
                    break;
                }

                if (mcMMO.getPlaceStore().isTrue(relativeBlock)) {
                    mcMMO.getPlaceStore().setFalse(relativeBlock);
                }
                else {
                    dropAmount++;
                }
            }
        }

        return dropAmount;
    }

    /**
     * Convert blocks affected by the Green Thumb & Green Terra abilities.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @return true if the ability was successful, false otherwise
     */
    protected static boolean convertShroomThumb(BlockState blockState) {
        switch (blockState.getType()) {
            case DIRT:
            case GRASS:
            case GRASS_PATH:
                blockState.setType(Material.MYCEL);
                return true;

            default:
                return false;
        }
    }

    /**
     * Check if the block has a recently grown crop from Green Thumb
     *
     * @param blockState The {@link BlockState} to check green thumb regrown for
     * @return true if the block is recently regrown, false otherwise
     */
    public static boolean isRecentlyRegrown(BlockState blockState) {
        return blockState.hasMetadata(mcMMO.greenThumbDataKey) && !SkillUtils.cooldownExpired(blockState.getMetadata(mcMMO.greenThumbDataKey).get(0).asInt(), 1);
    }
}
