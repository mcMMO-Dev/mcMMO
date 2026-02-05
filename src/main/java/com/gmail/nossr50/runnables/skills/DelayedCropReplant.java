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
        final BlockState blockState = cropLocation.getBlock().getState();
        PlantAnchorType plantAnchorType = PlantAnchorType.NORMAL;

        //Remove the metadata marking the block as recently replanted
        mcMMO.p.getFoliaLib().getScheduler()
                .runAtLocationLater(blockBreakEvent.getBlock().getLocation(),
                        new markPlantAsOld(blockBreakEvent.getBlock().getLocation()), 10);

        if (blockBreakEvent.isCancelled()) {
            wasImmaturePlant = true;
        }

        if (blockIsAirOrExpectedCrop(blockState)) {
            // Modify the new state of the block, not any old snapshot of it
            blockState.setType(cropMaterial);
            final BlockData newData = blockState.getBlockData();

            // Immature plants should be age 0, others get the desired age
            int age = wasImmaturePlant ? 0 : desiredCropAge;

            if (newData instanceof Directional) {
                // Cocoa Version
                Directional directional = (Directional) blockState.getBlockData();
                directional.setFacing(cropFace);

                blockState.setBlockData(directional);

                if (newData instanceof Cocoa) {
                    plantAnchorType = PlantAnchorType.COCOA;
                }
            }

            if (blockState.getBlockData() instanceof Ageable ageable) {
                ageable.setAge(age);
                blockState.setBlockData(ageable);
                blockState.update(true, true);

                //Play an effect
                ParticleEffectUtils.playGreenThumbEffect(cropLocation);
                mcMMO.p.getFoliaLib().getScheduler().runAtLocationLater(blockState.getLocation(),
                        new PhysicsBlockUpdate(blockState.getBlock(), cropFace, plantAnchorType), 1);
            }
        }
    }

    private boolean blockIsAirOrExpectedCrop(BlockState blockState) {
        return blockState.getType().equals(cropMaterial) || blockState.getType()
                .equals(Material.AIR) || blockState.getType().equals(Material.CAVE_AIR);
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
