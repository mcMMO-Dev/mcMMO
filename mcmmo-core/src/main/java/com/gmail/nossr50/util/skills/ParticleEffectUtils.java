package com.gmail.nossr50.util.skills;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.sounds.SoundType;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public final class ParticleEffectUtils {

    private final mcMMO pluginRef;

    public ParticleEffectUtils(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    public void playGreenThumbEffect(Location location) {
        World world = location.getWorld();
        playSmokeEffect(location);
        pluginRef.getSoundManager().worldSendSoundMaxPitch(world, location, SoundType.POP);
    }

    public void playBleedEffect(LivingEntity livingEntity) {
        /*if (!MainConfig.getInstance().getBleedEffectEnabled()) {
            return;
        }*/

        livingEntity.getWorld().playEffect(livingEntity.getEyeLocation(), Effect.STEP_SOUND, Material.REDSTONE_WIRE);
    }

    public void playDodgeEffect(Player player) {
        /*if (!MainConfig.getInstance().getDodgeEffectEnabled()) {
            return;
        }*/

        playSmokeEffect(player.getLocation());
    }

    public void playFluxEffect(Location location) {
        /*if (!MainConfig.getInstance().getFluxEffectEnabled()) {
            return;
        }*/

        if(location.getWorld() == null)
            return;

        location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 1);
    }

    public void playSmokeEffect(Location location) {
        World world = location.getWorld();

        if(world == null)
            return;

        // Have to do it this way, because not all block directions are valid for smoke
        world.playEffect(location, Effect.SMOKE, BlockFace.SOUTH_EAST);
        world.playEffect(location, Effect.SMOKE, BlockFace.SOUTH);
        world.playEffect(location, Effect.SMOKE, BlockFace.SOUTH_WEST);
        world.playEffect(location, Effect.SMOKE, BlockFace.EAST);
        world.playEffect(location, Effect.SMOKE, BlockFace.SELF);
        world.playEffect(location, Effect.SMOKE, BlockFace.WEST);
        world.playEffect(location, Effect.SMOKE, BlockFace.NORTH_EAST);
        world.playEffect(location, Effect.SMOKE, BlockFace.NORTH);
        world.playEffect(location, Effect.SMOKE, BlockFace.NORTH_WEST);
    }

    public void playGreaterImpactEffect(LivingEntity livingEntity) {
        /*if (!MainConfig.getInstance().getGreaterImpactEffectEnabled()) {
            return;
        }*/

        Location location = livingEntity.getEyeLocation();

        livingEntity.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), 0F, false, false);
    }

    public void playCallOfTheWildEffect(LivingEntity livingEntity) {
        /*if (!MainConfig.getInstance().getCallOfTheWildEffectEnabled()) {
            return;
        }*/

        livingEntity.getWorld().playEffect(livingEntity.getEyeLocation(), Effect.MOBSPAWNER_FLAMES, 1);
    }

/*    public static void fireworkParticleShower(Player player, Color color) {
        Location location = player.getLocation();
        location.setY(location.getY() + (player.isInsideVehicle() ? 1.0 : -1.0));
        location.setPitch(-90);

        Firework firework = (Firework) player.getWorld().spawnEntity(location, EntityType.FIREWORK);
        firework.setMetadata(mcMMO.SPAWNED_FIREWORKS_METAKEY, new FixedMetadataValue(mcMMO.p, null));
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(color).with((MainConfig.getInstance().getLargeFireworks() ? Type.BALL_LARGE : Type.BALL)).trail(true).build();
        fireworkMeta.addEffect(effect);
        fireworkMeta.addEffect(effect);
        fireworkMeta.setPower(0);
        firework.setFireworkMeta(fireworkMeta);
    }*/

    private boolean hasHeadRoom(Player player) {
        boolean hasHeadRoom = true;
        Block headBlock = player.getEyeLocation().getBlock();

        for (int i = 0; i < 3; i++) {
            if (!headBlock.getRelative(BlockFace.UP, i).isEmpty()) {
                hasHeadRoom = false;
                break;
            }
        }

        return hasHeadRoom;
    }
}
