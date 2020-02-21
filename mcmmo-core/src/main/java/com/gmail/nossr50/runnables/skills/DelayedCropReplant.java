package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.core.MetadataConstants;
import com.gmail.nossr50.datatypes.meta.RecentlyReplantedCropMeta;
import com.gmail.nossr50.mcMMO;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class DelayedCropReplant extends BukkitRunnable {

    private final int desiredCropAge;
    private final Location cropLocation;
    private final Material cropMaterial;
    private boolean wasImmaturePlant;
    private final BlockBreakEvent blockBreakEvent;
    private final mcMMO pluginRef;
    private BlockFace cropFace;

    /**
     * Replants a crop after a delay setting the age to desiredCropAge
     * @param cropState target {@link BlockState}
     * @param desiredCropAge desired age of the crop
     */
    public DelayedCropReplant(mcMMO pluginRef, BlockBreakEvent blockBreakEvent, BlockState cropState, int desiredCropAge, boolean wasImmaturePlant) {
        this.pluginRef = pluginRef;
        BlockData cropData = cropState.getBlockData();

        if(cropData instanceof Directional) {
            Directional cropDir = (Directional) cropData;
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

        //Remove the metadata marking the block as recently replanted
        new markPlantAsOld(blockBreakEvent.getBlock().getLocation()).runTaskLater(pluginRef, 10);

        if(blockBreakEvent.isCancelled()) {
            wasImmaturePlant = true;
        }

        //Two kinds of air in Minecraft
        if(currentState.getType().equals(cropMaterial) || currentState.getType().equals(Material.AIR) || currentState.getType().equals(Material.CAVE_AIR)) {
//            if(currentState.getBlock().getRelative(BlockFace.DOWN))
            //The space is not currently occupied by a block so we can fill it
            cropBlock.setType(cropMaterial);


            //Get new state (necessary?)
            BlockState newState = cropBlock.getState();
            BlockData newData = newState.getBlockData();

            int age = 0;

            //Crop age should always be 0 if the plant was immature
            if(!wasImmaturePlant) {
                age = desiredCropAge;
                //Otherwise make the plant the desired age
            }

            if(newData instanceof Directional) {
                //Cocoa Version
                Directional directional = (Directional) newState.getBlockData();
                directional.setFacing(cropFace);

                newState.setBlockData(directional);
            }

            //Age the crop
            Ageable ageable = (Ageable) newState.getBlockData();
            ageable.setAge(age);
            newState.setBlockData(ageable);


            newState.update(true);

            //Play an effect
            pluginRef.getParticleEffectUtils().playGreenThumbEffect(cropLocation);

        }

    }

    private class markPlantAsOld extends BukkitRunnable {

        private final Location cropLoc;

        public markPlantAsOld(Location cropLoc) {
            this.cropLoc = cropLoc;
        }

        @Override
        public void run() {
            Block cropBlock = cropLoc.getBlock();

            if(cropBlock.getMetadata(MetadataConstants.REPLANT_META_KEY).size() > 0) {
                cropBlock.setMetadata(MetadataConstants.REPLANT_META_KEY, new RecentlyReplantedCropMeta(pluginRef, false));
                pluginRef.getParticleEffectUtils().playFluxEffect(cropLocation);
            }
        }
    }

}
