package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.meta.RecentlyReplantedCropMeta;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.CancellableRunnable;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Cocoa;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DelayedCropReplant extends CancellableRunnable {

    private final int desiredCropAge;
    private final Location cropLocation;
    private final Material cropMaterial;
    private boolean wasImmaturePlant;
    private final BlockBreakEvent blockBreakEvent;
    private @Nullable BlockFace cropFace;

    /**
     * Replants a crop after a delay setting the age to desiredCropAge
     *
     * @param cropState target {@link BlockState}
     * @param desiredCropAge desired age of the crop
     */
    public DelayedCropReplant(BlockBreakEvent blockBreakEvent, BlockState cropState,
            int desiredCropAge, boolean wasImmaturePlant) {
        BlockData cropData = cropState.getBlockData();

        if (cropData instanceof Directional cropDir) {
            cropFace = cropDir.getFacing();
        }

        //The plant was either immature or something cancelled the event, therefor we need to treat it differently
        this.blockBreakEvent = blockBreakEvent;
        this.wasImmaturePlant = wasImmaturePlant;
        this.cropMaterial = cropState.getType();
        this.desiredCropAge = desiredCropAge;
        this.cropLocation = cropState.getLocation();
    }

    @Override
    public void run() {
        Block cropBlock = cropLocation.getBlock();
        BlockState currentState = cropBlock.getState();
        PlantAnchorType plantAnchorType = PlantAnchorType.NORMAL;

        //Remove the metadata marking the block as recently replanted
        mcMMO.p.getFoliaLib().getScheduler()
                .runAtLocationLater(blockBreakEvent.getBlock().getLocation(),
                        new markPlantAsOld(blockBreakEvent.getBlock().getLocation()), 10);

        if (blockBreakEvent.isCancelled()) {
            wasImmaturePlant = true;
        }

        //Two kinds of air in Minecraft
        if (currentState.getType().equals(cropMaterial) || currentState.getType()
                .equals(Material.AIR) || currentState.getType().equals(Material.CAVE_AIR)) {
//            if (currentState.getBlock().getRelative(BlockFace.DOWN))
            //The space is not currently occupied by a block so we can fill it
            cropBlock.setType(cropMaterial);

            //Get new state (necessary?)
            BlockState newState = cropBlock.getState();
            BlockData newData = newState.getBlockData();

            int age = 0;

            //Crop age should always be 0 if the plant was immature
            if (!wasImmaturePlant) {
                age = desiredCropAge;
                //Otherwise make the plant the desired age
            }

            if (newData instanceof Directional) {
                //Cocoa Version
                Directional directional = (Directional) newState.getBlockData();
                directional.setFacing(cropFace);

                newState.setBlockData(directional);

                if (newData instanceof Cocoa) {
                    plantAnchorType = PlantAnchorType.COCOA;
                }
            }

            //Age the crop
            Ageable ageable = (Ageable) newState.getBlockData();
            ageable.setAge(age);
            newState.setBlockData(ageable);

            newState.update(true, true);

            //Play an effect
            ParticleEffectUtils.playGreenThumbEffect(cropLocation);
            mcMMO.p.getFoliaLib().getScheduler().runAtLocationLater(newState.getLocation(),
                    new PhysicsBlockUpdate(newState.getBlock(), cropFace, plantAnchorType), 1);
        }
    }

    private enum PlantAnchorType {
        NORMAL,
        COCOA
    }

    private static class PhysicsBlockUpdate extends CancellableRunnable {
        private final Block plantBlock;
        private final PlantAnchorType plantAnchorType;
        private BlockFace plantFace;

        private PhysicsBlockUpdate(@NotNull Block plantBlock, @Nullable BlockFace plantFace,
                @NotNull PlantAnchorType plantAnchorType) {
            this.plantBlock = plantBlock;
            this.plantAnchorType = plantAnchorType;

            if (plantFace != null) {
                this.plantFace = plantFace;
            }
        }

        @Override
        public void run() {
            //Update neighbors
            switch (plantAnchorType) {
                case COCOA:
                    checkPlantIntegrity(plantFace);
                    break;
                case NORMAL:
                    checkPlantIntegrity(BlockFace.DOWN);
                    break;
            }
        }

        private void checkPlantIntegrity(@NotNull BlockFace blockFace) {
            Block neighbor = plantBlock.getRelative(blockFace);

            if (plantAnchorType == PlantAnchorType.COCOA) {
                if (!neighbor.getType().toString().toLowerCase().contains("jungle")) {
                    plantBlock.breakNaturally();
                }
            } else {
                switch (neighbor.getType()) {
                    case AIR:
                    case CAVE_AIR:
                    case WATER:
                    case LAVA:
                        plantBlock.breakNaturally();
                        break;
                    default:
                }
            }
        }
    }


    private static class markPlantAsOld extends CancellableRunnable {

        private final Location cropLoc;

        public markPlantAsOld(Location cropLoc) {
            this.cropLoc = cropLoc;
        }

        @Override
        public void run() {
            Block cropBlock = cropLoc.getBlock();
            if (cropBlock.getMetadata(MetadataConstants.METADATA_KEY_REPLANT).size() > 0) {
                cropBlock.setMetadata(MetadataConstants.METADATA_KEY_REPLANT,
                        new RecentlyReplantedCropMeta(mcMMO.p, false));
            }
        }
    }

}
